package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;
import com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.util.List;

public class ExportarUsuariosPreviewController {
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Usuario> tablePreview;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colNombres;
    @FXML
    private TableColumn<Usuario, String> colApellidos;
    @FXML
    private TableColumn<Usuario, String> colDni;
    @FXML
    private TableColumn<Usuario, String> colTelefono;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colEstado;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private Button btnExportarPDF;

    private UsuarioService usuarioService;
    private ObservableList<Usuario> usuariosFiltrados = FXCollections.observableArrayList();

    public void initialize() {
        usuarioService = UsuarioService.getInstance();
        cargarFiltros();
        setupTableColumns();
        setupActions();
        tablePreview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        aplicarFiltros();
    }

    private void cargarFiltros() {
        cmbEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        cmbEstado.getSelectionModel().select(0);
    }

    private void setupTableColumns() {
        colUsuario.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("username"));
        colNombres.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRol() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRol().getDescripcion());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isActivo() ? "Activo" : "Inactivo"));
    }

    private void setupActions() {
        btnExportarExcel.setOnAction(e -> exportar("excel"));
        btnExportarPDF.setOnAction(e -> exportar("pdf"));
        cmbEstado.setOnAction(e -> aplicarFiltros());
        txtBuscar.setOnKeyReleased(e -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String estado = cmbEstado.getValue();
        String texto = txtBuscar.getText().trim().toLowerCase();
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        usuariosFiltrados.setAll(usuarios.stream()
                .filter(u -> {
                    if (estado == null || estado.equals("Todos"))
                        return true;
                    if (estado.equals("Activo"))
                        return u.isActivo();
                    if (estado.equals("Inactivo"))
                        return !u.isActivo();
                    return true;
                })
                .filter(u -> texto.isEmpty() || u.getNombres().toLowerCase().contains(texto)
                        || u.getApellidos().toLowerCase().contains(texto) || u.getDni().toLowerCase().contains(texto)
                        || u.getUsername().toLowerCase().contains(texto))
                .toList());
        tablePreview.setItems(usuariosFiltrados);
    }

    private void exportar(String tipo) {
        String nombre = "usuarios";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(nombre + (tipo.equals("excel") ? ".xlsx" : ".pdf"));
        fileChooser.getExtensionFilters().clear();
        if (tipo.equals("excel")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel", "*.xlsx"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        }
        fileChooser.setTitle("Guardar reporte de usuarios");
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean exito = false;
            try {
                if (tipo.equals("excel")) {
                    ExportFacade.exportarUsuariosExcel(usuariosFiltrados, file.getAbsolutePath().replace(".xlsx", ""));
                } else {
                    ExportFacade.exportarUsuariosPDF(usuariosFiltrados, file.getAbsolutePath().replace(".pdf", ""));
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
        alert.setTitle("Exportación de Usuarios");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) btnExportarExcel.getScene().getWindow();
        stage.close();
    }
}
