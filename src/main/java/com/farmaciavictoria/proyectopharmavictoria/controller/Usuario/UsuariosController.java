package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

// Estrategia de filtrado de usuarios
// Estrategias importadas desde strategy/Usuario
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.UsuarioFilterStrategy;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorNombre;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorApellido;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorDni;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorRol;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorSucursal;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorEstado;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.HBox;

import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Controlador principal para la gestión de usuarios
 */
public class UsuariosController {
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblTotalUsuarios;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private ComboBox<String> cmbSucursal;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private Button btnNuevoUsuario;
    @FXML
    private Button btnExportar;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private Button btnAjustesVista;
    @FXML
    private TableView<Usuario> tablaUsuarios;
    // Columna ID eliminada del FXML
    @FXML
    private TableColumn<Usuario, String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colNombres;
    @FXML
    private TableColumn<Usuario, String> colApellidos;
    @FXML
    private TableColumn<Usuario, String> colDni;
    @FXML
    private TableColumn<Usuario, String> colTelefono;
    @FXML
    private TableColumn<Usuario, String> colSucursal;
    @FXML
    private TableColumn<Usuario, String> colEstado;
    // Columna Último Acceso eliminada del FXML
    @FXML
    private TableColumn<Usuario, Void> colAcciones;

    private final UsuarioService usuarioService = UsuarioService.getInstance();
    private Usuario usuarioAutenticado;
    private ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();
    private javafx.collections.transformation.FilteredList<Usuario> usuariosFiltrados;
    private java.util.Map<Integer, String> sucursalNombreMap = new java.util.HashMap<>();

    // Variables de paginación
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
    public void initialize() {
        cargarSucursales();
        configurarTabla();
        cargarUsuarios();
        configurarFiltros();
        configurarPaginacion();
        // Dashboard eliminado: no se actualizan estadísticas ni gráficas
        btnNuevoUsuario.setOnAction(e -> onNuevoUsuario());
        if (btnExportar != null)
            btnExportar.setOnAction(e -> exportarUsuariosExcel());
        if (btnExportarPDF != null)
            btnExportarPDF.setOnAction(e -> exportarUsuariosPDF());
    }

    // Método para recibir el usuario autenticado desde el DashboardController
    public void setUsuarioAutenticado(Usuario usuario) {
        this.usuarioAutenticado = usuario;
    }

