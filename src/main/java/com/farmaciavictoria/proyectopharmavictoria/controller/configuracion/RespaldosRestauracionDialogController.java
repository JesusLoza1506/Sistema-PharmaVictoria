package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import com.farmaciavictoria.proyectopharmavictoria.service.BackupService;
import com.farmaciavictoria.proyectopharmavictoria.service.RestoreService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class RespaldosRestauracionDialogController implements Initializable {
    public RespaldosRestauracionDialogController() {
        System.out.println("[DEBUG] Constructor RespaldosRestauracionDialogController llamado");
    }

    private ScheduledExecutorService scheduler;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblDescripcion;

    @FXML
    private ComboBox<String> comboDestino;
    @FXML
    private ComboBox<String> comboFrecuencia;
    @FXML
    private ComboBox<String> comboHora;
    @FXML
    private TextField txtRutaRespaldo;
    @FXML
    private Button btnEjecutarRespaldo;
    @FXML
    private Button btnIniciarRespaldoProgramado;
    @FXML
    private Button btnRestaurarInformacion;
    @FXML
    private Button btnSeleccionarCarpeta;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("[DEBUG] Inicializando RespaldosRestauracionDialogController...");
        if (comboDestino != null) {
            comboDestino.setItems(javafx.collections.FXCollections.observableArrayList("Local", "Nube", "Ambos"));
            System.out.println("[DEBUG] comboDestino items: " + comboDestino.getItems());
            if (comboDestino.getItems().isEmpty()) {
                System.out.println("[ERROR] comboDestino no tiene items!");
                mostrarAlerta("Error de interfaz",
                        "No se pudo inicializar el ComboBox de destino. Revisa el FXML y el controller.",
                        javafx.scene.control.Alert.AlertType.ERROR);
            }
            comboDestino.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean mostrar = "Local".equals(newVal) || "Ambos".equals(newVal);
                txtRutaRespaldo.setDisable(!mostrar);
                btnSeleccionarCarpeta.setDisable(!mostrar);
                txtRutaRespaldo.setVisible(mostrar);
                btnSeleccionarCarpeta.setVisible(mostrar);
                if (mostrar) {
                    String rutaGuardada = leerUltimaRutaRespaldo();
                    if (rutaGuardada != null && !rutaGuardada.isEmpty()) {
                        txtRutaRespaldo.setText(rutaGuardada);
                    }
                } else {
                    txtRutaRespaldo.clear();
                }
            });
            // Inicializar estado
            boolean mostrar = "Local".equals(comboDestino.getValue()) || "Ambos".equals(comboDestino.getValue());
            txtRutaRespaldo.setDisable(!mostrar);
            btnSeleccionarCarpeta.setDisable(!mostrar);
            txtRutaRespaldo.setVisible(mostrar);
            btnSeleccionarCarpeta.setVisible(mostrar);
            if (mostrar) {
                String rutaGuardada = leerUltimaRutaRespaldo();
                if (rutaGuardada != null && !rutaGuardada.isEmpty()) {
                    txtRutaRespaldo.setText(rutaGuardada);
                }
            } else {
                txtRutaRespaldo.clear();
            }
        }
        if (comboFrecuencia != null) {
            comboFrecuencia
                    .setItems(javafx.collections.FXCollections.observableArrayList("Diario", "Semanal", "Mensual"));
            System.out.println("[DEBUG] comboFrecuencia items: " + comboFrecuencia.getItems());
            if (comboFrecuencia.getItems().isEmpty()) {
                System.out.println("[ERROR] comboFrecuencia no tiene items!");
                mostrarAlerta("Error de interfaz",
                        "No se pudo inicializar el ComboBox de frecuencia. Revisa el FXML y el controller.",
                        javafx.scene.control.Alert.AlertType.ERROR);
            }
        }
        if (comboHora != null) {
            java.util.List<String> horas = new java.util.ArrayList<>();
            for (int h = 0; h < 24; h++) {
                horas.add(String.format("%02d:00", h));
            }
            comboHora.setItems(javafx.collections.FXCollections.observableArrayList(horas));
            System.out.println("[DEBUG] comboHora items: " + comboHora.getItems());
            if (comboHora.getItems().isEmpty()) {
                System.out.println("[ERROR] comboHora no tiene items!");
                mostrarAlerta("Error de interfaz",
                        "No se pudo inicializar el ComboBox de hora. Revisa el FXML y el controller.",
                        javafx.scene.control.Alert.AlertType.ERROR);
            }
        }

        if (btnEjecutarRespaldo != null) {
            btnEjecutarRespaldo.setOnAction(e -> ejecutarRespaldoRapido());
        }
        if (btnIniciarRespaldoProgramado != null) {
            btnIniciarRespaldoProgramado.setOnAction(e -> iniciarRespaldoProgramado());
        }
        if (btnRestaurarInformacion != null) {
            btnRestaurarInformacion.setOnAction(e -> restaurarInformacion());
        }
        if (btnSeleccionarCarpeta != null) {
            btnSeleccionarCarpeta.setOnAction(e -> seleccionarCarpeta());
        }
        if (comboDestino != null) {
            comboDestino.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean mostrar = "Local".equals(newVal) || "Ambos".equals(newVal);
                txtRutaRespaldo.setDisable(!mostrar);
                btnSeleccionarCarpeta.setDisable(!mostrar);
                if (mostrar) {
                    String rutaGuardada = leerUltimaRutaRespaldo();
                    if (rutaGuardada != null && !rutaGuardada.isEmpty()) {
                        txtRutaRespaldo.setText(rutaGuardada);
                    }
                }
            });
            // Inicializar estado
            boolean mostrar = "Local".equals(comboDestino.getValue()) || "Ambos".equals(comboDestino.getValue());
            txtRutaRespaldo.setDisable(!mostrar);
            btnSeleccionarCarpeta.setDisable(!mostrar);
            if (mostrar) {
                String rutaGuardada = leerUltimaRutaRespaldo();
                if (rutaGuardada != null && !rutaGuardada.isEmpty()) {
                    txtRutaRespaldo.setText(rutaGuardada);
                }
            }
        }
        // Programar respaldo automático si el usuario lo desea (ejemplo: al abrir la
        // ventana)
        // Puedes llamar a programarRespaldoAutomatico() cuando el usuario lo configure
        // programarRespaldoAutomatico();
    }

    private void seleccionarCarpeta() {
        Window window = btnSeleccionarCarpeta.getScene().getWindow();
        javafx.stage.DirectoryChooser directoryChooser = new javafx.stage.DirectoryChooser();
        directoryChooser.setTitle("Selecciona carpeta de respaldo");
        java.io.File carpeta = directoryChooser.showDialog(window);
        if (carpeta != null && carpeta.exists() && carpeta.isDirectory()) {
            txtRutaRespaldo.setText(carpeta.getAbsolutePath());
            // Guardar ruta si destino es Local/Ambas
            String destino = comboDestino.getValue();
            if ("Local".equals(destino) || "Ambos".equals(destino)) {
                guardarUltimaRutaRespaldo(carpeta.getAbsolutePath());
            }
        }
    }

    // Método para programar respaldo automático
    private void programarRespaldoAutomatico() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        String frecuencia = comboFrecuencia.getValue();
        String hora = comboHora.getValue();
        String ruta = txtRutaRespaldo.getText();
        if (frecuencia == null || hora == null) {
            mostrarAlerta("Configuración incompleta",
                    "Debes seleccionar frecuencia y hora para programar el respaldo automático.",
                    Alert.AlertType.WARNING);
            return;
        }
        // Solo validar ruta si es Local o Ambos
        String destino = comboDestino.getValue();
        if ("Local".equals(destino) || "Ambos".equals(destino)) {
            if (ruta == null || ruta.isEmpty()) {
                mostrarAlerta("Ruta requerida", "Debes seleccionar la ruta de respaldo local.",
                        Alert.AlertType.WARNING);
                return;
            }
        }
        long initialDelay = calcularDelayHastaHora(hora);
        long period = calcularPeriodoFrecuencia(frecuencia);
        scheduler.scheduleAtFixedRate(() -> {
            ejecutarRespaldoRapido();
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        mostrarAlerta("Respaldo automático", "Respaldo automático programado correctamente.",
                Alert.AlertType.INFORMATION);
    }

    // Calcula el delay inicial hasta la hora seleccionada
    private long calcularDelayHastaHora(String hora) {
        java.time.LocalTime ahora = java.time.LocalTime.now();
        java.time.LocalTime objetivo = java.time.LocalTime.parse(hora);
        long delay = java.time.Duration.between(ahora, objetivo).toMillis();
        if (delay < 0)
            delay += TimeUnit.HOURS.toMillis(24); // Si ya pasó, espera hasta el día siguiente
        return delay;
    }

    // Calcula el periodo según la frecuencia
    private long calcularPeriodoFrecuencia(String frecuencia) {
        switch (frecuencia) {
            case "Diario":
                return TimeUnit.DAYS.toMillis(1);
            case "Semanal":
                return TimeUnit.DAYS.toMillis(7);
            case "Mensual":
                return TimeUnit.DAYS.toMillis(30);
            default:
                return TimeUnit.DAYS.toMillis(1);
        }
    }

    // Respaldo rápido: solo ruta y tipo
    private void ejecutarRespaldoRapido() {
        String destino = comboDestino.getValue();
        String ruta = txtRutaRespaldo.getText();
        if (destino == null) {
            mostrarAlerta("Destino requerido", "Selecciona el tipo de respaldo (Local, Nube, Ambos).",
                    Alert.AlertType.WARNING);
            return;
        }
        if ("Local".equals(destino) || "Ambos".equals(destino)) {
            if (ruta == null || ruta.isEmpty()) {
                mostrarAlerta("Ruta requerida", "Debes seleccionar la ruta de respaldo local.",
                        Alert.AlertType.WARNING);
                return;
            }
        }
        // Guardar solo la carpeta si es Local o Ambos
        if (("Local".equals(destino) || "Ambos".equals(destino)) && ruta != null && !ruta.isEmpty()) {
            java.io.File carpetaSolo = new java.io.File(ruta);
            String soloCarpeta = carpetaSolo.isDirectory() ? carpetaSolo.getAbsolutePath() : carpetaSolo.getParent();
            if (soloCarpeta != null) {
                guardarUltimaRutaRespaldo(soloCarpeta);
            }
        }
        // Generar nombre único para el respaldo
        String nombreArchivo = generarNombreRespaldo();
        String rutaCompleta;
        if ("Local".equals(destino) || "Ambos".equals(destino)) {
            String rutaCarpeta = ruta != null ? ruta.trim().replace("\\", "/") : "";
            rutaCompleta = rutaCarpeta + java.io.File.separator + nombreArchivo;
        } else if ("Nube".equals(destino)) {
            // Solo nube: no usar carpeta local, solo nombre de archivo
            rutaCompleta = nombreArchivo;
        } else {
            mostrarAlerta("Destino inválido", "El tipo de respaldo seleccionado no es válido.", Alert.AlertType.ERROR);
            return;
        }
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        String user = dbConfig.getProperty("db.username", "root");
        String pass = dbConfig.getProperty("db.password", "");
        String db = dbConfig.getProperty("app.name", "pharmavictoria");
        String host = "localhost";
        int port = 3307;
        BackupService backupService = new BackupService(user, pass, db, host, port);
        javafx.concurrent.Task<BackupService.BackupResult> task = new javafx.concurrent.Task<BackupService.BackupResult>() {
            @Override
            protected BackupService.BackupResult call() {
                // Usar rutaCompleta como destino del respaldo
                return backupService.realizarRespaldo(rutaCompleta, null);
            }
        };
        task.setOnSucceeded(ev -> {
            BackupService.BackupResult result = task.getValue();
            if (result.success) {
                String msg = "Respaldo realizado correctamente.";
                if ("Local".equals(destino)) {
                    msg += "\nGuardado en: " + rutaCompleta;
                } else if ("Nube".equals(destino)) {
                    msg += "\nSubido a Google Drive.";
                } else if ("Ambos".equals(destino)) {
                    msg += "\nGuardado en: " + rutaCompleta + " y subido a Google Drive.";
                } else {
                    msg += "\nDestino no reconocido.";
                }
                mostrarAlerta("Respaldo exitoso", msg, Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", result.message, Alert.AlertType.ERROR);
            }
        });
        task.setOnFailed(ev -> {
            mostrarAlerta("Error", "Error inesperado al realizar respaldo rápido.", Alert.AlertType.ERROR);
        });
        new Thread(task).start();
    }

    // Genera un nombre único para el archivo de respaldo usando fecha y hora
    private String generarNombreRespaldo() {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "respaldo_" + ahora.format(formatter) + ".sql";
    }

    // Respaldo programado: frecuencia, hora, ruta, tipo
    private void iniciarRespaldoProgramado() {
        String destino = comboDestino.getValue();
        String ruta = txtRutaRespaldo.getText();
        String frecuencia = comboFrecuencia.getValue();
        String hora = comboHora.getValue();
        if (destino == null || frecuencia == null || hora == null) {
            mostrarAlerta("Campos incompletos", "Completa destino, frecuencia y hora para programar el respaldo.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (("Local".equals(destino) || "Ambos".equals(destino)) && (ruta == null || ruta.isEmpty())) {
            mostrarAlerta("Ruta requerida", "Debes seleccionar la ruta de respaldo local.", Alert.AlertType.WARNING);
            return;
        }
        programarRespaldoAutomatico();
    }

    private void restaurarInformacion() {
        Window window = btnRestaurarInformacion.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona archivo de respaldo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos SQL", "*.sql"));
        java.io.File archivo = fileChooser.showOpenDialog(window);
        if (archivo == null || !archivo.exists()) {
            mostrarAlerta("Archivo inválido", "Selecciona un archivo de respaldo válido.", Alert.AlertType.WARNING);
            return;
        }
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        String user = dbConfig.getProperty("db.username", "root");
        String pass = dbConfig.getProperty("db.password", "");
        String db = dbConfig.getProperty("app.name", "pharmavictoria");
        String host = "localhost";
        int port = 3307;
        RestoreService restoreService = new RestoreService(user, pass, db, host, port);
        boolean ok = restoreService.restaurarDesdeArchivo(archivo.getAbsolutePath());
        if (ok) {
            mostrarAlerta("Restauración exitosa", "Restauración realizada correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "Error al restaurar información.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        System.out.println("[ALERTA] " + titulo + ": " + mensaje);
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos para guardar y leer la última ruta de respaldo
    private String leerUltimaRutaRespaldo() {
        try {
            Properties props = new Properties();
            String path = getBackupPathPropertiesFile();
            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
                String ruta = props.getProperty("lastBackupPath", "");
                // Validar que sea carpeta
                if (ruta != null && !ruta.isEmpty()) {
                    java.io.File carpeta = new java.io.File(ruta);
                    if (carpeta.exists() && carpeta.isDirectory()) {
                        return carpeta.getAbsolutePath();
                    }
                }
                return "";
            }
        } catch (IOException e) {
            System.out.println("[DEBUG] No se pudo leer backup-path.properties: " + e.getMessage());
            return "";
        }
    }

    private void guardarUltimaRutaRespaldo(String ruta) {
        try {
            Properties props = new Properties();
            String path = getBackupPathPropertiesFile();
            // Leer primero para no perder otras propiedades
            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
            } catch (IOException e) {
                // Si no existe, se crea
            }
            // Solo guardar la carpeta, nunca la ruta completa del archivo
            java.io.File carpetaSolo = new java.io.File(ruta);
            String soloCarpeta = carpetaSolo.isDirectory() ? carpetaSolo.getAbsolutePath() : carpetaSolo.getParent();
            if (soloCarpeta != null) {
                props.setProperty("lastBackupPath", soloCarpeta);
            }
            try (FileOutputStream fos = new FileOutputStream(path)) {
                props.store(fos, "Ruta de respaldo local");
            }
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo guardar backup-path.properties: " + e.getMessage());
        }
    }

    private String getBackupPathPropertiesFile() {
        // Usar ruta absoluta del resources
        String resourcePath = "src/main/resources/backup-path.properties";
        java.io.File file = new java.io.File(resourcePath);
        if (!file.exists()) {
            // Intentar en target/classes si está empaquetado
            resourcePath = "target/classes/backup-path.properties";
        }
        return resourcePath;
    }
}
