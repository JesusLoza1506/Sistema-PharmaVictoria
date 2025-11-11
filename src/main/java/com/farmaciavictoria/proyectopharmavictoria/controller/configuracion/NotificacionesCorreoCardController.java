// Controlador para la tarjeta de notificaciones por correo
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/configuracion/ConfiguracionNotificaciones.fxml"));
            Parent formRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Configuración de Notificaciones por Correo");
            stage.setScene(new Scene(formRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
