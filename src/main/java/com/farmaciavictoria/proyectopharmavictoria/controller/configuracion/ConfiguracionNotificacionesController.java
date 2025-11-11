// Controlador para la configuración de notificaciones por correo
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfiguracionNotificacionesController {
    // Checkbox para activar/desactivar alertas
    @FXML
    private CheckBox chkActivarAlertas;
    // Campo para destinatario del correo
    @FXML
    private TextField txtDestinatario;
    // Campo para servidor SMTP
    @FXML
    private TextField txtSmtpServer;
    // Campo para puerto SMTP
    @FXML
    private TextField txtSmtpPort;
    // Usuario SMTP (remitente)
    @FXML
    private TextField txtSmtpUser;
    // Contraseña del remitente
    @FXML
    private PasswordField txtSmtpPassword;
    // Checkbox para usar SSL
    @FXML
    private CheckBox chkSSL;
    // Checkbox para usar TLS
    @FXML
    private CheckBox chkTLS;
    // Botón para probar envío de correo
    @FXML
    private Button btnProbar;
    // Botón para guardar configuración
    @FXML
    private Button btnGuardar;
    // Etiqueta para mostrar estado o mensajes
    @FXML
    private Label lblEstado;

    // Configuración de correo electrónico
    private EmailConfig config;

    // Inicializa el controlador y carga la configuración actual
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

    // Carga los datos de configuración en el formulario
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

    // Guarda la configuración modificada por el usuario
    private void guardarConfig() {
        try {
            config.setAlertasActivas(chkActivarAlertas.isSelected());
            config.setDestinatario(txtDestinatario.getText());
            config.setSmtpServer(txtSmtpServer.getText());
            config.setSmtpPort(Integer.parseInt(txtSmtpPort.getText()));
            config.setUseSSL(chkSSL.isSelected());
            config.setUseTLS(chkTLS.isSelected());
            EmailConfigService.guardarConfig(config);
            lblEstado.setText("Configuración guardada correctamente.");
        } catch (Exception e) {
            lblEstado.setText("Error al guardar: " + e.getMessage());
        }
    }

    // Prueba el envío de correo con la configuración actual
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
