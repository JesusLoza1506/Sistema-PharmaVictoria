package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PermisosVendedorCardController {
    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {
        root.setOnMouseClicked(this::abrirGestionPermisos);
    }

    private void abrirGestionPermisos(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Configuracion/PermisosVendedorGestion.fxml"));
            Parent formRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Permisos de Vendedor");
            stage.setScene(new Scene(formRoot));
            stage.setMinWidth(650);
            stage.setMinHeight(450);
            // Obtener el controlador y pasar el Stage
            PermisosVendedorGestionController controller = loader.getController();
            controller.setStage(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
