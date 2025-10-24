package com.farmaciavictoria.proyectopharmavictoria.controller.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.List;

public class ExportarClientesController {
    @FXML
    private TableView<Cliente> tablePreview;
    @FXML
    private TableColumn<Cliente, String> colDni;
    @FXML
    private TableColumn<Cliente, String> colNombres;
    @FXML
    private TableColumn<Cliente, String> colApellidos;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colEmail;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colFechaNacimiento;
    @FXML
    private TableColumn<Cliente, String> colPuntosTotales;
    @FXML
    private TableColumn<Cliente, String> colPuntosUsados;
    @FXML
    private TableColumn<Cliente, String> colPuntosDisponibles;
    @FXML
    private TableColumn<Cliente, String> colFrecuente;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private ComboBox<String> cmbTipoFiltro;

    private ObservableList<Cliente> clientesFiltrados = FXCollections.observableArrayList();

    public void initialize() {
        if (cmbTipoFiltro != null) {
            cmbTipoFiltro.getItems().clear();
            cmbTipoFiltro.getItems().addAll("DNI", "Nombre", "Apellidos", "Teléfono", "Email");
            cmbTipoFiltro.setValue("Nombre");
        }
        colDni.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dni"));
        colNombres.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("direccion"));
        colFechaNacimiento.setCellValueFactory(cellData -> {
            java.time.LocalDate fecha = cellData.getValue().getFechaNacimiento();
            String texto = (fecha != null) ? fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "";
            return new javafx.beans.property.SimpleStringProperty(texto);
        });
        colPuntosTotales.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getPuntosTotales())));
        colPuntosUsados.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getPuntosUsados())));
        colPuntosDisponibles.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getPuntosDisponibles())));
        colFrecuente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                Boolean.TRUE.equals(cellData.getValue().getEsFrecuente()) ? "Sí" : "No"));
        tablePreview.setItems(clientesFiltrados);
        tablePreview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnExportarExcel.setOnAction(e -> exportar("excel"));
        btnExportarPDF.setOnAction(e -> exportar("pdf"));
    }

    public void setClientesFiltrados(List<Cliente> lista) {
        // Filtrar cliente genérico (DNI '00000000')
        List<Cliente> listaSinGenerico = lista.stream()
                .filter(c -> c.getDni() == null || !c.getDni().equals("00000000"))
                .toList();
        clientesFiltrados.setAll(listaSinGenerico);
        tablePreview.setItems(clientesFiltrados);
    }

    private void exportar(String tipo) {
        String nombre = "clientes";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(nombre + (tipo.equals("excel") ? ".xlsx" : ".pdf"));
        fileChooser.getExtensionFilters().clear();
        if (tipo.equals("excel")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        }
        fileChooser.setTitle("Guardar reporte de clientes");
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean exito = false;
            try {
                if (tipo.equals("excel")) {
                    ExportFacade.exportarClientesExcel(clientesFiltrados, file.getAbsolutePath().replace(".xlsx", ""));
                } else {
                    ExportFacade.exportarClientesPDF(clientesFiltrados, file.getAbsolutePath().replace(".pdf", ""));
                }
                exito = true;
            } catch (Exception e) {
                exito = false;
            }
            mostrarMensaje(exito ? "Archivo exportado correctamente en:\n" + file.getAbsolutePath()
                    : "Error al exportar el archivo. Intente nuevamente.");
            if (exito)
                cerrar();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportación de Clientes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        stage.close();
    }
}
