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
        String basePath = "/fxml/configuracion/"; // carpeta real en resources (minúsculas)
        String[] archivos = {
                "NotificacionesCorreoCard.fxml",
                "PermisosVendedorCard.fxml",
                "RespaldosRestauracionCard.fxml",
                "VencimientoAlertaCard.fxml"
        };
        try {
            for (String archivo : archivos) {
                String ruta = basePath + archivo;
                java.net.URL url = getClass().getResource(ruta);
                if (url == null) {
                    throw new IllegalStateException("Recurso FXML no encontrado: " + ruta);
                }
                FXMLLoader loader = new FXMLLoader(url);
                AnchorPane card = loader.load();
                cardsPane.getChildren().add(card);
            }
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
