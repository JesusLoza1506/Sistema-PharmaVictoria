package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Window;

public class BoletaRapidaDialogController {
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDni;
    @FXML
    private Label lblAdvertencia;
    @FXML
    private DialogPane dialogPane;

    @FXML
    public void initialize() {
        lblAdvertencia.setVisible(false);
        // Validación en tiempo real para el campo DNI
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,8}")) {
                txtDni.setText(oldVal);
            }
        });
    }

    public String getNombre() {
        return txtNombre.getText().trim().isEmpty() ? "Cliente Genérico" : txtNombre.getText().trim();
    }

    public String getDni() {
        return txtDni.getText().trim();
    }

    public boolean validar(double montoTotal) {
        String dni = getDni();
        lblAdvertencia.setVisible(false);
        if (montoTotal >= 700.0) {
            if (dni.length() != 8) {
                lblAdvertencia.setText("Para montos mayores a S/ 700, el DNI es obligatorio (8 dígitos).");
                lblAdvertencia.setVisible(true);
                return false;
            }
        } else if (!dni.isEmpty() && dni.length() != 8) {
            lblAdvertencia.setText("El DNI debe tener 8 dígitos o dejarse vacío.");
            lblAdvertencia.setVisible(true);
            return false;
        }
        return true;
    }

    public void setWarning(String msg) {
        lblAdvertencia.setText(msg);
        lblAdvertencia.setVisible(true);
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }
}
