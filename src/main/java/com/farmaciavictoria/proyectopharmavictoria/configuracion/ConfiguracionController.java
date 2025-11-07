package com.farmaciavictoria.proyectopharmavictoria.configuracion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class ConfiguracionController {
    @FXML
    private FlowPane cardsPane;

    @FXML
    public void initialize() {
        cargarCardsConfiguracion();
    }

    private void cargarCardsConfiguracion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/configuracion/NotificacionesCorreoCard.fxml"));
            AnchorPane cardCorreo = loader.load();
            cardsPane.getChildren().add(cardCorreo);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar el card de notificaciones: " + e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}
