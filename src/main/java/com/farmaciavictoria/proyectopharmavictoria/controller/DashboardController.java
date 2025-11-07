package com.farmaciavictoria.proyectopharmavictoria.controller;

import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.controller.Cliente.ClienteFormController;
import com.farmaciavictoria.proyectopharmavictoria.controller.Usuario.UsuariosController;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventObserver;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import com.farmaciavictoria.proyectopharmavictoria.PharmavictoriaApplication;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.StringWriter;
import java.io.PrintWriter;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ✅ ENTERPRISE CONTROLLER - Dashboard refactorizado
 * Aplica Service Layer Pattern, Dependency Injection y Observer Pattern
 * Panel principal con actualizaciones en tiempo real
 */
public class DashboardController implements Initializable, SystemEventObserver {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private Label fechaHoraLabel;
    @FXML
    private Label usuarioLabel;
    @FXML
    private Label rolLabel;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ListView<String> ultimasVentasList;
    @FXML
    private Label ventasHoyLabel;
    @FXML
    private Label ventasHoyCountLabel;
    @FXML
    private Label productosLabel;
    @FXML
    private Label productosStockLabel;
    @FXML
    private Label clientesLabel;
    @FXML
    private Label clientesNuevosLabel;
    @FXML
    private ListView<String> stockBajoList;

    @FXML
    private TableView<ProductoStockBajo> stockBajoTable;
    @FXML
    private TableColumn<ProductoStockBajo, String> colProducto;
    @FXML
    private TableColumn<ProductoStockBajo, String> colStockActual;
    @FXML
    private TableColumn<ProductoStockBajo, String> colStockMinimo;

    @FXML
    private TableView<VentaReciente> ultimasVentasTable;
    @FXML
    private TableColumn<VentaReciente, String> colFechaHora;
    @FXML
    private TableColumn<VentaReciente, String> colCliente;
    @FXML
    private TableColumn<VentaReciente, String> colTotal;
    @FXML
    private TableColumn<VentaReciente, String> colMetodoPago;

    private Timeline relojTimeline;
    private SystemEventManager eventManager = SystemEventManager.getInstance();
    private ProductoService productoService = ServiceContainer.getInstance().getProductoService();
    private ProveedorService proveedorService = ServiceContainer.getInstance().getProveedorService();
    private Usuario usuarioLogueado;

    /**
     * Manejador de evento para el botón de reporte de ventas en el dashboard.
     */
    public void reporteVentas(javafx.event.ActionEvent event) {
        mostrarError("En desarrollo", "Funcionalidad de reporte de ventas en desarrollo.");
    }

    // Event handlers para navegación

