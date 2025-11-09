package com.farmaciavictoria.proyectopharmavictoria.controller.Cliente;

import javafx.scene.control.TableCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * ✅ MVC CONTROLLER PATTERN - Controlador de Clientes
 * Implementa el patrón MVC con inyección de dependencias
 * Maneja la interfaz de usuario para gestión de clientes
 * 
 * @author PHARMAVICTORIA Development Team
 * @version 1.0
 */
public class ClientesController implements Initializable {
    // Dashboard: Gráficas
    @FXML
    private javafx.scene.chart.BarChart<String, Number> barChartTopClientes;
    @FXML
    private javafx.scene.chart.CategoryAxis barChartClientesXAxis;
    @FXML
    private javafx.scene.chart.NumberAxis barChartClientesYAxis;
    @FXML
    private javafx.scene.chart.PieChart pieChartRangoEdad;

    private void cargarTopClientesConMasCompras() {
        // Obtener todos los clientes reales (sin genéricos)
        java.util.Map<Integer, Integer> comprasPorCliente = new java.util.HashMap<>();
        java.util.Map<Integer, com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientesMap = new java.util.HashMap<>();
        try {
            System.out.println("[DASHBOARD] Iniciando carga de Top Clientes...");
            if (clienteService == null) {
                System.err.println("[DASHBOARD ERROR] clienteService es null");
                return;
            }
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes = clienteService
                    .obtenerTodos();
            System.out.println("[DASHBOARD] Total clientes obtenidos: " + clientes.size());
            com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl ventaRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl();
            com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl detalleVentaRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl();
            com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl comprobanteRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl();
            com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepositoryJdbcImpl historialCambioRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepositoryJdbcImpl();
            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository productoRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository();
            com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl ventaService = new com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl(
                    ventaRepository,
                    detalleVentaRepository,
                    comprobanteRepository,
                    historialCambioRepository,
                    productoRepository);
            for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente : clientes) {
                try {
                    // Incluir tanto clientes NATURAL como EMPRESA, excluyendo solo los genéricos
                    // (documento 00000000)
                    if (cliente.getId() != null
                            && (cliente.getDocumento() == null || !cliente.getDocumento().equals("00000000"))) {
                        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta> ventasCliente = ventaService
                                .listarVentasPorCliente(cliente.getId());
                        System.out.println("[DASHBOARD] Cliente: " + cliente.getNombres() + " " + cliente.getApellidos()
                                + " | Tipo: " + cliente.getTipoCliente() + " | Compras: " + ventasCliente.size());
                        comprasPorCliente.put(cliente.getId(), ventasCliente.size());
                        clientesMap.put(cliente.getId(), cliente);
                    }
                } catch (Exception ex) {
                    System.err.println("[DASHBOARD ERROR] Error al obtener ventas para cliente ID " + cliente.getId()
                            + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            // ...existing code...
        } catch (Exception e) {
            System.err.println("[DASHBOARD ERROR] Error general en cargarTopClientesConMasCompras: " + e.getMessage());
            e.printStackTrace();
        }
        // Obtener top 3 clientes
        java.util.List<java.util.Map.Entry<Integer, Integer>> top = comprasPorCliente.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();
        barChartTopClientes.getData().clear();
        barChartTopClientes.setLegendVisible(false);
        barChartTopClientes.setAnimated(false);
        // Forzar el título correcto siempre
        barChartTopClientes.setTitle("Top 3 clientes con más compras");
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Compras realizadas");
        for (java.util.Map.Entry<Integer, Integer> entry : top) {
            com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente = clientesMap.get(entry.getKey());
            String nombre;
            if (cliente != null) {
                if ("Empresa".equalsIgnoreCase(cliente.getTipoCliente())) {
                    nombre = cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty()
                            ? cliente.getRazonSocial()
                            : (cliente.getNombres() != null ? cliente.getNombres() : "") + " "
                                    + (cliente.getApellidos() != null ? cliente.getApellidos() : "");
                } else {
                    nombre = (cliente.getNombres() != null ? cliente.getNombres() : "") + " "
                            + (cliente.getApellidos() != null ? cliente.getApellidos() : "");
                }
            } else {
                nombre = "Cliente " + entry.getKey();
            }
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>(nombre.trim(),
                    entry.getValue());
            series.getData().add(data);
        }
        int faltantes = 3 - top.size();
        for (int i = 0; i < faltantes; i++) {
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>("Sin datos",
                    0);
            series.getData().add(data);
        }
        barChartTopClientes.getData().add(series);
        javafx.application.Platform.runLater(() -> {
            for (javafx.scene.chart.XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    final javafx.scene.control.Label label = new javafx.scene.control.Label(
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
        // Eliminado código de alertas no relevante para el dashboard
    }

    // Panel/label para alertas persistentes
    @FXML
    private Label lblAlertasClientes;
    // Paginación
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
    private int tamanoPagina = 10;
    @FXML
    private CheckBox chkFrecuentes;

    @FXML
    private void onToggleFrecuentes() {
        if (chkFrecuentes != null && chkFrecuentes.isSelected()) {
            javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> filtrados = javafx.collections.FXCollections
                    .observableArrayList();
            for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c : clientesList) {
                if (c.isFrecuente() && (c.getDocumento() == null || !c.getDocumento().equals("00000000"))) {
                    filtrados.add(c);
                }
            }
            tablaClientes.setItems(filtrados);
        } else {
            tablaClientes.setItems(clientesList);
        }
    }

    @FXML
    private ComboBox<String> cmbEstado;

    // Handler para filtrar por estado
    @FXML
    private void onFiltrarEstado() {
        String filtro = cmbEstado != null && cmbEstado.getValue() != null ? cmbEstado.getValue() : "Todos";
        if (filtro == null || filtro.equalsIgnoreCase("Todos")) {
            javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> filtrados = javafx.collections.FXCollections
                    .observableArrayList();
            for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c : clientesList) {
                if (c.getDocumento() == null || !c.getDocumento().equals("00000000")) {
                    filtrados.add(c);
                }
            }
            tablaClientes.setItems(filtrados);
            return;
        }
        javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> filtrados = javafx.collections.FXCollections
                .observableArrayList();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c : clientesList) {
            if ("Frecuente".equalsIgnoreCase(filtro) && c.isFrecuente()) {
                filtrados.add(c);
            } else if ("No frecuente".equalsIgnoreCase(filtro) && !c.isFrecuente()) {
                filtrados.add(c);
            }
        }
        tablaClientes.setItems(filtrados);
    }

    // Handler para el botón Actualizar
    @FXML
    private void onRefrescar() {
        refrescarDatos();
    }

    // === FXML Elements para la nueva UI ===
    @FXML
    private TextField txtBuscar;
    // Eliminados ComboBox y CheckBox redundantes: cbFiltroTipo, cbOrdenar,
    // chkSoloFrecuentes
    @FXML
    private TableView<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> tablaClientes;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colTipoCliente;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colDocumento;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colNombreRazonSocial;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colTelefono;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colEmail;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, String> colDireccion;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, Void> colAcciones;
    @FXML
    private Label lblTotalClientes;
    @FXML
    private Label lblClientesFrecuentes;
    @FXML
    private Label lblNuevosEsteMes;
    @FXML
    private Label lblPuntosPromedio;
    @FXML
    private Label lblUltimaActualizacion;

    @FXML
    private ComboBox<String> cbTipoBusqueda;

    // Inyección de dependencias vía setter
    private com.farmaciavictoria.proyectopharmavictoria.service.ClienteService clienteService;

    public void setClienteService(com.farmaciavictoria.proyectopharmavictoria.service.ClienteService clienteService) {
        this.clienteService = clienteService;
        inicializarDatos();
        cargarTopClientesConMasCompras();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colTipoCliente.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setGraphic(null);
                } else {
                    String iconPath;
                    if (tipo.trim().replaceAll("\\s+", "").equalsIgnoreCase("Empresa")) {
                        iconPath = "/icons/empresa.png";
                    } else {
                        iconPath = "/icons/persona.png";
                    }
                    javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
                    imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane(imageView);
                    pane.setPrefWidth(column.getWidth());
                    pane.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(pane);
                }
            }
        });
        colTipoCliente.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoCliente()));
        colDocumento.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDocumento()));
        colNombreRazonSocial.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colTelefono.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
        colEmail.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colDireccion.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDireccion()));
        // ...asignar acciones y demás columnas como ya estaban...

        inicializarDashboardGraficas();
        // No inicializar datos aquí, esperar a que se inyecte el servicio
        // Control de visibilidad del botón 'Nuevo Cliente' según el rol
        javafx.application.Platform.runLater(() -> {
            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                    .getInstance()
                    .getAuthenticationService().getUsuarioActual();
            boolean esAdmin = usuario != null && usuario.isAdmin();
            if (tablaClientes != null && tablaClientes.getScene() != null) {
                javafx.scene.control.Button btnNuevoCliente = (javafx.scene.control.Button) tablaClientes.getScene()
                        .lookup("#btnNuevoCliente");
                if (btnNuevoCliente != null) {
                    btnNuevoCliente.setVisible(esAdmin);
                    btnNuevoCliente.setManaged(esAdmin);
                }
            }
        });
        if (lblTotalClientes != null && clienteService != null) {
            int totalClientes = clienteService.obtenerTodos().size();
            lblTotalClientes.setText("Total: " + totalClientes + " clientes");
            lblTotalClientes.setStyle("-fx-text-fill: #1bb934; -fx-font-weight: bold;");
        }
        if (cmbEstado != null) {
            cmbEstado.setItems(FXCollections.observableArrayList("Todos", "Frecuente", "No frecuente"));
            cmbEstado.setValue("Todos");
        }
        if (cbTipoBusqueda != null) {
            cbTipoBusqueda.getItems().clear();
            cbTipoBusqueda.getItems().addAll(
                    "Documento", // DNI o RUC
                    "Nombre / Razón social",
                    "Teléfono",
                    "Email");
            cbTipoBusqueda.setValue("Nombre / Razón social");
            cbTipoBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> onBuscar());
        }
        if (cmbTamanoPagina != null) {
            cmbTamanoPagina.getItems().clear();
            cmbTamanoPagina.getItems().addAll(5, 10, 20, 50);
            cmbTamanoPagina.setValue(10);
            cmbTamanoPagina.valueProperty().addListener((obs, oldVal, newVal) -> {
                tamanoPagina = newVal;
                paginaActual = 1;
                actualizarPaginacion();
            });
        }
        if (btnPaginaAnterior != null) {
            btnPaginaAnterior.setOnAction(e -> onPaginaAnterior());
        }
        if (btnPaginaSiguiente != null) {
            btnPaginaSiguiente.setOnAction(e -> onPaginaSiguiente());
        }
        if (tablaClientes != null && clientesList != null) {
            tablaClientes.setItems(clientesList);
        }
        // cargarTopClientesConMasCompras(); // Se ejecuta solo tras inyección
    }

    private void inicializarDashboardGraficas() {
        // --- BarChart: Top clientes con más compras ---
        // ...existing code...

        // --- PieChart: Distribución real por rango de edad ---
        if (pieChartRangoEdad != null && clienteService != null) {
            pieChartRangoEdad.getData().clear();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientes = clienteService
                    .obtenerTodos();
            int r18_25 = 0, r26_35 = 0, r36_50 = 0, r51 = 0;
            for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c : clientes) {
                // Incluir ambos tipos de clientes en la estadística de edad
                Integer edad = c.getEdad();
                if (edad == null)
                    continue;
                if (edad >= 18 && edad <= 25)
                    r18_25++;
                else if (edad >= 26 && edad <= 35)
                    r26_35++;
                else if (edad >= 36 && edad <= 50)
                    r36_50++;
                else if (edad >= 51)
                    r51++;
            }
            javafx.scene.chart.PieChart.Data d1 = new javafx.scene.chart.PieChart.Data("18-25", r18_25);
            javafx.scene.chart.PieChart.Data d2 = new javafx.scene.chart.PieChart.Data("26-35", r26_35);
            javafx.scene.chart.PieChart.Data d3 = new javafx.scene.chart.PieChart.Data("36-50", r36_50);
            javafx.scene.chart.PieChart.Data d4 = new javafx.scene.chart.PieChart.Data("51+", r51);
            pieChartRangoEdad.getData().addAll(d1, d2, d3, d4);
            pieChartRangoEdad.setLabelsVisible(true);
            pieChartRangoEdad.setLegendVisible(true);
            pieChartRangoEdad.setStyle(""); // Elimina cualquier fondo
        }
    }

    @FXML
    private void onPaginaSiguiente() {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            actualizarPaginacion();
        }
    }

    private void inicializarDatos() {
        // Eliminada llamada a mostrarAlertasClientes();
        if (cbTipoBusqueda != null) {
            cbTipoBusqueda.getItems().clear();
            cbTipoBusqueda.getItems().addAll(
                    "Documento", // DNI o RUC
                    "Nombre / Razón social",
                    "Teléfono",
                    "Email");
            cbTipoBusqueda.setValue("Nombre / Razón social");
        }
        // No sobrescribir cell value factories, solo configurar acciones y refrescar
        // datos
        configurarColumnaAcciones();
        refrescarDatos();
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(
                col -> new javafx.scene.control.TableCell<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente, Void>() {
                    private final javafx.scene.control.Button btnEditar = new javafx.scene.control.Button();
                    private final javafx.scene.control.Button btnVer = new javafx.scene.control.Button();
                    private final javafx.scene.control.Button btnEliminar = new javafx.scene.control.Button();
                    // Cambiar el orden: Editar - Ver - Eliminar
                    private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, btnEditar, btnVer,
                            btnEliminar);
                    {
                        // Usar los mismos iconos que inventario
                        javafx.scene.image.ImageView iconEditar = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/editar.png").toExternalForm());
                        javafx.scene.image.ImageView iconVer = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/ver.png").toExternalForm());
                        javafx.scene.image.ImageView iconEliminar = new javafx.scene.image.ImageView(
                                getClass().getResource("/icons/eliminar.png").toExternalForm());
                        iconVer.setFitWidth(24);
                        iconVer.setFitHeight(24);
                        iconEditar.setFitWidth(24);
                        iconEditar.setFitHeight(24);
                        iconEliminar.setFitWidth(24);
                        iconEliminar.setFitHeight(24);
                        btnVer.setGraphic(iconVer);
                        btnEditar.setGraphic(iconEditar);
                        btnEliminar.setGraphic(iconEliminar);
                        btnEditar.getStyleClass().setAll("btn-action", "btn-edit");
                        btnVer.getStyleClass().setAll("btn-action", "btn-view");
                        btnEliminar.getStyleClass().setAll("btn-action", "btn-delete");
                        btnEditar.setPrefWidth(35);
                        btnEditar.setPrefHeight(35);
                        btnVer.setPrefWidth(35);
                        btnVer.setPrefHeight(35);
                        btnEliminar.setPrefWidth(35);
                        btnEliminar.setPrefHeight(35);
                        btnEditar.setTooltip(new javafx.scene.control.Tooltip("Editar"));
                        btnVer.setTooltip(new javafx.scene.control.Tooltip("Ver detalles"));
                        btnEliminar.setTooltip(new javafx.scene.control.Tooltip("Eliminar"));
                        btnEditar.setOnAction(e -> editarCliente(getTableView().getItems().get(getIndex())));
                        btnEliminar.setOnAction(e -> {
                            btnEliminar.setDisable(true); // Evita dobles clics
                            eliminarClienteConFeedback(getTableView().getItems().get(getIndex()), btnEliminar);
                        });
                        btnVer.setOnAction(e -> verDetallesCliente(getTableView().getItems().get(getIndex())));
                        box.setAlignment(javafx.geometry.Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Control de visibilidad por rol
                            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                                    .getInstance()
                                    .getAuthenticationService().getUsuarioActual();
                            boolean esAdmin = usuario != null && usuario.isAdmin();
                            btnEditar.setVisible(esAdmin);
                            btnEditar.setManaged(esAdmin);
                            btnEliminar.setVisible(esAdmin);
                            btnEliminar.setManaged(esAdmin);
                            btnVer.setVisible(true);
                            btnVer.setManaged(true);
                            setGraphic(box);
                            btnEliminar.setDisable(false); // Habilita siempre al refrescar
                        }
                    }
                });
    }

    // Acción para activar/inactivar cliente y registrar en historial
    private void toggleActivoCliente(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente) {
        boolean nuevoEstado = !cliente.isFrecuente();
        cliente.setEsFrecuente(nuevoEstado);
        clienteService.actualizarCliente(cliente); // El service debe registrar el cambio en historial
        refrescarDatos();
    }

    // Nuevo método para feedback visual y manejo de errores
    private void eliminarClienteConFeedback(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente,
            javafx.scene.control.Button btnEliminar) {
        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion
                .setContentText("¿Está seguro de que desea eliminar el cliente '" + cliente.getNombreCompleto() + "'?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    boolean eliminado = clienteService.eliminarCliente(cliente.getId());
                    if (eliminado) {
                        javafx.scene.control.Alert ok = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.INFORMATION, "Cliente eliminado correctamente.");
                        ok.setTitle("Éxito");
                        ok.setHeaderText(null);
                        ok.showAndWait();
                        refrescarDatos();
                    } else {
                        javafx.scene.control.Alert error = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.ERROR, "No se pudo eliminar el cliente.");
                        error.setTitle("Error");
                        error.setHeaderText(null);
                        error.showAndWait();
                    }
                } catch (Exception e) {
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR, "Error al eliminar cliente: " + e.getMessage());
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.showAndWait();
                }
            }
            btnEliminar.setDisable(false);
        });
    }

    private void editarCliente(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente) {
        System.out.println("[EDITAR CLIENTE] Iniciando edición de cliente: tipo=" + cliente.getTipoCliente()
                + " | documento=" + cliente.getDocumento() + " | razon_social=" + cliente.getRazonSocial());
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/Cliente/cliente-form.fxml"));
            javafx.scene.Parent root = loader.load();
            ClienteFormController formController = loader.getController();
            formController.setCliente(cliente); // precargar datos
            formController.setClientesExistentes(clientesList);
            formController.setOnClienteEditado(editado -> {
                if (editado != null) {
                    if ("Empresa".equalsIgnoreCase(editado.getTipoCliente())) {
                        System.out.println("[EDITAR CLIENTE EMPRESA] RUC=" + editado.getRuc() + " | Razón Social="
                                + editado.getRazonSocial());
                        if (editado.getRuc() == null || editado.getRazonSocial() == null
                                || editado.getRazonSocial().trim().isEmpty()) {
                            System.err.println("[ERROR EMPRESA] RUC o Razón Social vacíos al editar empresa");
                        }
                    }
                    clienteService.actualizarCliente(editado);
                }
                refrescarDatos();
            });

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Editar Cliente");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("[ERROR FXML] No se pudo abrir el formulario de edición: " + e.getMessage());
            System.err.println("[ERROR FXML] Stacktrace:");
            e.printStackTrace(System.err);
        }
    }

    private void eliminarCliente(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Cliente");
        alert.setHeaderText("¿Estás seguro de eliminar este cliente?");
        alert.setContentText(cliente.getNombreCompleto());
        alert.showAndWait().ifPresent(result -> {
            if (result == javafx.scene.control.ButtonType.OK) {
                clienteService.eliminarCliente(cliente.getId());
                refrescarDatos();
            }
        });
    }

    private void verDetallesCliente(com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/Cliente/cliente-detalles.fxml"));
            javafx.scene.Parent root = loader.load();
            com.farmaciavictoria.proyectopharmavictoria.controller.Cliente.ClienteDetallesController detallesController = loader
                    .getController();
            detallesController.setCliente(cliente);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Detalles del Cliente");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo abrir la vista de detalles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // === Listas observables para la tabla de clientes ===
    private javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> clientesList = javafx.collections.FXCollections
            .observableArrayList();

    // === Métodos para enlazar con FXML ===
    @FXML
    public void refrescarDatos() {
        // Recargar lista completa de clientes desde la base de datos
        int paginaAnterior = paginaActual;
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> actualizados = clienteService
                .obtenerTodos();
        clientesList.setAll(actualizados); // Actualiza la lista observable
        // Mantener la página actual si es válida
        if (paginaAnterior > 0) {
            paginaActual = paginaAnterior;
        } else {
            paginaActual = 1;
        }
        actualizarPaginacion();
        lblUltimaActualizacion.setText("Última actualización: " + java.time.LocalDateTime.now().withNano(0));
        inicializarDashboardGraficas(); // Actualiza las gráficas con los datos actuales
    }

    private void actualizarPaginacion() {
        int total = clientesList.size();
        totalPaginas = (int) Math.ceil((double) total / tamanoPagina);
        if (totalPaginas < 1)
            totalPaginas = 1;
        if (paginaActual < 1)
            paginaActual = 1;
        if (paginaActual > totalPaginas)
            paginaActual = totalPaginas;
        int desde = (paginaActual - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, total);
        javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> pagina = javafx.collections.FXCollections
                .observableArrayList();
        for (int i = desde; i < hasta; i++) {
            com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente c = clientesList.get(i);
            if (c.getDocumento() == null || !c.getDocumento().equals("00000000")) {
                pagina.add(c);
            }
        }
        tablaClientes.setItems(pagina);
        // Reasignar el cell factory de acciones para asegurar que los botones siempre
        // se muestren correctamente
        configurarColumnaAcciones();
        if (lblPaginaActual != null) {
            lblPaginaActual.setText("Página " + paginaActual + " de " + totalPaginas);
        }
        if (btnPaginaAnterior != null) {
            btnPaginaAnterior.setDisable(paginaActual <= 1);
        }
        if (btnPaginaSiguiente != null) {
            btnPaginaSiguiente.setDisable(paginaActual >= totalPaginas);
        }
        // ...actualización de métricas eliminada, si se requiere se puede implementar
        // aquí...
    }

    @FXML
    private void onPaginaAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarPaginacion();
        }
    }

    @FXML
    private void onBuscar() {
        String criterio = txtBuscar.getText().trim().toLowerCase();
        String tipo = cbTipoBusqueda != null && cbTipoBusqueda.getValue() != null ? cbTipoBusqueda.getValue()
                : "Nombre";
        String estado = cmbEstado != null && cmbEstado.getValue() != null ? cmbEstado.getValue() : "Todos";
        boolean soloFrecuentes = chkFrecuentes != null && chkFrecuentes.isSelected();

        // Estrategia unificada para Documento y Nombre/Razón social
        com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.ClienteFilterStrategy strategy;
        switch (tipo) {
            case "Documento":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.FiltroPorDocumento();
                break;
            case "Nombre / Razón social":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.FiltroPorNombreRazonSocial();
                break;
            case "Email":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.FiltroPorEmail();
                break;
            case "Teléfono":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.FiltroPorTelefono();
                break;
            default:
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente.FiltroPorNombreRazonSocial();
                break;
        }

        // Filtrar por tipo
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> base = clientesList;
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> filtrados = strategy
                .filtrar(base, criterio);

        // Filtrar por estado
        if ("Frecuente".equalsIgnoreCase(estado)) {
            filtrados = filtrados.stream().filter(c -> c.isFrecuente()).toList();
        } else if ("No frecuente".equalsIgnoreCase(estado)) {
            filtrados = filtrados.stream().filter(c -> !c.isFrecuente()).toList();
        }

        // Filtrar por solo frecuentes
        if (soloFrecuentes) {
            filtrados = filtrados.stream().filter(c -> c.isFrecuente()).toList();
        }

        // Filtrar cliente genérico
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> filtradosSinGenerico = filtrados
                .stream()
                .filter(c -> c.getDocumento() == null || !c.getDocumento().equals("00000000"))
                .toList();
        tablaClientes.setItems(javafx.collections.FXCollections.observableArrayList(filtradosSinGenerico));
        // Mostrar total real de clientes en verde, igual que proveedores
        if (lblTotalClientes != null && clienteService != null) {
            int totalClientes = clienteService.obtenerTodos().stream()
                    .filter(c -> c.getDocumento() == null || !c.getDocumento().equals("00000000"))
                    .toList().size(); // Incluye NATURAL y EMPRESA
            lblTotalClientes.setText("Total: " + totalClientes + " clientes");
            lblTotalClientes.setStyle("-fx-text-fill: #1bb934; -fx-font-weight: bold;");
        }
    }

    // Nuevo método para mostrar historial de cambios (simulado)
    @FXML
    public void onVerHistorialCambios() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Funcionalidad de historial de cambios en desarrollo.");
        alert.showAndWait();
    }

    // Nuevo método para exportar clientes (Excel/PDF)
    @FXML
    public void onExportarClientes() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/Cliente/exportar-clientes-preview.fxml"));
            javafx.scene.Parent root = loader.load();
            com.farmaciavictoria.proyectopharmavictoria.controller.Cliente.ExportarClientesController exportController = loader
                    .getController();
            // Siempre pasar el listado total de clientes, no solo la paginación actual
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente> listaExportar = clienteService
                    .obtenerTodos();
            exportController.setClientesFiltrados(listaExportar);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1100, 600); // tamaño más pequeño
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Exportar Listado de Clientes");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo abrir la vista de exportación: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Nuevo método para mostrar alertas de clientes incompletos/inactivos
    // (simulado)
    @FXML
    public void onVerAlertasClientes() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION, lblAlertasClientes.getText());
        alert.showAndWait();
    }

    @FXML
    public void onNuevoCliente() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/Cliente/cliente-form.fxml"));
            javafx.scene.Parent root = loader.load();
            ClienteFormController formController = loader.getController();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Nuevo Cliente");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Inyectar lista de clientes para validaciones
            formController.setClientesExistentes(clientesList);
            // Inyectar callback para guardar y refrescar
            formController.setOnClienteEditado(nuevo -> {
                try {
                    if (nuevo != null && nuevo.getTipoCliente() != null && !nuevo.getTipoCliente().isEmpty()) {
                        clienteService.guardarCliente(nuevo);
                        refrescarDatos();
                        javafx.scene.control.Alert ok = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.INFORMATION,
                                "Cliente guardado correctamente.");
                        ok.setTitle("Éxito");
                        ok.setHeaderText(null);
                        ok.showAndWait();
                        // Cerrar el formulario
                        javafx.stage.Stage s = (javafx.stage.Stage) root.getScene().getWindow();
                        s.close();
                    }
                } catch (Exception ex) {
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR, "Error al guardar cliente: " + ex.getMessage());
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.showAndWait();
                }
            });

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo abrir el formulario de cliente: " + e.getMessage());
            alert.showAndWait();
        }
    }
}