package com.farmaciavictoria.proyectopharmavictoria.controller;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ForgotPasswordDialogController {
    @FXML
    private TextField emailField;
    @FXML
    private Button sendButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorLabel;

    private UsuarioRepository usuarioRepository;

    public void setUsuarioRepository(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @FXML
    private void initialize() {
        sendButton.setOnAction(e -> handleSend());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void handleSend() {
        String email = emailField.getText().trim();
        errorLabel.setVisible(false);
        if (email.isEmpty()) {
            showError("Debe ingresar un correo electrónico.");
            return;
        }
        boolean ok = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService.getInstance()
                .iniciarRecuperacion(email);
        if (ok) {
            showAlert(AlertType.INFORMATION, "Se enviaron instrucciones de recuperación a su correo.");
            closeDialog();
            abrirVentanaRestablecerPassword();
        } else {
            showError("Correo no registrado o error al enviar correo.");
        }

    }

    private void abrirVentanaRestablecerPassword() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/restablecer-contraseña.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/restablecer-contraseña.css").toExternalForm());
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Restablecer contraseña");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 13px;");
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Recuperación de acceso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}
