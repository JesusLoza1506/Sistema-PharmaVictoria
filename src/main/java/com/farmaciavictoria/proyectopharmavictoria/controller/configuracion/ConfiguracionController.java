package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class ConfiguracionController {
    // Panel que contiene las tarjetas de configuración
    @FXML
    private FlowPane cardsPane;

    // Inicializa el controlador y carga las tarjetas de configuración
    @FXML
    public void initialize() {
        cargarCardsConfiguracion();
    }

    // Carga las tarjetas de configuración en el panel
    private void cargarCardsConfiguracion() {
        try {
            FXMLLoader loaderCorreo = new FXMLLoader(
                    getClass().getResource("/fxml/Configuracion/NotificacionesCorreoCard.fxml"));
            AnchorPane cardCorreo = loaderCorreo.load();
            cardsPane.getChildren().add(cardCorreo);

            FXMLLoader loaderVencimiento = new FXMLLoader(
                    getClass().getResource("/fxml/Configuracion/VencimientoAlertaCard.fxml"));
            AnchorPane cardVencimiento = loaderVencimiento.load();
            cardsPane.getChildren().add(cardVencimiento);

            // Nuevo card: Permisos de Vendedor
            FXMLLoader loaderPermisos = new FXMLLoader(
                    getClass().getResource("/fxml/Configuracion/PermisosVendedorCard.fxml"));
            AnchorPane cardPermisos = loaderPermisos.load();
            cardsPane.getChildren().add(cardPermisos);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar los cards de configuración: " + e.getMessage());
        }
    }

    // Muestra un mensaje de error en la interfaz
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