    @FXML
    private void navegarADashboard() {
        try {
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent dashboardRoot = loader.load();
                ScrollPane dashboardPane = (ScrollPane) dashboardRoot.lookup("#dashboardPane");
                if (dashboardPane != null) {
                    contentArea.getChildren().setAll(dashboardPane);
                    // ✅ CORRECCIÓN: Llamar a refrescarDatos para actualizar la vista
                    refrescarDatos();
                    marcarBotonActivo("dashboard");
                    // Forzar pantalla completa y maximizado
                    Stage stage = (Stage) fechaHoraLabel.getScene().getWindow();
                    stage.setResizable(true);
                    stage.setMaximized(true);
                    stage.centerOnScreen();
                    stage.show();
                } else {
                    mostrarError("Error", "No se pudo mostrar el dashboard principal.");
                }
            } else {
                mostrarError("Error", "No se pudo encontrar el área de contenido.");
            }
        } catch (Exception e) {
            logger.error("Error navegando al dashboard: {}", e.getMessage(), e);
            mostrarError("Error", "No se pudo mostrar el dashboard principal.");
        }
    }

    private void marcarBotonActivo(String modulo) {
        Button[] botones = new Button[] {
                (Button) fechaHoraLabel.getScene().lookup("#btnDashboard"),
                (Button) fechaHoraLabel.getScene().lookup("#btnInventario"),
                (Button) fechaHoraLabel.getScene().lookup("#btnClientes"),
                (Button) fechaHoraLabel.getScene().lookup("#btnProveedores"),
                (Button) fechaHoraLabel.getScene().lookup("#btnVentas"),
                (Button) fechaHoraLabel.getScene().lookup("#btnReportes"),
                (Button) fechaHoraLabel.getScene().lookup("#btnConfiguracion"),
                (Button) fechaHoraLabel.getScene().lookup("#btnUsuarios")
        };
        for (Button btn : botones) {
            if (btn != null) {
                btn.getStyleClass().remove("menu-button-active");
            }
        }
        switch (modulo) {
            case "dashboard":
                Button btnDashboard = (Button) fechaHoraLabel.getScene().lookup("#btnDashboard");
                if (btnDashboard != null)
                    btnDashboard.getStyleClass().add("menu-button-active");
                break;
            case "inventario":
                Button btnInventario = (Button) fechaHoraLabel.getScene().lookup("#btnInventario");
                if (btnInventario != null)
                    btnInventario.getStyleClass().add("menu-button-active");
                break;
            case "clientes":
                Button btnClientes = (Button) fechaHoraLabel.getScene().lookup("#btnClientes");
                if (btnClientes != null)
                    btnClientes.getStyleClass().add("menu-button-active");
                break;
            case "proveedores":
                Button btnProveedores = (Button) fechaHoraLabel.getScene().lookup("#btnProveedores");
                if (btnProveedores != null)
                    btnProveedores.getStyleClass().add("menu-button-active");
                break;
            case "ventas":
                Button btnVentas = (Button) fechaHoraLabel.getScene().lookup("#btnVentas");
                if (btnVentas != null)
                    btnVentas.getStyleClass().add("menu-button-active");
                break;
            case "reportes":
                Button btnReportes = (Button) fechaHoraLabel.getScene().lookup("#btnReportes");
                if (btnReportes != null)
                    btnReportes.getStyleClass().add("menu-button-active");
                break;
            case "configuracion":
                Button btnConfiguracion = (Button) fechaHoraLabel.getScene().lookup("#btnConfiguracion");
                if (btnConfiguracion != null)
                    btnConfiguracion.getStyleClass().add("menu-button-active");
                break;
            case "usuarios":
                Button btnUsuarios = (Button) fechaHoraLabel.getScene().lookup("#btnUsuarios");
                if (btnUsuarios != null)
                    btnUsuarios.getStyleClass().add("menu-button-active");
                break;
        }
    }

    /**
     * ✅ CORRECCIÓN: Método unificado para la carga de datos.
     * Es llamado por initialize (una vez) y por refrescarDatos().
     */
    private void configurarValoresIniciales() {
        try {
            // Configurar fecha/hora
            if (fechaHoraLabel != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                fechaHoraLabel.setText(now.format(formatter));
            }
            // Configurar usuario y rol
            if (usuarioLogueado != null) {
                if (usuarioLabel != null)
                    usuarioLabel.setText(usuarioLogueado.getNombreCompleto());
                if (rolLabel != null)
                    rolLabel.setText(usuarioLogueado.getRol().getDescripcion());
            } else {
                if (usuarioLabel != null)
                    usuarioLabel.setText("Usuario no identificado");
                if (rolLabel != null)
                    rolLabel.setText("Sin rol");
            }

            // Cargar todos los datos del dashboard
            cargarDatosProductos();
            cargarDatosVentas();
            cargarDatosClientes();
            cargarProductosStockBajo();
            cargarVentasRecientes();

            // Ocultar módulos para VENDEDOR
            if (usuarioLogueado != null && usuarioLogueado.isVendedor()) {
                Button btnReportes = (Button) fechaHoraLabel.getScene().lookup("#btnReportes");
                Button btnConfiguracion = (Button) fechaHoraLabel.getScene().lookup("#btnConfiguracion");
                Button btnUsuarios = (Button) fechaHoraLabel.getScene().lookup("#btnUsuarios");
                if (btnReportes != null) {
                    btnReportes.setVisible(false);
                    btnReportes.setManaged(false);
                }
                if (btnConfiguracion != null) {
                    btnConfiguracion.setVisible(false);
                    btnConfiguracion.setManaged(false);
                }
                if (btnUsuarios != null) {
                    btnUsuarios.setVisible(false);
                    btnUsuarios.setManaged(false);
                }
            }
        } catch (Exception e) {
            logger.error("Error configurando valores iniciales: {}", e.getMessage(), e);
        }
    }

    private void configurarRelojTiempoReal() {
        relojTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizarReloj()));
        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();
    }

    private void actualizarReloj() {
        if (fechaHoraLabel != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            fechaHoraLabel.setText(now.format(formatter));
        }
    }

    /**
     * ✅ MÉTODO PRINCIPAL DE REFRESCO DE DATOS
     */
    public void refrescarDatos() {
        // Asegurarse de que la actualización se haga en el hilo de la UI
        Platform.runLater(this::configurarValoresIniciales);
    }

    // Archivo: DashboardController.java

    // Archivo: DashboardController.java

    private void cargarDatosProductos() {
        try {
            List<Producto> productos = productoService.obtenerTodos();
            // Total de productos
            if (productosLabel != null) {
                productosLabel.setText(String.valueOf(productos.size()));
            }
            // Sincronizar contador de stock bajo con la lista real
            List<Producto> productosStockBajo = productoService.obtenerProductosStockBajo();
            logger.info("[DEBUG STOCK BAJO] Productos con stock bajo: {}", productosStockBajo.size());
            for (Producto p : productosStockBajo) {
                logger.info("[DEBUG STOCK BAJO] {} - Stock: {} (Min: {})", p.getNombre(), p.getStockActual(),
                        p.getStockMinimo());
            }
            if (productosStockLabel != null) {
                productosStockLabel.setText(productosStockBajo.size() + " con stock bajo");
            }
        } catch (Exception e) {
            logger.error("[ERROR STOCK BAJO] Fallo al cargar datos de productos: {}", e.getMessage(), e);
            if (productosLabel != null)
                productosLabel.setText("0");
            if (productosStockLabel != null)
                productosStockLabel.setText("0 con stock bajo");
        }
    }

    private void cargarDatosVentas() {
        try {
            if (ventasHoyLabel != null) {
                // CORRECCIÓN: Usar String.format para asegurar 2 decimales
                ventasHoyLabel.setText("S/ " + String.format("%.2f", obtenerMontoVentasHoyDesdeBD()));
            }
            if (ventasHoyCountLabel != null) {
                // CORRECCIÓN: El método obtenerVentasHoyDesdeBD() ahora cuenta transacciones
                // (boletas)
                int ventasHoy = obtenerVentasHoyDesdeBD();
                ventasHoyCountLabel.setText(ventasHoy + " ventas realizadas");
            }
        } catch (Exception e) {
            if (ventasHoyLabel != null)
                ventasHoyLabel.setText("S/ 0.00");
            if (ventasHoyCountLabel != null)
                ventasHoyCountLabel.setText("0 ventas realizadas");
            logger.warn("Error al cargar datos de ventas: {}", e.getMessage());
        }
    }

    private void cargarDatosClientes() {
        try {
            // Total de clientes desde la BD
            int totalClientes = obtenerClientesDesdeBD();
            if (clientesLabel != null) {
                clientesLabel.setText(String.valueOf(totalClientes));
            }

            // Clientes nuevos este mes desde la BD
            int clientesNuevos = obtenerClientesNuevosDesdeBD();
            if (clientesNuevosLabel != null) {
                clientesNuevosLabel.setText(clientesNuevos + " nuevos este mes");
            }
        } catch (Exception e) {
            if (clientesLabel != null)
                clientesLabel.setText("0");
            if (clientesNuevosLabel != null)
                clientesNuevosLabel.setText("0 nuevos este mes");
            logger.warn("Error al cargar datos de clientes: {}", e.getMessage());
        }
    }

    // En DashboardController.java

    private void cargarProductosStockBajo() {
        try {
            if (stockBajoTable != null) {
                List<Producto> productosStockBajo = productoService.obtenerProductosStockBajo();
                ObservableList<ProductoStockBajo> items = FXCollections.observableArrayList();
                for (Producto producto : productosStockBajo) {
                    items.add(new ProductoStockBajo(
                            producto.getNombre(),
                            producto.getStockActual(),
                            producto.getStockMinimo()));
                }
                stockBajoTable.setItems(items);
                if (productosStockLabel != null) {
                    productosStockLabel.setText(productosStockBajo.size() + " con stock bajo");
                }
            }
        } catch (Exception e) {
            if (stockBajoTable != null) {
                stockBajoTable.setItems(FXCollections.observableArrayList());
            }
            if (productosStockLabel != null) {
                productosStockLabel.setText("Error");
            }
            logger.error("Error al cargar productos con stock bajo: {}", e.getMessage(), e);
        }
    }

    private void cargarVentasRecientes() {
        try {
            if (ultimasVentasTable != null) {
                List<String> ultimasVentas = obtenerUltimasVentasDesdeBD();
                ObservableList<VentaReciente> ventas = javafx.collections.FXCollections.observableArrayList();
                for (String venta : ultimasVentas) {
                    String[] partes = venta.split("\\|");
                    if (partes.length == 4) {
                        ventas.add(new VentaReciente(
                                partes[0].trim(),
                                partes[1].trim(),
                                partes[2].trim(),
                                partes[3].trim()));
                    }
                }
                ultimasVentasTable.setItems(ventas);
            }
        } catch (Exception e) {
            if (ultimasVentasTable != null) {
                ultimasVentasTable.setItems(javafx.collections.FXCollections.observableArrayList());
            }
            logger.error("Error al cargar ventas recientes: {}", e.getMessage(), e);
        }
    }

    // Implementación de métodos requeridos por las interfaces
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas del TableView de Stock Bajo
        if (colProducto != null)
            colProducto.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProducto()));
        if (colStockActual != null)
            colStockActual.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStockActual()));
        if (colStockMinimo != null)
            colStockMinimo.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStockMinimo()));
        // Inicializar reloj
        configurarRelojTiempoReal();

        // Configurar columnas del TableView de ventas recientes
        if (colFechaHora != null)
            colFechaHora.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaHora()));
        if (colCliente != null)
            colCliente.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCliente()));
        if (colTotal != null)
            colTotal.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTotal()));
        if (colMetodoPago != null)
            colMetodoPago.setCellValueFactory(
                    data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComprobanteSerie()));

        // ✅ CORRECCIÓN: Unificar la carga de datos inicial en una sola llamada
        // y asegurarla en el hilo de la UI.
        Platform.runLater(this::configurarValoresIniciales);

        // Suscribirse a eventos para actualizaciones en tiempo real
        eventManager.subscribe(this);
    }

    @Override
    public void onEvent(SystemEvent event) {
        logger.info("Evento recibido: " + event.toString());

        // Refrescar el dashboard ante eventos relevantes
        // ✅ CORRECCIÓN 1: Usar event.getTipo() en lugar de event.getType()
        // ✅ CORRECCIÓN 2: Usar SystemEvent.EventType en lugar de SystemEvent.Type
        if ((event.getTipo() == SystemEvent.EventType.VENTA_REGISTRADA) ||
                (event.getTipo() == SystemEvent.EventType.PRODUCTO_ACTUALIZADO) ||
                (event.getTipo() == SystemEvent.EventType.CLIENTE_ACTUALIZADO)) {
            refrescarDatos();
        }
    }

    @Override
    public String getObserverId() {
        return "DashboardController";
    }

    // ✅ MÉTODOS DE NAVEGACIÓN (Se eliminaron onProductos/onProveedores duplicados)

    @FXML
    private void showClientes() {
        try {
            com.farmaciavictoria.proyectopharmavictoria.service.ClienteService clienteService = ServiceContainer
                    .getInstance().getService(com.farmaciavictoria.proyectopharmavictoria.service.ClienteService.class);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Cliente/clientes.fxml"));
            Parent clientesView = loader.load();
            com.farmaciavictoria.proyectopharmavictoria.controller.Cliente.ClientesController clientesController = loader
                    .getController();
            clientesController.setClienteService(clienteService);

            try {
                String css = getClass().getResource("/css/Cliente/clientes.css").toExternalForm();
                clientesView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de clientes: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(clientesView);
                marcarBotonActivo("clientes");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Clientes.");
            }
        } catch (IOException e) {
            mostrarError("Error", "No se pudo mostrar el módulo de Clientes: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error", "Error inesperado al mostrar Clientes: " + e.getMessage());
        }
    }

    @FXML
    private void showInventario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventario/productos.fxml"));
            Parent inventarioView = loader.load();
            try {
                String css = getClass().getResource("/css/Inventario/productos.css").toExternalForm();
                inventarioView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de inventario: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(inventarioView);
                marcarBotonActivo("inventario");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Inventario.");
            }
        } catch (Exception e) {
            mostrarError("Error",
                    "El módulo Inventario no está disponible o hubo un error de carga: " + e.getMessage());
        }
    }

    @FXML
    private void showProveedores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Proveedor/proveedores.fxml"));
            Parent proveedoresView = loader.load();
            try {
                String css = getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm();
                proveedoresView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de proveedores: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(proveedoresView);
                marcarBotonActivo("proveedores");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Proveedores.");
            }
        } catch (Exception e) {
            mostrarError("Error",
                    "El módulo Proveedores no está disponible o hubo un error de carga: " + e.getMessage());
        }
    }

    @FXML
    private void showVentas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Ventas/ventas.fxml"));
            Parent ventasView = loader.load();
            try {
                String css = getClass().getResource("/css/Ventas/ventas.css").toExternalForm();
                ventasView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de ventas: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(ventasView);
                marcarBotonActivo("ventas");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Punto de Venta.");
            }
        } catch (Exception e) {
            // Imprimir el stacktrace en el terminal
            System.err.println("No se pudo mostrar el módulo de Punto de Venta:");
            e.printStackTrace();
            mostrarError("Error", "No se pudo mostrar el módulo de Punto de Venta. Ver detalles en la consola.");
        }
    }

    @FXML
    private void showReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes/reportes.fxml"));
            Parent reportesView = loader.load();
            try {
                String css = getClass().getResource("/css/reportes.css").toExternalForm();
                reportesView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de reportes: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(reportesView);
                marcarBotonActivo("reportes");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Reportes.");
            }
        } catch (Exception e) {
            logger.error("Error al mostrar el módulo de Reportes: {}", e.getMessage(), e);
            mostrarError("Error",
                    "No se pudo mostrar el módulo de Reportes. Verifica la estructura y los archivos FXML.");
        }
    }

    @FXML
    private void showConfiguracion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configuracion.fxml"));
            Parent configuracionView = loader.load();
            try {
                String css = getClass().getResource("/css/configuracion.css").toExternalForm();
                configuracionView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de configuración: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(configuracionView);
                marcarBotonActivo("configuracion");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Configuración.");
            }
        } catch (Exception e) {
            logger.error("Error técnico al cargar módulo Configuración:", e);
            mostrarError("Error técnico",
                    "No se pudo mostrar el módulo de Configuración.\n\nDetalles: " + e.getMessage());
        }
    }

    @FXML
    private void showUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Usuario/usuarios.fxml"));
            Parent usuariosView = loader.load();
            // Pasar el usuario autenticado al controlador de usuarios
            UsuariosController usuariosController = loader.getController();
            if (usuariosController != null && usuarioLogueado != null) {
                usuariosController.setUsuarioAutenticado(usuarioLogueado);
            }
            try {
                String css = getClass().getResource("/css/Usuario/usuarios.css").toExternalForm();
                usuariosView.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("No se pudo cargar CSS de usuarios: {}", e.getMessage());
            }
            StackPane contentArea = (StackPane) fechaHoraLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(usuariosView);
                marcarBotonActivo("usuarios");
            } else {
                mostrarError("Error", "No se pudo mostrar el módulo de Usuarios.");
            }
        } catch (Exception e) {
            logger.error("Error técnico al cargar módulo Usuarios:", e);
            mostrarError("En desarrollo", "El módulo Usuarios aún no está disponible.\n\nDetalles: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        onCerrarSesion();
    }

    @FXML
    private void onCerrarSesion() {
        try {
            if (relojTimeline != null) {
                relojTimeline.stop();
            }
            eventManager.unsubscribe(this);
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            // Usar la misma lógica y animación que al inicio
            PharmavictoriaApplication app = new PharmavictoriaApplication();
            Parent root = app.loadLoginScreenWithFade(stage);
            stage.setTitle("PHARMAVICTORIA - Iniciar Sesión");
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();
            logger.info("Sesión cerrada correctamente");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: {}", e.getMessage(), e);
            mostrarError("Error", "No se pudo cerrar la sesión correctamente.");
        }
    }

    @FXML
    private void nuevoCliente() {
        try {
            logger.info("Abriendo formulario de nuevo cliente...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente-form.fxml"));
            Parent root = loader.load();
            ClienteFormController formController = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Nuevo Cliente");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            Cliente nuevo = formController.getClienteFromForm();
            if (nuevo != null && nuevo.getNombres() != null && !nuevo.getNombres().trim().isEmpty()) {
                ServiceContainer.getInstance().getClienteService().guardarCliente(nuevo);
                // ✅ CORRECCIÓN: Refrescar el dashboard para ver el nuevo cliente inmediatamente
                refrescarDatos();
            }
            logger.info("✅ Formulario de nuevo cliente iniciado");
        } catch (Exception e) {
            logger.error("❌ Error al abrir nuevo cliente: {}", e.getMessage(), e);
            mostrarError("Error", "Error al crear nuevo cliente: " + e.getMessage());
        }
    }

    @FXML
    private void nuevaVenta() {
        mostrarError("En desarrollo", "La funcionalidad de nueva venta rápida aún no está disponible.");
    }

    @FXML
    private void agregarProducto() {
        mostrarError("En desarrollo", "La funcionalidad de agregar producto rápido aún no está disponible.");
    }

    // ✅ MÉTODOS AUXILIARES PARA OBTENER DATOS REALES DE LA BASE DE DATOS

    /**
     * Obtener monto total de ventas de hoy desde la BD
     */
    private double obtenerMontoVentasHoyDesdeBD() {
        String sql = "SELECT COALESCE(SUM(total), 0.0) as monto FROM ventas WHERE DATE(fecha_venta) = CURDATE() AND estado = 'REALIZADA'";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("monto");
            }
        } catch (SQLException e) {
            logger.warn("Error al consultar monto de ventas desde tabla ventas: {}", e.getMessage());
            return 0.0;
        }
        return 0.0;
    }

    /**
     * Obtener lista de últimas ventas
     */
    private List<String> obtenerUltimasVentasDesdeBD() {
        // Consulta: fecha, cliente_id, total, tipo_comprobante, numero_boleta (máx 5)
        String sql = "SELECT fecha_venta, cliente_id, total, tipo_comprobante, numero_boleta FROM ventas WHERE estado = 'REALIZADA' ORDER BY fecha_venta DESC LIMIT 5";
        java.util.List<String> ventas = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String fecha = rs.getString("fecha_venta");
                int clienteId = rs.getInt("cliente_id");
                double total = rs.getDouble("total");
                String tipoComprobante = rs.getString("tipo_comprobante");
                String numeroBoleta = rs.getString("numero_boleta");
                String clienteNombre = "";
                // Buscar nombre del cliente
                try {
                    com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository clienteRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository();
                    java.util.Optional<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clienteOpt = clienteRepo
                            .findById(clienteId);
                    clienteNombre = clienteOpt
                            .map(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente::getNombreCompleto)
                            .orElse("-");
                } catch (Exception ex) {
                    clienteNombre = "-";
                }
                String comprobanteBoleta = tipoComprobante + " - " + numeroBoleta;
                String resumen = String.format("%s | %s | S/ %.2f | %s", fecha, clienteNombre, total,
                        comprobanteBoleta);
                ventas.add(resumen);
            }
        } catch (SQLException e) {
            logger.warn("Error al consultar últimas ventas: {}", e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtener número real de transacciones de hoy desde la BD (número de boletas
     * únicas)
     */
    private int obtenerVentasHoyDesdeBD() {
        String sql = """
                SELECT COUNT(DISTINCT numero_boleta) as total
                FROM ventas
                WHERE DATE(fecha_venta) = CURDATE()
                """;

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.warn("Error al consultar transacciones desde tabla ventas: {}", e.getMessage());
            return 0;
        }
        return 0;
    }

    /**
     * Obtener número real de clientes desde la BD
     */
    private int obtenerClientesDesdeBD() {
        String sql = "SELECT COUNT(*) as total FROM clientes WHERE dni <> '00000000' AND UPPER(nombres) <> 'GENÉRICO'";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.warn("Error al consultar tabla clientes: {}", e.getMessage());
            return 0;
        }
        return 0;
    }

    /**
     * Obtener número real de clientes nuevos del mes desde la BD
     */
    private int obtenerClientesNuevosDesdeBD() {
        String sql = """
                SELECT COUNT(*) as total
                FROM clientes
                WHERE YEAR(created_at) = YEAR(CURDATE())
                AND MONTH(created_at) = MONTH(CURDATE())
                AND dni <> '00000000'
                AND UPPER(nombres) <> 'GENÉRICO'
                """;

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.warn("Error al consultar clientes nuevos desde tabla clientes: {}", e.getMessage());
            return 0;
        }
        return 0;
    }

    /**
     * Establecer el usuario logueado y actualizar la interfaz
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;

        Platform.runLater(() -> {
            if (usuarioLabel != null) {
                usuarioLabel.setText(
                        usuarioLogueado != null ? usuarioLogueado.getNombreCompleto() : "Usuario no identificado");
                logger.debug("Usuario establecido en dashboard: {}",
                        usuarioLogueado != null ? usuarioLogueado.getNombreCompleto() : "null");
            }

            if (rolLabel != null) {
                rolLabel.setText(usuarioLogueado != null ? usuarioLogueado.getRol().getDescripcion() : "Sin rol");
                logger.debug("Rol establecido en dashboard: {}",
                        usuarioLogueado != null ? usuarioLogueado.getRol().getDescripcion() : "null");
            }

            // Refrescar datos después de asignar el usuario
            refrescarDatos();
        });
    }

    /**
     * UI: Mostrar mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    // NOTA: El método obtenerTransaccionesHoyDesdeBD() ha sido declarado redundante
    // ya que obtenerVentasHoyDesdeBD() ahora cuenta transacciones únicas (boletas).
    private int obtenerTransaccionesHoyDesdeBD() {
        return obtenerVentasHoyDesdeBD();
    }

    // NOTA: El método obtenerStockBajoNombres() es redundante con
    // cargarProductosStockBajo()
    // y se eliminó su uso.
    private List<String> obtenerStockBajoNombres() {
        return productoService.obtenerProductosStockBajo().stream().map(Producto::getNombre).toList();
    }

    /**
     * Clase modelo para las ventas recientes en el TableView
     */
    public static class VentaReciente {
        private final String fechaHora;
        private final String cliente;
        private final String total;
        private final String comprobanteSerie;

        public VentaReciente(String fechaHora, String cliente, String total, String comprobanteSerie) {
            this.fechaHora = fechaHora;
            this.cliente = cliente;
            this.total = total;
            this.comprobanteSerie = comprobanteSerie;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public String getCliente() {
            return cliente;
        }

        public String getTotal() {
            return total;
        }

        public String getComprobanteSerie() {
            return comprobanteSerie;
        }
    }

    /**
     * Clase modelo para los productos con stock bajo en el TableView
     */
    public static class ProductoStockBajo {
        private final String producto;
        private final String stockActual;
        private final String stockMinimo;

        public ProductoStockBajo(String producto, int stockActual, int stockMinimo) {
            this.producto = producto;
            this.stockActual = String.valueOf(stockActual);
            this.stockMinimo = String.valueOf(stockMinimo);
        }

        public String getProducto() {
            return producto;
        }

        public String getStockActual() {
            return stockActual;
        }

        public String getStockMinimo() {
            return stockMinimo;
        }
    }
}