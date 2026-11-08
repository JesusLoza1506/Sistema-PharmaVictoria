// Controlador para la tarjeta de notificaciones por correo
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.net.URL;

public class NotificacionesCorreoCardController {
    // Panel raíz de la tarjeta
    @FXML
    private AnchorPane root;

    // Inicializa el controlador y asigna el evento de clic
    @FXML
    public void initialize() {
        root.setOnMouseClicked(this::abrirConfiguracion);
    }

    // Abre la ventana de configuración de notificaciones por correo
    private void abrirConfiguracion(MouseEvent event) {
        final String path = "/fxml/configuracion/ConfiguracionNotificaciones.fxml";
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("FXML no encontrado: " + path);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent formRoot = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            // Establecer la ventana que originó el evento como owner (si existe)
            Stage owner = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.setTitle("Configuración de Notificaciones por Correo");
            stage.setScene(new Scene(formRoot));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
