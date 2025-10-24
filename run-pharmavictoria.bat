@echo off
echo ========================================
echo    PHARMAVICTORIA - Sistema de Gestion
echo    Iniciando aplicacion JavaFX...
echo ========================================
cd /d "%~dp0"

REM Compilar si es necesario
echo Verificando compilacion...
if not exist "target\classes" (
    echo Compilando proyecto...
    call mvn clean compile
    if errorlevel 1 (
        echo ERROR: No se pudo compilar el proyecto
        pause
        exit /b 1
    )
)

echo Ejecutando PHARMAVICTORIA...
java --module-path "target/dependency" --add-modules javafx.controls,javafx.fxml --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -cp "target/classes;target/dependency/*" com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication

if errorlevel 1 (
    echo.
    echo ERROR: La aplicacion no se pudo ejecutar correctamente
    echo Asegurate de que:
    echo - Java 21 este instalado
    echo - MySQL este ejecutandose
    echo - Las dependencias esten en target/dependency/
    echo.
    echo Ejecuta: mvn dependency:copy-dependencies
)

pause