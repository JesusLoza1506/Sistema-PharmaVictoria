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

/**
 * Controlador para la gestión de proveedores
 */
public class ProveedoresController implements Initializable {
    @FXML
    private Button btnContactarProveedores;

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
        // Cargar credenciales desde application.properties
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

    /**
     * Inicializa y carga las gráficas del dashboard inferior
     */
    private void inicializarDashboardGraficas() {
        cargarGraficaTopProveedores();
        cargarGraficaEstadoProveedores();
    }

    /**
     * Carga la gráfica de barras: Top proveedores con más productos asociados
     * Utiliza ProductoService y mapeo por proveedorId/proveedorNombre
     */
    private void cargarGraficaTopProveedores() {
        if (barChartTopProveedores == null)
            return;
        barChartTopProveedores.getData().clear();
        barChartTopProveedores.setLegendVisible(false);
        barChartTopProveedores.setAnimated(false);
        barChartTopProveedores.getStylesheets()
                .add(getClass().getResource("/css/Proveedor/barChart-custom.css").toExternalForm());

        List<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto> productos = ServiceContainer
                .getInstance().getProductoService().obtenerTodos();
        // Obtener proveedores activos
        List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedoresActivos = ServiceContainer
                .getInstance().getProveedorService().obtenerTodos();
        java.util.Set<Integer> proveedoresValidos = new java.util.HashSet<>();
        java.util.Map<Integer, String> proveedorNombreMap = new java.util.HashMap<>();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor proveedor : proveedoresActivos) {
            proveedoresValidos.add(proveedor.getId());
            proveedorNombreMap.put(proveedor.getId(), proveedor.getRazonSocial());
        }
        java.util.Map<Integer, Integer> proveedorProductoCount = new java.util.HashMap<>();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto prod : productos) {
            Integer proveedorId = prod.getProveedorId();
            if (proveedorId != null && proveedoresValidos.contains(proveedorId)) {
                proveedorProductoCount.put(proveedorId, proveedorProductoCount.getOrDefault(proveedorId, 0) + 1);
            }
        }
        // Obtener top 3 proveedores
        java.util.List<java.util.Map.Entry<Integer, Integer>> top = proveedorProductoCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Productos asociados");
        // Añadir los proveedores reales
        for (java.util.Map.Entry<Integer, Integer> entry : top) {
            String nombre = proveedorNombreMap.get(entry.getKey());
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>(
                    nombre != null ? nombre : "Proveedor " + entry.getKey(), entry.getValue());
            series.getData().add(data);
        }
        // Si hay menos de 3, rellenar con barras vacías
        int faltantes = 3 - top.size();
        for (int i = 0; i < faltantes; i++) {
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>("Sin datos",
                    0);
            series.getData().add(data);
        }
        barChartTopProveedores.getData().add(series);
        // Etiquetas de valor sobre cada barra
        Platform.runLater(() -> {
            for (javafx.scene.chart.XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    final Label label = new Label(
                            data.getYValue().intValue() > 0 ? String.valueOf(data.getYValue()) : "");
                    label.getStyleClass().add("bar-value-label");
                    label.setStyle(
                            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-background-color: transparent;");
                    data.getNode().parentProperty().addListener((obs, oldParent, newParent) -> {
                        if (newParent != null) {
                            ((javafx.scene.layout.StackPane) data.getNode()).getChildren().add(label);
                        }
                    });
                }
            }
        });
    }

    /**
     * Carga la gráfica de pastel: Distribución de proveedores por estado
     */
    private void cargarGraficaEstadoProveedores() {
        if (pieChartEstadoProveedores == null)
            return;
        pieChartEstadoProveedores.getData().clear();
        long activos = proveedoresOriginales.stream().filter(Proveedor::getActivo).count();
        long inactivos = proveedoresOriginales.size() - activos;
        javafx.scene.chart.PieChart.Data activosData = new javafx.scene.chart.PieChart.Data("Activos", activos);
        javafx.scene.chart.PieChart.Data inactivosData = new javafx.scene.chart.PieChart.Data("Inactivos", inactivos);
        pieChartEstadoProveedores.getData().addAll(activosData, inactivosData);
    }

    @FXML
    public void onExportarProveedores(javafx.event.ActionEvent event) {
        exportar();
    }

    @FXML
    private Button btnExportar;

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

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(ProveedoresController.class);

    /**
     * Evento para avanzar una página en la tabla de proveedores
     */
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
    private int tamanoPagina = 10;

    /**
     * Actualiza la paginación de la tabla de proveedores
     */
    private void actualizarPaginacion() {
        // Calcular total de páginas
        int total = proveedoresList != null ? proveedoresList.size() : 0;
        totalPaginas = (int) Math.ceil((double) total / tamanoPagina);
        if (paginaActual > totalPaginas)
            paginaActual = totalPaginas;
        if (paginaActual < 1)
            paginaActual = 1;

        // Obtener sublista para la página actual
        int fromIndex = (paginaActual - 1) * tamanoPagina;
        int toIndex = Math.min(fromIndex + tamanoPagina, total);
        List<Proveedor> pagina = proveedoresList != null ? proveedoresList.subList(fromIndex, toIndex)
                : java.util.Collections.emptyList();
        if (tableProveedores != null)
            tableProveedores.setItems(FXCollections.observableArrayList(pagina));

        // Actualizar label de página
        if (lblPaginaActual != null)
            lblPaginaActual.setText("Página " + paginaActual + " de " + totalPaginas);
    }

    /**
     * Evento para retroceder una página en la tabla de proveedores
     */
    @FXML
    public void onPaginaAnterior(javafx.event.ActionEvent event) {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarPaginacion();
        }
    }

    /**
     * Maneja el evento de activar/desactivar el filtro de solo activos
     */
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

    // FXML Elements - Filtros avanzados
    @FXML
    private ComboBox<String> cmbTipoFiltro;

    /**
     * Filtro avanzado usando Strategy Pattern
     */
    private void aplicarFiltroAvanzado() {
        String criterio = txtBuscar.getText();
        String tipo = cmbTipoFiltro != null ? cmbTipoFiltro.getValue() : "Razón Social";
        List<Proveedor> base = proveedoresOriginales != null ? proveedoresOriginales : proveedoresList;
        com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.ProveedorFilterStrategy strategy = null;
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
            case "Estado":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor.FiltroPorEstado();
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

    // FXML Elements - Search
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnNuevoProveedor;
    @FXML
    private Button btnHistorial;

    // FXML Elements - Filters
    @FXML
    private ComboBox<String> cmbOrdenar;
    @FXML
    private CheckBox chkSoloActivos;

    // FXML Elements - Table
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
    // Columna 'Condiciones de Pago' eliminada por petición de UI
    @FXML
    private TableColumn<Proveedor, String> colTipoProducto;
    @FXML
    private TableColumn<Proveedor, String> colEstado;
    @FXML
    private TableColumn<Proveedor, Void> colAcciones;

    // FXML Elements - Footer
    @FXML
    private Label lblEstadistica;
    @FXML
    private Label lblUltimaActualizacion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ...existing code...
        // Configurar ComboBox de tipo de búsqueda avanzada
        if (cmbTipoFiltro != null) {
            cmbTipoFiltro.setItems(FXCollections.observableArrayList(
                    "Razón Social", "RUC", "Contacto", "Estado", "Tipo de Producto"));
            cmbTipoFiltro.getSelectionModel().selectFirst();
            cmbTipoFiltro.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroAvanzado());
        }
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltroAvanzado());
        System.out.println("Inicializando ProveedoresController...");

        // ✅ DEPENDENCY INJECTION: Obtener services desde ServiceContainer
        ServiceContainer container = ServiceContainer.getInstance();
        this.proveedorService = container.getProveedorService();
        this.notificationService = container.getNotificationService();

        // Inicializar listas
        proveedoresList = FXCollections.observableArrayList();
        proveedoresOriginales = FXCollections.observableArrayList();
        // Configurar ComboBox de tamaño de página (paginación)
        if (cmbTamanoPagina != null) {
            cmbTamanoPagina.getItems().clear();
            cmbTamanoPagina.getItems().addAll(5, 10, 20, 50);
            cmbTamanoPagina.setValue(10);
            cmbTamanoPagina.valueProperty().addListener((obs, oldVal, newVal) -> {
                tamanoPagina = newVal;
                paginaActual = 1;
                actualizarPaginacion();
                actualizarEstadisticas();
            });
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

    /**
     * Configurar la tabla de proveedores
     */
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
            // Doble clic para editar
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editarProveedor(row.getItem());
                }
                // Click derecho para historial de cambios
                if (event.isSecondaryButtonDown() && !row.isEmpty()) {
                    mostrarHistorialProveedor(row.getItem());
                }
            });
            return row;
        });
        tableProveedores.setItems(proveedoresList);
    }

    /**
     * Mostrar historial de cambios de un proveedor
     */
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

    /**
     * Configurar la columna de acciones
     */
    private void configurarColumnaAcciones() {
        // Reusar el patrón del módulo Inventario: Editar - Ver - Toggle - Eliminar
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnVer = new Button();
            private final Button btnToggleEstado = new Button();
            private final Button btnEliminar = new Button();

            {
                // Íconos: los mismos recursos que usa Inventario
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

                // Clases CSS coherentes con Inventario
                btnEditar.getStyleClass().setAll("btn-action", "btn-add");
                btnVer.getStyleClass().setAll("btn-action", "btn-eye");
                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle");
                btnEliminar.getStyleClass().setAll("btn-action", "btn-trash");

                btnEditar.setPrefSize(35, 35);
                btnVer.setPrefSize(35, 35);
                btnToggleEstado.setPrefSize(35, 35);
                btnEliminar.setPrefSize(35, 35);

                // Tooltips
                btnEditar.setTooltip(new Tooltip("Editar proveedor"));
                btnVer.setTooltip(new Tooltip("Ver detalles"));
                btnEliminar.setTooltip(new Tooltip("Eliminar"));

                // Acciones
                btnEditar.setOnAction(e -> {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    editarProveedor(proveedor);
                });
                btnVer.setOnAction(e -> {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    verDetalles(proveedor);
                });
                btnToggleEstado.setOnAction(e -> {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    toggleEstadoProveedor(proveedor);
                });
                btnEliminar.setOnAction(e -> {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    eliminarProveedorDefinitivo(proveedor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(6, btnEditar, btnVer, btnToggleEstado, btnEliminar);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    // Ajustar el texto/estilo del toggle según el estado
                    if (proveedor != null && proveedor.getActivo()) {
                        btnToggleEstado.setText("⏸");
                        btnToggleEstado.setTooltip(new Tooltip("Inactivar proveedor"));
                        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
                    } else {
                        btnToggleEstado.setText("▶");
                        btnToggleEstado.setTooltip(new Tooltip("Activar proveedor"));
                        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-inactive");
                    }
                    setGraphic(box);
                }
            }
        });
    }

    /**
     * Búsqueda en tiempo real
     */
    @FXML
    private void buscarProveedores(javafx.event.ActionEvent event) {
        // Búsqueda en tiempo real
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

    /**
     * ✅ SERVICE LAYER: Cargar proveedores usando ProveedorService
     * Migrado de acceso directo a Repository hacia Service Layer
     */
    private void cargarProveedores() {
        try {
            System.out.println("Cargando proveedores...");

            // ✅ SERVICE LAYER: Cargando todos los proveedores (activos e inactivos) para
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

                System.out.println("Proveedores cargados: " + proveedores.size());
            });

        } catch (Exception e) {
            System.err.println("Error cargando proveedores: " + e.getMessage());
            Platform.runLater(() -> {
                mostrarError("Error", "No se pudieron cargar los proveedores", e.getMessage());
            });
        }
    }

    /**
     * Método público para refrescar la tabla (llamado desde formularios)
     */
    public void refrescarTabla() {
        cargarProveedores();
    }

    /**
     * Filtrar proveedores según criterios (búsqueda simple)
     */
    private void filtrarProveedores() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        boolean soloActivos = chkSoloActivos.isSelected();

        List<Proveedor> proveedoresFiltrados = proveedoresOriginales.stream()
                .filter(p -> {
                    // Filtro por estado
                    if (soloActivos && !p.getActivo()) {
                        return false;
                    }

                    // Filtro por texto de búsqueda
                    if (!textoBusqueda.isEmpty()) {
                        return (p.getRazonSocial() != null && p.getRazonSocial().toLowerCase().contains(textoBusqueda))
                                ||
                                (p.getRuc() != null && p.getRuc().toLowerCase().contains(textoBusqueda)) ||
                                (p.getContacto() != null && p.getContacto().toLowerCase().contains(textoBusqueda)) ||
                                (p.getEmail() != null && p.getEmail().toLowerCase().contains(textoBusqueda));
                    }

                    return true;
                })
                .toList();

        proveedoresList.clear();
        proveedoresList.addAll(proveedoresFiltrados);

        actualizarEstadisticas();
    }

    /**
     * Actualizar estadísticas
     */
    private void actualizarEstadisticas() {
        int total = tableProveedores.getItems().size();
        int activos = (int) tableProveedores.getItems().stream().filter(Proveedor::getActivo).count();
        if (lblContadorTotal != null) {
            lblContadorTotal.setText("Total: " + total + " proveedores");
        }
        if (lblEstadistica != null) {
            lblEstadistica.setText(String.format("Total: %d proveedores (%d activos)", total, activos));
        }
    }

    /**
     * Actualizar timestamp de última actualización
     */
    private void actualizarUltimaActualizacion() {
        if (lblUltimaActualizacion != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblUltimaActualizacion.setText("Última actualización: " + LocalDateTime.now().format(formatter));
        }
    }

    // MÉTODOS DE ACCIÓN

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

    /**
     * Editar proveedor seleccionado
     */
    private void editarProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            abrirFormularioProveedor(proveedor);
        }
    }

    /**
     * Eliminar proveedor
     */
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

    /**
     * ✅ NUEVO: Toggle rápido de estado activo/inactivo (sin confirmación)
     */
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

    /**
     * ✅ NUEVO: Eliminación DEFINITIVA con doble confirmación
     */
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

    /**
     * ✅ ENTERPRISE PATTERN: Abrir formulario de proveedor
     */
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

            // Cargar CSS
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

    // MÉTODOS DE UTILIDAD

    /**
     * Mostrar diálogo de error
     */
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

    /**
     * Mostrar diálogo de información
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Ver detalles del proveedor
     */
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