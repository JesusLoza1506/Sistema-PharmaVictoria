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
            FXMLLoader loaderCorreo = new FXMLLoader(
                    getClass().getResource("/fxml/configuracion/NotificacionesCorreoCard.fxml"));
            AnchorPane cardCorreo = loaderCorreo.load();
            cardsPane.getChildren().add(cardCorreo);

            FXMLLoader loaderVencimiento = new FXMLLoader(
                    getClass().getResource("/fxml/configuracion/VencimientoAlertaCard.fxml"));
            AnchorPane cardVencimiento = loaderVencimiento.load();
            cardsPane.getChildren().add(cardVencimiento);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar los cards de configuración: " + e.getMessage());
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
