package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;

public class PermisosVendedorCardController {
    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {
        root.setOnMouseClicked(this::abrirGestionPermisos);
    }

    private void abrirGestionPermisos(MouseEvent event) {
        final String path = "/fxml/configuracion/PermisosVendedorGestion.fxml";
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("FXML no encontrado: " + path);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent formRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Permisos de Vendedor");
            stage.setScene(new Scene(formRoot));
            stage.setMinWidth(650);
            stage.setMinHeight(450);
            PermisosVendedorGestionController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}