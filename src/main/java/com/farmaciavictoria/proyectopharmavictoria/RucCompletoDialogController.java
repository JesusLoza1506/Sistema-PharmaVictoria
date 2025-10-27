package com.farmaciavictoria.proyectopharmavictoria;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RucCompletoDialogController {
    @FXML
    private TextField rucTextField;
    @FXML
    private TextField razonSocialTextField;
    @FXML
    private TextField direccionTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label errorLabel;

    public String getRuc() {
        return rucTextField.getText();
    }

    public String getRazonSocial() {
        return razonSocialTextField.getText();
    }

    public String getDireccion() {
        return direccionTextField.getText();
    }

    public String getEmail() {
        return emailTextField.getText();
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}
