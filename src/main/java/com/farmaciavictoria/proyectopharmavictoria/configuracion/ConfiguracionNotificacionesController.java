package com.farmaciavictoria.proyectopharmavictoria.configuracion;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfiguracionNotificacionesController {
    @FXML
    private CheckBox chkActivarAlertas;
    @FXML
    private TextField txtDestinatario;
    @FXML
    private TextField txtSmtpServer;
    @FXML
    private TextField txtSmtpPort;
    @FXML
    private TextField txtSmtpUser;
    @FXML
    private PasswordField txtSmtpPassword;
    @FXML
    private CheckBox chkSSL;
    @FXML
    private CheckBox chkTLS;
    @FXML
    private Button btnProbar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lblEstado;

    private EmailConfig config;

    @FXML
    public void initialize() {
        try {
            config = EmailConfigService.cargarConfig();
            cargarEnFormulario();
        } catch (Exception e) {
            lblEstado.setText("No se pudo cargar configuración: " + e.getMessage());
        }
        btnGuardar.setOnAction(e -> guardarConfig());
        btnProbar.setOnAction(e -> probarEnvio());
    }

    private void cargarEnFormulario() {
        chkActivarAlertas.setSelected(config.isAlertasActivas());
        txtDestinatario.setText(config.getDestinatario());
        txtSmtpServer.setText(config.getSmtpServer());
        txtSmtpPort.setText(config.getSmtpPort() > 0 ? String.valueOf(config.getSmtpPort()) : "");
        txtSmtpUser.setText(config.getSmtpUser());
        txtSmtpUser.setEditable(false); // Remitente no editable
        txtSmtpPassword.setText(config.getSmtpPassword());
        txtSmtpPassword.setEditable(false); // Contraseña remitente no editable
        chkSSL.setSelected(config.isUseSSL());
        chkTLS.setSelected(config.isUseTLS());
    }

    private void guardarConfig() {
        try {
            config.setAlertasActivas(chkActivarAlertas.isSelected());
            config.setDestinatario(txtDestinatario.getText());
            config.setSmtpServer(txtSmtpServer.getText());
            config.setSmtpPort(Integer.parseInt(txtSmtpPort.getText()));
            // No modificar remitente ni contraseña desde la UI
            config.setUseSSL(chkSSL.isSelected());
            config.setUseTLS(chkTLS.isSelected());
            EmailConfigService.guardarConfig(config);
            lblEstado.setText("Configuración guardada correctamente.");
        } catch (Exception e) {
            lblEstado.setText("Error al guardar: " + e.getMessage());
        }
    }

    private void probarEnvio() {
        try {
            guardarConfig();
            EmailService.enviarCorreo(config, "Prueba de alerta PharmaVictoria", "Este es un correo de prueba.");
            lblEstado.setText("¡Correo de prueba enviado!");
        } catch (Exception e) {
            lblEstado.setText("Error al enviar: " + e.getMessage());
        }
    }
}
