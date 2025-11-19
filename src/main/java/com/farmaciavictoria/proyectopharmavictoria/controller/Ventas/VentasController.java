package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import com.farmaciavictoria.proyectopharmavictoria.util.ComprobanteUtils;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class VentasController implements Initializable {

    private int calcularPuntosPorVenta(Venta venta) {
        if (venta == null || venta.getTotal() == null)
            return 0;
        return venta.getTotal().intValue();
    }

    private final com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository transaccionPuntosRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository();

    private int consultarPuntosCliente(int clienteId) {
        int saldo = 0;
        List<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos> movimientos = transaccionPuntosRepository
                .findByClienteId(clienteId);
        for (com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos mov : movimientos) {
            if ("GANADO".equalsIgnoreCase(mov.getTipo()) || "AJUSTE".equalsIgnoreCase(mov.getTipo())) {
                saldo += mov.getPuntos();
            } else if ("USADO".equalsIgnoreCase(mov.getTipo()) || "EXPIRADO".equalsIgnoreCase(mov.getTipo())) {
                saldo -= mov.getPuntos();
            }
        }
        return Math.max(saldo, 0);
    }

    @FXML
    private TextField txtTipoCliente;
    @FXML
    private HBox panelPuntosCliente;
    @FXML
    private Label lblNombreCliente;
    @FXML
    private Label lblPuntosDisponibles;
    @FXML
    private Label lblEquivalenteSoles;
    @FXML
    private TextField txtDocumento;
    @FXML
    private TextField txtNombreRazon;
    @FXML
    private HBox panelDatosCliente;
    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private ListView<Producto> listSugerencias;
    @FXML
    private Spinner<Integer> spinnerCantidad;
    @FXML
    private TableView<DetalleVenta> tablaCarrito;
    @FXML
    private TableColumn<DetalleVenta, String> colCodigo;
    @FXML
    private TableColumn<DetalleVenta, String> colNombre;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colPrecioUnitario;
    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colSubtotal;
    @FXML
    private TableColumn<DetalleVenta, Void> colEditar;
    @FXML
    private TableColumn<DetalleVenta, Void> colEliminar;
    @FXML
    private ComboBox<Cliente> comboClientes;
    @FXML
    private ComboBox<String> comboPago;
    @FXML
    private ComboBox<String> comboComprobante;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblIGV;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnConfirmarVenta;
    @FXML
    private Button btnHistorial;
    @FXML
    private Button btnAgregarProducto;

    private final ProductoRepository productoRepository = new ProductoRepository();
    private final ClienteRepository clienteRepository = new ClienteRepository();
    private final VentaServiceImpl ventaService;
    private final com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl ventaRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl();
    private final com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl detalleVentaRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl();
    private final com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl comprobanteRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl();
    private final com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepositoryJdbcImpl historialCambioRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepositoryJdbcImpl();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();

    public VentasController() {
        this.ventaService = new VentaServiceImpl(
                ventaRepository,
                detalleVentaRepository,
                comprobanteRepository,
                historialCambioRepository,
                productoRepository);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Estilos modernos para tabla de ventas
        btnHistorial.setOnAction(e -> mostrarHistorialVentas24h());
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #26a69a; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, #26a69a, 16, 0.4, 0, 2);");
        tablaCarrito.getStylesheets().clear();
        tablaCarrito.getStylesheets().add(getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm());
        tablaCarrito.getStyleClass().setAll("productos-table", "ventas-table");
        tablaCarrito.setPlaceholder(new Label("Tabla sin contenido. Busca y agrega productos al carrito."));
        // Configurar CellValueFactory para mostrar texto en las columnas
        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProducto().getCodigo()));
        colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidad())
                        .asObject());
        colPrecioUnitario.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrecioUnitario()));
        colSubtotal.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSubtotal()));
        // Encabezados con alto contraste y fuente grande
        tablaCarrito.lookupAll(".column-header").forEach(node -> node.setStyle(
                "-fx-background-color: #26a69a; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"));
        // Filas con efecto hover y fuente grande
        tablaCarrito.setRowFactory(tv -> new TableRow<DetalleVenta>() {
            @Override
            protected void updateItem(DetalleVenta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-color: white;");
                    this.hoverProperty().addListener((obs, wasHover, isHover) -> {
                        if (isHover) {
                            setStyle("-fx-background-color: #e0f7fa; -fx-font-size: 15px; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-background-color: white; -fx-font-size: 15px; -fx-font-weight: bold;");
                        }
                    });
                }
            }
        });
        colEditar.setCellFactory(tc -> new TableCell<DetalleVenta, Void>() {
            private final Button btn = new Button();
            {
                javafx.scene.image.ImageView iconEditar = new javafx.scene.image.ImageView(
                        getClass().getResource("/icons/editar.png").toExternalForm());
                iconEditar.setFitWidth(24);
                iconEditar.setFitHeight(24);
                btn.setGraphic(iconEditar);
                btn.setStyle(
                        "-fx-background-color: white; -fx-border-color: #26a69a; -fx-border-width: 2px; -fx-border-radius: 8px;");
                btn.getStyleClass().setAll("btn-action", "btn-add");
                btn.setPrefWidth(35);
                btn.setPrefHeight(35);
                btn.setOnAction(e -> {
                    tablaCarrito.getSelectionModel().select(getTableView().getItems().get(getIndex()));
                    editarCantidadProducto();
                    tablaCarrito.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        // Listener para actualizar el panel dinámico de datos de cliente
        comboClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldCliente, newCliente) -> {
            if (newCliente != null) {
                String tipo = newCliente.getTipoCliente();
                String doc = newCliente.getDocumento();
                String nombre = ("EMPRESA".equals(tipo)) ? newCliente.getRazonSocial()
                        : newCliente.getNombreCompleto();
                txtTipoCliente.setText(tipo != null ? tipo : "");
                txtDocumento.setText(doc != null ? doc : "");
                txtNombreRazon.setText(nombre != null ? nombre : "");
                panelDatosCliente.setVisible(true);
                panelDatosCliente.setManaged(true);

                // Mostrar panel de puntos solo para clientes NATURAL
                if ("NATURAL".equalsIgnoreCase(tipo)) {
                    panelPuntosCliente.setVisible(true);
                    panelPuntosCliente.setManaged(true);
                    lblNombreCliente.setText("Cliente: " + nombre);
                    // TODO: Consultar puntos del cliente desde la BD
                    int puntos = consultarPuntosCliente(newCliente.getId());
                    lblPuntosDisponibles.setText("Puntos disponibles: " + puntos);
                    double soles = puntos / 100.0;
                    lblEquivalenteSoles.setText(String.format("Equivalente: S/%.2f", soles));
                } else {
                    panelPuntosCliente.setVisible(false);
                    panelPuntosCliente.setManaged(false);
                }
            } else {
                txtTipoCliente.clear();
                txtDocumento.clear();
                txtNombreRazon.clear();
                panelDatosCliente.setVisible(false);
                panelDatosCliente.setManaged(false);
                panelPuntosCliente.setVisible(false);
                panelPuntosCliente.setManaged(false);
            }
        });
        // Inicialmente ocultar el panel de datos de cliente
        panelDatosCliente.setVisible(false);
        panelDatosCliente.setManaged(false);
        colEliminar.setCellFactory(tc -> new TableCell<DetalleVenta, Void>() {
            private final Button btn = new Button();
            {
                javafx.scene.image.ImageView iconEliminar = new javafx.scene.image.ImageView(
                        getClass().getResource("/icons/eliminar.png").toExternalForm());
                iconEliminar.setFitWidth(24);
                iconEliminar.setFitHeight(24);
                btn.setGraphic(iconEliminar);
                btn.setStyle(
                        "-fx-background-color: white; -fx-border-color: #d32f2f; -fx-border-width: 2px; -fx-border-radius: 8px;");
                btn.getStyleClass().setAll("btn-action", "btn-trash");
                btn.setPrefWidth(35);
                btn.setPrefHeight(35);
                btn.setOnAction(e -> {
                    DetalleVenta detalle = getTableView().getItems().get(getIndex());
                    tablaCarrito.getSelectionModel().select(detalle);
                    eliminarProductoDelCarrito();
                    tablaCarrito.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        // Buscar productos al escribir
        txtBuscarProducto.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                limpiarSugerencias();
                btnAgregarProducto.setDisable(true);
                return;
            }
            ObservableList<Producto> filtrados = productos
                    .filtered(p -> p.getNombre().toLowerCase().contains(newText.trim().toLowerCase()) ||
                            p.getCodigo().toLowerCase().contains(newText.trim().toLowerCase()) ||
                            (p.getPrincipioActivo() != null
                                    && p.getPrincipioActivo().toLowerCase().contains(newText.trim().toLowerCase())));
            listSugerencias.setItems(filtrados);
            listSugerencias.setVisible(!filtrados.isEmpty());
            listSugerencias.setManaged(!filtrados.isEmpty());
            listSugerencias.setDisable(filtrados.isEmpty());
            // Habilitar el botón solo si el texto coincide exactamente con algún producto
            String texto = newText.trim().toLowerCase();
            boolean existe = productos.stream().anyMatch(
                    p -> p.getNombre().toLowerCase().equals(texto) || p.getCodigo().toLowerCase().equals(texto));
            btnAgregarProducto.setDisable(!existe);
            if (filtrados.isEmpty()) {
                listSugerencias.getSelectionModel().clearSelection();
            }
        });

        // Resaltar ListView cuando está visible
        listSugerencias.setStyle(
                "-fx-background-color: white; -fx-border-color: #26a69a; -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, #26a69a, 12, 0.3, 0, 2);");

        // DEBUG: Forzar borde rojo y fondo amarillo para verificar visibilidad
        listSugerencias.setStyle(
                "-fx-background-color: white; -fx-border-color: #26a69a; -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, #26a69a, 12, 0.3, 0, 2);");
        // Mejorar visualización del ListView de sugerencias
        listSugerencias.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                try {
                    if (empty || item == null) {
                        setText("");
                        setStyle("");
                        setGraphic(null);
                    } else {
                        String codigo = item.getCodigo();
                        String nombre = item.getNombre();
                        String stock = (item.getStockActual() != null ? item.getStockActual().toString() : "0");
                        setText("");
                        // Forzar color y tamaño de fuente en la celda
                        setStyle(
                                "-fx-background-color: white; -fx-padding: 8px 12px; -fx-border-color: #b2dfdb; -fx-border-width: 0 0 1px 0; -fx-font-size: 16px; -fx-text-fill: #222;");
                        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                        javafx.scene.control.Label lblCodigo = new javafx.scene.control.Label(codigo);
                        lblCodigo.setStyle(
                                "-fx-font-weight: bold; -fx-text-fill: #388e3c; -fx-min-width: 70px; -fx-font-size: 16px;");
                        javafx.scene.control.Label lblNombre = new javafx.scene.control.Label(nombre);
                        lblNombre.setStyle(
                                "-fx-text-fill: #222; -fx-font-size: 16px; -fx-min-width: 220px; -fx-font-weight: bold;");
                        javafx.scene.control.Label lblStock = new javafx.scene.control.Label("Stock: " + stock);
                        lblStock.setStyle(
                                "-fx-font-weight: bold; -fx-text-fill: #0288d1; -fx-min-width: 80px; -fx-font-size: 16px;");
                        hbox.getChildren().addAll(lblCodigo, lblNombre, lblStock);
                        setGraphic(hbox);
                        // Efecto hover
                        this.hoverProperty().addListener((obs, wasHover, isHover) -> {
                            if (isHover) {
                                setStyle(
                                        "-fx-background-color: #e0f7fa; -fx-padding: 8px 12px; -fx-border-color: #26a69a; -fx-border-width: 0 0 2px 0; -fx-font-size: 16px; -fx-text-fill: #222;");
                            } else {
                                setStyle(
                                        "-fx-background-color: white; -fx-padding: 8px 12px; -fx-border-color: #b2dfdb; -fx-border-width: 0 0 1px 0; -fx-font-size: 16px; -fx-text-fill: #222;");
                            }
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("[ERROR ListCell] Error al renderizar producto en ListView: " + ex.getMessage());
                    ex.printStackTrace();
                    setText("ERROR");
                    setGraphic(null);
                }
            }
        });
        // Seleccionar producto desde sugerencias
        listSugerencias.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            int idx = listSugerencias.getSelectionModel().getSelectedIndex();
            ObservableList<Producto> items = listSugerencias.getItems();
            if (items == null || items.isEmpty()) {
                listSugerencias.getSelectionModel().clearSelection();
                btnAgregarProducto.setDisable(true);
                // Solo log en terminal, nunca en pantalla
                System.err.println("[DEBUG ListView] Intento de selección con lista vacía. Selección cancelada.");
                return;
            }
            if (newSel != null && idx >= 0 && idx < items.size()) {
                txtBuscarProducto.setText(newSel.getNombre());
                limpiarSugerencias();
                btnAgregarProducto.setDisable(false);
            } else {
                btnAgregarProducto.setDisable(true);
                if (idx < 0 || idx >= items.size()) {
                    // Solo log en terminal, nunca en pantalla
                    System.err.println(
                            "[DEBUG ListView] Índice de selección fuera de rango: " + idx + " Tamaño: " + items.size());
                }
            }
        });
        // Mostrar todos los productos al hacer click en el buscador
        txtBuscarProducto.setOnMouseClicked(e -> {
            ObservableList<Producto> todos = FXCollections.observableArrayList(productos);
            if (todos.isEmpty()) {
                listSugerencias.setItems(FXCollections.observableArrayList());
                listSugerencias.setVisible(false);
                listSugerencias.setManaged(false);
                listSugerencias.getSelectionModel().clearSelection();
                btnAgregarProducto.setDisable(true);
                System.err.println("[DEBUG ListView] Click en buscador con lista vacía. No se permite selección.");
                return;
            }
            listSugerencias.setItems(todos);
            listSugerencias.setVisible(true);
            listSugerencias.setManaged(true);
        });

        // Mejorar visualización de tabla de carrito
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        // Usar solo el CSS para el diseño de la tabla
        tablaCarrito.getStylesheets().clear();
        tablaCarrito.getStylesheets().add(getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm());
        tablaCarrito.getStyleClass().setAll("productos-table");
        // Eliminar setStyle en columnas y filas, dejar que el CSS controle todo
        tablaCarrito.setPlaceholder(new Label("Tabla sin contenido. Busca y agrega productos al carrito."));
        cargarProductos();
        cargarClientes();
        comboPago.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "TRANSFERENCIA", "MIXTO"));
        comboPago.setPromptText("Seleccionar método de pago");
        comboComprobante.setItems(FXCollections.observableArrayList("BOLETA", "FACTURA", "TICKET"));
        comboComprobante.setPromptText("Selecciona comprobante");
        comboComprobante.setVisibleRowCount(2);
        tablaCarrito.setItems(carrito);
        btnConfirmarVenta.setOnAction(e -> confirmarVenta());
        btnAgregarProducto.setOnAction(e -> agregarProductoDesdeBuscador());
        actualizarTotales();

        // Personalizar visualización de clientes en ComboBox: tipo, documento y
        // nombre/razón social
        comboClientes.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String tipo = item.getTipoCliente();
                    String doc = item.getDocumento();
                    String nombre = ("EMPRESA".equals(tipo)) ? item.getRazonSocial() : item.getNombreCompleto();
                    setText((tipo != null ? tipo : "") + " | " + doc + " | " + nombre);
                }
            }
        });
        comboClientes.setButtonCell(new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Seleccionar cliente");
                } else {
                    String tipo = item.getTipoCliente();
                    String doc = item.getDocumento();
                    String nombre = ("EMPRESA".equals(tipo)) ? item.getRazonSocial() : item.getNombreCompleto();
                    setText((tipo != null ? tipo : "") + " | " + doc + " | " + nombre);
                }
            }
        });
        // Buscar y agregar producto al carrito según tipo de búsqueda
    }

    // Método utilitario para limpiar el ListView de sugerencias
    private void limpiarSugerencias() {
        listSugerencias.getSelectionModel().clearSelection();
        listSugerencias.setItems(FXCollections.observableArrayList());
        listSugerencias.setVisible(false);
        listSugerencias.setManaged(false);
        btnAgregarProducto.setDisable(true);
    }

    private void cargarProductos() {
        productos.clear();
        productos.addAll(productoRepository.findAll());
        listSugerencias.setItems(FXCollections.observableArrayList());
        listSugerencias.setVisible(false);
        listSugerencias.setManaged(false);
        listSugerencias.getSelectionModel().clearSelection();
        btnAgregarProducto.setDisable(true);
    }

    private void agregarProductoDesdeBuscador() {
        final Producto seleccionado;
        if (!listSugerencias.getItems().isEmpty() && listSugerencias.getSelectionModel().getSelectedIndex() >= 0
                && listSugerencias.getSelectionModel().getSelectedIndex() < listSugerencias.getItems().size()) {
            seleccionado = listSugerencias.getSelectionModel().getSelectedItem();
        } else if (txtBuscarProducto.getText() != null && !txtBuscarProducto.getText().isEmpty()) {
            String texto = txtBuscarProducto.getText().trim().toLowerCase();
            Optional<Producto> prod = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().equals(texto) || p.getCodigo().toLowerCase().equals(texto))
                    .findFirst();
            seleccionado = prod.orElse(null);
        } else {
            seleccionado = null;
        }
        if (seleccionado == null) {
            mostrarMensaje(
                    "Selecciona un producto válido de la lista de sugerencias o escribe el nombre/código exacto.");
            btnAgregarProducto.setDisable(true);
            return;
        }
        int cantidad = spinnerCantidad.getValue();
        // Validación de stock
        if (seleccionado.getStockActual() == null || seleccionado.getStockActual() < cantidad) {
            mostrarMensaje(
                    "Stock insuficiente para el producto seleccionado. Stock actual: " + seleccionado.getStockActual());
            return;
        }
        // Validación de vencimiento
        if (seleccionado.isVencido()) {
            mostrarMensaje("El producto está vencido y no puede ser vendido.");
            return;
        }
        if (seleccionado.isProximoVencer()) {
            mostrarMensaje(
                    "Advertencia: El producto está próximo a vencer (" + seleccionado.getFechaVencimiento() + ").");
        }
        Optional<DetalleVenta> existente = carrito.stream().filter(d -> d.getProducto().getId() == seleccionado.getId())
                .findFirst();
        if (existente.isPresent()) {
            DetalleVenta detalle = existente.get();
            int nuevaCantidad = detalle.getCantidad() + cantidad;
            if (seleccionado.getStockActual() < nuevaCantidad) {
                mostrarMensaje("No hay suficiente stock para sumar la cantidad. Stock actual: "
                        + seleccionado.getStockActual());
                return;
            }
            detalle.setCantidad(nuevaCantidad);
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            actualizarTotales();
            mostrarMensaje("Cantidad actualizada en el carrito.");
        } else {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(seleccionado);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(seleccionado.getPrecioVenta());
            detalle.setSubtotal(seleccionado.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
            detalle.setDescuento(BigDecimal.ZERO);
            carrito.add(detalle);
            actualizarTotales();
        }
        // Limpiar selección y sugerencias de forma segura
        txtBuscarProducto.clear();
        limpiarSugerencias();
        spinnerCantidad.getValueFactory().setValue(1);
        btnAgregarProducto.setDisable(true);
    }

    // Método eliminado: agregarProductoAlCarrito

    private void editarCantidadProducto() {
        DetalleVenta seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null || tablaCarrito.getItems().isEmpty()) {
            mostrarMensaje("Selecciona un producto válido del carrito para editar la cantidad.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(seleccionado.getCantidad()));
        dialog.setTitle("Editar cantidad");
        dialog.setHeaderText("Cantidad para " + seleccionado.getProducto().getNombre());
        dialog.setContentText("Nueva cantidad:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cantStr -> {
            try {
                int cantidad = Integer.parseInt(cantStr);
                if (cantidad < 1)
                    throw new NumberFormatException();
                seleccionado.setCantidad(cantidad);
                seleccionado.setSubtotal(seleccionado.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)));
                actualizarTotales();
            } catch (NumberFormatException ex) {
                mostrarMensaje("Cantidad inválida.");
            }
        });
    }

    private void eliminarProductoDelCarrito() {
        DetalleVenta seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null || tablaCarrito.getItems().isEmpty()) {
            mostrarMensaje("Selecciona un producto válido del carrito para eliminar.");
            return;
        }
        carrito.remove(seleccionado);
        actualizarTotales();
    }

    private void cargarClientes() {
        comboClientes.getItems().clear();
        java.util.List<Cliente> clientes = clienteRepository.findAll(0, 100);
        // Filtrar cliente genérico por DNI o ID
        clientes.removeIf(c -> "00000000".equals(c.getDocumento()) || c.getId() == 1);
        comboClientes.getItems().addAll(clientes);
    }

    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal igv = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta d : carrito) {
            if (d.getSubtotal() != null)
                subtotal = subtotal.add(d.getSubtotal());
            if (d.getDescuento() != null)
                descuento = descuento.add(d.getDescuento());
        }
        igv = subtotal.multiply(new BigDecimal("0.18"));
        total = subtotal.subtract(descuento).add(igv);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        df.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols() {
            {
                setDecimalSeparator(',');
                setGroupingSeparator('.');
            }
        });
        lblSubtotal.setText("Subtotal: S/ " + (subtotal != null ? df.format(subtotal) : "0,00"));
        lblDescuento.setText("Descuento: S/ " + (descuento != null ? df.format(descuento) : "0,00"));
        lblIGV.setText("IGV: S/ " + (igv != null ? df.format(igv) : "0,00"));
        lblTotal.setText("Total: S/ " + (total != null ? df.format(total) : "0,00"));
    }

    // Obtiene el siguiente número de boleta para la serie dada
    private String obtenerSiguienteNumeroBoleta(String serie) {
        int ultimoNumero = comprobanteRepository.obtenerUltimoNumeroPorSerieYTipo(serie, "BOLETA");
        return String.format("%06d", ultimoNumero + 1);
    }

    private String obtenerSiguienteNumeroFactura(String serie) {
        int ultimoNumero = comprobanteRepository.obtenerUltimoNumeroPorSerieYTipo(serie, "FACTURA");
        int siguienteNumero = ultimoNumero + 1;
        return String.format("%06d", siguienteNumero);
    }

    private void anularVenta() {
        mostrarMensaje("Funcionalidad de anulación implementada.");
    }

    private void mostrarMensaje(String msg) {

        if (msg != null && (msg.startsWith("[DEBUG") || msg.startsWith("[ERROR") || msg.contains("Exception")
                || msg.contains("IndexOutOfBounds"))) {
            // Solo log en terminal
            System.err.println(msg);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Convierte un monto numérico a letras en formato "CINCO CON 70/100 SOLES"
    private String convertirMontoALetras(double monto) {
        final String[] UNIDADES = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE",
                "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO",
                "DIECINUEVE" };
        final String[] DECENAS = { "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA",
                "OCHENTA", "NOVENTA" };
        final String[] CENTENAS = { "", "CIEN", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS",
                "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };
        long parteEntera = (long) monto;
        int parteDecimal = (int) Math.round((monto - parteEntera) * 100);
        String letras = "";
        if (parteEntera == 0) {
            letras = "CERO";
        } else if (parteEntera < 20) {
            letras = UNIDADES[(int) parteEntera];
        } else if (parteEntera < 100) {
            int dec = (int) (parteEntera / 10);
            int uni = (int) (parteEntera % 10);
            letras = DECENAS[dec] + (uni > 0 ? " Y " + UNIDADES[uni] : "");
        } else if (parteEntera < 1000) {
            int cen = (int) (parteEntera / 100);
            int resto = (int) (parteEntera % 100);
            letras = CENTENAS[cen] + (resto > 0 ? " " + convertirMontoALetras(resto) : "");
        } else if (parteEntera < 1000000) {
            int mil = (int) (parteEntera / 1000);
            int resto = (int) (parteEntera % 1000);
            if (mil == 1) {
                letras = "MIL" + (resto > 0 ? " " + convertirMontoALetras(resto) : "");
            } else {
                letras = convertirMontoALetras(mil) + " MIL" + (resto > 0 ? " " + convertirMontoALetras(resto) : "");
            }
        } else {
            letras = "UN MILLÓN O MÁS";
        }
        return letras + String.format(" CON %02d/100 SOLES", parteDecimal);
    }

    // Muestra un listado de ventas de las últimas 24 horas en una ventana
    // modal.Incluye botón para revertir/anular cada venta (solo si no está
    // anulada).

    private void mostrarHistorialVentas24h() {
        // Obtener fecha límite (hace 24 horas)
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime hace24h = ahora.minusHours(24);
        // Obtener todas las ventas y filtrar por fecha
        java.util.List<Venta> ventas = ventaRepository.findAll();
        java.util.List<Venta> ventas24h = new java.util.ArrayList<>();
        for (Venta v : ventas) {
            if (v.getFechaVenta() != null && !v.getFechaVenta().isBefore(hace24h)) {
                ventas24h.add(v);
            }
        }
        // Crear ventana/modal
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Historial de ventas (últimas 24h)");
        javafx.scene.control.TableView<Venta> table = new javafx.scene.control.TableView<>();
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        // Columnas
        javafx.scene.control.TableColumn<Venta, String> colFecha = new javafx.scene.control.TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaVenta() != null ? data.getValue().getFechaVenta().toString() : ""));
        javafx.scene.control.TableColumn<Venta, String> colCliente = new javafx.scene.control.TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCliente() != null ? data.getValue().getCliente().getNombreCompleto() : ""));
        javafx.scene.control.TableColumn<Venta, String> colMonto = new javafx.scene.control.TableColumn<>("Total S/");
        colMonto.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTotal() != null ? data.getValue().getTotal().toPlainString() : ""));
        javafx.scene.control.TableColumn<Venta, String> colEstado = new javafx.scene.control.TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getEstado() != null ? data.getValue().getEstado() : ""));
        // Columna botón revertir/anular
        javafx.scene.control.TableColumn<Venta, Void> colRevertir = new javafx.scene.control.TableColumn<>(
                "Revertir/Anular");
        colRevertir.setCellFactory(tc -> new javafx.scene.control.TableCell<Venta, Void>() {
            private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Anular");
            {
                btn.setStyle(
                        "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 8px;");
                btn.setOnAction(e -> {
                    Venta venta = getTableView().getItems().get(getIndex());
                    if (venta != null && !"ANULADA".equalsIgnoreCase(venta.getEstado())) {
                        // Solo llama a la lógica central, que ya maneja el feedback y el envío a
                        // NubeFacT
                        VentasController.this.anularVentaDesdeHistorial(venta);
                        getTableView().refresh();
                    } else {
                        mostrarMensaje("La venta ya está anulada.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Venta venta = empty ? null : getTableView().getItems().get(getIndex());
                setGraphic((!empty && venta != null && !"ANULADA".equalsIgnoreCase(venta.getEstado())) ? btn : null);
            }
        });
        table.getColumns().addAll(colFecha, colCliente, colMonto, colEstado, colRevertir);
        table.setItems(FXCollections.observableArrayList(ventas24h));
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10, table);
        root.setStyle("-fx-padding: 18px; -fx-background-color: #f8f9fa;");
        stage.setScene(new javafx.scene.Scene(root, 800, 400));
        stage.show();
    }

    // Método principal para confirmar la venta y emitir comprobante electrónico
    private void confirmarVenta() {
        final Cliente clienteSeleccionado = comboClientes.getValue();
        final String metodoPago = comboPago.getValue();
        if (metodoPago == null || carrito.isEmpty()) {
            mostrarMensaje("Completa todos los datos y agrega productos al carrito.");
            return;
        }
        // Si no hay cliente seleccionado, buscar cliente genérico por DNI en la BD
        final Cliente clienteActual;
        if (clienteSeleccionado != null) {
            clienteActual = clienteSeleccionado;
        } else {
            com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository clienteRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes = clienteRepo
                    .findAll(0, 10);
            java.util.Optional<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> optGenerico = clientes
                    .stream()
                    .filter(c -> "00000000".equals(c.getDocumento()))
                    .findFirst();
            if (optGenerico.isPresent()) {
                clienteActual = optGenerico.get();
            } else {
                // Crear cliente genérico automáticamente como 'CONSUMIDOR FINAL'
                com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente clienteGenerico = new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente();
                clienteGenerico.setDocumento("00000000");
                clienteGenerico.setNombreCompleto("CONSUMIDOR FINAL");
                clienteGenerico.setTipoCliente("NATURAL");
                clienteGenerico.setRazonSocial("");
                // Si hay otros campos requeridos, inicialízalos aquí
                clienteRepo.save(clienteGenerico); // O el método correcto para guardar
                clienteActual = clienteGenerico;
            }
        }
        // Calcular totales antes de crear boleta y venta
        final java.math.BigDecimal subtotal = carrito.stream().map(DetalleVenta::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        final java.math.BigDecimal[] descuento = { carrito.stream().map(DetalleVenta::getDescuento)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add) };
        final java.math.BigDecimal igv = subtotal.multiply(new java.math.BigDecimal("0.18"));
        final java.math.BigDecimal[] total = { subtotal.subtract(descuento[0]).add(igv) };

        // Lógica de puntos para clientes NATURAL y boleta
        final boolean[] puntosUsados = { false };
        final int[] puntosDescontados = { 0 };
        double solesDescuento = 0.0;
        if (clienteActual != null && "NATURAL".equalsIgnoreCase(clienteActual.getTipoCliente())
                && "BOLETA".equalsIgnoreCase(comboComprobante.getValue())) {
            int puntos = consultarPuntosCliente(clienteActual.getId());
            if (puntos >= 100) {
                solesDescuento = Math.floor(puntos / 100.0);
                // Límite opcional: máximo 50% del total
                double maxDescuento = total[0].doubleValue() * 0.5;
                if (solesDescuento > maxDescuento) {
                    solesDescuento = Math.floor(maxDescuento);
                }
                puntosDescontados[0] = (int) (solesDescuento * 100);
                if (solesDescuento > 0) {
                    // Mostrar pop-up para preguntar si desea usar los puntos
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Redención de puntos");
                    alert.setHeaderText("¿Desea usar sus puntos?");
                    alert.setContentText("Tiene " + puntos + " puntos disponibles (S/" + solesDescuento
                            + " de descuento). ¿Desea aplicarlos a esta compra?");
                    ButtonType btnSi = new ButtonType("Sí, usar puntos", ButtonBar.ButtonData.YES);
                    ButtonType btnNo = new ButtonType("No usar puntos", ButtonBar.ButtonData.NO);
                    alert.getButtonTypes().setAll(btnSi, btnNo);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == btnSi) {
                        puntosUsados[0] = true;
                        descuento[0] = descuento[0].add(new java.math.BigDecimal(solesDescuento));
                        total[0] = subtotal.subtract(descuento[0]).add(igv);
                    }
                }
            }
        }
        String tipoComprobante = comboComprobante.getValue();
        if ("TICKET".equalsIgnoreCase(tipoComprobante)) {
            // Lógica para registrar venta tipo TICKET
            com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.TicketVentaController ticketController = new com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.TicketVentaController();
            Venta ventaGuardada = ticketController.registrarVentaTicket(new ArrayList<>(carrito), metodoPago,
                    SessionManager.getUsuarioActual().getUsername(), "Farmacia Victoria", clienteActual);
            // Mostrar vista previa del ticket
            String textoTicket = ticketController.generarTextoTicket(ventaGuardada, "Farmacia Victoria");
            ticketController.mostrarVistaPreviaTicket(textoTicket);
            carrito.clear();
            actualizarTotales();
            mostrarMensaje("Venta tipo TICKET registrada correctamente.");
            return;
        }
        if ("FACTURA".equalsIgnoreCase(tipoComprobante)) {
            // Validar que el cliente seleccionado tenga datos completos de empresa
            if (clienteActual == null || clienteActual.getRuc() == null || clienteActual.getRazonSocial() == null
                    || clienteActual.getDireccion() == null || clienteActual.getEmail() == null
                    || clienteActual.getRuc().isEmpty() || clienteActual.getRazonSocial().isEmpty()
                    || clienteActual.getDireccion().isEmpty()) {
                mostrarMensaje(
                        "Para emitir factura electrónica, selecciona un cliente tipo Empresa con RUC, razón social y dirección.");
                return;
            }
            // Ir directo a la vista previa usando los datos del cliente seleccionado
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/vista_previa_comprobante.fxml"));
                javafx.scene.Parent root = loader.load();
                VistaPreviaComprobanteController previewController = loader.getController();
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Vista Previa de Factura Electrónica");
                stage.setScene(new javafx.scene.Scene(root));
                // Convertir Cliente y DetalleVenta locales a tipos PharmavictoriaApplication
                ComprobanteUtils.Cliente clienteJsonPreview = new ComprobanteUtils.Cliente();
                clienteJsonPreview.tipo_documento = "6";
                clienteJsonPreview.numero_documento = clienteActual.getRuc();
                clienteJsonPreview.razon_social = clienteActual.getRazonSocial();
                clienteJsonPreview.direccion = clienteActual.getDireccion();
                clienteJsonPreview.direccion = clienteActual.getEmail();
                javafx.collections.ObservableList<ComprobanteUtils.DetalleVenta> detallesPreview = javafx.collections.FXCollections
                        .observableArrayList();
                for (DetalleVenta d : carrito) {
                    ComprobanteUtils.DetalleVenta det = new ComprobanteUtils.DetalleVenta();
                    det.unidad_de_medida = "NIU";
                    det.codigo_producto = d.getProducto().getCodigo();
                    det.descripcion = d.getProducto().getNombre();
                    det.cantidad = d.getCantidad();
                    det.valor_unitario = d.getPrecioUnitario().doubleValue();
                    det.precio_unitario = d.getPrecioUnitario().doubleValue();
                    det.subtotal = d.getSubtotal().doubleValue();
                    det.tipo_de_igv = 1;
                    double igvDetalle = d.getSubtotal().doubleValue() * 0.18;
                    det.igv = igvDetalle;
                    det.total = d.getSubtotal().doubleValue() + igvDetalle;
                    detallesPreview.add(det);
                }
                // Obtener el siguiente número de factura para la serie
                String serieFactura = "FFF1";
                String numeroFactura = obtenerSiguienteNumeroFactura(serieFactura);
                previewController.setDatos(clienteJsonPreview, "FACTURA", serieFactura, numeroFactura,
                        total[0].toPlainString(), detallesPreview);
                previewController.setStage(stage);
                previewController.setOnConfirmar(() -> {
                    try {
                        // Armado de JSON igual al ejemplo de PharmavictoriaApplication.java
                        java.util.LinkedHashMap<String, Object> factura = new java.util.LinkedHashMap<>();
                        factura.put("operacion", "generar_comprobante");
                        factura.put("tipo_de_comprobante", 1);
                        factura.put("serie", serieFactura);
                        factura.put("numero", Integer.parseInt(numeroFactura));
                        factura.put("sunat_transaction", 1);
                        factura.put("cliente_tipo_de_documento", 6);
                        factura.put("cliente_numero_de_documento", clienteActual.getRuc());
                        factura.put("cliente_denominacion", clienteActual.getRazonSocial());
                        factura.put("cliente_direccion", clienteActual.getDireccion());
                        factura.put("cliente_email", clienteActual.getEmail() != null ? clienteActual.getEmail() : "");
                        factura.put("cliente_email_1", "");
                        factura.put("cliente_email_2", "");
                        factura.put("fecha_de_emision", java.time.LocalDate.now().toString());
                        factura.put("fecha_de_vencimiento", "");
                        factura.put("moneda", 1);
                        factura.put("tipo_de_cambio", "");
                        factura.put("porcentaje_de_igv", 18.00);
                        factura.put("descuento_global", "");
                        factura.put("total_descuento", "");
                        factura.put("total_anticipo", "");
                        factura.put("total_gravada", subtotal.doubleValue());
                        factura.put("total_inafecta", "");
                        factura.put("total_exonerada", "");
                        factura.put("total_igv", igv.doubleValue());
                        factura.put("total_gratuita", "");
                        factura.put("total_otros_cargos", "");
                        factura.put("total", total[0].doubleValue());
                        factura.put("percepcion_tipo", "");
                        factura.put("percepcion_base_imponible", "");
                        factura.put("total_percepcion", "");
                        factura.put("total_incluido_percepcion", "");
                        factura.put("retencion_tipo", "");
                        factura.put("retencion_base_imponible", "");
                        factura.put("total_retencion", "");
                        factura.put("total_impuestos_bolsas", "");
                        factura.put("detraccion", false);
                        factura.put("observaciones", "");
                        factura.put("documento_que_se_modifica_tipo", "");
                        factura.put("documento_que_se_modifica_serie", "");
                        factura.put("documento_que_se_modifica_numero", "");
                        factura.put("tipo_de_nota_de_credito", "");
                        factura.put("tipo_de_nota_de_debito", "");
                        factura.put("enviar_automaticamente_a_la_sunat", true);
                        factura.put("enviar_automaticamente_al_cliente", false);
                        factura.put("condiciones_de_pago", "");
                        factura.put("medio_de_pago", "");
                        factura.put("placa_vehiculo", "");
                        factura.put("orden_compra_servicio", "");
                        factura.put("formato_de_pdf", "");
                        factura.put("generado_por_contingencia", "");
                        factura.put("bienes_region_selva", "");
                        factura.put("servicios_region_selva", "");
                        // Items
                        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                        for (DetalleVenta d : carrito) {
                            java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                            item.put("unidad_de_medida", "NIU");
                            item.put("codigo", d.getProducto().getCodigo());
                            item.put("codigo_producto_sunat", "10000000");
                            item.put("descripcion", d.getProducto().getNombre());
                            item.put("cantidad", d.getCantidad());
                            double valorUnitario = d.getPrecioUnitario().doubleValue();
                            item.put("valor_unitario", valorUnitario);
                            double precioUnitarioConIGV = Math.round(valorUnitario * 1.18 * 100.0) / 100.0;
                            item.put("precio_unitario", precioUnitarioConIGV);
                            item.put("descuento", "");
                            item.put("subtotal", valorUnitario * d.getCantidad());
                            item.put("tipo_de_igv", 1);
                            double igvDetalle = Math.round(valorUnitario * d.getCantidad() * 0.18 * 100.0) / 100.0;
                            item.put("igv", igvDetalle);
                            double totalLinea = Math.round(precioUnitarioConIGV * d.getCantidad() * 100.0) / 100.0;
                            item.put("total", totalLinea);
                            item.put("anticipo_regularizacion", false);
                            item.put("anticipo_documento_serie", "");
                            item.put("anticipo_documento_numero", "");
                            items.add(item);
                        }
                        factura.put("items", items);
                        factura.put("guias", new java.util.ArrayList<>());
                        factura.put("venta_al_credito", new java.util.ArrayList<>());
                        String jsonFactura = new com.google.gson.Gson().toJson(factura);
                        System.out.println("[DEBUG JSON FACTURA] JSON generado para NubeFacT:\n" + jsonFactura);
                        java.util.Properties nubefactProps = new java.util.Properties();
                        try (java.io.InputStream input = new java.io.FileInputStream(
                                "src/main/resources/venta_nubefact.properties")) {
                            nubefactProps.load(input);
                        }
                        String apiUrl = nubefactProps.getProperty("api.url");
                        String apiToken = nubefactProps.getProperty("api.token");
                        String respuesta = ComprobanteUtils.enviarFacturaNubeFact(jsonFactura, apiUrl, apiToken);
                        String hashSunat = "";
                        String estadoSunat = "";
                        String pdfUrl = "";
                        String xmlUrl = "";
                        String cdrUrl = "";
                        try {
                            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(respuesta)
                                    .getAsJsonObject();
                            hashSunat = json.has("hash") ? json.get("hash").getAsString() : "";
                            estadoSunat = json.has("sunat_description")
                                    ? json.get("sunat_description").getAsString()
                                    : "";
                            pdfUrl = json.has("enlace_del_pdf") ? json.get("enlace_del_pdf").getAsString() : "";
                            xmlUrl = json.has("enlace_xml") ? json.get("enlace_xml").getAsString() : "";
                            cdrUrl = json.has("enlace_cdr") ? json.get("enlace_cdr").getAsString() : "";
                            System.out.println("[DEBUG] Respuesta NubeFacT: " + respuesta);
                            System.out.println("[DEBUG] pdfUrl extraído: " + pdfUrl);
                        } catch (Exception ex) {
                            mostrarMensaje("Error procesando respuesta de NubeFacT: " + ex.getMessage());
                            System.err.println("[ERROR NubeFacT] " + ex.getMessage());
                            ex.printStackTrace();
                        }
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.ComprobanteFactory comprobanteFactory = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.FacturaFactory();
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante comprobante = comprobanteFactory
                                .crearComprobante();
                        comprobante.setSerie(serieFactura);
                        comprobante.setNumero(numeroFactura);
                        comprobante.setHashSunat(hashSunat);
                        comprobante.setEstadoSunat("GENERADO");
                        comprobante.setFechaEmision(java.time.LocalDateTime.now());

                        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                                .getUsuarioActual();
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder ventaBuilder = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder()
                                .conCliente(clienteActual)
                                .conUsuario(usuarioActual)
                                .conSubtotal(subtotal)
                                .conDescuentoMonto(descuento[0])
                                .conIgvMonto(igv)
                                .conTotal(total[0])
                                .conTipoPago(metodoPago)
                                .conTipoComprobante("FACTURA")
                                .conNumeroBoleta(numeroFactura)
                                .conSerie(serieFactura)
                                .conFechaVenta(now)
                                .conEstado("REALIZADA")
                                .conDetalles(new java.util.ArrayList<>(carrito))
                                .conComprobante(comprobante)
                                .conObservaciones("")
                                .conCreatedAt(now)
                                .conUpdatedAt(now);
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta venta = ventaBuilder.build();
                        Venta ventaGuardada = ventaRepository.save(venta);
                        if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                            mostrarMensaje("Error al guardar la venta. No se pudo obtener el ID generado.");
                            return;
                        }
                        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
                            detalle.setVenta(ventaGuardada);
                            try {
                                detalleVentaRepository.save(detalle);
                            } catch (Exception ex) {
                                mostrarMensaje("Error al guardar detalle de venta: " + ex.getMessage());
                                System.err.println("[ERROR DETALLE VENTA] " + ex.getMessage());
                            }
                            var producto = detalle.getProducto();
                            Integer cantidadVendida = detalle.getCantidad();
                            if (producto != null && producto.getId() != null && cantidadVendida != null) {
                                int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                                int nuevoStock = stockActual - cantidadVendida;
                                if (nuevoStock < 0) {
                                    mostrarMensaje("Error: El stock del producto '" + producto.getNombre()
                                            + "' no puede ser negativo. Venta no registrada correctamente.");
                                    System.err.println("[ERROR STOCK] Stock negativo para producto: "
                                            + producto.getNombre() + " (ID: " + producto.getId() + ")");
                                    continue;
                                }
                                producto.setStockActual(nuevoStock);
                                try {
                                    // Usar el método correcto del repositorio para actualizar el stock
                                    productoRepository.updateStock(producto.getId(), nuevoStock);
                                } catch (Exception ex) {
                                    mostrarMensaje("Error al actualizar el stock de '" + producto.getNombre() + "': "
                                            + ex.getMessage());
                                    System.err.println("[ERROR STOCK] " + ex.getMessage());
                                }
                            }
                        }
                        // Diagnóstico: mostrar tipo de cliente y total de venta antes de registrar
                        // puntos
                        System.out.println("[DEBUG DIAGNOSTICO PUNTOS] clienteActual="
                                + (clienteActual != null ? clienteActual.getTipoCliente() : "null") + " | totalVenta="
                                + (ventaGuardada != null && ventaGuardada.getTotal() != null ? ventaGuardada.getTotal()
                                        : "null"));
                        // Registrar puntos GANADOS por la venta
                        if (clienteActual != null && "NATURAL".equalsIgnoreCase(clienteActual.getTipoCliente())) {
                            int puntosGanados = calcularPuntosPorVenta(ventaGuardada);
                            if (puntosGanados > 0) {
                                com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos movGanado = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                                movGanado.setClienteId(clienteActual.getId());
                                movGanado.setVentaId(ventaGuardada.getId());
                                movGanado.setTipo("GANADO");
                                movGanado.setPuntos(puntosGanados);
                                movGanado.setDescripcion("Puntos ganados por compra");
                                movGanado.setFecha(java.time.LocalDateTime.now());
                                movGanado.setUsuarioId(usuarioActual != null
                                        ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                        : null);
                                System.out.println(
                                        "[DEBUG FLUJO PUNTOS] Se va a guardar transacción de puntos: " + movGanado);
                                boolean puntosGuardados = transaccionPuntosRepository.save(movGanado);
                                if (!puntosGuardados) {
                                    System.err.println(
                                            "[ERROR PUNTOS] No se pudo guardar la transacción de puntos GANADO: "
                                                    + movGanado);
                                    mostrarMensaje(
                                            "Error al registrar los puntos ganados. Verifique los datos del cliente y la venta.");
                                }
                            }
                        }
                        // 2. Crear comprobante y asociar la venta guardada
                        comprobante.setVenta(ventaGuardada);
                        comprobanteRepository.save(comprobante);

                        // Registrar historial de venta
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio historial = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio();
                        historial.setVenta(ventaGuardada);
                        historial.setTipoCambio("CREACION"); // ENUM correcto
                        historial.setMotivo("Venta registrada");
                        historial.setUsuario(usuarioActual);
                        historial.setFecha(java.time.LocalDateTime.now());
                        try {
                            historialCambioRepository.save(historial);
                        } catch (Exception ex) {
                            mostrarMensaje("Error al registrar historial de venta: " + ex.getMessage());
                            System.err.println("[ERROR HISTORIAL VENTA] " + ex.getMessage());
                        }
                        StringBuilder msg = new StringBuilder();
                        msg.append("Factura electrónica enviada a SUNAT (demo)\n");
                        msg.append("Estado SUNAT: ").append(estadoSunat).append("\n");
                        if (!pdfUrl.isEmpty())
                            msg.append("PDF: ").append(pdfUrl).append("\n");
                        if (!xmlUrl.isEmpty())
                            msg.append("XML: ").append(xmlUrl).append("\n");
                        if (!cdrUrl.isEmpty())
                            msg.append("CDR: ").append(cdrUrl).append("\n");
                        mostrarMensaje(msg.toString());
                        // Abrir ventana de acciones con PDF y datos del cliente (FACTURA/BOLETA)
                        try {
                            javafx.fxml.FXMLLoader accionesLoader = new javafx.fxml.FXMLLoader(
                                    getClass().getResource("/fxml/comprobante_acciones.fxml"));
                            javafx.scene.Parent accionesRoot = accionesLoader.load();
                            ComprobanteAccionesController accionesController = accionesLoader.getController();
                            accionesController.setDatos(pdfUrl, clienteActual.getNombreCompleto(),
                                    clienteActual.getEmail(), clienteActual.getTelefono());
                            javafx.stage.Stage accionesStage = new javafx.stage.Stage();
                            accionesController.setStage(accionesStage);
                            accionesStage.setTitle("Acciones sobre Comprobante Electrónico");
                            accionesStage.setScene(new javafx.scene.Scene(accionesRoot));
                            accionesStage.show();
                        } catch (Exception ex) {
                            mostrarMensaje("No se pudo abrir la ventana de acciones: " + ex.getMessage());
                        }
                        carrito.clear();
                        actualizarTotales();
                    } catch (Exception ex) {
                        mostrarMensaje("Error en emisión de factura electrónica: " + ex.getMessage());
                        System.err.println("[ERROR Emisión Factura] " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                stage.showAndWait();
            } catch (Exception ex) {
                mostrarMensaje("Error en emisión de factura electrónica: " + ex.getMessage());
                System.err.println("[ERROR Emisión Factura] " + ex.getMessage());
                ex.printStackTrace();
            }
        } else if ("BOLETA".equalsIgnoreCase(tipoComprobante)) {
            // Validar que el cliente seleccionado sea NATURAL y tenga datos completos
            if (clienteActual == null || clienteActual.getDni() == null || clienteActual.getNombreCompleto() == null
                    || clienteActual.getDireccion() == null || clienteActual.getDni().isEmpty()
                    || clienteActual.getNombreCompleto().isEmpty() || clienteActual.getDireccion().isEmpty()) {
                mostrarMensaje(
                        "Para emitir boleta electrónica, selecciona un cliente tipo NATURAL con DNI, nombre y dirección.");
                return;
            }
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/vista_previa_comprobante.fxml"));
                javafx.scene.Parent root = loader.load();
                VistaPreviaComprobanteController previewController = loader.getController();
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Vista Previa de Boleta Electrónica");
                stage.setScene(new javafx.scene.Scene(root));
                ComprobanteUtils.Cliente clienteJsonPreview = new ComprobanteUtils.Cliente();
                clienteJsonPreview.tipo_documento = "1";
                clienteJsonPreview.numero_documento = clienteActual.getDni();
                clienteJsonPreview.razon_social = clienteActual.getNombreCompleto();
                clienteJsonPreview.direccion = clienteActual.getDireccion();
                clienteJsonPreview.direccion = clienteActual.getEmail();
                javafx.collections.ObservableList<ComprobanteUtils.DetalleVenta> detallesPreview = javafx.collections.FXCollections
                        .observableArrayList();
                for (DetalleVenta d : carrito) {
                    ComprobanteUtils.DetalleVenta det = new ComprobanteUtils.DetalleVenta();
                    det.unidad_de_medida = "NIU";
                    det.codigo_producto = d.getProducto().getCodigo();
                    det.descripcion = d.getProducto().getNombre();
                    det.cantidad = d.getCantidad();
                    det.valor_unitario = d.getPrecioUnitario().doubleValue();
                    det.precio_unitario = d.getPrecioUnitario().doubleValue();
                    det.subtotal = d.getSubtotal().doubleValue();
                    det.tipo_de_igv = 1;
                    double igvDetalle = d.getSubtotal().doubleValue() * 0.18;
                    det.igv = igvDetalle;
                    det.total = d.getSubtotal().doubleValue() + igvDetalle;
                    detallesPreview.add(det);
                }
                String serieBoleta = "BBB1";
                String numeroBoleta = obtenerSiguienteNumeroBoleta(serieBoleta);
                previewController.setDatos(clienteJsonPreview, "BOLETA", serieBoleta, numeroBoleta,
                        total[0].toPlainString(), detallesPreview);
                previewController.setStage(stage);
                previewController.setOnConfirmar(() -> {
                    try {
                        // Armado de JSON igual al ejemplo de PharmavictoriaApplication.java
                        java.util.LinkedHashMap<String, Object> boleta = new java.util.LinkedHashMap<>();
                        boleta.put("operacion", "generar_comprobante");
                        boleta.put("tipo_de_comprobante", 2);
                        boleta.put("serie", serieBoleta);
                        boleta.put("numero", Integer.parseInt(numeroBoleta));
                        boleta.put("sunat_transaction", 1);
                        boleta.put("cliente_tipo_de_documento", 1);
                        boleta.put("cliente_numero_de_documento", clienteActual.getDni());
                        boleta.put("cliente_denominacion", clienteActual.getNombreCompleto());
                        boleta.put("cliente_direccion", clienteActual.getDireccion());
                        boleta.put("cliente_email", clienteActual.getEmail() != null ? clienteActual.getEmail() : "");
                        boleta.put("cliente_email_1", "");
                        boleta.put("cliente_email_2", "");
                        boleta.put("fecha_de_emision", java.time.LocalDate.now().toString());
                        boleta.put("fecha_de_vencimiento", "");
                        boleta.put("moneda", 1);
                        boleta.put("tipo_de_cambio", "");
                        boleta.put("porcentaje_de_igv", 18.00);
                        boleta.put("descuento_global", "");
                        boleta.put("total_descuento", "");
                        boleta.put("total_anticipo", "");
                        boleta.put("total_gravada", subtotal.doubleValue());
                        boleta.put("total_inafecta", "");
                        boleta.put("total_exonerada", "");
                        boleta.put("total_igv", igv.doubleValue());
                        boleta.put("total_gratuita", "");
                        boleta.put("total_otros_cargos", "");
                        boleta.put("total", total[0].doubleValue());
                        boleta.put("percepcion_tipo", "");
                        boleta.put("percepcion_base_imponible", "");
                        boleta.put("total_percepcion", "");
                        boleta.put("total_incluido_percepcion", "");
                        boleta.put("retencion_tipo", "");
                        boleta.put("retencion_base_imponible", "");
                        boleta.put("total_retencion", "");
                        boleta.put("total_impuestos_bolsas", "");
                        boleta.put("detraccion", false);
                        boleta.put("observaciones", "");
                        boleta.put("documento_que_se_modifica_tipo", "");
                        boleta.put("documento_que_se_modifica_serie", "");
                        boleta.put("documento_que_se_modifica_numero", "");
                        boleta.put("tipo_de_nota_de_credito", "");
                        boleta.put("tipo_de_nota_de_debito", "");
                        boleta.put("enviar_automaticamente_a_la_sunat", true);
                        boleta.put("enviar_automaticamente_al_cliente", false);
                        boleta.put("condiciones_de_pago", "");
                        boleta.put("medio_de_pago", "");
                        boleta.put("placa_vehiculo", "");
                        boleta.put("orden_compra_servicio", "");
                        boleta.put("formato_de_pdf", "");
                        boleta.put("generado_por_contingencia", "");
                        boleta.put("bienes_region_selva", "");
                        boleta.put("servicios_region_selva", "");
                        // Items
                        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                        for (DetalleVenta d : carrito) {
                            java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                            item.put("unidad_de_medida", "NIU");
                            item.put("codigo", d.getProducto().getCodigo());
                            item.put("codigo_producto_sunat", "10000000");
                            item.put("descripcion", d.getProducto().getNombre());
                            item.put("cantidad", d.getCantidad());
                            double valorUnitario = d.getPrecioUnitario().doubleValue();
                            item.put("valor_unitario", valorUnitario);
                            double precioUnitarioConIGV = Math.round(valorUnitario * 1.18 * 100.0) / 100.0;
                            item.put("precio_unitario", precioUnitarioConIGV);
                            item.put("descuento", "");
                            item.put("subtotal", valorUnitario * d.getCantidad());
                            item.put("tipo_de_igv", 1);
                            double igvDetalle = Math.round(valorUnitario * d.getCantidad() * 0.18 * 100.0) / 100.0;
                            item.put("igv", igvDetalle);
                            double totalLinea = Math.round(precioUnitarioConIGV * d.getCantidad() * 100.0) / 100.0;
                            item.put("total", totalLinea);
                            item.put("anticipo_regularizacion", false);
                            item.put("anticipo_documento_serie", "");
                            item.put("anticipo_documento_numero", "");
                            items.add(item);
                        }
                        boleta.put("items", items);
                        boleta.put("guias", new java.util.ArrayList<>());
                        boleta.put("venta_al_credito", new java.util.ArrayList<>());
                        String jsonBoleta = new com.google.gson.Gson().toJson(boleta);
                        System.out.println("[DEBUG JSON BOLETA] JSON generado para NubeFacT:\n" + jsonBoleta);
                        java.util.Properties nubefactProps = new java.util.Properties();
                        try (java.io.InputStream input = new java.io.FileInputStream(
                                "src/main/resources/venta_nubefact.properties")) {
                            nubefactProps.load(input);
                        }
                        String apiUrl = nubefactProps.getProperty("api.url");
                        String apiToken = nubefactProps.getProperty("api.token");
                        String respuesta = com.farmaciavictoria.proyectopharmavictoria.util.ComprobanteUtils
                                .enviarFacturaNubeFact(jsonBoleta, apiUrl, apiToken);
                        // Mostrar JSON y respuesta NubeFacT solo en terminal para diagnóstico
                        System.out.println("[DEBUG JSON enviado a NubeFacT]\n" + jsonBoleta);
                        System.out.println("[DEBUG Respuesta completa de NubeFacT]\n" + respuesta);
                        String hashSunat = "";
                        String estadoSunat = "";
                        String pdfUrl = "";
                        String xmlUrl = "";
                        String cdrUrl = "";
                        try {
                            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(respuesta)
                                    .getAsJsonObject();
                            if (json != null && !json.isJsonNull()) {
                                hashSunat = json.has("hash") && !json.get("hash").isJsonNull()
                                        ? json.get("hash").getAsString()
                                        : "";
                                estadoSunat = json.has("sunat_description")
                                        && !json.get("sunat_description").isJsonNull()
                                                ? json.get("sunat_description").getAsString()
                                                : "";
                                pdfUrl = json.has("enlace_del_pdf") && !json.get("enlace_del_pdf").isJsonNull()
                                        ? json.get("enlace_del_pdf").getAsString()
                                        : "";
                                xmlUrl = json.has("enlace_xml") && !json.get("enlace_xml").isJsonNull()
                                        ? json.get("enlace_xml").getAsString()
                                        : "";
                                cdrUrl = json.has("enlace_cdr") && !json.get("enlace_cdr").isJsonNull()
                                        ? json.get("enlace_cdr").getAsString()
                                        : "";
                                System.out.println("[DEBUG] Respuesta NubeFacT: " + respuesta);
                                System.out.println("[DEBUG] pdfUrl extraído: " + pdfUrl);
                            } else {
                                mostrarMensaje("Boleta registrada, pero no se recibió respuesta completa de NubeFacT.");
                            }
                        } catch (Exception ex) {
                            mostrarMensaje("Boleta registrada, pero no se recibió respuesta completa de NubeFacT.");
                            System.err.println("[ERROR NubeFacT] " + ex.getMessage());
                        }
                        // 1. Crear comprobante usando Factory
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.ComprobanteFactory comprobanteFactory = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.BoletaFactory();
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante comprobante = comprobanteFactory
                                .crearComprobante();
                        comprobante.setSerie(serieBoleta);
                        comprobante.setNumero(numeroBoleta);
                        comprobante.setHashSunat(hashSunat);
                        comprobante.setEstadoSunat("GENERADO");
                        comprobante.setFechaEmision(java.time.LocalDateTime.now());

                        // 2. Construir venta usando Builder
                        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                                .getUsuarioActual();
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder ventaBuilder = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder()
                                .conCliente(clienteActual)
                                .conUsuario(usuarioActual)
                                .conSubtotal(subtotal)
                                .conDescuentoMonto(descuento[0])
                                .conIgvMonto(igv)
                                .conTotal(total[0])
                                .conTipoPago(metodoPago)
                                .conTipoComprobante("BOLETA")
                                .conNumeroBoleta(numeroBoleta)
                                .conSerie(serieBoleta)
                                .conFechaVenta(now)
                                .conEstado("REALIZADA")
                                .conDetalles(new java.util.ArrayList<>(carrito))
                                .conComprobante(comprobante)
                                .conObservaciones("")
                                .conCreatedAt(now)
                                .conUpdatedAt(now);
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta venta = ventaBuilder.build();
                        Venta ventaGuardada = ventaRepository.save(venta);
                        if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                            mostrarMensaje("Error al guardar la venta. No se pudo obtener el ID generado.");
                            return;
                        }
                        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
                            detalle.setVenta(ventaGuardada);
                            try {
                                detalleVentaRepository.save(detalle);
                            } catch (Exception ex) {
                                mostrarMensaje("Error al guardar detalle de venta: " + ex.getMessage());
                                System.err.println("[ERROR DETALLE VENTA] " + ex.getMessage());
                            }
                            var producto = detalle.getProducto();
                            Integer cantidadVendida = detalle.getCantidad();
                            if (producto != null && producto.getId() != null && cantidadVendida != null) {
                                int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                                int nuevoStock = stockActual - cantidadVendida;
                                if (nuevoStock < 0) {
                                    mostrarMensaje("Error: El stock del producto '" + producto.getNombre()
                                            + "' no puede ser negativo. Venta no registrada correctamente.");
                                    System.err.println("[ERROR STOCK] Stock negativo para producto: "
                                            + producto.getNombre() + " (ID: " + producto.getId() + ")");
                                    continue;
                                }
                                producto.setStockActual(nuevoStock);
                                try {
                                    productoRepository.updateStock(producto.getId(), nuevoStock);
                                } catch (Exception ex) {
                                    mostrarMensaje("Error al actualizar el stock de '" + producto.getNombre() + "': "
                                            + ex.getMessage());
                                    System.err.println("[ERROR STOCK] " + ex.getMessage());
                                }
                            }
                        }
                        // Registrar puntos GANADOS por la venta SOLO para clientes NATURAL (boleta)
                        System.out.println("[DEBUG DIAGNOSTICO PUNTOS] clienteActual="
                                + (clienteActual != null ? clienteActual.getTipoCliente() : "null") + " | totalVenta="
                                + (ventaGuardada != null && ventaGuardada.getTotal() != null ? ventaGuardada.getTotal()
                                        : "null"));
                        if (clienteActual != null && "NATURAL".equalsIgnoreCase(clienteActual.getTipoCliente())) {
                            int puntosGanados = calcularPuntosPorVenta(ventaGuardada);
                            if (puntosGanados > 0) {
                                com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos movGanado = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                                movGanado.setClienteId(clienteActual.getId());
                                movGanado.setVentaId(ventaGuardada.getId());
                                movGanado.setTipo("GANADO");
                                movGanado.setPuntos(puntosGanados);
                                movGanado.setDescripcion("Puntos ganados por compra");
                                movGanado.setFecha(java.time.LocalDateTime.now());
                                movGanado.setUsuarioId(usuarioActual != null
                                        ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                        : null);
                                System.out.println(
                                        "[DEBUG FLUJO PUNTOS] Se va a guardar transacción de puntos: " + movGanado);
                                boolean puntosGuardados = transaccionPuntosRepository.save(movGanado);
                                if (!puntosGuardados) {
                                    System.err.println(
                                            "[ERROR PUNTOS] No se pudo guardar la transacción de puntos GANADO: "
                                                    + movGanado);
                                    mostrarMensaje(
                                            "Error al registrar los puntos ganados. Verifique los datos del cliente y la venta.");
                                }
                            }
                        }
                        // Registrar el uso de puntos si se usaron en la venta
                        if (puntosUsados[0] && puntosDescontados[0] > 0) {
                            com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos mov = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                            mov.setClienteId(clienteActual.getId());
                            mov.setVentaId(ventaGuardada.getId());
                            mov.setTipo("USADO");
                            mov.setPuntos(puntosDescontados[0]);
                            mov.setDescripcion("Redención de puntos en venta");
                            mov.setFecha(java.time.LocalDateTime.now());
                            mov.setUsuarioId(usuarioActual != null
                                    ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                    : null);
                            boolean puntosUsadosGuardados = transaccionPuntosRepository.save(mov);
                            if (!puntosUsadosGuardados) {
                                System.err.println(
                                        "[ERROR PUNTOS] No se pudo guardar la transacción de puntos USADO: " + mov);
                                mostrarMensaje(
                                        "Error al registrar el uso de puntos. Verifique los datos del cliente y la venta.");
                            }
                        }
                        comprobante.setVenta(ventaGuardada);
                        comprobanteRepository.save(comprobante);

                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio historial = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio();
                        historial.setVenta(ventaGuardada);
                        historial.setTipoCambio("CREACION");
                        historial.setMotivo("Venta registrada");
                        historial.setUsuario(usuarioActual);
                        historial.setFecha(java.time.LocalDateTime.now());
                        try {
                            historialCambioRepository.save(historial);
                        } catch (Exception ex) {
                            mostrarMensaje("Error al registrar historial de venta: " + ex.getMessage());
                            System.err.println("[ERROR HISTORIAL VENTA] " + ex.getMessage());
                        }
                        StringBuilder msg = new StringBuilder();
                        msg.append("Boleta electrónica enviada a SUNAT (demo)\n");
                        msg.append("Estado SUNAT: ").append(estadoSunat).append("\n");
                        if (!pdfUrl.isEmpty())
                            msg.append("PDF: ").append(pdfUrl).append("\n");
                        if (!xmlUrl.isEmpty())
                            msg.append("XML: ").append(xmlUrl).append("\n");
                        if (!cdrUrl.isEmpty())
                            msg.append("CDR: ").append(cdrUrl).append("\n");
                        mostrarMensaje(msg.toString());
                        carrito.clear();
                        actualizarTotales();
                        // Abrir ventana de acciones con PDF y datos del cliente (BOLETA)
                        try {
                            javafx.fxml.FXMLLoader accionesLoader = new javafx.fxml.FXMLLoader(
                                    getClass().getResource("/fxml/comprobante_acciones.fxml"));
                            javafx.scene.Parent accionesRoot = accionesLoader.load();
                            ComprobanteAccionesController accionesController = accionesLoader.getController();
                            accionesController.setDatos(pdfUrl, clienteActual.getNombreCompleto(),
                                    clienteActual.getEmail(), clienteActual.getTelefono());
                            javafx.stage.Stage accionesStage = new javafx.stage.Stage();
                            accionesController.setStage(accionesStage);
                            accionesStage.setTitle("Acciones sobre Comprobante Electrónico");
                            accionesStage.setScene(new javafx.scene.Scene(accionesRoot));
                            accionesStage.show();
                        } catch (Exception ex) {
                            System.err.println(
                                    "[ERROR BOLETA] No se pudo abrir la ventana de acciones: " + ex.getMessage());
                            mostrarMensaje("No se pudo abrir la ventana de acciones: " + ex.getMessage());
                        }
                    } catch (Exception ex) {
                        mostrarMensaje("Error en emisión de boleta electrónica: " + ex.getMessage());
                        System.err.println("[ERROR Emisión Boleta] " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                stage.showAndWait();
            } catch (Exception ex) {
                mostrarMensaje("Error en emisión de boleta electrónica: " + ex.getMessage());
                System.err.println("[ERROR Emisión Boleta] " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            mostrarMensaje("Tipo de comprobante no soportado o no seleccionado.");
        }
    }

    // Anula una venta y restaura el stock de los productos vendidos. Actualiza el
    // estado en la BD y registra el historial de cambio.
    private void anularVentaDesdeHistorial(Venta venta) {
        if (venta == null || "ANULADA".equalsIgnoreCase(venta.getEstado())) {
            mostrarMensaje("La venta ya está anulada.");
            return;
        }
        boolean esTicket = "TICKET".equalsIgnoreCase(venta.getTipoComprobante());
        try {
            // Cambiar estado de la venta
            venta.setEstado("ANULADA");
            // Asignar usuario actual para auditoría
            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                    .getUsuarioActual();
            venta.setUsuario(usuarioActual);
            ventaRepository.update(venta);

            // Restar puntos ganados por la venta anulada
            if (venta.getCliente() != null) {
                int puntosGanados = calcularPuntosPorVenta(venta);
                if (puntosGanados > 0) {
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos movExpirado = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                    movExpirado.setClienteId(venta.getCliente().getId());
                    movExpirado.setVentaId(venta.getId());
                    movExpirado.setTipo("EXPIRADO");
                    movExpirado.setPuntos(puntosGanados);
                    movExpirado.setDescripcion("Puntos expirados por anulación de venta");
                    movExpirado.setFecha(java.time.LocalDateTime.now());
                    movExpirado.setUsuarioId(usuarioActual != null
                            ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                            : null);
                    transaccionPuntosRepository.save(movExpirado);
                }
            }
            // Restaurar stock de cada producto consultando el stock real en BD y mostrando
            // logs
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    Producto producto = detalle.getProducto();
                    if (producto != null && producto.getId() != null) {
                        try {
                            // Stock antes de la anulación
                            java.util.Optional<Producto> productoBDAntes = productoRepository
                                    .findById(producto.getId().longValue());
                            int stockAntes = productoBDAntes.isPresent()
                                    && productoBDAntes.get().getStockActual() != null
                                            ? productoBDAntes.get().getStockActual()
                                            : 0;
                            int cantidadVendida = detalle.getCantidad();
                            int nuevoStock = stockAntes + cantidadVendida;
                            System.out.println("[ANULACION STOCK] Producto: " + producto.getNombre() + " (ID: "
                                    + producto.getId() + ") | Stock antes BD: " + stockAntes + " | Cantidad anulada: "
                                    + cantidadVendida + " | Nuevo stock: " + nuevoStock);
                            boolean actualizado = productoRepository.updateStock(producto.getId(), nuevoStock);
                            // Stock después de la anulación
                            java.util.Optional<Producto> productoBDAfter = productoRepository
                                    .findById(producto.getId().longValue());
                            int stockDespues = productoBDAfter.isPresent()
                                    && productoBDAfter.get().getStockActual() != null
                                            ? productoBDAfter.get().getStockActual()
                                            : 0;
                            if (!actualizado) {
                                System.err.println(
                                        "[ERROR UPDATE STOCK] No se actualizó el stock en BD para producto ID: "
                                                + producto.getId() + " | Cantidad: " + cantidadVendida
                                                + " | Nuevo stock: " + nuevoStock);
                            } else {
                                System.out.println(
                                        "[UPDATE STOCK OK] Stock actualizado correctamente en BD para producto ID: "
                                                + producto.getId() + " | Stock después BD: " + stockDespues);
                            }
                        } catch (Exception ex) {
                            System.err.println("[ERROR ANULACION STOCK] Producto: "
                                    + (producto.getNombre() != null ? producto.getNombre() : "(sin nombre)") + " (ID: "
                                    + producto.getId() + ") | Detalle: " + ex.getMessage());
                        }
                    } else {
                        System.err.println("[ERROR ANULACION STOCK] Producto nulo o sin ID en detalle venta");
                    }
                }
            }

            // Guardar historial de cambio de anulación
            com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio historial = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio();
            historial.setVenta(venta);
            historial.setTipoCambio("ANULACION");
            historial.setMotivo("Venta anulada desde historial");
            historial.setUsuario(usuarioActual);
            historial.setFecha(java.time.LocalDateTime.now());
            historialCambioRepository.save(historial);

            // Enviar anulación a NubeFacT/SUNAT
            if (esTicket) {
                mostrarMensaje("Venta tipo ticket anulada correctamente.");
            } else {
                java.util.Properties nubefactProps = new java.util.Properties();
                try (java.io.InputStream input = new java.io.FileInputStream(
                        "src/main/resources/venta_nubefact.properties")) {
                    nubefactProps.load(input);
                }
                String apiUrl = nubefactProps.getProperty("api.url");
                String apiToken = nubefactProps.getProperty("api.token");
                java.util.LinkedHashMap<String, Object> jsonAnulacion = new java.util.LinkedHashMap<>();
                jsonAnulacion.put("operacion", "generar_anulacion");
                int tipoComprobante = "FACTURA".equalsIgnoreCase(venta.getTipoComprobante()) ? 1 : 2;
                jsonAnulacion.put("tipo_de_comprobante", tipoComprobante);
                String serie = "";
                if (venta.getComprobante() != null && venta.getComprobante().getSerie() != null
                        && !venta.getComprobante().getSerie().isEmpty()) {
                    serie = venta.getComprobante().getSerie();
                } else if (venta.getSerie() != null && !venta.getSerie().isEmpty()) {
                    serie = venta.getSerie();
                } else {
                    mostrarMensaje("No se encontró la serie del comprobante para la anulación. Verifique la venta.");
                    return;
                }
                String numero = null;
                if (venta.getComprobante() != null && venta.getComprobante().getNumero() != null
                        && !venta.getComprobante().getNumero().isEmpty()) {
                    numero = venta.getComprobante().getNumero();
                } else if (venta.getNumeroBoleta() != null && !venta.getNumeroBoleta().isEmpty()) {
                    numero = venta.getNumeroBoleta();
                } else {
                    mostrarMensaje("No se encontró el número de comprobante para la anulación. Verifique la venta.");
                    return;
                }
                if (numero != null) {
                    numero = numero.replaceFirst("^0+", "");
                }
                jsonAnulacion.put("serie", serie);
                jsonAnulacion.put("numero", numero);
                jsonAnulacion.put("motivo", "ERROR DEL SISTEMA");
                jsonAnulacion.put("codigo_unico", "");
                String json = new com.google.gson.Gson().toJson(jsonAnulacion);
                String respuestaNubeFact = ComprobanteUtils.enviarFacturaNubeFact(json, apiUrl, apiToken);
                if (respuestaNubeFact != null) {
                    System.out.println("[NUBEFACT ANULACION] Respuesta: " + respuestaNubeFact);
                    com.google.gson.JsonObject jsonResp = null;
                    try {
                        jsonResp = new com.google.gson.JsonParser().parse(respuestaNubeFact).getAsJsonObject();
                    } catch (Exception e) {
                        mostrarMensaje("No se pudo procesar la respuesta de NubeFacT. Respuesta: " + respuestaNubeFact);
                        return;
                    }
                    boolean aceptadaPorSunat = false;
                    if (jsonResp.has("aceptada_por_sunat")) {
                        aceptadaPorSunat = jsonResp.get("aceptada_por_sunat").getAsBoolean();
                    }
                    String enlace = jsonResp.has("enlace") ? jsonResp.get("enlace").getAsString() : "";
                    String ticket = "";
                    if (jsonResp.has("sunat_ticket_numero") && !jsonResp.get("sunat_ticket_numero").isJsonNull()) {
                        try {
                            ticket = jsonResp.get("sunat_ticket_numero").getAsString();
                        } catch (Exception ex) {
                            ticket = "";
                        }
                    }
                    String estadoSunat = aceptadaPorSunat ? "Aceptada"
                            : (ticket.isEmpty() ? "Pendiente" : "En proceso");
                    String mensaje = "Venta anulada correctamente en NubeFacT. Estado SUNAT: " + estadoSunat
                            + ".\nEnlace: "
                            + enlace;
                    if (!ticket.isEmpty()) {
                        mensaje += "\nTicket SUNAT: " + ticket;
                    }
                    mostrarMensaje(mensaje);
                } else {
                    mostrarMensaje("No se pudo anular la venta en SUNAT/NubeFacT. Respuesta: null");
                }
            }
        } catch (Exception ex) {
            mostrarMensaje("Error al anular la venta: " + ex.getMessage());
        }
    }
}
