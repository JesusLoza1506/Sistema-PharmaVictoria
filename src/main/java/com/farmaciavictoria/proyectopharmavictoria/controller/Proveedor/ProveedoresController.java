package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.service.NotificationService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

public class ProveedoresController implements Initializable {
    @FXML
    private Button btnNuevoProveedor;
    @FXML
    private Label lblGraficoTopProveedores;
    @FXML
    private Label lblGraficoEstadoProveedores;
    @FXML
    private Button btnContactarProveedores;
    @FXML
    private Button btnExportar;

    // Permisos granulares del vendedor para Proveedores
    private boolean permisoNuevoProveedor = false;
    private boolean permisoExportar = false;
    private boolean permisoContactar = false;
    private boolean permisoEditar = false;
    private boolean permisoVer = false;
    private boolean permisoToggleEstado = false;
    private boolean permisoEliminar = false;
    private boolean permisoDashboardTop = false;
    private boolean permisoDashboardEstado = false;

    @FXML
    private void onContactarProveedores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Proveedor/contactar_proveedores.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Contactar Proveedores");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al abrir panel de contacto", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private Button btnAjustes;

    @FXML
    private void onEnviarCorreo() {
        String user = System.getProperty("mail.smtp.user", "TU_CORREO@gmail.com");
        String pass = System.getProperty("mail.smtp.password", "TU_CONTRASEÑA");
        com.farmaciavictoria.proyectopharmavictoria.service.EmailService emailService = new com.farmaciavictoria.proyectopharmavictoria.service.EmailService(
                user, pass);
        try {
            emailService.sendEmail("destinatario@correo.com", "Prueba desde PHARMAVICTORIA",
                    "¡Correo enviado correctamente desde el sistema!");
            mostrarAlerta("Correo enviado", "El correo se envió correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error al enviar correo", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    // Dashboard: Gráficas
    @FXML
    private javafx.scene.chart.BarChart<String, Number> barChartTopProveedores;
    @FXML
    private javafx.scene.chart.PieChart pieChartEstadoProveedores;

    // Inicializa y carga las gráficas del dashboard inferior
    private void inicializarDashboardGraficas() {
        cargarGraficaTopProveedores();
        cargarGraficaEstadoProveedores();
    }

    // Carga la gráfica de barras: Top proveedores con más productos asociados
    // Utiliza ProductoService y mapeo por proveedorId/proveedorNombre
    private void cargarGraficaTopProveedores() {
        GraficasProveedores.cargarTop(barChartTopProveedores);
    }

    // Carga la gráfica de pastel: Distribución de proveedores por estado
    private void cargarGraficaEstadoProveedores() {
        GraficasProveedores.cargarEstado(pieChartEstadoProveedores, proveedoresOriginales);
    }

    @FXML
    public void onExportarProveedores(javafx.event.ActionEvent event) {
        exportar();
    }

    @FXML
    private void exportar() {
        abrirVistaPreviaExportacion();
    }

    private void abrirVistaPreviaExportacion() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/Proveedor/exportar-proveedores-preview.fxml"));
            javafx.scene.Parent root = loader.load();
            ExportarProveedoresController controller = loader.getController();
            List<Proveedor> proveedores = proveedoresOriginales != null ? proveedoresOriginales : proveedoresList;
            controller.setProveedoresFiltrados(proveedores);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Vista previa de exportación de proveedores");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir la vista previa de exportación", e.getMessage());
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ProveedoresController.class);

    // Evento para avanzar una página en la tabla de proveedores
    @FXML
    public void onPaginaSiguiente(javafx.event.ActionEvent event) {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            actualizarPaginacion();
        }
    }

    // Variables de paginación
    private int paginaActual = 1;
    private int totalPaginas = 1;
    private int tamanoPagina = -1; // -1 = Todos por defecto

