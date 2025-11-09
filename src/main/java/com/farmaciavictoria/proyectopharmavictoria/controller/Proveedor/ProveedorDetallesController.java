package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.SessionManager;

public class ProveedorDetallesController {

    @FXML
    private javafx.scene.control.TableView<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto> tablaProductos;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto, String> colNombreProducto;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto, String> colLote;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto, String> colLaboratorio;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto, Integer> colCantidad;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto, String> colVencimiento;
    @FXML
    private Label subtituloLabel;
    @FXML
    private Label razonSocialLabel;
    @FXML
    private Label rucLabel;
    @FXML
    private Label tipoLabel;
    @FXML
    private Label tipoProductoLabel;
    // historialTextArea removed; using TableView 'historialTable' instead
    @FXML
    private javafx.scene.control.TableView<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO> historialTable;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO, String> colHistFecha;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO, String> colHistUsuario;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO, String> colHistCampo;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO, String> colHistAnterior;
    @FXML
    private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO, String> colHistNuevo;
    @FXML
    private Label estadoLabel;
    @FXML
    private Label contactoLabel;
    @FXML
    private Label telefonoLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label direccionLabel;
    @FXML
    private Label condicionesPagoLabel;
    @FXML
    private Label observacionesLabel;
    @FXML
    private javafx.scene.layout.VBox historialCambiosVBox;
    @FXML
    private Label historialCambiosLabel;
    @FXML
    private Label fechaRegistroLabel;
    private Proveedor proveedor;
    private Stage stage;

