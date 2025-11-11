package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class HistorialInventarioController implements Initializable {
    @FXML
    private TableView<ProductoHistorialCambioRepository.HistorialCambioDTO> tableHistorial;
    @FXML
    private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colFecha;
    @FXML
    private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colUsuario;
    @FXML
    private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colCampo;
    @FXML
    private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colAnterior;
    @FXML
    private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colNuevo;

    private Producto producto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setProducto(Producto producto) {
        this.producto = producto;
        cargarHistorial();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                formatter.format(cell.getValue().fecha.toLocalDateTime())));
        colUsuario.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().usuario));
        colCampo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().campo));
        colAnterior
                .setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().anterior));
        colNuevo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().nuevo));

        tableHistorial.widthProperty().addListener((obs, oldVal, newVal) -> {
            javafx.scene.Node header = tableHistorial.lookup(".column-header-background");
            if (header != null) {
                header.setStyle(
                        "-fx-background-color: linear-gradient(to right, #28a745, #20c997); -fx-text-fill: white; font-weight: bold; -fx-border-color: #20c997; -fx-border-width: 0 0 2px 0;");
            }
        });
    }

    private void cargarHistorial() {
        List<ProductoHistorialCambioRepository.HistorialCambioDTO> historial = ProductoHistorialCambioRepository
                .obtenerCambiosPorProducto(producto.getId());
        ObservableList<ProductoHistorialCambioRepository.HistorialCambioDTO> data = FXCollections
                .observableArrayList(historial);
        tableHistorial.setItems(data);
    }

    @FXML
    private void onCerrar() {
        ((Stage) tableHistorial.getScene().getWindow()).close();
    }
}
