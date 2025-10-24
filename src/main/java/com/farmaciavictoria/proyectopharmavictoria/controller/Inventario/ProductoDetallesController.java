package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository;

public class ProductoDetallesController implements Initializable {
    @FXML private Label txtCodigo;
    @FXML private Label txtNombre;
    @FXML private Label txtDescripcion;
    @FXML private Label txtEstado;
    @FXML private Label txtPrincipioActivo;
    @FXML private Label txtConcentracion;
    @FXML private Label txtFormaFarmaceutica;
    @FXML private Label txtCategoria;
    @FXML private Label txtPrecioCompra;
    @FXML private Label txtPrecioVenta;
    @FXML private Label txtMargenGanancia;
    @FXML private Label txtStockActual;
    @FXML private Label txtStockMinimo;
    @FXML private Label txtStockMaximo;
    @FXML private Label txtUbicacion;
    @FXML private Label txtLote;
    @FXML private Label txtFechaVencimiento;
    @FXML private Label txtLaboratorio;
    @FXML private Label txtFechaFabricacion;
    @FXML private Label txtProveedor;
    @FXML private Label txtRequiereReceta;
    @FXML private Label txtEsControlado;

    // Historial de cambios
    @FXML private TableView<ProductoHistorialCambioRepository.HistorialCambioDTO> tableHistorial;
    @FXML private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colFecha;
    @FXML private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colUsuario;
    @FXML private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colCampo;
    @FXML private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colAnterior;
    @FXML private TableColumn<ProductoHistorialCambioRepository.HistorialCambioDTO, String> colNuevo;

    private final DecimalFormat formatoMoneda = new DecimalFormat("S/ #,##0.00");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Producto producto;
        private final java.time.format.DateTimeFormatter formatterHistorial = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            // Inicializar columnas de historial si existen en el FXML
            if (colFecha != null) {
                colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().fecha.toLocalDateTime().format(formatterHistorial)));
            }
            if (colUsuario != null) {
                colUsuario.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().usuario));
            }
            if (colCampo != null) {
                colCampo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().campo));
            }
            if (colAnterior != null) {
                colAnterior.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().anterior));
            }
            if (colNuevo != null) {
                colNuevo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().nuevo));
            }
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        cargarDatos();
            cargarHistorial();
        }

        private void cargarHistorial() {
            if (tableHistorial != null && producto != null && producto.getId() != null) {
                java.util.List<ProductoHistorialCambioRepository.HistorialCambioDTO> historial = ProductoHistorialCambioRepository.obtenerCambiosPorProducto(producto.getId());
                ObservableList<ProductoHistorialCambioRepository.HistorialCambioDTO> data = FXCollections.observableArrayList(historial);
                tableHistorial.setItems(data);
            }
    }

    private void cargarDatos() {
        if (producto == null) return;
        txtCodigo.setText(producto.getCodigo() != null ? producto.getCodigo() : "-");
        txtNombre.setText(producto.getNombre() != null ? producto.getNombre() : "-");
        txtDescripcion.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "-");
        txtEstado.setText(producto.getActivo() ? "ACTIVO" : "INACTIVO");
        txtPrincipioActivo.setText(producto.getPrincipioActivo() != null ? producto.getPrincipioActivo() : "-");
        txtConcentracion.setText(producto.getConcentracion() != null ? producto.getConcentracion() : "-");
        txtFormaFarmaceutica.setText(producto.getFormaFarmaceutica() != null ? producto.getFormaFarmaceutica().toString() : "-");
        txtCategoria.setText(producto.getCategoria() != null ? producto.getCategoria().toString() : "-");
        txtPrecioCompra.setText(formatearPrecio(producto.getPrecioCompra()));
        txtPrecioVenta.setText(formatearPrecio(producto.getPrecioVenta()));
        txtMargenGanancia.setText(formatearMargen(producto.getMargenGanancia()));
        txtStockActual.setText(String.valueOf(producto.getStockActual()));
        txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        txtStockMaximo.setText(String.valueOf(producto.getStockMaximo()));
        txtUbicacion.setText(producto.getUbicacion() != null ? producto.getUbicacion() : "-");
        txtLote.setText(producto.getLote() != null ? producto.getLote() : "-");
        txtFechaVencimiento.setText(producto.getFechaVencimiento() != null ? producto.getFechaVencimiento().format(formatoFecha) : "No especificada");
        txtLaboratorio.setText(producto.getLaboratorio() != null && !producto.getLaboratorio().trim().isEmpty() ? producto.getLaboratorio() : "No especificado");
        txtFechaFabricacion.setText(producto.getFechaFabricacion() != null ? producto.getFechaFabricacion().format(formatoFecha) : "No especificada");
        txtProveedor.setText(producto.getProveedorNombre() != null ? producto.getProveedorNombre() : "Sin proveedor");
        txtRequiereReceta.setText(producto.getRequiereReceta() ? "SÍ" : "NO");
        txtEsControlado.setText(producto.getEsControlado() ? "SÍ" : "NO");
        aplicarEstiloSegunEstado();
    }

    private String formatearMargen(java.math.BigDecimal margen) {
        if (margen == null) return "0.00%";
        return String.format("%.2f%%", margen.doubleValue());
    }

    private String formatearPrecio(BigDecimal precio) {
        if (precio == null) return "S/ 0.00";
        return formatoMoneda.format(precio);
    }

    private void aplicarEstiloSegunEstado() {
        if (producto == null) return;
        txtEstado.getStyleClass().removeAll("estado-activo", "estado-inactivo");
        if (!producto.getActivo()) {
            txtEstado.getStyleClass().add("estado-inactivo");
        } else {
            txtEstado.getStyleClass().add("estado-activo");
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
        stage.close();
    }
}
