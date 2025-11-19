package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.NotificationService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventObserver;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductosController implements Initializable, SystemEventObserver {
    // Botones granulares para permisos
    @FXML
    private Button btnAgregarProducto;
    @FXML
    private Button btnEditarProducto;
    @FXML
    private Button btnEliminarProducto;
    @FXML
    private Button btnActivarInactivarProducto;
    @FXML
    private Button btnExportar;

    @FXML
    private Button btnPaginaAnterior;
    @FXML
    private Button btnPaginaSiguiente;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private ComboBox<Integer> cmbTamanoPagina;

    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int tamanoPagina = -1; // -1 = Todos por defecto

    private static final Logger logger = LoggerFactory.getLogger(ProductosController.class);
    @FXML
    private TableView<Producto> tableProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, BigDecimal> colPrecioVenta;
    @FXML
    private TableColumn<Producto, Integer> colStockActual;
    @FXML
    private TableColumn<Producto, String> colFechaVencimiento;
    @FXML
    private TableColumn<Producto, Boolean> colEstado;
    @FXML
    private TableColumn<Producto, Void> colAcciones;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<CategoriaProducto> cmbCategoria;
    @FXML
    private Button btnEdicionMasiva;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private CheckBox chkMostrarInactivos;
    @FXML
    private ComboBox<String> cmbBusquedaStrategy;
    @FXML
    private Button btnNuevoProducto;
    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblTotalActivos;
    @FXML
    private Label lblStockBajo;
    @FXML
    private Label lblProximosVencer;
    @FXML
    private Label lblValorInventario;

    private final ProductoService productoService;
    private final NotificationService notificationService;
    private final SystemEventManager eventManager;

    private ObservableList<Producto> productos;
    private FilteredList<Producto> productosFiltrados;

    private final DecimalFormat formatoMoneda = new DecimalFormat("S/ #,##0.00");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProductosController() {
        try {
            ServiceContainer container = ServiceContainer.getInstance();
            this.productoService = container.getProductoService();
            this.notificationService = container.getNotificationService();
            this.eventManager = SystemEventManager.getInstance();

            logger.info("ProductosController inicializado con enterprise patterns");
        } catch (Exception e) {
            logger.error("Error al inicializar ProductosController: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo crítico en inicialización del controlador", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                .getInstance()
                .getAuthenticationService().getUsuarioActual();
        // Ocultar/mostrar botones principales según permisos granulares del vendedor
        boolean esAdmin = usuario != null && usuario.isAdmin();
        boolean esVendedor = usuario != null && usuario.isVendedor();
        if (btnExportar != null) {
            btnExportar.setOnAction(e -> mostrarModalExportar());
        }
        setupTableColumns();
        initializeComponents();
        loadData();
        setupRowStyles();
        setupFilters();
        setupActionColumn();
        setupEventHandlers();

        // Ocultar/mostrar botones principales según permisos granulares del vendedor
        // Variables ya declaradas previamente en el método, no repetir

        if (esAdmin) {
            if (btnNuevoProducto != null) {
                btnNuevoProducto.setVisible(true);
                btnNuevoProducto.setManaged(true);
            }
            if (btnEdicionMasiva != null) {
                btnEdicionMasiva.setVisible(true);
                btnEdicionMasiva.setManaged(true);
            }
            if (btnExportar != null) {
                btnExportar.setVisible(true);
                btnExportar.setManaged(true);
            }
        } else if (esVendedor) {
            com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService usuarioService = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService
                    .getInstance();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                    .obtenerUsuarios().stream()
                    .filter(u -> u
                            .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                    .collect(java.util.stream.Collectors.toList());
            final java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso> permisos;
            if (!vendedores.isEmpty()) {
                permisos = usuarioService.obtenerPermisos(vendedores.get(0).getId());
            } else {
                permisos = java.util.Collections.emptyList();
            }
            java.util.function.Function<String, Boolean> tienePermiso = clave -> permisos.stream()
                    .anyMatch(p -> clave.equals(p.getPermiso()) && p.isValor());

            if (btnNuevoProducto != null) {
                boolean visible = tienePermiso.apply("inventario.agregar");
                btnNuevoProducto.setVisible(visible);
                btnNuevoProducto.setManaged(visible);
            }
            if (btnEdicionMasiva != null) {
                boolean visible = tienePermiso.apply("inventario.edicion_masiva");
                btnEdicionMasiva.setVisible(visible);
                btnEdicionMasiva.setManaged(visible);
            }
            if (btnExportar != null) {
                boolean visible = tienePermiso.apply("inventario.exportar");
                btnExportar.setVisible(visible);
                btnExportar.setManaged(visible);
            }
        } else {
            if (btnNuevoProducto != null) {
                btnNuevoProducto.setVisible(false);
                btnNuevoProducto.setManaged(false);
            }
            if (btnEdicionMasiva != null) {
                btnEdicionMasiva.setVisible(false);
                btnEdicionMasiva.setManaged(false);
            }
            if (btnExportar != null) {
                btnExportar.setVisible(false);
                btnExportar.setManaged(false);
            }
        }

        // Suscribirse al Observer para recibir eventos del sistema
        eventManager.subscribe(this);

        mostrarNotificacionesInventario();

        if (esAdmin) {
            if (btnNuevoProducto != null) {
                btnNuevoProducto.setVisible(true);
                btnNuevoProducto.setManaged(true);
            }
            if (btnEdicionMasiva != null) {
                btnEdicionMasiva.setVisible(true);
                btnEdicionMasiva.setManaged(true);
            }
            if (lblValorInventario != null) {
                lblValorInventario.setVisible(true);
                lblValorInventario.setManaged(true);
            }
            if (btnAgregarProducto != null) {
                btnAgregarProducto.setVisible(true);
                btnAgregarProducto.setManaged(true);
            }
            if (btnEditarProducto != null) {
                btnEditarProducto.setVisible(true);
                btnEditarProducto.setManaged(true);
            }
            if (btnEliminarProducto != null) {
                btnEliminarProducto.setVisible(true);
                btnEliminarProducto.setManaged(true);
            }
            if (btnActivarInactivarProducto != null) {
                btnActivarInactivarProducto.setVisible(true);
                btnActivarInactivarProducto.setManaged(true);
            }
            if (btnExportar != null) {
                btnExportar.setVisible(true);
                btnExportar.setManaged(true);
            }
        } else if (esVendedor) {
            // Obtener permisos granulares del primer vendedor (global)
            com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService usuarioService = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService
                    .getInstance();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                    .obtenerUsuarios()
                    .stream()
                    .filter(u -> u
                            .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                    .collect(java.util.stream.Collectors.toList());
            final java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso> permisos;
            if (!vendedores.isEmpty()) {
                permisos = usuarioService.obtenerPermisos(vendedores.get(0).getId());
            } else {
                permisos = java.util.Collections.emptyList();
            }
            java.util.function.Function<String, Boolean> tienePermiso = clave -> permisos.stream()
                    .anyMatch(p -> clave.equals(p.getPermiso()) && p.isValor());

            if (btnNuevoProducto != null) {
                boolean visible = tienePermiso.apply("inventario.agregar");
                btnNuevoProducto.setVisible(visible);
                btnNuevoProducto.setManaged(visible);
            }
            if (btnEdicionMasiva != null) {
                boolean visible = tienePermiso.apply("inventario.edicion_masiva");
                btnEdicionMasiva.setVisible(visible);
                btnEdicionMasiva.setManaged(visible);
            }
            if (btnAgregarProducto != null) {
                boolean visible = tienePermiso.apply("inventario.agregar");
                btnAgregarProducto.setVisible(visible);
                btnAgregarProducto.setManaged(visible);
            }
            if (btnEditarProducto != null) {
                boolean visible = tienePermiso.apply("inventario.editar");
                btnEditarProducto.setVisible(visible);
                btnEditarProducto.setManaged(visible);
            }
            if (btnEliminarProducto != null) {
                boolean visible = tienePermiso.apply("inventario.eliminar");
                btnEliminarProducto.setVisible(visible);
                btnEliminarProducto.setManaged(visible);
            }
            if (btnActivarInactivarProducto != null) {
                boolean visible = tienePermiso.apply("inventario.activar_inactivar");
                btnActivarInactivarProducto.setVisible(visible);
                btnActivarInactivarProducto.setManaged(visible);
            }
            if (btnExportar != null) {
                boolean visible = tienePermiso.apply("inventario.exportar");
                btnExportar.setVisible(visible);
                btnExportar.setManaged(visible);
            }
            // Estadística: valor inventario solo para admin
            if (lblValorInventario != null) {
                lblValorInventario.setVisible(false);
                lblValorInventario.setManaged(false);
            }
        }
    }

    @FXML
    public void onExportar(javafx.event.ActionEvent event) {
        mostrarModalExportar();
    }

    private void mostrarModalExportar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/exportar-inventario.fxml"));
            Parent root = loader.load();
            ExportarInventarioController exportController = loader.getController();
            exportController.setProductosFiltrados(new java.util.ArrayList<>(productosFiltrados));
            Stage stage = new Stage();
            stage.setTitle("Exportar Inventario / Reporte");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            notificationService.mostrarError("No se pudo abrir la ventana de exportación: " + ex.getMessage());
        }
    }

    @Override
    public void onEvent(SystemEvent event) {
        Platform.runLater(() -> {
            try {
                switch (event.getTipo()) {
                    case PRODUCTO_CREADO:
                    case PRODUCTO_ACTUALIZADO:
                    case PRODUCTO_ELIMINADO:
                        logger.debug("Recibido evento de producto: {}", event.getTipo());
                        loadData();
                        break;
                    case STOCK_BAJO:
                        logger.debug("Evento de stock bajo recibido: {}", event.getMensaje());
                        break;
                    case PRODUCTO_VENCIMIENTO_PROXIMO:
                        logger.debug("Evento de vencimiento próximo recibido: {}", event.getMensaje());
                        break;
                    default:
                        logger.debug("Evento no manejado: {}", event.getTipo());
                }
            } catch (Exception e) {
                logger.error("Error al procesar evento {}: {}", event.getTipo(), e.getMessage(), e);
            }
        });
    }

    @Override
    public String getObserverId() {
        return "ProductosController-" + this.hashCode();
    }

    private void initializeComponents() {
        productos = FXCollections.observableArrayList();
        productosFiltrados = new FilteredList<>(productos, p -> true);
        tableProductos.setItems(productosFiltrados);

        // Inicializar opciones de paginación
        if (cmbTamanoPagina != null) {
            cmbTamanoPagina.getItems().clear();
            cmbTamanoPagina.getItems().addAll(-1, 5, 10, 20, 50); // -1 = Todos
            cmbTamanoPagina.setValue(-1);
            cmbTamanoPagina.setConverter(new javafx.util.StringConverter<Integer>() {
                @Override
                public String toString(Integer value) {
                    if (value == null || value == -1)
                        return "Todos";
                    return value.toString();
                }

                @Override
                public Integer fromString(String string) {
                    if ("Todos".equals(string))
                        return -1;
                    try {
                        return Integer.parseInt(string);
                    } catch (Exception e) {
                        return -1;
                    }
                }
            });
            cmbTamanoPagina.setCellFactory(listView -> new javafx.scene.control.ListCell<Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item == -1 ? "Todos" : item.toString());
                    }
                }
            });
            cmbTamanoPagina.setButtonCell(new javafx.scene.control.ListCell<Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item == -1 ? "Todos" : item.toString());
                    }
                }
            });
            cmbTamanoPagina.valueProperty().addListener((obs, oldVal, newVal) -> {
                tamanoPagina = newVal;
                paginaActual = 1;
                actualizarPaginacion();
                actualizarEstadisticas();
            });
            // Mostrar todos por defecto al iniciar
            paginaActual = 1;
            actualizarPaginacion();
            actualizarEstadisticas();
        }

        // Inicializar opciones de estrategia de búsqueda
        if (cmbBusquedaStrategy != null) {
            cmbBusquedaStrategy.getItems().clear();
            cmbBusquedaStrategy.getItems().addAll("Nombre", "Laboratorio", "Lote", "Proveedor", "Vencimiento");
            cmbBusquedaStrategy.setValue("Nombre");
            cmbBusquedaStrategy.valueProperty().addListener((obs, oldVal, newVal) -> {
                aplicarFiltros();
            });
        }
        logger.debug("Componentes de UI inicializados");
    }

    private void setupTableColumns() {
        colPrecioVenta.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        colPrecioVenta.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            productoService.actualizarCampo(producto.getId(), "precio_venta", event.getNewValue());
            // Consultar el valor actualizado desde la base de datos
            Producto actualizado = productoService.obtenerPorId(producto.getId());
            if (actualizado != null) {
                producto.setPrecioVenta(actualizado.getPrecioVenta());
            } else {
                producto.setPrecioVenta(event.getNewValue());
            }
            tableProductos.refresh();
        });

        colStockActual.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colStockActual.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            producto.setStockActual(event.getNewValue());
            productoService.actualizarCampo(producto.getId(), "stock_actual", event.getNewValue());
            tableProductos.refresh();
        });

        colFechaVencimiento.setCellFactory(TextFieldTableCell.forTableColumn());
        colFechaVencimiento.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            try {
                LocalDate nuevaFecha = LocalDate.parse(event.getNewValue(), formatoFecha);
                productoService.actualizarCampo(producto.getId(), "fecha_vencimiento", nuevaFecha);
                // Consultar el valor actualizado desde la base de datos
                Producto actualizado = productoService.obtenerPorId(producto.getId());
                if (actualizado != null) {
                    producto.setFechaVencimiento(actualizado.getFechaVencimiento());
                } else {
                    producto.setFechaVencimiento(nuevaFecha);
                }
            } catch (Exception e) {
                // Si la fecha no es válida, no actualiza
            }
            tableProductos.refresh();
        });

        // Ejemplo para nombre, puedes replicar para los demás campos editables
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colNombre.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            producto.setNombre(event.getNewValue());
            productoService.actualizarCampo(producto.getId(), "nombre", event.getNewValue());
            tableProductos.refresh();
        });

        // Repetir para otros campos editables si lo necesitas (categoría, proveedor,
        // etc.)

        tableProductos.setEditable(true);
        // Configurar columnas básicas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Precio con formato y consulta en BD
        colPrecioVenta.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            BigDecimal precio = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                precio = actualizado != null ? actualizado.getPrecioVenta() : p.getPrecioVenta();
            } catch (Exception e) {
                precio = p.getPrecioVenta();
            }
            return new SimpleObjectProperty<>(precio);
        });
        colPrecioVenta.setCellFactory(column -> new TableCell<Producto, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(item));
                }
            }
        });

        colStockActual.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            Integer stock = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                stock = actualizado != null ? actualizado.getStockActual() : p.getStockActual();
            } catch (Exception e) {
                stock = p.getStockActual();
            }
            return new SimpleObjectProperty<>(stock);
        });

        // Fecha de vencimiento consultando en BD
        colFechaVencimiento.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            LocalDate fecha = null;
            try {
                Producto actualizado = productoService.obtenerPorId(p.getId());
                fecha = actualizado != null ? actualizado.getFechaVencimiento() : p.getFechaVencimiento();
            } catch (Exception e) {
                fecha = p.getFechaVencimiento();
            }
            if (fecha != null) {
                return new SimpleStringProperty(fecha.format(formatoFecha));
            }
            return new SimpleStringProperty("Sin fecha");
        });

        // Estado con colores y fondo especial si es inactivo
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colEstado.setCellFactory(column -> new TableCell<Producto, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-background-color: transparent;");
                    } else {
                        setText("Inactivo");
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-background-color: #F3F4F6;");
                    }
                }
            }
        });
    }

    private void setupRowStyles() {
        tableProductos.setRowFactory(tv -> {
            TableRow<Producto> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldProducto, newProducto) -> {
                if (newProducto == null) {
                    row.getStyleClass().removeAll("row-stock-bajo", "row-proximo-vencer",
                            "row-sin-stock", "row-vencido", "row-inactivo");
                } else {
                    // Limpiar estilos anteriores
                    row.getStyleClass().removeAll("row-stock-bajo", "row-proximo-vencer",
                            "row-sin-stock", "row-vencido", "row-inactivo");

                    // Aplicar estilos según el estado del producto
                    if (!newProducto.getActivo()) {
                        row.getStyleClass().add("row-inactivo");
                    } else if (newProducto.getFechaVencimiento() != null &&
                            newProducto.getFechaVencimiento().isBefore(LocalDate.now())) {
                        row.getStyleClass().add("row-vencido");
                    } else if (newProducto.getStockActual() == 0) {
                        row.getStyleClass().add("row-sin-stock");
                    } else if (newProducto.getStockActual() <= newProducto.getStockMinimo()) {
                        row.getStyleClass().add("row-stock-bajo");
                    } else if (newProducto.getFechaVencimiento() != null) {
                        int dias = com.farmaciavictoria.proyectopharmavictoria.service.ProductoService
                                .getDiasVencimientoAlerta();
                        java.time.LocalDate hoy = java.time.LocalDate.now();
                        java.time.LocalDate limite = hoy.plusDays(dias);
                        if (!newProducto.getFechaVencimiento().isBefore(hoy) &&
                                !newProducto.getFechaVencimiento().isAfter(limite)) {
                            row.getStyleClass().add("row-proximo-vencer");
                        }
                    }
                }
            });
            return row;
        });
    }

    private void setupFilters() {
        // Configurar ComboBox de categorías
        cmbCategoria.getItems().add(null); // Opción "Todas"
        cmbCategoria.getItems().addAll(CategoriaProducto.values());
        cmbCategoria.setConverter(new StringConverter<CategoriaProducto>() {
            @Override
            public String toString(CategoriaProducto categoria) {
                return categoria == null ? "Todas las categorías" : categoria.toString();
            }

            @Override
            public CategoriaProducto fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de estados con opciones especiales
        cmbEstado.getItems().addAll(
                "Todos",
                "Activo",
                "Inactivo",
                "Stock Bajo",
                "Próximos a Vencer",
                "Sin Stock",
                "Productos Vencidos");
        cmbEstado.setValue("Todos");
    }

    private void setupActionColumn() {
        colAcciones.setCellFactory(new Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>>() {
            @Override
            public TableCell<Producto, Void> call(TableColumn<Producto, Void> param) {
                return new TableCell<Producto, Void>() {
                    private final Button btnEditar = new Button();
                    private final Button btnVer = new Button();
                    private final Button btnToggleEstado = new Button();
                    private final Button btnEliminar = new Button();

                    {
                        // Íconos personalizados para agregar, ver y eliminar
                        // Usar imágenes descargadas como íconos
                        // Íconos correctos para acciones: editar, ver detalles y eliminar
                        javafx.scene.image.ImageView iconEditar = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/editar.png").toExternalForm());
                        javafx.scene.image.ImageView iconVer = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/ver.png").toExternalForm());
                        javafx.scene.image.ImageView iconEliminar = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/eliminar.png").toExternalForm());

                        iconEditar.setFitWidth(24);
                        iconEditar.setFitHeight(24);
                        iconVer.setFitWidth(24);
                        iconVer.setFitHeight(24);
                        iconEliminar.setFitWidth(24);
                        iconEliminar.setFitHeight(24);

                        btnEditar.setGraphic(iconEditar);
                        btnVer.setGraphic(iconVer);
                        btnEliminar.setGraphic(iconEliminar);
                        btnEditar.getStyleClass().setAll("btn-action", "btn-add");
                        btnVer.getStyleClass().setAll("btn-action", "btn-eye");
                        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle");
                        if (btnPaginaAnterior != null && btnPaginaSiguiente != null && lblPaginaActual != null) {
                            btnPaginaAnterior.setOnAction(e -> onPaginaAnterior());
                            btnPaginaSiguiente.setOnAction(e -> onPaginaSiguiente());
                        }
                        btnEliminar.getStyleClass().setAll("btn-action", "btn-trash");
                        btnEditar.setPrefWidth(35);
                        btnEditar.setPrefHeight(35);
                        btnVer.setPrefWidth(35);
                        btnVer.setPrefHeight(35);
                        btnToggleEstado.setPrefWidth(35);
                        btnToggleEstado.setPrefHeight(35);
                        btnEliminar.setPrefWidth(35);
                        btnEliminar.setPrefHeight(35);

                        btnEditar.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            editarProducto(producto);
                        });
                        btnVer.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            verDetallesProducto(producto);
                        });
                        btnToggleEstado.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            toggleEstadoProducto(producto);
                        });
                        btnEliminar.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            eliminarProductoDefinitivo(producto);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Producto producto = getTableView().getItems().get(getIndex());
                            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                                    .getInstance()
                                    .getAuthenticationService().getUsuarioActual();
                            boolean esAdmin = usuario != null && usuario.isAdmin();
                            // Acciones por producto según rol
                            if (esAdmin) {
                                btnEditar.setVisible(true);
                                btnEditar.setManaged(true);
                                btnToggleEstado.setVisible(true);
                                btnToggleEstado.setManaged(true);
                                btnEliminar.setVisible(true);
                                btnEliminar.setManaged(true);
                                btnVer.setVisible(true);
                                btnVer.setManaged(true);
                            } else if (usuario != null && usuario.isVendedor()) {
                                // Obtener permisos granulares del primer vendedor (global)
                                com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService usuarioService = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService
                                        .getInstance();
                                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                                        .obtenerUsuarios().stream()
                                        .filter(u -> u
                                                .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                                        .collect(java.util.stream.Collectors.toList());
                                final java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso> permisos;
                                if (!vendedores.isEmpty()) {
                                    permisos = usuarioService.obtenerPermisos(vendedores.get(0).getId());
                                } else {
                                    permisos = java.util.Collections.emptyList();
                                }
                                java.util.function.Function<String, Boolean> tienePermiso = clave -> permisos.stream()
                                        .anyMatch(p -> clave.equals(p.getPermiso()) && p.isValor());

                                btnEditar.setVisible(tienePermiso.apply("inventario.editar"));
                                btnEditar.setManaged(tienePermiso.apply("inventario.editar"));
                                btnToggleEstado.setVisible(tienePermiso.apply("inventario.activar_inactivar"));
                                btnToggleEstado.setManaged(tienePermiso.apply("inventario.activar_inactivar"));
                                btnEliminar.setVisible(tienePermiso.apply("inventario.eliminar"));
                                btnEliminar.setManaged(tienePermiso.apply("inventario.eliminar"));
                                btnVer.setVisible(true); // Ver detalles siempre visible
                                btnVer.setManaged(true);
                            } else {
                                btnEditar.setVisible(false);
                                btnEditar.setManaged(false);
                                btnToggleEstado.setVisible(false);
                                btnToggleEstado.setManaged(false);
                                btnEliminar.setVisible(false);
                                btnEliminar.setManaged(false);
                                btnVer.setVisible(true);
                                btnVer.setManaged(true);
                            }
                            if (producto.getActivo()) {
                                btnToggleEstado.setText("⏸");
                                btnToggleEstado.setTooltip(new Tooltip("Inactivar producto"));
                                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
                            } else {
                                btnToggleEstado.setText("▶");
                                btnToggleEstado.setTooltip(new Tooltip("Activar producto"));
                                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle",
                                        "btn-toggle-inactive");
                            }
                            HBox buttons = new HBox(5, btnEditar, btnVer, btnToggleEstado, btnEliminar);
                            buttons.setAlignment(javafx.geometry.Pos.CENTER);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }

    // Mostrar historial de movimientos de inventario para un producto
    private void mostrarHistorialInventario(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/historial-inventario.fxml"));
            Parent root = loader.load();
            HistorialInventarioController controller = loader.getController();
            controller.setProducto(producto);
            Stage stage = new Stage();
            stage.setTitle("Historial de Inventario - " + producto.getNombre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.showAndWait();
        } catch (Exception e) {
            notificationService.mostrarError("No se pudo mostrar el historial: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        btnNuevoProducto.setOnAction(e -> onNuevoProducto());
        // btnActualizar.setOnAction(e -> onActualizar()); // Eliminado porque el botón
        // ya no existe en el FXML
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cmbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        if (chkMostrarInactivos != null) {
            chkMostrarInactivos.selectedProperty().addListener((obs, oldVal, newVal) -> {
                aplicarFiltroMostrarInactivos();
            });
        }
    }

    // Cargar datos usando service layer
    private void loadData() {
        Task<List<Producto>> task = new Task<List<Producto>>() {
            @Override
            protected List<Producto> call() throws Exception {
                logger.debug("Cargando productos desde service layer");
                return productoService.obtenerTodos();
            }

            @Override
            protected void succeeded() {
                productos.setAll(getValue());
                paginaActual = 1;
                aplicarFiltros(); // Mostrar productos según filtros activos al inicio
                logger.debug("Productos cargados exitosamente: {}", getValue().size());
            }

            @Override
            protected void failed() {
                logger.error("Error al cargar productos: {}", getException().getMessage(), getException());
                Platform.runLater(() -> {
                    notificationService
                            .mostrarError("No se pudieron cargar los productos: " + getException().getMessage());
                });
            }
        };
        new Thread(task).start();
    }

    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText();
        CategoriaProducto categoriaSeleccionada = cmbCategoria.getValue();
        String estadoSeleccionado = cmbEstado.getValue();
        boolean mostrarInactivos = chkMostrarInactivos != null && chkMostrarInactivos.isSelected();

        String estrategia = (cmbBusquedaStrategy != null && cmbBusquedaStrategy.getValue() != null)
                ? cmbBusquedaStrategy.getValue()
                : "Nombre";
        com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.ProductSearchStrategy strategy;
        switch (estrategia) {
            case "Laboratorio":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByLaboratorioStrategy();
                break;
            case "Lote":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByLoteStrategy();
                break;
            case "Proveedor":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByProveedorStrategy();
                break;
            case "Vencimiento":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByVencimientoStrategy();
                break;
            case "Nombre":
            default:
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByNameStrategy();
                break;
        }

        int diasVencimiento = com.farmaciavictoria.proyectopharmavictoria.service.ProductoService
                .getDiasVencimientoAlerta();
        List<Producto> filtrados = strategy.buscar(productos, textoBusqueda)
                .stream()
                .filter(producto -> mostrarInactivos || producto.getActivo())
                .filter(producto -> categoriaSeleccionada == null
                        || producto.getCategoria().equals(categoriaSeleccionada))
                .filter(producto -> {
                    if ("Todos".equals(estadoSeleccionado))
                        return true;
                    if ("Activo".equals(estadoSeleccionado))
                        return producto.getActivo();
                    if ("Inactivo".equals(estadoSeleccionado))
                        return !producto.getActivo();
                    if ("Stock Bajo".equals(estadoSeleccionado))
                        return producto.getStockActual() <= producto.getStockMinimo();
                    if ("Próximos a Vencer".equals(estadoSeleccionado))
                        return producto.getFechaVencimiento() != null
                                && producto.getFechaVencimiento().isAfter(LocalDate.now())
                                && (producto.getFechaVencimiento().isEqual(LocalDate.now().plusDays(diasVencimiento))
                                        || producto.getFechaVencimiento()
                                                .isBefore(LocalDate.now().plusDays(diasVencimiento)));
                    if ("Sin Stock".equals(estadoSeleccionado))
                        return producto.getStockActual() == 0;
                    if ("Productos Vencidos".equals(estadoSeleccionado))
                        return producto.getFechaVencimiento() != null
                                && producto.getFechaVencimiento().isBefore(LocalDate.now());
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());

        productosFiltrados = new javafx.collections.transformation.FilteredList<>(
                javafx.collections.FXCollections.observableArrayList(filtrados));
        paginaActual = 1;
        actualizarPaginacion();
        actualizarEstadisticas();
    }

    // Nuevo método para aplicar el filtro de mostrar inactivos
    private void aplicarFiltroMostrarInactivos() {
        aplicarFiltros(); // La paginación ya se actualiza dentro de aplicarFiltros()
    }

    private void actualizarPaginacion() {
        // Filtrar productos válidos si aplica (aquí solo se usa productosFiltrados)
        java.util.List<Producto> productosValidos = productosFiltrados;
        int total = productosValidos.size();
        ObservableList<Producto> pagina = FXCollections.observableArrayList();
        if (tamanoPagina == -1) {
            pagina.addAll(productosValidos);
            tableProductos.setItems(pagina);
            if (lblPaginaActual != null) {
                lblPaginaActual.setText("Mostrando todos");
            }
            if (btnPaginaAnterior != null)
                btnPaginaAnterior.setDisable(true);
            if (btnPaginaSiguiente != null)
                btnPaginaSiguiente.setDisable(true);
        } else {
            totalPaginas = (int) Math.ceil((double) total / tamanoPagina);
            if (totalPaginas < 1)
                totalPaginas = 1;
            if (paginaActual < 1)
                paginaActual = 1;
            if (paginaActual > totalPaginas)
                paginaActual = totalPaginas;
            int desde = (paginaActual - 1) * tamanoPagina;
            int hasta = Math.min(desde + tamanoPagina, total);
            for (int i = desde; i < hasta; i++) {
                pagina.add(productosValidos.get(i));
            }
            tableProductos.setItems(pagina);
            if (lblPaginaActual != null) {
                lblPaginaActual.setText("Página " + paginaActual + " de " + totalPaginas);
            }
            if (btnPaginaAnterior != null)
                btnPaginaAnterior.setDisable(paginaActual <= 1);
            if (btnPaginaSiguiente != null)
                btnPaginaSiguiente.setDisable(paginaActual >= totalPaginas);
        }
        // Mostrar el total de productos filtrados (no solo la página)
        if (lblTotalProductos != null) {
            lblTotalProductos.setText("Total: " + productosValidos.size() + " productos");
        }
    }

    @FXML
    private void onPaginaAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarPaginacion();
        }
    }

    @FXML
    private void onPaginaSiguiente() {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            actualizarPaginacion();
        }
    }

    private void actualizarEstadisticas() {
        Platform.runLater(() -> {
            // Usar productosFiltrados (total filtrado), no solo la página actual
            List<Producto> lista = productosFiltrados;
            int diasVencimiento = com.farmaciavictoria.proyectopharmavictoria.service.ProductoService
                    .getDiasVencimientoAlerta();
            int productosActivos = (int) lista.stream().filter(Producto::getActivo).count();
            int stockBajo = (int) lista.stream().filter(p -> p.getStockActual() <= p.getStockMinimo()).count();
            int proximosVencer = (int) lista.stream().filter(p -> p.getFechaVencimiento() != null
                    && p.getFechaVencimiento().isAfter(LocalDate.now())
                    && (p.getFechaVencimiento().isEqual(LocalDate.now().plusDays(diasVencimiento))
                            || p.getFechaVencimiento().isBefore(LocalDate.now().plusDays(diasVencimiento))))
                    .count();
            BigDecimal valorInventario = lista.stream()
                    .filter(Producto::getActivo)
                    .map(p -> p.getPrecioVenta().multiply(BigDecimal.valueOf(p.getStockActual())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lblTotalActivos.setText(String.valueOf(productosActivos));
            lblStockBajo.setText(String.valueOf(stockBajo));
            lblProximosVencer.setText(String.valueOf(proximosVencer));
            lblValorInventario.setText(formatoMoneda.format(valorInventario));
        });
    }

    @FXML
    private void onNuevoProducto() {
        abrirFormularioProducto(null);
    }

    @FXML
    private void onActualizar() {
        // Recargar productos
        loadData();
        // Refrescar paneles de estadísticas
        actualizarEstadisticas();
        // Refrescar historial de inventario si está visible
        if (isHistorialInventarioVisible()) {
            refrescarHistorialInventario();
        }
        // Mostrar notificación visual de éxito
        notificationService.mostrarInfo("¡Inventario actualizado correctamente!");
    }

    // Verifica si el panel de historial de inventario está visible
    private boolean isHistorialInventarioVisible() {
        // Implementa la lógica según tu UI, por defecto retorna true si existe el panel
        // Si tienes un TabPane, verifica si el tab está seleccionado
        return true;
    }

    // Refresca el historial de inventario
    private void refrescarHistorialInventario() {
    }

    @FXML
    private void onEditarProducto() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            editarProducto(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para editar");
        }
    }

    @FXML
    private void onEliminarProducto() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            eliminarProductoDefinitivo(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para eliminar");
        }
    }

    @FXML
    private void onBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void onFiltrarCategoria() {
        aplicarFiltros();
    }

    @FXML
    private void onFiltrarEstado() {
        aplicarFiltros();
    }

    @FXML
    private void onToggleMostrarInactivos() {
        aplicarFiltros();
    }

    @FXML
    private void onVerDetalles() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            verDetallesProducto(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para ver detalles");
        }
    }

    @FXML
    private void onAjustarStock() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            ajustarStock(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para ajustar stock");
        }
    }

    @FXML
    private void onCambiarUbicacion() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            cambiarUbicacion(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para cambiar ubicación");
        }
    }

    @FXML
    private void onToggleEstadoProducto() {
        Producto productoSeleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            toggleEstadoProducto(productoSeleccionado);
        } else {
            notificationService.mostrarAdvertencia("Por favor seleccione un producto para cambiar estado");
        }
    }

    private void editarProducto(Producto producto) {
        Producto productoActualizado = producto != null ? productoService.obtenerPorId(producto.getId()) : null;
        abrirFormularioProducto(productoActualizado != null ? productoActualizado : producto);
    }

    private void verDetallesProducto(Producto producto) {
        try {
            // Refrescar el producto desde la base de datos antes de mostrar detalles
            Producto productoActualizado = productoService.obtenerPorId(producto.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/producto-detalles.fxml"));
            Parent root = loader.load();

            ProductoDetallesController controller = loader.getController();
            controller.setProducto(productoActualizado != null ? productoActualizado : producto);

            Stage stage = new Stage();
            stage.setTitle("Detalles del Producto - "
                    + (productoActualizado != null ? productoActualizado.getNombre() : producto.getNombre()));
            Scene scene = new Scene(root);

            try {
                String css = getClass().getResource("/css/Inventario/producto-detalles.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el CSS de detalles: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(500);
            stage.setMinHeight(400);
            stage.setWidth(800);
            stage.setHeight(700);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al abrir ventana de detalles: " + e.getMessage());
            e.printStackTrace();
            notificationService.mostrarError("No se pudo abrir la ventana de detalles del producto.");
        }
    }

    // Ajustar stock usando service layer
    private void ajustarStock(Producto producto) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(producto.getStockActual()));
        dialog.setTitle("Ajustar Stock");
        dialog.setHeaderText("Ajustar stock del producto: " + producto.getNombre());
        dialog.setContentText("Nuevo stock:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(stock -> {
            try {
                int stockAnterior = producto.getStockActual();
                int nuevoStock = Integer.parseInt(stock);
                if (producto.getId() != null && stockAnterior != nuevoStock) {
                    producto.setStockActual(nuevoStock);
                    productoService.actualizar(producto);
                    notificationService.mostrarExito("Stock actualizado correctamente");
                } else {
                    notificationService.mostrarError("No se realizó ningún cambio de stock.");
                }
            } catch (NumberFormatException e) {
                notificationService.mostrarError("Por favor ingrese un número válido");
            } catch (Exception e) {
                logger.error("Error al actualizar stock: {}", e.getMessage(), e);
                notificationService.mostrarError("No se pudo actualizar el stock: " + e.getMessage());
            }
        });
    }

    private void cambiarUbicacion(Producto producto) {
        TextInputDialog dialog = new TextInputDialog(producto.getUbicacion());
        dialog.setTitle("Cambiar Ubicación");
        dialog.setHeaderText("Cambiar ubicación del producto: " + producto.getNombre());
        dialog.setContentText("Nueva ubicación:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ubicacion -> {
            try {
                producto.setUbicacion(ubicacion);
                productoService.actualizar(producto);
                notificationService.mostrarExito("Ubicación actualizada correctamente");
                // No necesario llamar loadData() - Observer Pattern se encarga
            } catch (Exception e) {
                logger.error("Error al actualizar ubicación: {}", e.getMessage(), e);
                notificationService.mostrarError("No se pudo actualizar la ubicación: " + e.getMessage());
            }
        });
    }

    // Desactivar producto
    private void abrirFormularioProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/producto-form.fxml"));
            Parent root = loader.load();

            ProductoFormController controller = loader.getController();
            Producto productoRefrescado = producto != null ? productoService.obtenerPorId(producto.getId()) : null;
            if (productoRefrescado != null) {
                controller.setProducto(productoRefrescado);
            } else if (producto != null) {
                controller.setProducto(producto);
            }

            controller.setOnProductoGuardado(prodGuardado -> {
                if (producto != null && productos.contains(producto)) {
                    // Copia profunda del producto previo para comparación
                    Producto previo = productos.stream().filter(p -> p.getId().equals(prodGuardado.getId())).findFirst()
                            .orElse(null);
                    if (previo != null && prodGuardado.equals(previo)) {
                        return;
                    }
                    String usuario = ServiceContainer.getInstance().getAuthenticationService()
                            .getUsuarioActual() != null
                                    ? ServiceContainer.getInstance().getAuthenticationService().getUsuarioActual()
                                            .getNombreCompleto()
                                    : "Desconocido";
                    // Solo registrar cambios si el valor realmente cambió
                    if (previo != null) {
                        if (!previo.getNombre().equals(prodGuardado.getNombre())) {
                            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository
                                    .registrarCambio(previo.getId(), "nombre", previo.getNombre(),
                                            prodGuardado.getNombre(), usuario);
                        }
                        if (previo.getPrecioVenta() != null && prodGuardado.getPrecioVenta() != null
                                && previo.getPrecioVenta().compareTo(prodGuardado.getPrecioVenta()) != 0) {
                            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository
                                    .registrarCambio(previo.getId(), "precio_venta", previo.getPrecioVenta().toString(),
                                            prodGuardado.getPrecioVenta().toString(), usuario);
                        }
                        if (previo.getUbicacion() != null
                                && !previo.getUbicacion().equals(prodGuardado.getUbicacion())) {
                            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository
                                    .registrarCambio(previo.getId(), "ubicacion", previo.getUbicacion(),
                                            prodGuardado.getUbicacion(), usuario);
                        }
                        if (previo.getStockActual() != null
                                && !previo.getStockActual().equals(prodGuardado.getStockActual())) {
                            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository
                                    .registrarCambio(previo.getId(), "stock_actual", previo.getStockActual().toString(),
                                            prodGuardado.getStockActual().toString(), usuario);
                        }
                        for (int i = 0; i < productos.size(); i++) {
                            if (productos.get(i).getId().equals(prodGuardado.getId())) {
                                productos.set(i, prodGuardado);
                                break;
                            }
                        }
                    }
                }
                boolean encontrado = false;
                for (int i = 0; i < productos.size(); i++) {
                    if (productos.get(i).getId().equals(prodGuardado.getId())) {
                        productos.set(i, prodGuardado);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    productos.add(prodGuardado);
                }
                int paginaActualTmp = paginaActual;
                aplicarFiltros();
                paginaActual = paginaActualTmp;
                actualizarPaginacion();
                actualizarEstadisticas();
                if (tableProductos != null) {
                    tableProductos.refresh();
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Formulario de Producto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.showAndWait();
        } catch (Exception e) {
            notificationService.mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    // Cambia el estado (activo/inactivo) de un producto
    public void toggleEstadoProducto(Producto producto) {
        if (producto != null && producto.getId() != null) {
            try {
                boolean nuevoEstado = !producto.getActivo();
                producto.setActivo(nuevoEstado);
                productoService.actualizar(producto);
                notificationService.mostrarExito("Estado del producto actualizado correctamente");
                aplicarFiltros();
                tableProductos.refresh();
            } catch (Exception e) {
                logger.error("Error al cambiar estado del producto: {}", e.getMessage(), e);
                notificationService.mostrarError("No se pudo cambiar el estado del producto: " + e.getMessage());
            }
        }
    }

    // Elimina definitivamente un producto
    public void eliminarProductoDefinitivo(Producto producto) {
        if (producto != null && producto.getId() != null) {
            // Primera advertencia
            Alert advertencia = new Alert(Alert.AlertType.WARNING);
            advertencia.setTitle("🟡 Eliminación definitiva");
            advertencia.setHeaderText("¡Atención! Esta acción NO se puede deshacer");
            advertencia.setContentText("¿Realmente desea ELIMINAR PARA SIEMPRE el producto '" + producto.getNombre()
                    + "'?\n\nSe eliminarán TODOS los registros relacionados.\nNo hay función de 'deshacer'.\n\nPulse 'Continuar' para confirmar.");
            ButtonType btnContinuar = new ButtonType("Continuar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            advertencia.getButtonTypes().setAll(btnContinuar, btnCancelar);

            advertencia.showAndWait().ifPresent(resultado1 -> {
                if (resultado1 == btnContinuar) {
                    // Segunda confirmación con campo de texto
                    Alert confirmacionFinal = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacionFinal.setTitle("🚨 Confirmación final");
                    confirmacionFinal.setHeaderText(null);
                    confirmacionFinal.setContentText(
                            "🟢 ¡ÚLTIMA OPORTUNIDAD!\n\nEscriba 'ELIMINAR' para confirmar la eliminación definitiva del producto.\n\nProducto: "
                                    + producto.getNombre());

                    javafx.scene.control.TextField input = new javafx.scene.control.TextField();
                    input.setPromptText("Escriba: ELIMINAR");
                    javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(
                            new javafx.scene.control.Label(confirmacionFinal.getContentText()), input);
                    content.setSpacing(12);
                    content.setPadding(new javafx.geometry.Insets(10));
                    confirmacionFinal.getDialogPane().setContent(content);

                    ButtonType btnEliminarDefinitivo = new ButtonType("ELIMINAR DEFINITIVAMENTE",
                            javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    ButtonType btnCancelar2 = new ButtonType("Cancelar",
                            javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    confirmacionFinal.getButtonTypes().setAll(btnEliminarDefinitivo, btnCancelar2);

                    final javafx.scene.control.Button btnOk = (javafx.scene.control.Button) confirmacionFinal
                            .getDialogPane().lookupButton(btnEliminarDefinitivo);
                    btnOk.setDisable(true);
                    input.textProperty().addListener((obs, oldVal, newVal) -> {
                        btnOk.setDisable(!"ELIMINAR".equalsIgnoreCase(newVal.trim()));
                    });

                    confirmacionFinal.showAndWait().ifPresent(resultado2 -> {
                        if (resultado2 == btnEliminarDefinitivo
                                && "ELIMINAR".equalsIgnoreCase(input.getText().trim())) {
                            try {
                                productoService.eliminarDefinitivamente(producto.getId());
                                productos.remove(producto);
                                notificationService.mostrarExito("Producto eliminado definitivamente");
                                aplicarFiltros();
                                tableProductos.refresh();
                            } catch (Exception e) {
                                logger.error("Error al eliminar producto definitivamente: {}", e.getMessage(), e);
                                notificationService.mostrarError("No se pudo eliminar el producto: " + e.getMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    // Solo mostrar notificaciones al entrar al inventario
    private void mostrarNotificacionesInventario() {
        try {
            notificationService.mostrarNotificacionesInventario();
            logger.debug("Notificaciones de inventario mostradas correctamente");
        } catch (Exception e) {
            logger.error("Error al mostrar notificaciones de inventario: {}", e.getMessage(), e);
        }
    }

    // Método FXML para abrir la edición masiva (llamado desde productos.fxml)
    @FXML
    public void onEdicionMasiva(javafx.event.ActionEvent event) {
        mostrarModalEdicionMasiva();
    }

    private void mostrarModalEdicionMasiva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/edicion-masiva-wizard.fxml"));
            Parent root = loader.load();
            com.farmaciavictoria.proyectopharmavictoria.controller.Inventario.EdicionMasivaWizardController controller = loader
                    .getController();
            controller.setProductos(new java.util.ArrayList<>(productosFiltrados));
            controller.setProductoService(productoService);
            controller.setNotificationService(notificationService);
            controller.setProveedorService(com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                    .getInstance().getProveedorService());
            controller.mostrarVistaSeleccion();
            Stage stage = new Stage();
            stage.setTitle("Edición Masiva de Productos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(600);
            stage.setWidth(1100);
            stage.setHeight(600);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            notificationService.mostrarError("No se pudo abrir la ventana de edición masiva: " + ex.getMessage());
        }
    }
}