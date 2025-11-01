package com.farmaciavictoria.proyectopharmavictoria.reportes.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;

/**
 * Controlador para la vista general de reportes.
 * Muestra los cards de cada tipo de reporte y gestiona la navegación.
 */
public class ReportesController {
    @FXML
    private FlowPane cardsPane;

    @FXML
    public void initialize() {
        cargarCardsReportes();
    }

    private void cargarCardsReportes() {
        try {
            // Solo el card de ventas por ahora
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes/ReporteVentasCard.fxml"));
            AnchorPane cardVentas = loader.load();
            // Manejar click en el card para abrir el detalle
            cardVentas.setOnMouseClicked(event -> abrirDetalleReporteVentas(event));
            cardsPane.getChildren().add(cardVentas);
        } catch (IOException e) {
            mostrarError("Error", "No se pudo cargar el card de reporte de ventas: " + e.getMessage());
        }
    }

    private void abrirDetalleReporteVentas(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes/ReporteVentasDetalle.fxml"));
            Parent detalleRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reporte de Ventas por Período");
            stage.setScene(new Scene(detalleRoot));
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el detalle del reporte de ventas: " + e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
