package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.util.List;

public class ExportarInventarioController {
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Producto> tablePreview;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private Button btnExportarPDF;

    private ProductoService productoService;
    private ObservableList<Producto> productosFiltrados = FXCollections.observableArrayList();

    public void initialize() {
        productoService = ProductoService.getInstance();
        cargarFiltros();
        setupTableColumns();
        setupActions();
        tablePreview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setProductosFiltrados(java.util.List<Producto> lista) {
        productosFiltrados.setAll(lista);
        tablePreview.setItems(productosFiltrados);
    }

    private void cargarFiltros() {
        cmbCategoria.getItems().addAll(productoService.obtenerCategorias());
        cmbCategoria.getItems().add(0, "Todas las categorías");
        cmbCategoria.getSelectionModel().select(0);
        cmbEstado.getItems().addAll("Todos", "Activo", "Inactivo", "Stock Bajo", "Próximos a Vencer", "Sin Stock",
                "Productos Vencidos");
        cmbEstado.getSelectionModel().select(0);
    }

    private void setupTableColumns() {
        for (TableColumn<Producto, ?> col : tablePreview.getColumns()) {
            col.setCellValueFactory(null);
        }
        ((TableColumn<Producto, String>) tablePreview.getColumns().get(0))
                .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("codigo"));
        ((TableColumn<Producto, String>) tablePreview.getColumns().get(1))
                .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        ((TableColumn<Producto, java.math.BigDecimal>) tablePreview.getColumns().get(2))
                .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("precioVenta"));
        ((TableColumn<Producto, Integer>) tablePreview.getColumns().get(3))
                .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stockActual"));
        ((TableColumn<Producto, String>) tablePreview.getColumns().get(4)).setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaVencimiento() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaVencimiento()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        ((TableColumn<Producto, String>) tablePreview.getColumns().get(5)).setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getActivo() ? "Activo" : "Inactivo");
        });
    }

    private void setupActions() {
        btnExportarExcel.setOnAction(e -> exportar("excel"));
        btnExportarPDF.setOnAction(e -> exportar("pdf"));
        cmbCategoria.setOnAction(e -> aplicarFiltros());
        cmbEstado.setOnAction(e -> aplicarFiltros());
        txtBuscar.setOnKeyReleased(e -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String categoria = cmbCategoria.getValue();
        String estado = cmbEstado.getValue();
        String texto = txtBuscar.getText().trim().toLowerCase();
        List<Producto> productos = productoService.obtenerTodos();
        productosFiltrados.setAll(productos.stream()
                .filter(p -> categoria == null || categoria.equals("Todas las categorías")
                        || p.getCategoria().getNombre().equalsIgnoreCase(categoria))
                .filter(p -> {
                    if (estado == null || estado.equals("Todos"))
                        return true;
                    if (estado.equals("Activo"))
                        return p.getActivo();
                    if (estado.equals("Inactivo"))
                        return !p.getActivo();
                    if (estado.equals("Stock Bajo"))
                        return p.getStockActual() <= p.getStockMinimo();
                    if (estado.equals("Próximos a Vencer"))
                        return p.getFechaVencimiento() != null
                                && p.getFechaVencimiento().isBefore(java.time.LocalDate.now().plusDays(30));
                    if (estado.equals("Sin Stock"))
                        return p.getStockActual() == 0;
                    if (estado.equals("Productos Vencidos"))
                        return p.getFechaVencimiento() != null
                                && p.getFechaVencimiento().isBefore(java.time.LocalDate.now());
                    return true;
                })
                .filter(p -> texto.isEmpty() || p.getNombre().toLowerCase().contains(texto)
                        || p.getLaboratorio().toLowerCase().contains(texto)
                        || p.getLote().toLowerCase().contains(texto))
                .toList());
    }

    private void exportar(String tipo) {
        String nombre = "inventario";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(nombre + (tipo.equals("excel") ? ".xlsx" : ".pdf"));
        fileChooser.getExtensionFilters().clear();
        if (tipo.equals("excel")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        }
        fileChooser.setTitle("Guardar reporte de inventario");
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean exito = false;
            try {
                if (tipo.equals("excel")) {
                    ExportFacade.exportarExcel(productosFiltrados, file.getAbsolutePath().replace(".xlsx", ""));
                } else {
                    ExportFacade.exportarPDF(productosFiltrados, file.getAbsolutePath().replace(".pdf", ""));
                }
                exito = true;
            } catch (Exception e) {
                exito = false;
            }
            if (exito) {
                mostrarMensaje("Archivo exportado correctamente en:\n" + file.getAbsolutePath());
                cerrar();
            } else {
                mostrarMensaje("Error al exportar el archivo. Intente nuevamente.");
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportación de Inventario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
    }
}