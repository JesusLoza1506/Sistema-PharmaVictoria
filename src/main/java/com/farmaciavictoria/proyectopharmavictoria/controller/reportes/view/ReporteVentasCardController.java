package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ReporteVentasCardController {
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblDetalle;

    // Método para inicializar el card con datos
    public void setDatos(String titulo) {
        lblTitulo.setText(titulo);
    }

    @FXML
    private void onDetalleClicked(MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/reportes/ReporteVentasDetalle.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Reporte de Ventas por Período");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
