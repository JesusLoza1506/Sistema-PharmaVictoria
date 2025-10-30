package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

// Estrategia de filtrado de usuarios
// Estrategias importadas desde strategy/Usuario
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.UsuarioFilterStrategy;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorNombre;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorApellido;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorDni;
import com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario.FiltroPorRol;
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
    private ComboBox<String> cmbEstado;
    @FXML
    private Button btnNuevoUsuario;
    @FXML
    private Button btnExportar;
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
    private TableColumn<Usuario, String> colEstado;
    @FXML
    private TableColumn<Usuario, Void> colAcciones;
    // Columna Último Acceso eliminada del FXML
    private final UsuarioService usuarioService = UsuarioService.getInstance();
    private Usuario usuarioAutenticado;
    private ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();
    private javafx.collections.transformation.FilteredList<Usuario> usuariosFiltrados;
    // Variables de paginación eliminadas

    @FXML
    public void initialize() {
        configurarTabla();
        cargarUsuarios();
        configurarFiltros();
        // Paginación eliminada
        // Dashboard eliminado: no se actualizan estadísticas ni gráficas
        btnNuevoUsuario.setOnAction(e -> onNuevoUsuario());
        // La acción de btnExportar se define en el FXML con onAction="#onExportar"
    }

    // Método vinculado al botón Exportar en el FXML
    public void onExportar(javafx.event.ActionEvent event) {
        exportarUsuariosExcel(); // Ahora abre la ventana de vista previa de exportación
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
            // ...existing code...
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
        javafx.stage.Stage stage = (javafx.stage.Stage) btnExportar.getScene().getWindow();
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
        // ...existing code...
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isActivo() ? "Activo" : "Inactivo"));
        tablaUsuarios.setItems(usuariosList);

        // CellFactory para acciones (editar, ver, activar/desactivar, eliminar)
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnVer = new Button();
            private final Button btnToggleEstado = new Button();
            private final Button btnEliminar = new Button();

            {
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

                btnEditar.setPrefSize(35, 35);
                btnVer.setPrefSize(35, 35);
                btnToggleEstado.setPrefSize(35, 35);
                btnEliminar.setPrefSize(35, 35);

                btnEditar.setTooltip(new Tooltip("Editar usuario"));
                btnVer.setTooltip(new Tooltip("Ver detalles"));
                btnEliminar.setTooltip(new Tooltip("Eliminar usuario"));

                // Acciones
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
                    HBox box = new HBox(6, btnEditar, btnVer, btnToggleEstado, btnEliminar);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    // Toggle estado visual
                    if (usuario != null && usuario.isActivo()) {
                        btnToggleEstado.setText("⏸");
                        btnToggleEstado.setTooltip(new Tooltip("Inactivar usuario"));
                        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
                    } else {
                        btnToggleEstado.setText("▶");
                        btnToggleEstado.setTooltip(new Tooltip("Activar usuario"));
                        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-inactive");
                    }
                    setGraphic(box);
                }
            }
        });
    }

    private void cargarSucursales() {
        // ...existing code...

        // Reutilizar exactamente la implementación de acciones del Inventario
        // ...existing code...
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
                    // Eliminado campo sucursalId
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
                    // Eliminado campo sucursal
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
                    // ...existing code...
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
                        // Eliminado control de auditoría para sucursalId
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
        // Paginación eliminada
        lblTotalUsuarios.setText("Total: " + usuarios.size() + " usuarios");
    }

    private void configurarFiltros() {
        cmbRol.setItems(FXCollections.observableArrayList("ADMIN", "VENDEDOR"));
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
        cmbEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrarUsuarios());
    }

    private void filtrarUsuarios() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        String rol = cmbRol.getValue();
        String estado = cmbEstado.getValue();
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
            return coincideBusqueda && coincideRol && coincideEstado;
        });
        // Paginación eliminada
        lblTotalUsuarios.setText("Total: " + usuariosFiltrados.size() + " usuarios");
    }

    // Configuración de paginación profesional
    private void configurarPaginacion() {
        // Método de paginación eliminado
    }

    private void actualizarPaginacion() {
        // Método de paginación eliminado
    }

    private void onPaginaAnterior() {
        // Método de paginación eliminado
    }

    private void onPaginaSiguiente() {
        // Método de paginación eliminado
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
                    // ...existing code...
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
