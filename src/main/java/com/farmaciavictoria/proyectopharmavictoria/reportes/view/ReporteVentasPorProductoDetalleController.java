package com.farmaciavictoria.proyectopharmavictoria.reportes.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.reportes.repository.ReporteVentasPorProductoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import com.farmaciavictoria.proyectopharmavictoria.reportes.export.ExportadorReporteFactory;
import com.farmaciavictoria.proyectopharmavictoria.reportes.export.ExportadorReporteProducto;
import javafx.stage.FileChooser;
import java.io.File;

public class ReporteVentasPorProductoDetalleController {
    @FXML
    private DatePicker dateInicio;
    @FXML
    private DatePicker dateFin;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private TableView<VentaPorProductoDTO> tableProductos;
    @FXML
    private TableColumn<VentaPorProductoDTO, String> colProducto;
    @FXML
    private TableColumn<VentaPorProductoDTO, Integer> colCantidad;
    @FXML
    private TableColumn<VentaPorProductoDTO, Double> colTotal;

    private final ReporteVentasPorProductoRepository repo = new ReporteVentasPorProductoRepository();

    @FXML
    public void initialize() {
        colProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProducto()));
        colCantidad.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalVentas()).asObject());
        // Opcional: carga inicial con fechas del mes actual
        dateInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dateFin.setValue(LocalDate.now());
        cargarDatos();
    }

    @FXML
    private void onFiltrar() {
        cargarDatos();
    }

    private void cargarDatos() {
        LocalDate desde = dateInicio.getValue();
        LocalDate hasta = dateFin.getValue();
        List<VentaPorProductoDTO> lista = repo.obtenerRankingProductos(desde, hasta);
        ObservableList<VentaPorProductoDTO> datos = FXCollections.observableArrayList(lista);
        tableProductos.setItems(datos);
    }

    @FXML
    private void onExportarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar a Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(tableProductos.getScene().getWindow());
        if (file != null) {
            ExportadorReporteProducto exportador = (ExportadorReporteProducto) ExportadorReporteFactory
                    .getExportadorProducto("EXCEL");
            exportador.exportar(tableProductos.getItems(), file);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El reporte de ventas por producto se ha exportado correctamente a Excel.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onExportarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar a PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(tableProductos.getScene().getWindow());
        if (file != null) {
            ExportadorReporteProducto exportador = (ExportadorReporteProducto) ExportadorReporteFactory
                    .getExportadorProducto("PDF");
            exportador.exportar(tableProductos.getItems(), file);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Exportación exitosa");
            alert.setHeaderText(null);
            alert.setContentText("El reporte de ventas por producto se ha exportado correctamente a PDF.");
            alert.showAndWait();
        }
    }
}
