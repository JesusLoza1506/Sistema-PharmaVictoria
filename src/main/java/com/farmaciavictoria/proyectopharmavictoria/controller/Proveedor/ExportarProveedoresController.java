package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.List;

public class ExportarProveedoresController {
    @FXML private TableView<Proveedor> tablePreview;
    @FXML private TableColumn<Proveedor, String> colRazonSocial;
    @FXML private TableColumn<Proveedor, String> colRuc;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colCondicionesPago;
    @FXML private TableColumn<Proveedor, String> colTipoProducto;
    @FXML private TableColumn<Proveedor, String> colSucursalId;
    @FXML private TableColumn<Proveedor, String> colEstado;
    @FXML private Button btnExportarExcel;
    @FXML private Button btnExportarPDF;

    private ObservableList<Proveedor> proveedoresFiltrados = FXCollections.observableArrayList();

    public void initialize() {
        colRazonSocial.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("razonSocial"));
        colRuc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("ruc"));
        colContacto.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("contacto"));
        colTelefono.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefono"));
        colCondicionesPago.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("condicionesPago"));
        colTipoProducto.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipoProducto"));
        colSucursalId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("sucursalNombre"));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivo() != null && cellData.getValue().getActivo() ? "Activo" : "Inactivo"));
        tablePreview.setItems(proveedoresFiltrados);
        tablePreview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnExportarExcel.setOnAction(e -> exportar("excel"));
        btnExportarPDF.setOnAction(e -> exportar("pdf"));
    }

    public void setProveedoresFiltrados(List<Proveedor> lista) {
        proveedoresFiltrados.setAll(lista);
        tablePreview.setItems(proveedoresFiltrados);
    }

    private void exportar(String tipo) {
        String nombre = "proveedores";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(nombre + (tipo.equals("excel") ? ".xlsx" : ".pdf"));
        fileChooser.getExtensionFilters().clear();
        if (tipo.equals("excel")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        }
        fileChooser.setTitle("Guardar reporte de proveedores");
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean exito = false;
            try {
                if (tipo.equals("excel")) {
                    ExportFacade.exportarProveedoresExcel(proveedoresFiltrados, file.getAbsolutePath().replace(".xlsx", ""));
                } else {
                    ExportFacade.exportarProveedoresPDF(proveedoresFiltrados, file.getAbsolutePath().replace(".pdf", ""));
                }
                exito = true;
            } catch (Exception e) {
                exito = false;
            }
            mostrarMensaje(exito ? "Archivo exportado correctamente en:\n" + file.getAbsolutePath() : "Error al exportar el archivo. Intente nuevamente.");
            if (exito) cerrar();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportación de Proveedores");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
Stage stage = (Stage) btnExportarExcel.getScene().getWindow();        stage.close();
    }
}
