package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.net.URL;

public class RespaldosRestauracionCardController {
    @FXML
    private ImageView imgIcono;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblDetalle;
    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {
        root.setOnMouseClicked(this::abrirDialogoRespaldos);
    }

    private void abrirDialogoRespaldos(MouseEvent event) {
        final String path = "/fxml/configuracion/RespaldosRestauracionDialog.fxml";
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("FXML no encontrado: " + path);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url); // location válido
            Parent dialogRoot = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            // dialogStage.initOwner(((javafx.scene.Node)event.getSource()).getScene().getWindow());
            // // opcional
            dialogStage.setTitle("Gestión de Respaldos y Restauración");
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}