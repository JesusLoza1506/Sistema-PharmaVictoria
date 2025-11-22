package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class FacturaRapidaDialogController {
    @FXML
    private TextField txtRazonSocial;
    @FXML
    private TextField txtRuc;
    @FXML
    private TextField txtDireccion;

    public String getRazonSocial() {
        return txtRazonSocial.getText() != null ? txtRazonSocial.getText().trim() : "";
    }

    public String getRuc() {
        return txtRuc.getText() != null ? txtRuc.getText().trim() : "";
    }

    public String getDireccion() {
        return txtDireccion.getText() != null ? txtDireccion.getText().trim() : "";
    }

    public boolean validar() {
        String razon = getRazonSocial();
        String ruc = getRuc();
        String direccion = getDireccion();
        if (razon.isEmpty() || ruc.isEmpty() || direccion.isEmpty()) {
            return false;
        }
        if (!ruc.matches("^\\d{11}$")) {
            return false;
        }
        return true;
    }
}
