package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.DetalleVenta;

public class VistaPreviaComprobanteController {
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblDocumento;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblTipoComprobante;
    @FXML
    private Label lblSerie;
    @FXML
    private Label lblNumero;
    @FXML
    private Label lblTotal;
    @FXML
    private TableView<DetalleVenta> tableDetalles;
    @FXML
    private TableColumn<DetalleVenta, Number> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, String> colDescripcion;
    @FXML
    private TableColumn<DetalleVenta, Number> colPrecioUnitario;
    @FXML
    private TableColumn<DetalleVenta, Number> colIGV;
    @FXML
    private TableColumn<DetalleVenta, Number> colTotal;
    @FXML
    private Button btnEmitir;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblAdvertencia;

    private Runnable onConfirmar;
    private Stage stage;

    public void setDatos(Cliente cliente, String tipoComprobante, String serie, String numero, String total,
            ObservableList<DetalleVenta> detalles) {
        lblCliente.setText(cliente.razon_social);
        lblDocumento.setText(cliente.numero_documento);
        lblDireccion.setText(cliente.direccion);
        lblTipoComprobante.setText(tipoComprobante);
        lblSerie.setText(serie);
        lblNumero.setText(numero);
        lblTotal.setText(total);
        tableDetalles.setItems(detalles);
    }

    public void setOnConfirmar(Runnable r) {
        this.onConfirmar = r;
    }

    public void setStage(Stage s) {
        this.stage = s;
    }

    @FXML
    private void initialize() {
        colCantidad
                .setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().cantidad));
        colDescripcion.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().descripcion));
        colPrecioUnitario.setCellValueFactory(
                data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().precio_unitario));
        colIGV.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().igv));
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().total));
        btnEmitir.setOnAction(e -> {
            if (onConfirmar != null)
                onConfirmar.run();
            if (stage != null)
                stage.close();
        });
        btnCancelar.setOnAction(e -> {
            if (stage != null)
                stage.close();
        });
    }
}
