@echo off
setlocal
pushd "%~dp0"
if not exist "config\application.properties" (
    echo Copia config\application.properties.example a config\application.properties.
    echo Completa los datos de tu servidor MySQL antes de iniciar.
    popd
    exit /b 1
)
"runtime\bin\java.exe" --add-modules javafx.controls,javafx.fxml -Dprism.order=sw -cp "config;PharmaVictoria.jar" com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication
set "resultado=%errorlevel%"
popd
exit /b %resultado%
