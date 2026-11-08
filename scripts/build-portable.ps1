# Ejecutar después de mvn -Pportable-ci clean verify, desde Windows con Liberica JDK Full 21.
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root 'target'
[xml]$pom = Get-Content -LiteralPath (Join-Path $root 'pom.xml') -Raw
$jar = Join-Path $target "$($pom.project.artifactId)-$($pom.project.version).jar"
if (-not (Test-Path -LiteralPath $jar)) {
    throw 'Falta el JAR. Ejecuta mvn -Pportable-ci clean verify.'
}

$mods = Join-Path $env:JAVA_HOME 'jmods'
if (-not (Test-Path -LiteralPath (Join-Path $mods 'javafx.fxml.jmod'))) {
    throw 'JAVA_HOME debe apuntar a un JDK Full 21 con módulos JavaFX.'
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
$jarZip = [System.IO.Compression.ZipFile]::OpenRead($jar)
try {
    $configuracionesPrivadas = @(
        'application.properties', 'alerta-config.properties', 'backup-path.properties',
        'default-email.properties', 'gmail_nubefact.properties', 'recuperacion_email.properties',
        'smtp_proveedores.properties', 'venta_nubefact.properties', 'client_secret.json',
        'google/client_secret.json'
    )
    foreach ($entrada in $jarZip.Entries) {
        if ($entrada.FullName -in $configuracionesPrivadas) {
            throw "El JAR incluye configuración local: $($entrada.FullName). Recompila con -Pportable-ci."
        }
    }
} finally {
    $jarZip.Dispose()
}

$carpeta = Join-Path $target 'PharmaVictoria-Windows-x64'
$zip = "$carpeta.zip"
if (Test-Path -LiteralPath $carpeta) { Remove-Item -LiteralPath $carpeta -Recurse -Force }
if (Test-Path -LiteralPath $zip) { Remove-Item -LiteralPath $zip -Force }
New-Item -ItemType Directory -Path $carpeta | Out-Null

$jlink = Join-Path $env:JAVA_HOME 'bin/jlink.exe'
$runtime = Join-Path $carpeta 'runtime'
& $jlink --module-path $mods --add-modules 'java.se,jdk.crypto.ec,jdk.unsupported,javafx.controls,javafx.fxml,javafx.swing,javafx.media,javafx.web' --strip-debug --no-header-files --no-man-pages --compress=2 --output $runtime
if ($LASTEXITCODE -ne 0) { throw 'Falló jlink al generar el runtime portátil.' }

& (Join-Path $runtime 'bin/java.exe') --describe-module javafx.fxml
if ($LASTEXITCODE -ne 0) { throw 'El runtime generado no reconoce JavaFX.' }

Copy-Item -LiteralPath $jar -Destination (Join-Path $carpeta 'PharmaVictoria.jar')
Copy-Item -Path (Join-Path $PSScriptRoot 'portable/*') -Destination $carpeta -Recurse
[System.IO.Compression.ZipFile]::CreateFromDirectory($carpeta, $zip)
Write-Host "ZIP portátil creado en $zip"
