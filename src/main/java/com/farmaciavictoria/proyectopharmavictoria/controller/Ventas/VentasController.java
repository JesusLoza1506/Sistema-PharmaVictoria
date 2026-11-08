package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.AnulacionVentaService;
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.PuntosVenta;
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
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.TotalesCarrito;
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class VentasController implements Initializable {
    // --- MIXTO: Controles de pago mixto ---
    @FXML
    private HBox panelPagoMixto;
    @FXML
    private TextField txtMontoEfectivo;
    @FXML
    private TextField txtMontoTarjeta;
    @FXML
    private TextField txtMontoTransferencia;
    @FXML
    private CheckBox chkEfectivo;
    @FXML
    private CheckBox chkTarjeta;
    @FXML
    private CheckBox chkTransferencia;
    @FXML
    private Label lblTotalMixto;

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

    private final AnulacionVentaService anulacionVenta;
    private EmisionComprobantesVenta emisionComprobantes;
    private PanelPagoMixto pagoMixto;

    public VentasController() {
        this.anulacionVenta = new AnulacionVentaService(ventaRepository, productoRepository,
                transaccionPuntosRepository, historialCambioRepository, this::mostrarMensaje);
        this.ventaService = new VentaServiceImpl(
                ventaRepository,
                detalleVentaRepository,
                comprobanteRepository,
                historialCambioRepository,
                productoRepository);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Abrir lista de clientes al hacer clic en el campo de texto
        comboClientes.getEditor().setOnMouseClicked(e -> {
            comboClientes.show();
        });

        // Configuración inicial y estilos para tabla de ventas
        btnHistorial.setOnAction(e -> mostrarHistorialVentas24h());
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #26a69a; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, #26a69a, 16, 0.4, 0, 2);");
        tablaCarrito.getStylesheets().clear();
        tablaCarrito.getStylesheets().add(getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm());
        tablaCarrito.getStyleClass().setAll("productos-table", "ventas-table");
        tablaCarrito.setPlaceholder(new Label("Tabla sin contenido. Busca y agrega productos al carrito."));

        pagoMixto = new PanelPagoMixto(panelPagoMixto, txtMontoEfectivo, txtMontoTarjeta,
                txtMontoTransferencia, chkEfectivo, chkTarjeta, chkTransferencia, lblTotalMixto, lblTotal);
        pagoMixto.configurar(comboPago);
        emisionComprobantes = new EmisionComprobantesVenta(carrito, ventaRepository, detalleVentaRepository,
                comprobanteRepository, historialCambioRepository, productoRepository, transaccionPuntosRepository,
                pagoMixto, this::mostrarMensaje, this::actualizarTotales);

        // --- Configuración de Columnas de la Tabla Carrito (Parte del initialize) ---

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

        // Columna de edición
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

        // Columna de eliminación
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

        // Buscar productos al escribir (lógica de autocompletado/sugerencias)
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
                        String codigo = item.getCodigo() != null ? item.getCodigo() : "";
                        String nombre = item.getNombre() != null ? item.getNombre() : "";
                        Integer stock = item.getStockActual() != null ? item.getStockActual() : 0;
                        java.math.BigDecimal precio = item.getPrecioVenta() != null ? item.getPrecioVenta()
                                : java.math.BigDecimal.ZERO;
                        String precioStr = "S/. " + precio.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();

                        javafx.scene.control.Label lblCodigo = new javafx.scene.control.Label(codigo);
                        lblCodigo.setStyle(
                                "-fx-font-weight: bold; -fx-text-fill: #388e3c; -fx-min-width: 70px; -fx-font-size: 16px;");
                        javafx.scene.control.Label lblNombre = new javafx.scene.control.Label(nombre);
                        lblNombre.setStyle(
                                "-fx-text-fill: #222; -fx-font-size: 16px; -fx-min-width: 220px; -fx-font-weight: bold;");
                        javafx.scene.control.Label lblStock = new javafx.scene.control.Label("Stock: " + stock);
                        lblStock.setStyle(
                                "-fx-font-weight: bold; -fx-text-fill: #0288d1; -fx-min-width: 80px; -fx-font-size: 16px;");
                        javafx.scene.control.Label lblPrecio = new javafx.scene.control.Label(precioStr);
                        lblPrecio.setStyle(
                                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d32f2f; -fx-padding: 0 0 0 12px;");

                        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10, lblCodigo, lblNombre, lblStock,
                                lblPrecio);
                        hbox.setStyle("-fx-padding: 4 8 4 8; -fx-background-color: transparent;");
                        setGraphic(hbox);
                        setText("");

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
            ObservableList<Producto> items = listSugerencias.getItems();
            int idx = listSugerencias.getSelectionModel().getSelectedIndex();

            // Validar que la lista no esté vacía y el índice sea válido
            if (items == null || items.isEmpty() || idx < 0 || idx >= items.size()) {
                listSugerencias.getSelectionModel().clearSelection();
                btnAgregarProducto.setDisable(true);
                System.err.println("[DEBUG ListView] Selección inválida o lista vacía. Selección cancelada.");
                return;
            }

            if (newSel != null) {
                txtBuscarProducto.setText(newSel.getNombre());
                limpiarSugerencias();
                btnAgregarProducto.setDisable(false);

                // Mostrar advertencia si el producto requiere receta o es controlado
                boolean requiereReceta = Boolean.TRUE.equals(newSel.getRequiereReceta());
                boolean esControlado = Boolean.TRUE.equals(newSel.getEsControlado());
                if (requiereReceta || esControlado) {
                    StringBuilder mensaje = new StringBuilder("Advertencia: ");
                    if (requiereReceta && esControlado) {
                        mensaje.append("Este producto es CONTROLADO y REQUIERE RECETA médica.");
                    } else if (requiereReceta) {
                        mensaje.append("Este producto REQUIERE RECETA médica.");
                    } else if (esControlado) {
                        mensaje.append("Este producto es CONTROLADO.");
                    }
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Advertencia de producto restringido");
                    alerta.setHeaderText(null);
                    alerta.setContentText(mensaje.toString());
                    alerta.showAndWait();
                }
            } else {
                btnAgregarProducto.setDisable(true);
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

        // ----------------------------------------------------------------------
        // --- Configuración Final y Carga de Datos ---
        // ----------------------------------------------------------------------

        // Cargar datos y configurar ComboBoxes
        cargarProductos();
        cargarClientes();

        comboPago.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "TRANSFERENCIA", "MIXTO"));
        comboPago.setPromptText("Seleccionar método de pago");

        comboComprobante.setItems(FXCollections.observableArrayList("BOLETA", "FACTURA", "TICKET"));
        comboComprobante.setPromptText("Selecciona comprobante");
        comboComprobante.setVisibleRowCount(2);

        tablaCarrito.setItems(carrito);

        // Acciones de botones
        btnConfirmarVenta.setOnAction(e -> confirmarVenta());
        btnAgregarProducto.setOnAction(e -> agregarProductoDesdeBuscador());

        // Asegurar que los totales se calculen al inicio
        actualizarTotales();

        // Personalizar visualización de clientes en ComboBox: tipo, documento y
        // nombre/razón social
        comboClientes.setEditable(true);

        // Guardar la lista original de clientes para filtrar
        final ObservableList<Cliente> clientesOriginales = FXCollections.observableArrayList();
        java.util.List<Cliente> clientesCargados = clienteRepository.findAll(0, 100);
        clientesCargados.removeIf(c -> "00000000".equals(c.getDocumento()));
        clientesOriginales.addAll(clientesCargados);
        comboClientes.setItems(FXCollections.observableArrayList(clientesOriginales));

        // Configuración visual de las celdas del ComboBox (desplegable)
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

        // Configuración visual de la celda de botón del ComboBox (seleccionado)
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

        // Filtro dinámico en el editor del ComboBox (Autocompletado de clientes)
        comboClientes.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                comboClientes.setItems(FXCollections.observableArrayList(clientesOriginales));
                comboClientes.setVisibleRowCount(8);
            } else {
                String filtro = newText.toLowerCase();
                ObservableList<Cliente> filtrados = clientesOriginales.stream()
                        .filter(c -> c.getNombreCompleto().toLowerCase().contains(filtro)
                                || c.getDocumento().toLowerCase().contains(filtro)
                                || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(filtro)))
                        .collect(java.util.stream.Collectors.toCollection(FXCollections::observableArrayList));
                comboClientes.setItems(filtrados);

                // Ajustar altura del popup según resultados
                if (filtrados.size() <= 1) {
                    comboClientes.setVisibleRowCount(filtrados.size() == 0 ? 1 : 2);
                } else if (filtrados.size() < 8) {
                    comboClientes.setVisibleRowCount(filtrados.size());
                } else {
                    comboClientes.setVisibleRowCount(8);
                }
            }
        });
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
        ObservableList<Producto> items = listSugerencias.getItems();
        int idx = listSugerencias.getSelectionModel().getSelectedIndex();
        if (items != null && !items.isEmpty() && idx >= 0 && idx < items.size()) {
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
        TotalesCarrito totales = TotalesCarrito.calcular(carrito);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        df.setDecimalFormatSymbols(new java.text.DecimalFormatSymbols() {
            {
                setDecimalSeparator(',');
                setGroupingSeparator('.');
            }
        });
        lblSubtotal.setText("Subtotal: S/ " + df.format(totales.subtotal()));
        lblDescuento.setText("Descuento: S/ " + df.format(totales.descuento()));
        lblIGV.setText("IGV: S/ " + df.format(totales.igv()));
        lblTotal.setText("Total: S/ " + df.format(totales.total()));
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
        DialogoHistorialVentas.mostrar(ventaRepository, anulacionVenta::anular, this::mostrarMensaje);
    }

    // Método principal para confirmar la venta y emitir comprobante electrónico
    private void confirmarVenta() {
        Object valorCombo = comboClientes.getValue();
        final String metodoPago = comboPago.getValue();
        if (metodoPago == null || carrito.isEmpty()) {
            mostrarMensaje("Completa todos los datos y agrega productos al carrito.");
            return;
        }
        final String tipoComprobante = comboComprobante.getValue();
        Cliente clienteActual = ClienteParaVenta.resolver(valorCombo, tipoComprobante, comboClientes,
                lblTotal, this::mostrarMensaje);
        if (clienteActual == null) {
            return;
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
        // String tipoComprobante = comboComprobante.getValue(); // Eliminada
        // declaración duplicada
        if ("TICKET".equalsIgnoreCase(tipoComprobante)) {
            // Lógica para registrar venta tipo TICKET
            com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.TicketVentaController ticketController = new com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.TicketVentaController();
            Venta ventaGuardada = ticketController.registrarVentaTicket(new ArrayList<>(carrito), metodoPago,
                    SessionManager.getUsuarioActual().getUsername(), "Farmacia Victoria", clienteActual);
            // Guardar los pagos mixtos en la tabla detalle_pago_venta para TICKET
            if ("MIXTO".equalsIgnoreCase(metodoPago) && ventaGuardada != null) {
                PagosMixtosVenta.guardar(pagoMixto, ventaGuardada, this::mostrarMensaje);
            }
            // Mostrar vista previa del ticket
            String textoTicket = ticketController.generarTextoTicket(ventaGuardada, "Farmacia Victoria");
            ticketController.mostrarVistaPreviaTicket(textoTicket);
            carrito.clear();
            actualizarTotales();
            mostrarMensaje("Venta tipo TICKET registrada correctamente.");
            return;
        }
        TotalesCarrito importes = new TotalesCarrito(subtotal, descuento[0], igv, total[0]);
        if ("FACTURA".equalsIgnoreCase(tipoComprobante)) {
            emisionComprobantes.emitirFactura(clienteActual, metodoPago, importes);
        } else if ("BOLETA".equalsIgnoreCase(tipoComprobante)) {
            emisionComprobantes.emitirBoleta(clienteActual, metodoPago, importes,
                    new EmisionComprobantesVenta.UsoPuntos(puntosUsados[0], puntosDescontados[0]));
        } else {
            mostrarMensaje("Tipo de comprobante no soportado o no seleccionado.");
        }
    }
}
