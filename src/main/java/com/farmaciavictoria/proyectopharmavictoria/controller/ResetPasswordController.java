package com.farmaciavictoria.proyectopharmavictoria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;

public class ResetPasswordController {
    @FXML
    private TextField tokenField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button confirmButton;
    @FXML
    private Label mensajeLabel;

    private final UsuarioService usuarioService = ServiceContainer.getInstance().getUsuarioService();

    @FXML
    public void initialize() {
        confirmButton.setOnAction(e -> onConfirmar());
    }

    private void onConfirmar() {
        String codigo = tokenField.getText().trim();
        String nuevaPassword = newPasswordField.getText();
        if (codigo.isEmpty() || nuevaPassword.isEmpty()) {
            mensajeLabel.setText("Debe ingresar el código y la nueva contraseña.");
            mensajeLabel.setStyle("-fx-text-fill: #e53935;");
            return;
        }
        boolean exito = usuarioService.restablecerPasswordConToken(codigo, nuevaPassword);
        if (exito) {
            mensajeLabel.setStyle("-fx-text-fill: #2e7d32;");
            mensajeLabel.setText("¡Contraseña actualizada correctamente!");
        } else {
            mensajeLabel.setStyle("-fx-text-fill: #e53935;");
            mensajeLabel.setText("Código inválido o expirado.");
        }
    }
}