    private void exportarUsuariosExcel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Usuario/exportar-usuarios-preview.fxml"));
            VBox vistaPrevia = loader.load();
            com.farmaciavictoria.proyectopharmavictoria.controller.Usuario.ExportarUsuariosPreviewController controller = loader
                    .getController();
            Stage stage = new Stage();
            stage.setTitle("Exportar Usuarios - Vista Previa");
            Scene scene = new Scene(vistaPrevia);
            stage.setScene(scene);
            stage.initOwner(btnExportar.getScene().getWindow());
            stage.setResizable(true);
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir la vista previa de exportación de usuarios.");
            alert.showAndWait();
        }
    }

    private void exportarUsuariosPDF() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setInitialFileName("usuarios.pdf");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setTitle("Exportar listado de usuarios");
        javafx.stage.Stage stage = (javafx.stage.Stage) btnExportarPDF.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                com.farmaciavictoria.proyectopharmavictoria.util.ExportFacade.exportarUsuariosPDF(usuariosList,
                        file.getAbsolutePath().replace(".pdf", ""));
                mostrarMensajeExportacion("Archivo exportado correctamente en:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                mostrarMensajeExportacion("Error al exportar el archivo. Intente nuevamente.");
            }
        }
    }

    private void mostrarMensajeExportacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportación de Usuarios");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void configurarTabla() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        // Mostrar nombre de sucursal en vez de ID
        colSucursal.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            Integer sucursalId = null;
            if (usuario.getSucursalId() != null) {
                try {
                    sucursalId = Integer.valueOf(usuario.getSucursalId().toString());
                } catch (Exception e) {
                    sucursalId = null;
                }
            }
            String nombre = (sucursalId != null && sucursalNombreMap.containsKey(sucursalId))
                    ? sucursalNombreMap.get(sucursalId)
                    : "-";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isActivo() ? "Activo" : "Inactivo"));
        tablaUsuarios.setItems(usuariosList);
    }

    private void cargarSucursales() {
        // Obtener el repositorio desde el ServiceContainer
        com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository sucursalRepo = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                .getInstance()
                .getRepository(com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository.class);
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Sucursal> sucursales = sucursalRepo
                .findAllActivas();
        sucursalNombreMap.clear();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Sucursal suc : sucursales) {
            sucursalNombreMap.put(suc.getId(), suc.getNombre());
        }

        // Reutilizar exactamente la implementación de acciones del Inventario
        colAcciones.setCellFactory(new javafx.util.Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>>() {
            @Override
            public TableCell<Usuario, Void> call(TableColumn<Usuario, Void> param) {
                return new TableCell<Usuario, Void>() {
                    private final Button btnEditar = new Button();
                    private final Button btnVer = new Button();
                    private final Button btnToggleEstado = new Button();
                    private final Button btnEliminar = new Button();

                    {
                        // Íconos iguales a los usados en Inventario (recursos /icons)
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
                        btnEliminar.getStyleClass().setAll("btn-action", "btn-trash");

                        btnEditar.setPrefWidth(35);
                        btnEditar.setPrefHeight(35);
                        btnVer.setPrefWidth(35);
                        btnVer.setPrefHeight(35);
                        btnToggleEstado.setPrefWidth(35);
                        btnToggleEstado.setPrefHeight(35);
                        btnEliminar.setPrefWidth(35);
                        btnEliminar.setPrefHeight(35);

                        HBox buttons = new HBox(5, btnEditar, btnVer, btnToggleEstado, btnEliminar);
                        buttons.setAlignment(javafx.geometry.Pos.CENTER);

                        btnEditar.setOnAction(e -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            editarUsuario(usuario);
                        });
                        btnVer.setOnAction(e -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            verDetallesUsuario(usuario);
                        });
                        btnToggleEstado.setOnAction(e -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            toggleEstadoUsuario(usuario);
                        });
                        btnEliminar.setOnAction(e -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            eliminarUsuario(usuario);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            if (usuario != null && usuario.isActivo()) {
                                btnToggleEstado.setText("⏸");
                                btnToggleEstado.setTooltip(new Tooltip("Inactivar usuario"));
                                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
                            } else {
                                btnToggleEstado.setText("▶");
                                btnToggleEstado.setTooltip(new Tooltip("Activar usuario"));
                                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle",
                                        "btn-toggle-inactive");
                            }
                            HBox botones = new HBox(5, btnEditar, btnVer, btnToggleEstado, btnEliminar);
                            botones.setAlignment(javafx.geometry.Pos.CENTER);
                            setGraphic(botones);
                        }
                    }
                };
            }
        });
    }

    // Métodos de acción para los botones
    private void verDetallesUsuario(Usuario usuario) {
        if (usuario != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Usuario/usuario-detalles.fxml"));
                BorderPane detalles = loader.load();
                UsuarioDetallesController detallesController = loader.getController();
                detallesController.cargarUsuario(usuario);
                Stage stage = new Stage();
                stage.setTitle("Detalles del Usuario");
                Scene scene = new Scene(detalles);
                stage.setScene(scene);
                stage.initOwner(btnNuevoUsuario.getScene().getWindow());
                stage.setResizable(false);
                stage.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de detalles de usuario.");
                alert.showAndWait();
            }
        }
    }

    private void editarUsuario(Usuario usuario) {
        if (usuario != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Usuario/usuario-form.fxml"));
                VBox form = loader.load();
                UsuarioFormController formController = loader.getController();
                formController.cargarUsuario(usuario); // Método para cargar datos en el formulario
                Stage stage = new Stage();
                stage.setTitle("Editar Usuario");
                Scene scene = new Scene(form);
                stage.setScene(scene);
                stage.initOwner(btnNuevoUsuario.getScene().getWindow());
                stage.setResizable(true);
                formController.btnCancelar.setOnAction(e -> stage.close());
                formController.btnGuardar.setOnAction(e -> {
                    // Guardar valores originales para auditoría ANTES de modificar el usuario
                    String oldUsername = usuario.getUsername() != null ? usuario.getUsername() : "";
                    Usuario.Rol oldRol = usuario.getRol();
                    Long oldSucursalId = usuario.getSucursalId();
                    String oldNombres = usuario.getNombres() != null ? usuario.getNombres() : "";
                    String oldApellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
                    String oldDni = usuario.getDni() != null ? usuario.getDni() : "";
                    String oldTelefono = usuario.getTelefono() != null ? usuario.getTelefono() : "";
                    String oldEmail = usuario.getEmail() != null ? usuario.getEmail() : "";
                    String oldPasswordHash = usuario.getPasswordHash() != null ? usuario.getPasswordHash() : "";

                    // Nuevos valores del formulario
                    String newUsername = formController.getUsername();
                    Usuario.Rol newRol = "ADMIN".equals(formController.getRol()) ? Usuario.Rol.ADMIN
                            : Usuario.Rol.VENDEDOR;
                    String sucursalValue = formController.getSucursal();
                    Long newSucursalId = null;
                    if (sucursalValue != null && sucursalValue.matches("^\\d+.*")) {
                        String idStr = sucursalValue.split(" ", 2)[0];
                        try {
                            newSucursalId = Long.parseLong(idStr);
                        } catch (NumberFormatException ex2) {
                            newSucursalId = null;
                        }
                    }
                    String newNombres = formController.getNombres();
                    String newApellidos = formController.getApellidos();
                    String newDni = formController.getDni();
                    String newTelefono = formController.getTelefono();
                    String newEmail = formController.getEmail();
                    String rawPassword = formController.getPassword();
                    String newPasswordHash = oldPasswordHash;
                    if (rawPassword != null && !rawPassword.isBlank()) {
                        newPasswordHash = com.farmaciavictoria.proyectopharmavictoria.util.PasswordUtil
                                .hashPassword(rawPassword);
                    }

                    // Actualiza el usuario con los datos del formulario
                    usuario.setUsername(newUsername);
                    usuario.setRol(newRol);
                    usuario.setSucursalId(newSucursalId);
                    usuario.setNombres(newNombres);
                    usuario.setApellidos(newApellidos);
                    usuario.setDni(newDni);
                    usuario.setTelefono(newTelefono);
                    usuario.setEmail(newEmail);
                    if (rawPassword != null && !rawPassword.isBlank()) {
                        usuario.setPasswordHash(newPasswordHash);
                    }

                    formController.guardarUsuario();
                    if (formController.isRegistrado()) {
                        Long adminId = (usuarioAutenticado != null && usuarioAutenticado.getId() != null)
                                ? usuarioAutenticado.getId()
                                : null;
                        // Auditoría campo a campo
                        if (!oldUsername.equals(newUsername)) {
                            usuarioService.editarUsuario(usuario, "username", oldUsername, newUsername, adminId);
                        }
                        if (oldRol != newRol) {
                            usuarioService.editarUsuario(usuario, "rol", oldRol != null ? oldRol.name() : "",
                                    newRol != null ? newRol.name() : "", adminId);
                        }
                        if ((oldSucursalId != null ? oldSucursalId : -1L) != (newSucursalId != null ? newSucursalId
                                : -1L)) {
                            usuarioService.editarUsuario(usuario, "sucursalId",
                                    oldSucursalId != null ? oldSucursalId.toString() : "",
                                    newSucursalId != null ? newSucursalId.toString() : "", adminId);
                        }
                        if (!oldNombres.equals(newNombres)) {
                            usuarioService.editarUsuario(usuario, "nombres", oldNombres, newNombres, adminId);
                        }
                        if (!oldApellidos.equals(newApellidos)) {
                            usuarioService.editarUsuario(usuario, "apellidos", oldApellidos, newApellidos, adminId);
                        }
                        if (!oldDni.equals(newDni)) {
                            usuarioService.editarUsuario(usuario, "dni", oldDni, newDni, adminId);
                        }
                        if (!oldTelefono.equals(newTelefono)) {
                            usuarioService.editarUsuario(usuario, "telefono", oldTelefono, newTelefono, adminId);
                        }
                        if (!oldEmail.equals(newEmail)) {
                            usuarioService.editarUsuario(usuario, "email", oldEmail, newEmail, adminId);
                        }
                        if (rawPassword != null && !rawPassword.isBlank() && !oldPasswordHash.equals(newPasswordHash)) {
                            usuarioService.editarUsuario(usuario, "passwordHash", "(hash anterior)", "(hash nuevo)",
                                    adminId);
                        }
                        cargarUsuarios();
                        mostrarConfirmacionEdicion();
                        stage.close();
                    }
                });
                stage.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario de edición de usuario.");
                alert.showAndWait();
            }
        }
    }

    private void eliminarUsuario(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar usuario?");
        confirmacion.setContentText("¿Está seguro de que desea eliminar el usuario '" + usuario.getUsername()
                + "'? Esta acción no se puede deshacer.");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Long adminId = (usuarioAutenticado != null && usuarioAutenticado.getId() != null)
                            ? usuarioAutenticado.getId()
                            : null;
                    usuarioService.eliminarUsuario(usuario.getId(), adminId);
                    cargarUsuarios();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Usuario eliminado");
                    info.setHeaderText(null);
                    info.setContentText("¡El usuario ha sido eliminado exitosamente!");
                    info.showAndWait();
                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el usuario: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void mostrarConfirmacionEliminacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usuario eliminado");
        alert.setHeaderText(null);
        alert.setContentText("¡El usuario ha sido eliminado exitosamente!");
        alert.showAndWait();
    }

    private void toggleEstadoUsuario(Usuario usuario) {
        try {
            // Cambiar estado
            boolean nuevoEstado = !usuario.isActivo();
            usuario.setActivo(nuevoEstado);

            // Actualizar en BD usando service layer
            String usuarioActual = System.getProperty("user.name", "sistema");
            // Usar el método correcto para actualizar el usuario
            usuarioService.actualizarEstado(usuario.getId(), nuevoEstado, usuarioActual);

            // Actualizar solo la fila modificada en la tabla (sin recargar toda la lista)
            tablaUsuarios.refresh();

            // Notificación discreta
            String mensaje = nuevoEstado ? String.format("Usuario '%s' ACTIVADO correctamente", usuario.getUsername())
                    : String.format("Usuario '%s' INACTIVADO correctamente", usuario.getUsername());

            // Si tienes notificationService, úsalo. Si no, muestra Alert.
            try {
                com.farmaciavictoria.proyectopharmavictoria.service.NotificationService notificationService = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                        .getInstance().getNotificationService();
                notificationService.mostrarExito(mensaje);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje);
                alert.showAndWait();
            }
            System.out.println("Estado del usuario " + usuario.getUsername() + " cambiado a: "
                    + (nuevoEstado ? "ACTIVO" : "INACTIVO"));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "No se pudo cambiar el estado del usuario: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void cambiarPasswordUsuario(Usuario usuario) {
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        usuariosList.setAll(usuarios);
        usuariosFiltrados = new javafx.collections.transformation.FilteredList<>(usuariosList, u -> true);
        paginaActual = 1;
        actualizarPaginacion();
        lblTotalUsuarios.setText("Total: " + usuarios.size() + " usuarios");
    }

    private void configurarFiltros() {
        cmbRol.setItems(FXCollections.observableArrayList("ADMIN", "VENDEDOR"));
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        // Poblar sucursales reales desde la base de datos
        com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository sucursalRepo = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                .getInstance()
                .getRepository(com.farmaciavictoria.proyectopharmavictoria.repository.SucursalRepository.class);
        java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Sucursal> sucursales = sucursalRepo
                .findAllActivas();
        javafx.collections.ObservableList<String> sucursalItems = FXCollections.observableArrayList();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Sucursal suc : sucursales) {
            sucursalItems.add(suc.getId() + " - " + suc.getNombre());
        }
        cmbSucursal.setItems(sucursalItems);
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
        cmbEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
        cmbSucursal.valueProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
    }

    private void filtrarUsuarios() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        String rol = cmbRol.getValue();
        String estado = cmbEstado.getValue();
        String sucursal = cmbSucursal.getValue();
        usuariosFiltrados.setPredicate(u -> {
            boolean coincideBusqueda = busqueda.isEmpty()
                    || u.getUsername().toLowerCase().contains(busqueda)
                    || (u.getNombres() != null && u.getNombres().toLowerCase().contains(busqueda))
                    || (u.getApellidos() != null && u.getApellidos().toLowerCase().contains(busqueda))
                    || (u.getDni() != null && u.getDni().toLowerCase().contains(busqueda))
                    || (u.getTelefono() != null && u.getTelefono().toLowerCase().contains(busqueda));
            boolean coincideRol = rol == null || rol.isBlank()
                    || (u.getRol() != null && u.getRol().name().equalsIgnoreCase(rol));
            boolean coincideEstado = estado == null || estado.isBlank() || (estado.equals("Activo") && u.isActivo())
                    || (estado.equals("Inactivo") && !u.isActivo());
            boolean coincideSucursal = sucursal == null || sucursal.isBlank()
                    || (u.getSucursalId() != null && sucursal.startsWith(u.getSucursalId().toString()));
            return coincideBusqueda && coincideRol && coincideEstado && coincideSucursal;
        });
        paginaActual = 1;
        actualizarPaginacion();
        lblTotalUsuarios.setText("Total: " + usuariosFiltrados.size() + " usuarios");
    }

    // Configuración de paginación profesional
    private void configurarPaginacion() {
        if (cmbTamanoPagina != null) {
            cmbTamanoPagina.getItems().clear();
            cmbTamanoPagina.getItems().addAll(5, 10, 20, 50);
            cmbTamanoPagina.setValue(10);
            tamanoPagina = 10;
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
    }

    private void actualizarPaginacion() {
        int total = usuariosFiltrados.size();
        totalPaginas = (int) Math.ceil((double) total / tamanoPagina);
        if (totalPaginas == 0)
            totalPaginas = 1;
        if (paginaActual > totalPaginas)
            paginaActual = totalPaginas;
        int desde = (paginaActual - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, total);
        ObservableList<Usuario> pagina = FXCollections.observableArrayList();
        for (int i = desde; i < hasta; i++) {
            pagina.add(usuariosFiltrados.get(i));
        }
        tablaUsuarios.setItems(pagina);
        if (lblPaginaActual != null) {
            lblPaginaActual.setText("Página " + paginaActual + " de " + totalPaginas);
        }
        if (btnPaginaAnterior != null) {
            btnPaginaAnterior.setDisable(paginaActual == 1);
        }
        if (btnPaginaSiguiente != null) {
            btnPaginaSiguiente.setDisable(paginaActual == totalPaginas);
        }
    }

    private void onPaginaAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarPaginacion();
        }
    }

    private void onPaginaSiguiente() {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            actualizarPaginacion();
        }
    }

    @FXML
    private void onNuevoUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Usuario/usuario-form.fxml"));
            VBox form = loader.load();
            UsuarioFormController formController = loader.getController();
            // Mostrar el formulario en un Stage modal personalizado, sin botones de Dialog
            Stage stage = new Stage();
            stage.setTitle("Registrar Usuario");
            Scene scene = new Scene(form);
            stage.setScene(scene);
            stage.initOwner(btnNuevoUsuario.getScene().getWindow());
            stage.setResizable(true);
            formController.btnCancelar.setOnAction(e -> stage.close());
            formController.btnGuardar.setOnAction(e -> {
                formController.guardarUsuario();
                if (formController.isRegistrado()) {
                    Usuario nuevo = new Usuario();
                    nuevo.setUsername(formController.getUsername());
                    // Hashear la contraseña con BCrypt
                    String rawPassword = formController.getPassword();
                    String hashedPassword = com.farmaciavictoria.proyectopharmavictoria.util.PasswordUtil
                            .hashPassword(rawPassword);
                    nuevo.setPasswordHash(hashedPassword);
                    nuevo.setRol("ADMIN".equals(formController.getRol()) ? Usuario.Rol.ADMIN : Usuario.Rol.VENDEDOR);
                    String sucursalValue = formController.getSucursal();
                    Long sucursalId = null;
                    if (sucursalValue != null && sucursalValue.matches("^\\d+.*")) {
                        String idStr = sucursalValue.split(" ", 2)[0];
                        try {
                            sucursalId = Long.parseLong(idStr);
                        } catch (NumberFormatException ex2) {
                            sucursalId = null;
                        }
                    }
                    nuevo.setSucursalId(sucursalId);
                    nuevo.setNombres(formController.getNombres());
                    nuevo.setApellidos(formController.getApellidos());
                    nuevo.setDni(formController.getDni());
                    nuevo.setTelefono(formController.getTelefono());
                    nuevo.setEmail(formController.getEmail());
                    nuevo.setActivo(true);
                    Long adminId = (usuarioAutenticado != null && usuarioAutenticado.getId() != null)
                            ? usuarioAutenticado.getId()
                            : null;
                    String error = usuarioService.registrarUsuario(nuevo, adminId);
                    if (error != null) {
                        mostrarErrorRegistro(error);
                    } else {
                        cargarUsuarios();
                        mostrarConfirmacionRegistro();
                        stage.close();
                    }
                }
            });
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario de usuario.");
            alert.showAndWait();
        }
    }

    private void mostrarConfirmacionRegistro() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usuario registrado");
        alert.setHeaderText(null);
        alert.setContentText("¡El usuario ha sido registrado exitosamente!");
        alert.showAndWait();
    }

    private void mostrarConfirmacionEdicion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usuario editado");
        alert.setHeaderText(null);
        alert.setContentText("¡El usuario ha sido editado exitosamente!");
        alert.showAndWait();
    }

    private void mostrarErrorRegistro(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de registro");
        alert.setHeaderText("No se pudo registrar el usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
