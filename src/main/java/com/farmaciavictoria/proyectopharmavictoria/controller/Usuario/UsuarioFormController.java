package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UsuarioFormController {
    public void cargarUsuario(com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario) {
        if (usuario == null)
            return;
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(""); // Por seguridad, no mostrar hash
        cmbRol.setValue(usuario.getRol() != null ? usuario.getRol().name() : null);
        // ...existing code...
        txtNombres.setText(usuario.getNombres());
        txtApellidos.setText(usuario.getApellidos());
        txtDni.setText(usuario.getDni());
        txtTelefono.setText(usuario.getTelefono());
        txtEmail.setText(usuario.getEmail());
    }

    @FXML
    public void guardarUsuario() {
        // Aquí puedes agregar la lógica de validación y registro
        registrado = true;
        cerrar();
    }

    @FXML
    private void limpiarCampos() {
        txtUsername.clear();
        txtPassword.clear();
        cmbRol.getSelectionModel().clearSelection();
        // ...existing code...
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
    }

    @FXML
    private void cancelar() {
        registrado = false;
        cerrar();
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return txtPassword.getText();
    }

    public String getRol() {
        return cmbRol.getValue();
    }

    // ...existing code...

    public String getNombres() {
        return txtNombres.getText();
    }

    public String getApellidos() {
        return txtApellidos.getText();
    }

    public String getDni() {
        return txtDni.getText();
    }

    public String getTelefono() {
        return txtTelefono.getText();
    }

    public String getEmail() {
        return txtEmail.getText();
    }

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbRol;
    // ...existing code...
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    public Button btnGuardar;
    @FXML
    public Button btnCancelar;

    private boolean registrado = false;

    @FXML
    public void initialize() {
        cmbRol.getItems().addAll("ADMIN", "VENDEDOR");
        // ...existing code...
        btnCancelar.setOnAction(e -> cerrar());
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public boolean isRegistrado() {
        return registrado;
    }

    // Puedes agregar aquí la lógica para validar y obtener los datos del usuario
}