    public void initialize() {
        // Ocultar historial de cambios para vendedores al inicializar
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = SessionManager
                .getUsuarioActual();
        if (usuarioActual != null && usuarioActual.isVendedor()) {
            if (historialCambiosVBox != null) {
                historialCambiosVBox.setVisible(false);
                historialCambiosVBox.setManaged(false);
            }
            if (historialTable != null) {
                historialTable.getItems().clear();
                historialTable.setVisible(false);
                historialTable.setManaged(false);
            }
            if (historialCambiosLabel != null) {
                historialCambiosLabel.setVisible(false);
                historialCambiosLabel.setManaged(false);
            }
            System.out.println("[DEBUG] Historial de cambios oculto para vendedor");
        }
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
        cargarDatosProveedor();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void cargarDatosProveedor() {
        // Ocultar historial de cambios para vendedores
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = SessionManager
                .getUsuarioActual();
        if (usuarioActual != null && usuarioActual.isVendedor()) {
            if (historialCambiosVBox != null) {
                historialCambiosVBox.setVisible(false);
                historialCambiosVBox.setManaged(false);
            }
            if (historialTable != null) {
                historialTable.getItems().clear();
                historialTable.setVisible(false);
                historialTable.setManaged(false);
            }
            if (historialCambiosLabel != null) {
                historialCambiosLabel.setVisible(false);
                historialCambiosLabel.setManaged(false);
            }
            System.out.println("[DEBUG] Historial de cambios oculto para vendedor (cargarDatosProveedor)");
        }
        if (tablaProductos != null && proveedor != null) {
            tablaProductos.getStyleClass().add("proveedor-productos-table");
            colNombreProducto.setCellValueFactory(
                    cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
            colLote.setCellValueFactory(
                    cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLote()));
            colLaboratorio.setCellValueFactory(
                    cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLaboratorio()));
            colCantidad.setCellValueFactory(
                    cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getStockActual())
                            .asObject());
            colVencimiento.setCellValueFactory(cell -> {
                java.time.LocalDate fecha = cell.getValue().getFechaVencimiento();
                String txt = (fecha != null) ? fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "-";
                return new javafx.beans.property.SimpleStringProperty(txt);
            });
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto> productos = obtenerProductosPorProveedor(
                    proveedor.getId());
            tablaProductos.getItems().setAll(productos);
        }
        if (proveedor != null) {
            subtituloLabel.setText("RUC: " + (proveedor.getRuc() != null ? proveedor.getRuc() : "N/A"));
            razonSocialLabel
                    .setText(proveedor.getRazonSocial() != null ? proveedor.getRazonSocial() : "No especificado");
            rucLabel.setText(proveedor.getRuc() != null ? proveedor.getRuc() : "No especificado");
            tipoLabel.setText("Proveedor");
            tipoProductoLabel
                    .setText(proveedor.getTipoProducto() != null ? proveedor.getTipoProducto() : "No especificado");
            // Cargar historial de cambios
            com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService service = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                    .getInstance().getProveedorService();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository.HistorialCambioDTO> historial = service
                    .obtenerHistorialCambios(proveedor.getId());

            if (historialTable != null) {
                // configurar columnas siempre (evita casos donde no se mostraba nada)
                try {
                    colHistFecha.setCellValueFactory(
                            cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().fecha != null
                                    ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(cell.getValue().fecha)
                                    : ""));
                    colHistUsuario.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().usuario != null ? cell.getValue().usuario : ""));
                    colHistCampo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().campo != null ? cell.getValue().campo : ""));
                    colHistAnterior.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().anterior != null ? cell.getValue().anterior : ""));
                    colHistNuevo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().nuevo != null ? cell.getValue().nuevo : ""));
                } catch (Exception ex) {
                    // no bloquear la carga si falla la configuración de columnas
                    System.err.println("Error al configurar columnas de historial: " + ex.getMessage());
                }

                // setear items en la hebra de UI para garantizar que el skin esté listo
                javafx.application.Platform.runLater(() -> {
                    historialTable.getItems().clear();
                    if (historial != null && !historial.isEmpty()) {
                        historialTable.getItems().setAll(historial);
                    }
                    // Forzar encabezados oscuros (fallback robusto)
                    try {
                        for (javafx.scene.control.TableColumn<?, ?> col : historialTable.getColumns()) {
                            javafx.scene.control.Label lbl = new javafx.scene.control.Label(col.getText());
                            lbl.setStyle("-fx-text-fill: #222; -fx-font-weight: bold; -fx-font-size: 13px;");
                            col.setGraphic(lbl);
                            col.setText("");
                        }
                    } catch (Exception ex) {
                        // ignorar si falla el ajuste visual
                    }
                });
            }
            if (proveedor.isActivo()) {
                estadoLabel.setText("ACTIVO");
                estadoLabel.setStyle(
                        "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 5px 12px; -fx-background-radius: 15px; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else {
                estadoLabel.setText("INACTIVO");
                estadoLabel.setStyle(
                        "-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 5px 12px; -fx-background-radius: 15px; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
            contactoLabel.setText(proveedor.getContacto() != null ? proveedor.getContacto() : "No especificado");
            telefonoLabel.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "No especificado");
            emailLabel.setText(proveedor.getEmail() != null ? proveedor.getEmail() : "No especificado");
            direccionLabel.setText(proveedor.getDireccion() != null ? proveedor.getDireccion() : "No especificado");
            condicionesPagoLabel.setText(
                    proveedor.getCondicionesPago() != null ? proveedor.getCondicionesPago() : "No especificado");
            observacionesLabel
                    .setText(proveedor.getObservaciones() != null && !proveedor.getObservaciones().trim().isEmpty()
                            ? proveedor.getObservaciones()
                            : "Sin observaciones");
            String fechaRegistro = "No especificado";
            if (proveedor.getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                fechaRegistro = proveedor.getCreatedAt().format(formatter);
            }
            fechaRegistroLabel.setText(fechaRegistro);
        }
    }

    private java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto> obtenerProductosPorProveedor(
            Integer proveedorId) {
        if (proveedorId == null)
            return java.util.Collections.emptyList();
        com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository repo = new com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository();
        return repo.findByFiltros(null, Long.valueOf(proveedorId), null, null);
    }

    // boton cerrar eliminado del FXML; el cierre del modal se gestiona donde se
    // abre
}