package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
                    DetalleVenta detalle = getTableView().getItems().get(getIndex());
                    tablaCarrito.getSelectionModel().select(detalle);
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
        // Mostrar todos los productos al hacer click en el buscador
        txtBuscarProducto.setOnMouseClicked(e -> {
            ObservableList<Producto> todos = FXCollections.observableArrayList(productos);
            listSugerencias.setItems(todos);
            listSugerencias.setVisible(!todos.isEmpty());
            listSugerencias.setManaged(!todos.isEmpty());
            btnAgregarProducto.setDisable(true);
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
            // Habilitar el botón solo si el texto coincide exactamente con algún producto
            String texto = newText.trim().toLowerCase();
            boolean existe = productos.stream().anyMatch(
                    p -> p.getNombre().toLowerCase().equals(texto) || p.getCodigo().toLowerCase().equals(texto));
            btnAgregarProducto.setDisable(!existe);
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
            if (newSel != null) {
                txtBuscarProducto.setText(newSel.getNombre());
                limpiarSugerencias();
                btnAgregarProducto.setDisable(false);
            } else {
                btnAgregarProducto.setDisable(true);
            }
        });
        btnAgregarProducto.setDisable(true);
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

        // Personalizar visualización de clientes en ComboBox: DNI - Nombres y Apellidos
        comboClientes.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDni() + " - " + item.getNombreCompleto());
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
                    setText(item.getDni() + " - " + item.getNombreCompleto());
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
    }

    private void cargarProductos() {
        productos.clear();
        productos.addAll(productoRepository.findAll());
        listSugerencias.setItems(FXCollections.observableArrayList());
        listSugerencias.setVisible(false);
        listSugerencias.setManaged(false);
    }

    private void agregarProductoDesdeBuscador() {
        // Usar variable final para producto seleccionado
        final Producto seleccionado;
        // Si hay selección en el ListView, usarla
        if (!listSugerencias.getItems().isEmpty() && listSugerencias.getSelectionModel().getSelectedIndex() >= 0) {
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
        clientes.removeIf(c -> "00000000".equals(c.getDni()) || c.getId() == 1);
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
        // Delegar al repositorio de comprobantes
        return comprobanteRepository.obtenerSiguienteNumeroBoleta(serie);
    }

    private void confirmarVenta() {
        final Cliente[] clienteRef = new Cliente[1];
        clienteRef[0] = comboClientes.getValue();
        final String metodoPago = comboPago.getValue();
        if (metodoPago == null || carrito.isEmpty()) {
            mostrarMensaje("Completa todos los datos y agrega productos al carrito.");
            return;
        }
        // Si no hay cliente seleccionado, buscar cliente genérico por DNI en la BD
        if (clienteRef[0] == null) {
            com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository clienteRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository();
            java.util.Optional<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> optGenerico = clienteRepo
                    .findByDni("00000000");
            if (optGenerico.isPresent()) {
                clienteRef[0] = optGenerico.get();
            } else {
                mostrarMensaje(
                        "No se encontró el cliente genérico en la base de datos. Verifique que exista el registro con DNI 00000000.");
                return;
            }
        }

        // Calcular totales antes de crear boleta y venta
        final java.math.BigDecimal subtotal = carrito.stream()
                .map(com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        final java.math.BigDecimal descuento = carrito.stream()
                .map(com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta::getDescuento)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        final java.math.BigDecimal igv = subtotal.multiply(new java.math.BigDecimal("0.18"));
        final java.math.BigDecimal total = subtotal.subtract(descuento).add(igv);

        // Obtener usuario actual y sucursal activa
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService
                .getUsuarioActual();
        com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository sucursalRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository();
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Sucursal> sucursales = sucursalRepo
                .findAllActivas();
        com.farmaciavictoria.proyectopharmavictoria.model.Sucursal sucursal = !sucursales.isEmpty() ? sucursales.get(0)
                : null;

        // Construir la boleta usando el BoletaBuilder
        com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.Boleta boleta = new com.farmaciavictoria.proyectopharmavictoria.model.Comprobante.BoletaBuilder()
                .conCliente(clienteRef[0])
                .conProductos(new java.util.ArrayList<>(carrito))
                .conTotales(subtotal.doubleValue(), igv.doubleValue(), total.doubleValue())
                .build();

        // Lógica para obtener la serie y el siguiente número de boleta
        String serie = "B001";
        String numero = obtenerSiguienteNumeroBoleta(serie);

        // Vista previa profesional con callback para imprimir y registrar
        String nombreFarmacia = sucursal != null ? sucursal.getNombre() : "BOTICA PHARMAVICTORIA S.A.C.";
        String ruc = "10468894501";
        String direccion = sucursal != null ? sucursal.getDireccion() : "Jr. Lima N° 456 – Huancayo";
        String cajero = usuarioActual != null ? usuarioActual.getNombres() + " " + usuarioActual.getApellidos()
                : "[Cajero]";
        String metodoPagoStr = metodoPago;
        String montoEnLetras = convertirMontoALetras(total.doubleValue());
        String fechaStr = java.time.LocalDate.now().toString();
        String horaStr = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

        com.farmaciavictoria.proyectopharmavictoria.view.BoletaPreviewDialog preview = new com.farmaciavictoria.proyectopharmavictoria.view.BoletaPreviewDialog.Builder()
                .boleta(boleta)
                .cajero(cajero)
                .nombreFarmacia(nombreFarmacia)
                .ruc(ruc)
                .direccion(direccion)
                .serie(serie)
                .numero(numero)
                .metodoPago(metodoPagoStr)
                .montoEnLetras(montoEnLetras)
                .fecha(fechaStr)
                .hora(horaStr)
                .onConfirm(b -> {
                    try {
                        String pdfPath = "boleta_temp.pdf";
                        com.farmaciavictoria.proyectopharmavictoria.print.PdfPrintStrategy pdfGen = new com.farmaciavictoria.proyectopharmavictoria.print.PdfPrintStrategy();
                        pdfGen.generarPDF(b, pdfPath, nombreFarmacia, ruc, direccion, serie, numero, cajero,
                                metodoPagoStr, montoEnLetras, fechaStr, horaStr);
                        pdfGen.imprimirPDF(pdfPath);
                    } catch (Exception ex) {
                        System.err.println("Error al imprimir PDF: " + ex.getMessage());
                    }
                    Venta venta = new Venta();
                    venta.setCliente(clienteRef[0]);
                    venta.setDetalles(new ArrayList<>(carrito));
                    venta.setTipoPago(metodoPago);
                    venta.setSubtotal(subtotal);
                    venta.setDescuentoMonto(descuento);
                    venta.setIgvMonto(igv);
                    venta.setTotal(total);
                    venta.setFechaVenta(java.time.LocalDateTime.now());
                    venta.setCreatedAt(java.time.LocalDateTime.now());
                    venta.setUpdatedAt(java.time.LocalDateTime.now());
                    venta.setEstado("REALIZADA");
                    venta.setTipoComprobante(comboComprobante.getValue());
                    venta.setUsuario(usuarioActual);
                    venta.setSucursal(sucursal);
                    venta.setNumeroBoleta(numero);
                    venta.setSerie(serie);
                    // Crear comprobante y asignar serie/numero
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante comprobante = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante();
                    comprobante.setTipo("BOLETA");
                    comprobante.setSerie(serie);
                    comprobante.setNumero(numero);
                    comprobante.setEstadoSunat("GENERADO");
                    comprobante.setFechaEmision(java.time.LocalDateTime.now());
                    comprobante.setVenta(venta);
                    venta.setComprobante(comprobante);
                    try {
                        ventaService.registrarVenta(venta);
                        mostrarMensaje("Venta registrada e impresa correctamente.");
                        carrito.clear();
                        actualizarTotales();
                    } catch (Exception ex) {
                        mostrarMensaje("Error al registrar la venta: " + ex.getMessage());
                    }
                })
                .build();
        boolean confirmado = preview.mostrar();
        if (!confirmado) {
            mostrarMensaje("Venta cancelada antes de imprimir.");
        }
    }

    private void anularVenta() {
        // Lógica mínima: solo muestra mensaje (implementación real requiere selección
        // de venta)
        mostrarMensaje("Funcionalidad de anulación pendiente de implementación.");
    }

    private void mostrarMensaje(String msg) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Convierte un monto numérico a letras en formato "CINCO CON 70/100 SOLES"
    private String convertirMontoALetras(double monto) {
        final String[] UNIDADES = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE",
                "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE", "DIECIOCHO",
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
}
