
package com.farmaciavictoria.proyectopharmavictoria.configuracion;

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
    private static final Logger logger = LoggerFactory.getLogger(VencimientoAlertaCardController.class);

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblDetalle;

    // Ya no se usa application.properties para los días de alerta

    @FXML
    public void initialize() {
        // Permite que todo el card sea clickeable
        lblTitulo.getParent().addEventHandler(MouseEvent.MOUSE_CLICKED, this::mostrarDialogoEditarDias);
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        int dias = leerDiasAlerta();
        lblDetalle.setText("Configurar días previos para alerta (actual: " + dias + ")");
    }

    private int leerDiasAlerta() {
        return com.farmaciavictoria.proyectopharmavictoria.service.ProductoService.getDiasVencimientoAlerta();
    }

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

    /**
     * Notifica a los controladores principales para refrescar la lógica de
     * productos próximos a vencer
     */
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

    private void mostrarDialogoEditarDias(MouseEvent event) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar días de alerta de vencimiento");

        Label lbl = new Label("Cantidad de días previos para alerta:");
        TextField txtDias = new TextField(String.valueOf(leerDiasAlerta()));
        txtDias.setMaxWidth(80);
        Button btnGuardar = new Button("Guardar");
        btnGuardar.setDefaultButton(true);
        btnGuardar.setOnAction(e -> {
            try {
                int nuevoDias = Integer.parseInt(txtDias.getText());
                if (nuevoDias < 1 || nuevoDias > 365) {
                    mostrarError("El valor debe estar entre 1 y 365 días.");
                    return;
                }
                guardarDiasAlerta(nuevoDias);
                actualizarDetalle();
                dialog.close();
                mostrarInfo("Valor actualizado correctamente.");
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un número válido.");
            }
        });

        VBox vbox = new VBox(12, lbl, txtDias, btnGuardar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(320);
        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
