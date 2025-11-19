package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.service.EmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class ContactarProveedoresController {

    @FXML
    private TableView<Proveedor> tableSeleccionProveedores;
    @FXML
    private TableColumn<Proveedor, CheckBox> colSeleccion;
    // Mapa para saber qué proveedor está seleccionado
    private java.util.Map<Proveedor, CheckBox> seleccionMap = new java.util.HashMap<>();
    @FXML
    private TableColumn<Proveedor, String> colRazonSocial;
    @FXML
    private TableColumn<Proveedor, String> colCorreo;
    @FXML
    private TextField txtAsunto;
    @FXML
    private TextArea txtMensaje;
    @FXML
    private Button btnAdjuntar;
    @FXML
    private Label lblArchivoAdjunto;
    @FXML
    private Button btnEnviar;
    @FXML
    private Button btnCancelar;

    private File archivoAdjunto;

    public void initialize() {
        // Configurar columnas
        colRazonSocial.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("razonSocial"));
        colCorreo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colSeleccion.setCellValueFactory(param -> {
            CheckBox checkBox = new CheckBox();
            seleccionMap.put(param.getValue(), checkBox);
            return new javafx.beans.property.SimpleObjectProperty<>(checkBox);
        });

        // Cargar proveedores reales desde el ServiceContainer
        List<Proveedor> proveedores = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer.getInstance()
                .getProveedorService().obtenerTodos();
        ObservableList<Proveedor> data = FXCollections.observableArrayList(proveedores);
        tableSeleccionProveedores.setItems(data);

    }

    @FXML
    private void onAdjuntar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo adjunto");
        Stage stage = (Stage) btnAdjuntar.getScene().getWindow();
        archivoAdjunto = fileChooser.showOpenDialog(stage);
        if (archivoAdjunto != null) {
            lblArchivoAdjunto.setText(archivoAdjunto.getName());
        } else {
            lblArchivoAdjunto.setText("Sin archivo adjunto");
        }
    }

    @FXML
    private void onEnviarCorreo() {
        // Obtener proveedores seleccionados
        ObservableList<Proveedor> seleccionados = FXCollections.observableArrayList();
        for (Proveedor proveedor : tableSeleccionProveedores.getItems()) {
            CheckBox check = seleccionMap.get(proveedor);
            if (check != null && check.isSelected()) {
                seleccionados.add(proveedor);
            }
        }
        String asunto = txtAsunto.getText();
        String mensaje = txtMensaje.getText();
        // Leer credenciales SMTP desde smtp_proveedores.properties
        java.util.Properties config = new java.util.Properties();
        try {
            java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("smtp_proveedores.properties");
            config.load(input);
        } catch (Exception ex) {
            mostrarAlerta("Error de configuración SMTP", "No se pudo leer smtp_proveedores.properties",
                    Alert.AlertType.ERROR);
            return;
        }
        String smtpUser = config.getProperty("mail.smtp.user", "");
        String smtpPass = config.getProperty("mail.smtp.password", "");
        EmailService emailService = new EmailService(smtpUser, smtpPass);
        for (Proveedor proveedor : seleccionados) {
            try {
                emailService.sendProveedorEmail(proveedor.getEmail(), asunto, mensaje, archivoAdjunto);
            } catch (Exception e) {
                mostrarAlerta("Error al enviar a " + proveedor.getEmail(), e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        mostrarAlerta("Envío completado", "Correos enviados correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
