// Controlador para la tarjeta de alerta de vencimiento de productos
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import java.io.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VencimientoAlertaCardController {
    // Logger para registrar eventos y errores
    private static final Logger logger = LoggerFactory.getLogger(VencimientoAlertaCardController.class);

    // Etiqueta para el título del card
    @FXML
    private Label lblTitulo;
    // Etiqueta para el detalle del card
    @FXML
    private Label lblDetalle;

    // Inicializa el controlador y asigna el evento de clic
    @FXML
    public void initialize() {
        // Permite que todo el card sea clickeable
        lblTitulo.getParent().addEventHandler(MouseEvent.MOUSE_CLICKED, this::mostrarDialogoEditarDias);
        actualizarDetalle();
    }

    // Actualiza el texto del detalle con los días configurados
    private void actualizarDetalle() {
        int dias = leerDiasAlerta();
        lblDetalle.setText("Días para alerta: " + dias);
    }

    // Lee el valor actual de días de alerta desde el servicio
    private int leerDiasAlerta() {
        return com.farmaciavictoria.proyectopharmavictoria.service.ProductoService.getDiasVencimientoAlerta();
    }

    // Guarda el nuevo valor de días de alerta y notifica el cambio
    private void guardarDiasAlerta(int dias) {
        try {
            logger.info("[VencimientoAlertaCard] Guardando nuevo valor de días de alerta: {}", dias);
            com.farmaciavictoria.proyectopharmavictoria.service.ProductoService.setDiasVencimientoAlerta(dias);
            logger.info("[VencimientoAlertaCard] Valor actualizado en ProductoService");
            notificarActualizacionAlertas();
        } catch (Exception e) {
            logger.error("[VencimientoAlertaCard] Error al actualizar el valor en ProductoService", e);
            mostrarError("No se pudo actualizar el valor de días de alerta");
        }
    }

    // Notifica a los controladores principales para refrescar la lógica de
    // productos próximos a vencer
    private void notificarActualizacionAlertas() {
        logger.info("[VencimientoAlertaCard] Notificando actualización de alertas de vencimiento");
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.stage.Window window = lblTitulo.getScene().getWindow();
                if (window != null) {
                    logger.info("[VencimientoAlertaCard] Window encontrada: {}", window);
                    javafx.scene.Scene scene = window.getScene();
                    if (scene != null) {
                        logger.info("[VencimientoAlertaCard] Scene encontrada: {}", scene);
                        javafx.scene.Parent root = scene.getRoot();
                        logger.info("[VencimientoAlertaCard] Root encontrada: {}", root);
                        Object controller = root.getProperties().get("controller");
                        logger.info("[VencimientoAlertaCard] Propiedades del root: {}", root.getProperties());
                        if (controller instanceof com.farmaciavictoria.proyectopharmavictoria.controller.DashboardController dash) {
                            logger.info("[VencimientoAlertaCard] DashboardController encontrado, refrescando datos");
                            dash.refrescarDatos();
                        } else {
                            logger.warn("[VencimientoAlertaCard] DashboardController no encontrado en root properties");
                        }
                    } else {
                        logger.warn("[VencimientoAlertaCard] Scene es null");
                    }
                } else {
                    logger.warn("[VencimientoAlertaCard] Window es null");
                }
            } catch (Exception ex) {
                logger.error("[VencimientoAlertaCard] Error al notificar actualización de alertas", ex);
            }
        });
    }

    // Muestra el diálogo para editar los días de alerta
    private void mostrarDialogoEditarDias(MouseEvent event) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar días de alerta de vencimiento");

        Label lbl = new Label("Días para alerta:");
        TextField txtDias = new TextField(String.valueOf(leerDiasAlerta()));
        txtDias.setPromptText("Ej: 10");
        txtDias.setMaxWidth(100);
        txtDias.setStyle(
                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 12; -fx-font-size: 15px; -fx-border-color: #bdbdbd;");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setDefaultButton(true);
        btnGuardar.setStyle(
                "-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 24; -fx-font-size: 15px; -fx-effect: dropshadow(gaussian, #1976d2, 8, 0.3, 0, 2);");

        btnGuardar.setOnAction(e -> {
            try {
                int nuevoDias = Integer.parseInt(txtDias.getText());
                if (nuevoDias < 1 || nuevoDias > 365) {
                    mostrarError("El valor debe estar entre 1 y 365 días.");
                    txtDias.setStyle(txtDias.getStyle() + "-fx-border-color: #e53935;");
                    return;
                }
                guardarDiasAlerta(nuevoDias);
                actualizarDetalle();
                dialog.close();
                mostrarInfo("Valor actualizado correctamente.");
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un número válido.");
                txtDias.setStyle(txtDias.getStyle() + "-fx-border-color: #e53935;");
            }
        });

        VBox vbox = new VBox(16, lbl, txtDias, btnGuardar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(340);
        vbox.setStyle(
                "-fx-background-color: #fff; -fx-background-radius: 16; -fx-padding: 32 32 24 32; -fx-effect: dropshadow(gaussian, #bdbdbd, 16, 0.2, 0, 4);");

        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Muestra un mensaje de error en la interfaz
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Muestra un mensaje informativo en la interfaz
    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
