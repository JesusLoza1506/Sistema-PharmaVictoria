package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
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
    @FXML
    private TextField txtTipoCliente;
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
    private Button btnAnularVenta;
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
        // Íconos editar/eliminar con fondo blanco y borde verde/rojo
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
                String nombre = ("EMPRESARIAL".equals(tipo)) ? newCliente.getRazonSocial()
                        : newCliente.getNombreCompleto();
                txtTipoCliente.setText(tipo != null ? tipo : "");
                txtDocumento.setText(doc != null ? doc : "");
                txtNombreRazon.setText(nombre != null ? nombre : "");
                panelDatosCliente.setVisible(true);
                panelDatosCliente.setManaged(true);
            } else {
                txtTipoCliente.clear();
                txtDocumento.clear();
                txtNombreRazon.clear();
                panelDatosCliente.setVisible(false);
                panelDatosCliente.setManaged(false);
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
                        System.out.println("[DEBUG ListCell] Producto renderizado: " + codigo + " | " + nombre
                                + " | Stock: " + stock);
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
        comboComprobante.setItems(FXCollections.observableArrayList("BOLETA", "FACTURA"));
        comboComprobante.setPromptText("Selecciona comprobante");
        comboComprobante.setVisibleRowCount(2);
        tablaCarrito.setItems(carrito);
        btnConfirmarVenta.setOnAction(e -> confirmarVenta());
        btnAnularVenta.setOnAction(e -> anularVenta());
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
                    String nombre = ("EMPRESARIAL".equals(tipo)) ? item.getRazonSocial() : item.getNombreCompleto();
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
                    String nombre = ("EMPRESARIAL".equals(tipo)) ? item.getRazonSocial() : item.getNombreCompleto();
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
        // Usar variable final para producto seleccionado
        final Producto seleccionado;
        // Si hay selección en el ListView, usarla solo si la lista no está vacía y el
        // índice es válido
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
        // Lógica mínima: solo muestra mensaje (implementación real requiere selección
        // de venta)
        mostrarMensaje("Funcionalidad de anulación pendiente de implementación.");
    }

    private void mostrarMensaje(String msg) {

        // Solo mostrar mensajes amigables, nunca logs técnicos ni errores de depuración
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
                mostrarMensaje(
                        "No se encontró el cliente genérico en la base de datos. Verifique que exista el registro con documento 00000000.");
                return;
            }
        }
        // Calcular totales antes de crear boleta y venta
        final java.math.BigDecimal subtotal = carrito.stream().map(DetalleVenta::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        final java.math.BigDecimal descuento = carrito.stream().map(DetalleVenta::getDescuento)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        final java.math.BigDecimal igv = subtotal.multiply(new java.math.BigDecimal("0.18"));
        final java.math.BigDecimal total = subtotal.subtract(descuento).add(igv);
        String tipoComprobante = comboComprobante.getValue();
        if ("FACTURA".equalsIgnoreCase(tipoComprobante)) {
            // --- FLUJO FACTURA ELECTRÓNICA SUNAT ---
            // Si el cliente no tiene datos completos de RUC, pedirlos manualmente
            class DatosRuc {
                String ruc, razonSocial, direccion, email;
            }
            DatosRuc datosRuc = new DatosRuc();
            try {
                System.out.println("[DEBUG RUC Dialog] Iniciando carga de FXML ruc_completo_dialog.fxml");
                javafx.fxml.FXMLLoader rucLoader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/ruc_completo_dialog.fxml"));
                System.out.println("[DEBUG RUC Dialog] FXMLLoader creado");
                javafx.scene.control.DialogPane dialogPane = rucLoader.load();
                System.out.println("[DEBUG RUC Dialog] DialogPane cargado");
                com.farmaciavictoria.proyectopharmavictoria.RucCompletoDialogController rucController = rucLoader
                        .getController();
                System.out.println("[DEBUG RUC Dialog] Controlador obtenido: " + rucController);
                javafx.scene.control.Dialog<Boolean> dialog = new javafx.scene.control.Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Completar datos de cliente para FACTURA");
                dialog.setHeaderText(
                        "El cliente seleccionado no tiene datos completos de RUC. Completa todos los campos.");

                // Validación antes de cerrar el diálogo
                javafx.scene.control.Button okButton = null;
                for (ButtonType bt : dialogPane.getButtonTypes()) {
                    if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        okButton = (javafx.scene.control.Button) dialogPane.lookupButton(bt);
                        break;
                    }
                }
                if (okButton != null) {
                    okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                        String rucIngresado = rucController.getRuc();
                        String razonIngresada = rucController.getRazonSocial();
                        String direccionIngresada = rucController.getDireccion();
                        String emailIngresado = rucController.getEmail();
                        boolean datosValidos = true;
                        if (rucIngresado == null || rucIngresado.trim().isEmpty() || rucIngresado.length() != 11) {
                            rucController.showError("El RUC debe tener 11 dígitos.");
                            System.err.println("[ERROR VALIDACIÓN RUC] RUC inválido: '" + rucIngresado + "'");
                            event.consume();
                            datosValidos = false;
                        }
                        if (razonIngresada == null || razonIngresada.trim().isEmpty()) {
                            rucController.showError("La razón social es obligatoria.");
                            System.err.println("[ERROR VALIDACIÓN RUC] Razón social vacía");
                            event.consume();
                            datosValidos = false;
                        }
                        if (direccionIngresada == null || direccionIngresada.trim().isEmpty()) {
                            rucController.showError("La dirección es obligatoria.");
                            System.err.println("[ERROR VALIDACIÓN RUC] Dirección vacía");
                            event.consume();
                            datosValidos = false;
                        }
                        if (datosValidos) {
                            rucController.clearError();
                            datosRuc.ruc = rucIngresado;
                            datosRuc.razonSocial = razonIngresada;
                            datosRuc.direccion = direccionIngresada;
                            datosRuc.email = emailIngresado;
                            System.out.println("[DEBUG VALIDACIÓN RUC] Datos válidos: RUC=" + rucIngresado
                                    + ", Razón Social=" + razonIngresada + ", Dirección=" + direccionIngresada
                                    + ", Email=" + emailIngresado);
                        }
                    });
                } else {
                    System.err.println(
                            "[ERROR RUC Dialog] El botón OK no está disponible en el DialogPane. El diálogo se mostrará sin validación extra.");
                }

                dialog.setResultConverter(buttonType -> {
                    // Leer los datos directamente del controlador y loguear el tipo de botón
                    System.out.println("[DEBUG RUC Dialog] ResultConverter: buttonType=" + buttonType + " ("
                            + (buttonType != null ? buttonType.getButtonData() : "null") + ")");
                    if (buttonType != null
                            && buttonType.getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                        String rucIngresado = rucController.getRuc();
                        String razonIngresada = rucController.getRazonSocial();
                        String direccionIngresada = rucController.getDireccion();
                        String emailIngresado = rucController.getEmail();
                        boolean datosValidos = rucIngresado != null && razonIngresada != null
                                && direccionIngresada != null && !rucIngresado.trim().isEmpty()
                                && !razonIngresada.trim().isEmpty() && !direccionIngresada.trim().isEmpty()
                                && rucIngresado.length() == 11;
                        System.out.println("[DEBUG RUC Dialog] ResultConverter: ruc=" + rucIngresado + ", razon="
                                + razonIngresada + ", direccion=" + direccionIngresada + ", email=" + emailIngresado
                                + ", datosValidos=" + datosValidos);
                        if (datosValidos) {
                            datosRuc.ruc = rucIngresado;
                            datosRuc.razonSocial = razonIngresada;
                            datosRuc.direccion = direccionIngresada;
                            datosRuc.email = emailIngresado;
                            return true;
                        } else {
                            System.err.println(
                                    "[ERROR RUC Dialog] Datos inválidos en ResultConverter aunque se presionó OK");
                        }
                    } else {
                        System.out.println("[DEBUG RUC Dialog] ResultConverter: botón no es OK_DONE, se devuelve null");
                    }
                    return null;
                });
                System.out.println("[DEBUG RUC Dialog] Mostrando diálogo");
                java.util.Optional<Boolean> result = dialog.showAndWait();
                System.out.println("[DEBUG RUC Dialog] Result: " + result);
                // Si el usuario presionó OK y los datos son válidos, continuar con la vista
                // previa
                if (!result.isPresent() || result.get() == null) {
                    System.err.println(
                            "[ERROR RUC Dialog] Optional vacío, null o datos incompletos (diálogo cerrado sin aceptar)");
                    System.err.println("[ERROR RUC Dialog] Estado final: ruc='" + datosRuc.ruc + "', razonSocial='"
                            + datosRuc.razonSocial + "', direccion='" + datosRuc.direccion + "', email='"
                            + datosRuc.email + "'");
                    mostrarMensaje("No se completaron los datos. No se puede emitir la factura.");
                    return;
                }
                // Si todo está OK, continuar con la vista previa
            } catch (Exception ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(ex.toString()).append("\n");
                for (StackTraceElement ste : ex.getStackTrace()) {
                    sb.append("    at ").append(ste.toString()).append("\n");
                }
                mostrarMensaje("Error mostrando diálogo de datos de RUC:\n" + sb.toString());
                System.err.println("[ERROR RUC Dialog] " + sb.toString());
                return;
            }
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/vista_previa_comprobante.fxml"));
                javafx.scene.Parent root = loader.load();
                VistaPreviaComprobanteController previewController = loader.getController();
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Vista Previa de Factura Electrónica");
                stage.setScene(new javafx.scene.Scene(root));
                // Convertir Cliente y DetalleVenta locales a tipos PharmavictoriaApplication
                com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.Cliente clienteJsonPreview = new com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.Cliente();
                clienteJsonPreview.tipo_documento = "6";
                clienteJsonPreview.numero_documento = datosRuc.ruc;
                clienteJsonPreview.razon_social = datosRuc.razonSocial;
                clienteJsonPreview.direccion = datosRuc.direccion;
                // Si el modelo lo permite, también email
                // clienteJsonPreview.email = email;
                javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.DetalleVenta> detallesPreview = javafx.collections.FXCollections
                        .observableArrayList();
                for (DetalleVenta d : carrito) {
                    com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.DetalleVenta det = new com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication.DetalleVenta();
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
                // Usar la serie demo permitida por NubeFacT
                String serieFactura = "FFF1";
                String numeroFactura = obtenerSiguienteNumeroFactura(serieFactura);
                previewController.setDatos(clienteJsonPreview, "FACTURA", serieFactura, numeroFactura,
                        total.toPlainString(), detallesPreview);
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
                        factura.put("cliente_numero_de_documento", datosRuc.ruc);
                        factura.put("cliente_denominacion", datosRuc.razonSocial);
                        factura.put("cliente_direccion", datosRuc.direccion);
                        factura.put("cliente_email", datosRuc.email != null ? datosRuc.email : "");
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
                        factura.put("total", total.doubleValue());
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
                            item.put("codigo_producto_sunat", "10000000"); // Si no tienes el código SUNAT, pon uno
                                                                           // genérico
                            item.put("descripcion", d.getProducto().getNombre());
                            item.put("cantidad", d.getCantidad());
                            // Precio unitario sin IGV
                            double valorUnitario = d.getPrecioUnitario().doubleValue();
                            item.put("valor_unitario", valorUnitario);
                            // Precio unitario con IGV
                            double precioUnitarioConIGV = Math.round(valorUnitario * 1.18 * 100.0) / 100.0;
                            item.put("precio_unitario", precioUnitarioConIGV);
                            item.put("descuento", "");
                            item.put("subtotal", valorUnitario * d.getCantidad());
                            item.put("tipo_de_igv", 1);
                            double igvDetalle = Math.round(valorUnitario * d.getCantidad() * 0.18 * 100.0) / 100.0;
                            item.put("igv", igvDetalle);
                            // Total de la línea: cantidad × precio_unitario (con IGV)
                            double totalLinea = Math.round(precioUnitarioConIGV * d.getCantidad() * 100.0) / 100.0;
                            item.put("total", totalLinea);
                            item.put("anticipo_regularizacion", false);
                            item.put("anticipo_documento_serie", "");
                            item.put("anticipo_documento_numero", "");
                            items.add(item);
                        }
                        factura.put("items", items);
                        // Guias (opcional)
                        factura.put("guias", new java.util.ArrayList<>());
                        // Venta al crédito (opcional)
                        factura.put("venta_al_credito", new java.util.ArrayList<>());
                        String jsonFactura = new com.google.gson.Gson().toJson(factura);
                        System.out.println("[DEBUG JSON FACTURA] JSON generado para NubeFacT:\n" + jsonFactura);
                        String apiUrl = "https://api.nubefact.com/api/v1/b1f7ac80-5d5e-4fd9-8c8d-7b00c2638da0";
                        String apiToken = "3e15b81cab1b46dc9881d4979e343a273d7abde7bb3e406686eb92f84f6bebd1";
                        String respuesta = com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication
                                .enviarFacturaNubeFact(jsonFactura, apiUrl, apiToken);
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
                            pdfUrl = json.has("enlace_pdf") ? json.get("enlace_pdf").getAsString() : "";
                            xmlUrl = json.has("enlace_xml") ? json.get("enlace_xml").getAsString() : "";
                            cdrUrl = json.has("enlace_cdr") ? json.get("enlace_cdr").getAsString() : "";
                        } catch (Exception ex) {
                            mostrarMensaje("Error procesando respuesta de NubeFacT: " + ex.getMessage());
                            System.err.println("[ERROR NubeFacT] " + ex.getMessage());
                            ex.printStackTrace();
                        }
                        // 1. Crear y guardar la venta antes de crear el comprobante
                        Venta venta = new Venta();
                        venta.setCliente(clienteActual);
                        // Asignar el usuario logueado
                        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                                .getUsuarioActual();
                        venta.setUsuario(usuarioActual);
                        venta.setSubtotal(subtotal);
                        venta.setDescuentoMonto(descuento);
                        venta.setIgvMonto(igv);
                        venta.setTotal(total);
                        venta.setTipoPago(metodoPago);
                        venta.setTipoComprobante("FACTURA");
                        venta.setNumeroBoleta(numeroFactura);
                        venta.setSerie(serieFactura);
                        venta.setFechaVenta(java.time.LocalDateTime.now());
                        venta.setEstado("REALIZADA");
                        venta.setObservaciones("");
                        venta.setCreatedAt(java.time.LocalDateTime.now());
                        venta.setUpdatedAt(java.time.LocalDateTime.now());
                        venta.setDetalles(new java.util.ArrayList<>(carrito));
                        // Guardar venta y recuperar el ID generado
                        Venta ventaGuardada = ventaRepository.save(venta);
                        if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                            mostrarMensaje("Error al guardar la venta. No se pudo obtener el ID generado.");
                            return;
                        }
                        // Guardar cada detalle en la tabla detalle_ventas
                        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
                            // Asignar la venta con el ID correcto
                            detalle.setVenta(ventaGuardada);
                            try {
                                detalleVentaRepository.save(detalle);
                            } catch (Exception ex) {
                                mostrarMensaje("Error al guardar detalle de venta: " + ex.getMessage());
                                System.err.println("[ERROR DETALLE VENTA] " + ex.getMessage());
                            }
                            // Descontar stock de cada producto vendido
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
                                    // No descontar ni actualizar si el stock es negativo
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
                        // 2. Crear comprobante y asociar la venta guardada
                        Comprobante comprobante = new Comprobante();
                        comprobante.setTipo("FACTURA");
                        comprobante.setSerie("FFF1");
                        comprobante.setNumero(obtenerSiguienteNumeroFactura("FFF1"));
                        comprobante.setHashSunat(hashSunat);
                        comprobante.setEstadoSunat("GENERADO");
                        comprobante.setFechaEmision(java.time.LocalDateTime.now());
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
            // Aquí puedes implementar el flujo de boleta si lo necesitas
            // Por ejemplo, mostrar la vista previa y solo emitir tras confirmación
        } else {
            mostrarMensaje("Tipo de comprobante no soportado o no seleccionado.");
        }
    }

}
