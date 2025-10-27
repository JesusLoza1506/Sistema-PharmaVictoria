package com.farmaciavictoria.proyectopharmavictoria;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;

public class RucDialogController {
    @FXML
    private TextField rucTextField;
    @FXML
    private Label errorLabel;

    public String getRuc() {
        return rucTextField.getText();
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
