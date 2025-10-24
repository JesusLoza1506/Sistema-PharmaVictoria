package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;

public class RecuperarContraseñaController {
    @FXML
    private TextField codigoField;
    @FXML
    private PasswordField nuevaPasswordField;
    @FXML
    private PasswordField confirmarPasswordField;
    @FXML
    private Button btnRecuperar;
    @FXML
    private Label lblMensaje;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void initialize() {
        btnRecuperar.setOnAction(e -> recuperarPassword());
    }

    private void recuperarPassword() {
        String codigo = codigoField.getText().trim();
        String nuevaPass = nuevaPasswordField.getText();
        String confirmarPass = confirmarPasswordField.getText();
        lblMensaje.setText("");
        if (codigo.isEmpty() || nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
            lblMensaje.setText("Completa todos los campos.");
            return;
        }
        if (!nuevaPass.equals(confirmarPass)) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }
        UsuarioService service = UsuarioService.getInstance();
        boolean ok = service.validarYActualizarPassword(email, codigo, nuevaPass);
        if (ok) {
            lblMensaje.setText("¡Contraseña actualizada correctamente!");
            btnRecuperar.setDisable(true);
        } else {
            lblMensaje.setText("Código inválido, expirado o error.");
        }
    }
}