    // Actualiza la paginación de la tabla de proveedores
    private void actualizarPaginacion() {
        // Ahora solo llama a filtrarProveedores para que la paginación siempre respete
        // el filtro
        filtrarProveedores();
    }

    // Evento para retroceder una página en la tabla de proveedores
    @FXML
    public void onPaginaAnterior(javafx.event.ActionEvent event) {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarPaginacion();
        }
    }

    // Maneja el evento de activar/desactivar el filtro de solo activos
    @FXML
    public void onToggleSoloActivos(javafx.event.ActionEvent event) {
        boolean soloActivos = chkSoloActivos.isSelected();
        // Filtrar la lista según el estado del checkbox
        List<Proveedor> filtrados = proveedoresOriginales.stream()
                .filter(p -> !soloActivos || p.getActivo())
                .collect(java.util.stream.Collectors.toList());
        proveedoresList.setAll(filtrados);
        actualizarEstadisticas();
    }

    // Servicios
    private ProveedorService proveedorService;
    private NotificationService notificationService;

    // Estrategia de búsqueda avanzada ELIMINADA: ahora solo se usan los nuevos
    // filtros con ProveedorFilterStrategy

    // Listas de proveedores
    private ObservableList<Proveedor> proveedoresList;
    private ObservableList<Proveedor> proveedoresOriginales;

    @FXML
    private ComboBox<String> cmbTipoFiltro;

    // Filtro avanzado usando Strategy Pattern
    private void aplicarFiltroAvanzado() {
        String criterio = txtBuscar.getText();
        String tipo = cmbTipoFiltro != null ? cmbTipoFiltro.getValue() : "Razón Social";
        List<Proveedor> base = proveedoresOriginales != null ? proveedoresOriginales : proveedoresList;
        com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.ProveedorFilterStrategy strategy = null;
        // Selección estricta del campo a filtrar
        switch (tipo) {
            case "Razón Social":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.FiltroPorRazonSocial();
                break;
            case "RUC":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.FiltroPorRuc();
                break;
            case "Contacto":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.FiltroPorContacto();
                break;
            case "Tipo de Producto":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.FiltroPorTipoProducto();
                break;
            default:
                strategy = null;
        }
        List<Proveedor> filtrados;
        if (strategy != null && criterio != null && !criterio.isBlank()) {
            filtrados = strategy.filtrar(base, criterio);
        } else {
            filtrados = base;
        }
        // Mostrar solo los filtrados, ignorando paginación
        proveedoresList.clear();
        proveedoresList.addAll(filtrados);
        actualizarEstadisticas();
    }

    @FXML
    private Label lblContadorProveedores;
    @FXML
    private Label lblContadorTotal;
    @FXML
    private Button btnPaginaAnterior;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Button btnPaginaSiguiente;
    @FXML
    private ComboBox<Integer> cmbTamanoPagina;
    @FXML
    private Label lblTotalActivos;
    @FXML
    private Label lblTotalInactivos;
    @FXML
    private Label lblTotalVIP;
    @FXML
    private Label lblTotalObservacion;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnHistorial;
    @FXML
    private ComboBox<String> cmbOrdenar;
    @FXML
    private CheckBox chkSoloActivos;
    @FXML
    private TableView<Proveedor> tableProveedores;
    @FXML
    private TableColumn<Proveedor, Integer> colId;
    @FXML
    private TableColumn<Proveedor, String> colRazonSocial;
    @FXML
    private TableColumn<Proveedor, String> colRuc;
    @FXML
    private TableColumn<Proveedor, String> colContacto;
    @FXML
    private TableColumn<Proveedor, String> colTelefono;
    @FXML
    private TableColumn<Proveedor, String> colEmail;
    @FXML
    private TableColumn<Proveedor, String> colTipoProducto;
    @FXML
    private TableColumn<Proveedor, String> colEstado;
    @FXML
    private TableColumn<Proveedor, Void> colAcciones;
    @FXML
    private Label lblEstadistica;
    @FXML
    private Label lblUltimaActualizacion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Control de visibilidad por rol
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                .getInstance().getAuthenticationService().getUsuarioActual();
        boolean esAdmin = usuario != null && usuario.isAdmin();
        boolean esVendedor = usuario != null && usuario.isVendedor();

        // Cargar permisos granulares del vendedor para Proveedores
        if (esVendedor) {
            var usuarioService = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer.getInstance()
                    .getUsuarioService();
            permisoNuevoProveedor = usuarioService.tienePermiso(usuario.getId(), "proveedores", "nuevo");
            permisoExportar = usuarioService.tienePermiso(usuario.getId(), "proveedores", "exportar");
            permisoContactar = usuarioService.tienePermiso(usuario.getId(), "proveedores", "contactar");
            permisoEditar = usuarioService.tienePermiso(usuario.getId(), "proveedores", "editar");
            // El permiso "ver" SIEMPRE debe estar habilitado para vendedores
            permisoVer = true;
            permisoToggleEstado = usuarioService.tienePermiso(usuario.getId(), "proveedores", "activar_inactivar");
            permisoEliminar = usuarioService.tienePermiso(usuario.getId(), "proveedores", "eliminar");
            permisoDashboardTop = usuarioService.tienePermiso(usuario.getId(), "proveedores", "dashboard_top");
            permisoDashboardEstado = usuarioService.tienePermiso(usuario.getId(), "proveedores", "dashboard_estado");
        }

        // Control de visibilidad por permisos
        if (btnNuevoProveedor != null) {
            btnNuevoProveedor.setVisible(esAdmin || permisoNuevoProveedor);
            btnNuevoProveedor.setManaged(esAdmin || permisoNuevoProveedor);
        }
        if (btnExportar != null) {
            // Solo mostrar si el permiso está habilitado para el vendedor
            boolean mostrarExportar = esAdmin || (esVendedor && permisoExportar);
            btnExportar.setVisible(mostrarExportar);
            btnExportar.setManaged(mostrarExportar);
        }
        if (btnContactarProveedores != null) {
            btnContactarProveedores.setVisible(esAdmin || permisoContactar);
            btnContactarProveedores.setManaged(esAdmin || permisoContactar);
        }
        if (barChartTopProveedores != null) {
            barChartTopProveedores.setVisible(esAdmin || permisoDashboardTop);
            barChartTopProveedores.setManaged(esAdmin || permisoDashboardTop);
        }
        if (lblGraficoTopProveedores != null) {
            lblGraficoTopProveedores.setVisible(esAdmin || permisoDashboardTop);
            lblGraficoTopProveedores.setManaged(esAdmin || permisoDashboardTop);
        }
        if (pieChartEstadoProveedores != null) {
            pieChartEstadoProveedores.setVisible(esAdmin || permisoDashboardEstado);
            pieChartEstadoProveedores.setManaged(esAdmin || permisoDashboardEstado);
        }
        if (lblGraficoEstadoProveedores != null) {
            lblGraficoEstadoProveedores.setVisible(esAdmin || permisoDashboardEstado);
            lblGraficoEstadoProveedores.setManaged(esAdmin || permisoDashboardEstado);
        }
        // Configurar ComboBox de tipo de búsqueda avanzada
        if (cmbTipoFiltro != null) {
            cmbTipoFiltro.setItems(FXCollections.observableArrayList(
                    "Razón Social", "RUC", "Contacto", "Tipo de Producto"));
            cmbTipoFiltro.getSelectionModel().selectFirst();
            cmbTipoFiltro.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtBuscar != null)
                    txtBuscar.clear();
                aplicarFiltroAvanzado();
            });
        }
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroAvanzado());
        System.out.println("Inicializando ProveedoresController...");

        // Obtener services desde ServiceContainer
        ServiceContainer container = ServiceContainer.getInstance();
        this.proveedorService = container.getProveedorService();
        this.notificationService = container.getNotificationService();

        // Inicializar listas
        proveedoresList = FXCollections.observableArrayList();
        proveedoresOriginales = FXCollections.observableArrayList();
        // Configurar ComboBox de tamaño de página (paginación)
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

        // Configurar tabla
        configurarTabla();

        // Aplicar CSS personalizado al BarChart
        if (barChartTopProveedores != null) {
            barChartTopProveedores.getStylesheets()
                    .add(getClass().getResource("/css/Proveedor/barChart-custom.css").toExternalForm());
        }

        // Cargar datos iniciales
        cargarProveedores();

        System.out.println("ProveedoresController inicializado correctamente");
    }

    // Configurar la tabla de proveedores

    private void configurarTabla() {
        // Configurar columnas
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colRuc.setCellValueFactory(new PropertyValueFactory<>("ruc"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        // Asegurar que la columna de acciones sea visible y no se compacte demasiado
        try {
            if (colAcciones != null) {
                colAcciones.setPrefWidth(200);
                colAcciones.setMinWidth(140);
                colAcciones.setMaxWidth(300);
            }
        } catch (Exception ex) {
            // No bloquear la inicialización si algo falla al ajustar tamaños
            logger.warn("No se pudo ajustar anchos de columnas de acciones: {}", ex.getMessage());
        }
        // colCondicionesPago.setVisible(false); // Ahora visible
        colEstado.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().getActivo();
            return new javafx.beans.property.SimpleStringProperty(activo ? "Activo" : "Inactivo");
        });
        configurarColumnaAcciones();
        tableProveedores.setRowFactory(tv -> {
            TableRow<Proveedor> row = new TableRow<Proveedor>() {
                @Override
                protected void updateItem(Proveedor proveedor, boolean empty) {
                    super.updateItem(proveedor, empty);
                    getStyleClass().removeAll("row-activo", "row-inactivo", "row-vip", "row-observacion");
                    if (empty || proveedor == null) {
                        setStyle("");
                    } else {
                        if (!proveedor.getActivo()) {
                            getStyleClass().add("row-inactivo");
                        } else {
                            getStyleClass().add("row-activo");
                        }
                    }
                }
            };
            // Solo click derecho para historial de cambios
            row.setOnMouseClicked(event -> {
                if (event.isSecondaryButtonDown() && !row.isEmpty()) {
                    mostrarHistorialProveedor(row.getItem());
                }
            });
            return row;
        });
        tableProveedores.setItems(proveedoresList);
    }

    // Mostrar historial de cambios de un proveedor
    private void mostrarHistorialProveedor(Proveedor proveedor) {
        List<ProveedorRepository.HistorialCambioDTO> historial = proveedorService
                .obtenerHistorialCambios(proveedor.getId());
        if (historial.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historial de Cambios");
            alert.setHeaderText("Proveedor: " + proveedor.getRazonSocial());
            alert.setContentText("No hay historial de cambios para este proveedor.");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.TableView<ProveedorRepository.HistorialCambioDTO> table = new javafx.scene.control.TableView<>();
        javafx.scene.control.TableColumn<ProveedorRepository.HistorialCambioDTO, String> colFecha = new javafx.scene.control.TableColumn<>(
                "Fecha");
        javafx.scene.control.TableColumn<ProveedorRepository.HistorialCambioDTO, String> colUsuario = new javafx.scene.control.TableColumn<>(
                "Usuario");
        javafx.scene.control.TableColumn<ProveedorRepository.HistorialCambioDTO, String> colCampo = new javafx.scene.control.TableColumn<>(
                "Campo");
        javafx.scene.control.TableColumn<ProveedorRepository.HistorialCambioDTO, String> colAnterior = new javafx.scene.control.TableColumn<>(
                "Anterior");
        javafx.scene.control.TableColumn<ProveedorRepository.HistorialCambioDTO, String> colNuevo = new javafx.scene.control.TableColumn<>(
                "Nuevo");

        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().fecha != null
                        ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(cell.getValue().fecha)
                        : ""));
        colUsuario.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().usuario));
        colCampo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().campo));
        colAnterior
                .setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().anterior));
        colNuevo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().nuevo));

        table.getColumns().addAll(colFecha, colUsuario, colCampo, colAnterior, colNuevo);
        table.setItems(javafx.collections.FXCollections.observableArrayList(historial));
        table.setPrefHeight(300);
        table.setPrefWidth(700);

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(table);
        vbox.setPrefHeight(320);
        vbox.setPrefWidth(720);

        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Historial de Cambios");
        dialog.setHeaderText("Proveedor: " + proveedor.getRazonSocial());
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // Configurar la columna de acciones
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(column -> new CeldaAccionesProveedor(this::editarProveedor,
                this::verDetalles, this::toggleEstadoProveedor, this::eliminarProveedorDefinitivo,
                () -> permisoEditar, () -> permisoToggleEstado, () -> permisoEliminar));
    }

    // Búsqueda en tiempo real
    @FXML
    private void buscarProveedores(javafx.event.ActionEvent event) {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filtrarProveedores();
            }
        });
    }

    @FXML
    private void onBuscar(javafx.scene.input.KeyEvent event) {
        filtrarProveedores();
    }

    // SERVICE LAYER: Cargar proveedores usando ProveedorService
    // Migrado de acceso directo a Repository hacia Service Layer
    private void cargarProveedores() {
        try {
            System.out.println("Cargando proveedores...");

            // SERVICE LAYER: Cargando todos los proveedores (activos e inactivos) para
            // filtros
            List<Proveedor> proveedores = proveedorService.obtenerTodosIncluyendoInactivos();

            Platform.runLater(() -> {
                proveedoresOriginales.clear();
                proveedoresOriginales.addAll(proveedores);

                aplicarFiltros();
                actualizarEstadisticas();
                actualizarUltimaActualizacion();

                // Actualizar dashboard de gráficas
                inicializarDashboardGraficas();

                // Refuerza visibilidad del botón Exportar cada vez que se cargan proveedores
                com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                        .getInstance().getAuthenticationService().getUsuarioActual();
                boolean esAdmin = usuario != null && usuario.isAdmin();
                boolean esVendedor = usuario != null && usuario.isVendedor();
                boolean mostrarExportar = esAdmin || (esVendedor && permisoExportar);
                if (btnExportar != null) {
                    btnExportar.setVisible(mostrarExportar);
                    btnExportar.setManaged(mostrarExportar);
                }

                System.out.println("Proveedores cargados: " + proveedores.size());
            });

        } catch (Exception e) {
            System.err.println("Error cargando proveedores: " + e.getMessage());
            Platform.runLater(() -> {
                mostrarError("Error", "No se pudieron cargar los proveedores", e.getMessage());
            });
        }
    }

    // Método público para refrescar la tabla (llamado desde formularios)
    public void refrescarTabla() {
        cargarProveedores();
    }

    // Filtrar proveedores según criterios (búsqueda simple)
    private void filtrarProveedores() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        boolean soloActivos = chkSoloActivos.isSelected();
        String tipo = cmbTipoFiltro != null ? cmbTipoFiltro.getValue() : "Razón Social";

        List<Proveedor> proveedoresFiltrados = proveedoresOriginales.stream()
                .filter(p -> {
                    if (soloActivos && !p.getActivo()) {
                        return false;
                    }
                    if (!textoBusqueda.isEmpty()) {
                        switch (tipo) {
                            case "Razón Social":
                                return p.getRazonSocial() != null
                                        && p.getRazonSocial().toLowerCase().contains(textoBusqueda);
                            case "RUC":
                                return p.getRuc() != null && p.getRuc().toLowerCase().contains(textoBusqueda);
                            case "Contacto":
                                return p.getContacto() != null && p.getContacto().toLowerCase().contains(textoBusqueda);
                            case "Tipo de Producto":
                                return p.getTipoProducto() != null
                                        && p.getTipoProducto().toLowerCase().contains(textoBusqueda);
                            default:
                                return true;
                        }
                    }
                    return true;
                })
                .toList();

        int total = proveedoresFiltrados.size();
        if (tamanoPagina == -1) {
            proveedoresList.clear();
            proveedoresList.addAll(proveedoresFiltrados);
            if (lblPaginaActual != null)
                lblPaginaActual.setText("Mostrando todos");
            if (btnPaginaAnterior != null)
                btnPaginaAnterior.setDisable(true);
            if (btnPaginaSiguiente != null)
                btnPaginaSiguiente.setDisable(true);
        } else {
            totalPaginas = (int) Math.max(1, Math.ceil((double) total / tamanoPagina));
            if (paginaActual > totalPaginas)
                paginaActual = totalPaginas;
            if (paginaActual < 1)
                paginaActual = 1;
            int fromIndex = (paginaActual - 1) * tamanoPagina;
            int toIndex = Math.min(fromIndex + tamanoPagina, total);
            List<Proveedor> pagina = proveedoresFiltrados.subList(fromIndex, toIndex);
            proveedoresList.clear();
            proveedoresList.addAll(pagina);
            if (lblPaginaActual != null)
                lblPaginaActual.setText("Página " + paginaActual + " de " + totalPaginas);
            if (btnPaginaAnterior != null)
                btnPaginaAnterior.setDisable(paginaActual <= 1);
            if (btnPaginaSiguiente != null)
                btnPaginaSiguiente.setDisable(paginaActual >= totalPaginas);
        }

        actualizarEstadisticas();
    }

    // Actualizar estadísticas
    private void actualizarEstadisticas() {
        // Mostrar el total general de proveedores de la BD
        int totalGeneral = proveedoresOriginales != null ? proveedoresOriginales.size() : 0;
        int activos = (int) tableProveedores.getItems().stream().filter(Proveedor::getActivo).count();
        if (lblContadorTotal != null) {
            lblContadorTotal.setText("Total: " + totalGeneral + " proveedores");
        }
        if (lblEstadistica != null) {
            lblEstadistica.setText(String.format("Total: %d proveedores (%d activos)", totalGeneral, activos));
        }
    }

    // Actualizar timestamp de última actualización
    private void actualizarUltimaActualizacion() {
        if (lblUltimaActualizacion != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblUltimaActualizacion.setText("Última actualización: " + LocalDateTime.now().format(formatter));
        }
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        filtrarProveedores();
    }

    @FXML
    private void aplicarFiltros() {
        filtrarProveedores();
    }

    @FXML
    private void nuevoProveedor() {
        abrirFormularioProveedor(null);
    }

    // Editar proveedor seleccionado
    private void editarProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            abrirFormularioProveedor(proveedor);
        }
    }

    // Eliminar proveedor
    private void eliminarProveedor(Proveedor proveedor) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar proveedor?");
        confirmacion.setContentText("¿Está seguro de que desea eliminar el proveedor \"" +
                proveedor.getRazonSocial() + "\"?");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean eliminado = proveedorService.eliminar(proveedor.getId());
                    if (eliminado) {
                        mostrarInformacion("Éxito", "Proveedor eliminado correctamente");
                        cargarProveedores();
                    } else {
                        mostrarError("Error", "No se pudo eliminar el proveedor", "");
                    }
                } catch (Exception e) {
                    mostrarError("Error", "Error al eliminar proveedor", e.getMessage());
                }
            }
        });
    }

    // Toggle rápido de estado activo/inactivo (sin confirmación)
    private void toggleEstadoProveedor(Proveedor proveedor) {
        try {
            // Cambiar estado
            boolean nuevoEstado = !proveedor.getActivo();
            proveedor.setActivo(nuevoEstado);

            // Actualizar en BD usando service layer
            String usuarioActual = System.getProperty("user.name", "sistema");
            proveedorService.actualizar(proveedor, usuarioActual);

            // Actualizar solo la fila modificada en la tabla (sin recargar toda la lista)
            tableProveedores.refresh();

            // Notificación discreta
            String mensaje = nuevoEstado
                    ? String.format("Proveedor '%s' ACTIVADO correctamente", proveedor.getRazonSocial())
                    : String.format("Proveedor '%s' INACTIVADO correctamente", proveedor.getRazonSocial());

            notificationService.mostrarExito(mensaje);
            logger.info("Estado del proveedor {} cambiado a: {}", proveedor.getRuc(),
                    nuevoEstado ? "ACTIVO" : "INACTIVO");
        } catch (Exception e) {
            logger.error("Error al cambiar estado del proveedor: {}", e.getMessage(), e);
            notificationService.mostrarError("No se pudo cambiar el estado del proveedor: " + e.getMessage());
        }
    }

    // Eliminación DEFINITIVA con doble confirmación
    private void eliminarProveedorDefinitivo(Proveedor proveedor) {
        // Primera confirmación: moderna y coherente con productos
        Alert confirmacion1 = new Alert(Alert.AlertType.WARNING);
        confirmacion1.setTitle("🟢 Eliminación definitiva");
        confirmacion1.setHeaderText(null);
        confirmacion1.setContentText(null);
        String mensaje1 = String.format(
                "🟢 ¡Atención!\n\n" +
                        "Esta acción NO se puede deshacer.\n\n" +
                        "Proveedor: %s\nRUC: %s\nEmail: %s\n\n" +
                        "Esta acción es IRREVERSIBLE",
                proveedor.getRazonSocial(),
                proveedor.getRuc(),
                proveedor.getEmail() != null ? proveedor.getEmail() : "No especificado");
        Label label1 = new Label(mensaje1);
        label1.getStyleClass().add("label");
        VBox content1 = new VBox(label1);
        content1.setSpacing(10);
        content1.setPadding(new javafx.geometry.Insets(10));
        confirmacion1.getDialogPane().setContent(content1);
        confirmacion1.getDialogPane().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        confirmacion1.getDialogPane().getStylesheets()
                .add(getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm());
        confirmacion1.getDialogPane().getStyleClass().add("proveedores-alert");

        ButtonType btnContinuar = new ButtonType("Continuar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar1 = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion1.getButtonTypes().setAll(btnContinuar, btnCancelar1);

        Optional<ButtonType> resultado1 = confirmacion1.showAndWait();
        if (resultado1.isPresent() && resultado1.get() == btnContinuar) {
            // Segunda confirmación: moderna y con campo de texto
            Alert confirmacion2 = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion2.setTitle("🚨 Confirmación final");
            confirmacion2.setHeaderText(null);
            confirmacion2.setContentText(null);
            String mensaje2 = String.format(
                    "🟢 ¡ÚLTIMA OPORTUNIDAD!\n\n" +
                            "¿Realmente desea ELIMINAR PARA SIEMPRE el proveedor '%s'?\n\n" +
                            "🟡 Después de esto NO podrá recuperarlo\n" +
                            "🟡 Se perderán TODOS los datos\n" +
                            "🟡 No hay función de 'deshacer'\n\n" +
                            "Escriba 'ELIMINAR' para confirmar",
                    proveedor.getRazonSocial());
            Label label2 = new Label(mensaje2);
            label2.getStyleClass().add("label");
            TextField input = new TextField();
            input.setPromptText("Escriba: ELIMINAR");
            input.getStyleClass().add("text-field");
            VBox content2 = new VBox(label2, input);
            content2.setSpacing(12);
            content2.setPadding(new javafx.geometry.Insets(10));
            confirmacion2.getDialogPane().setContent(content2);
            confirmacion2.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/styles.css").toExternalForm());
            confirmacion2.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/Proveedor/proveedores.css").toExternalForm());
            confirmacion2.getDialogPane().getStyleClass().add("proveedores-alert");

            ButtonType btnEliminarDefinitivo = new ButtonType("ELIMINAR DEFINITIVAMENTE", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancelar2 = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacion2.getButtonTypes().setAll(btnEliminarDefinitivo, btnCancelar2);

            final Button btnOk = (Button) confirmacion2.getDialogPane().lookupButton(btnEliminarDefinitivo);
            btnOk.setDisable(true);
            input.textProperty().addListener((obs, oldVal, newVal) -> {
                btnOk.setDisable(!"ELIMINAR".equalsIgnoreCase(newVal.trim()));
            });

            Optional<ButtonType> resultado2 = confirmacion2.showAndWait();
            if (resultado2.isPresent() && resultado2.get() == btnEliminarDefinitivo
                    && "ELIMINAR".equalsIgnoreCase(input.getText().trim())) {
                try {
                    // ELIMINACIÓN DEFINITIVA (hard delete)
                    proveedorService.eliminarDefinitivamente(proveedor.getId());
                    notificationService.mostrarExito(
                            String.format("Proveedor '%s' ELIMINADO DEFINITIVAMENTE", proveedor.getRazonSocial()));
                    logger.warn("Proveedor ELIMINADO DEFINITIVAMENTE: {} ({})", proveedor.getRazonSocial(),
                            proveedor.getRuc());
                } catch (Exception e) {
                    logger.error("Error al eliminar definitivamente el proveedor: {}", e.getMessage(), e);
                    notificationService.mostrarError("No se pudo eliminar el proveedor: " + e.getMessage());
                }
            }
        }
    }

    private void abrirFormularioProveedor(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Proveedor/proveedor-form.fxml"));
            Parent root = loader.load();

            ProveedorFormController controller = loader.getController();
            if (controller != null) {
                if (proveedor != null) {
                    controller.setProveedor(proveedor);
                }
                controller.setOnProveedorGuardado(proveedorGuardado -> {
                    // Guardar/actualizar proveedor con usuario actual
                    String usuarioActual = System.getProperty("user.name", "sistema");
                    if (proveedorGuardado.getId() == null) {
                        proveedorService.guardar(proveedorGuardado); // alta: no requiere historial
                    } else {
                        proveedorService.actualizar(proveedorGuardado, usuarioActual); // edición: registra historial
                    }
                    cargarProveedores();
                    Platform.runLater(() -> tableProveedores.refresh());
                });
            }

            Stage stage = new Stage();
            stage.setTitle(proveedor == null ? "Nuevo Proveedor" : "Editar Proveedor");
            Scene scene = new Scene(root);

            try {
                String css = getClass().getResource("/css/Proveedor/proveedor-form.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception ex) {
                logger.warn("No se pudo cargar CSS del formulario de proveedor: {}", ex.getMessage());
            }

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            logger.error("Error al abrir formulario de proveedor: {}", ex.getMessage(), ex);
            mostrarError("Error", "No se pudo abrir el formulario de proveedor", ex.getMessage());
        }
    }

    private void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
        // Si el contenido es un stacktrace, imprímelo
        if (contenido != null && contenido.contains("Exception")) {
            System.err.println("[ERROR] " + header + ": " + contenido);
        }
    }

    // Mostrar diálogo de información
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Ver detalles del proveedor
    public void verDetalles(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Proveedor/proveedor-detalles.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Detalles del Proveedor - " + proveedor.getRazonSocial());
            stage.setScene(new Scene(root, 800, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.centerOnScreen();

            ProveedorDetallesController controller = loader.getController();
            controller.setProveedor(proveedor);
            controller.setStage(stage);

            stage.show();

        } catch (IOException e) {
            System.err.println("Error al abrir detalles del proveedor: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir los detalles del proveedor", e.getMessage());
        }
    }
}