package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class ReporteVentasPorProductoCardController {
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblDetalle;
    @FXML
    private ImageView imgIcono;
    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {
        // Puedes personalizar el card aquí si lo necesitas
        root.setOnMouseClicked(this::abrirDetalle);
    }

    private void abrirDetalle(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/reportes/ReporteVentasPorProductoDetalle.fxml"));
            Parent detalleRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reporte de Ventas por Producto");
            stage.setScene(new Scene(detalleRoot));
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
