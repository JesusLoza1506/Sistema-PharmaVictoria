package com.farmaciavictoria.proyectopharmavictoria.controller;

import javafx.scene.image.ImageView;

import com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de inicio de sesión de PHARMAVICTORIA
 * Maneja la autenticación de usuarios y navegación al dashboard principal
 */
public class LoginController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // Elementos de la interfaz
    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private Button loginButton;
    @FXML
    private ProgressIndicator loginProgress;
    // @FXML // Eliminado porque no existe en FXML
    @FXML
    private Label errorLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label connectionStatus;

    // ✅ DEPENDENCY INJECTION: Services inyectados desde ServiceContainer
    private AuthenticationService authService;
    private UsuarioRepository usuarioRepository;

    // Estado de la aplicación
    private boolean isDatabaseConnected = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        togglePasswordButton.setOnAction(e -> togglePasswordVisibility());
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        Platform.runLater(() -> {
            ImageView icon = new ImageView(
                    new javafx.scene.image.Image(getClass().getResourceAsStream("/icons/eyelogin.png")));
            icon.setFitWidth(18);
            icon.setFitHeight(18);
            icon.setPreserveRatio(true);
            togglePasswordButton.setGraphic(icon);
        });
        logger.info("Inicializando controlador de Login");

        // Inicializar servicios
        initializeServices();

        // Configurar interfaz
        setupUI();

        // Verificar conexión a base de datos
        checkDatabaseConnection();

        // Configurar eventos
        setupEventHandlers();

        // Sincronizar campos de contraseña
        // El binding de managedProperty se elimina para evitar el error de JavaFX
        passwordVisibleField.setVisible(false);
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        logger.info("Controlador de Login inicializado correctamente");
    }

    /**
     * Muestra un diálogo para recuperación de contraseña
     */
    private void handleForgotPassword() {
        try {
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/fxml/forgot_password_dialog.fxml"));
            javafx.scene.layout.StackPane root = dialogLoader.load();
            ForgotPasswordDialogController dialogController = dialogLoader.getController();
            dialogController.setUsuarioRepository(usuarioRepository);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Recuperar acceso");
            dialogStage.initOwner(loginButton.getScene().getWindow());
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/forgot_password_dialog.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (Exception e) {
            logger.error("Error al mostrar diálogo de recuperación: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Recuperación de acceso");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo mostrar el diálogo de recuperación.");
            alert.showAndWait();
        }
    }

    /**
     * ✅ DEPENDENCY INJECTION: Inicializa los servicios desde ServiceContainer
     */
    private void initializeServices() {
        try {
            ServiceContainer container = ServiceContainer.getInstance();
            this.authService = container.getAuthenticationService();
            this.usuarioRepository = container.getUsuarioRepository();
            logger.info("Servicios inicializados correctamente via Dependency Injection");
        } catch (Exception e) {
            logger.error("Error al inicializar servicios: {}", e.getMessage(), e);
            showLoginError("Error al inicializar servicios del sistema");
        }
    }

    /**
     * Configura la interfaz de usuario
     */
    private void setupUI() {
        // Configurar información del sistema
        versionLabel.setText("PharmaVictoria v1.0 - 2025");
        statusLabel.setText("Sistema Operativo");

        // Foco inicial en campo usuario
        Platform.runLater(() -> usernameField.requestFocus());

        // Configurar botón de login como predeterminado
        loginButton.setDefaultButton(true);

        logger.debug("Interfaz configurada correctamente");
    }

    /**
     * Verifica la conexión a la base de datos
     */
    private void checkDatabaseConnection() {
        Task<Boolean> connectionTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    return DatabaseConfig.getInstance().testConnection();
                } catch (Exception e) {
                    logger.error("Error de conexión a BD: {}", e.getMessage(), e);
                    return false;
                }
            }
        };

        connectionTask.setOnSucceeded(e -> {
            isDatabaseConnected = connectionTask.getValue();
            updateConnectionStatus();
        });

        connectionTask.setOnFailed(e -> {
            isDatabaseConnected = false;
            updateConnectionStatus();
            logger.error("Fallo en verificación de conexión: {}", connectionTask.getException().getMessage());
        });

        new Thread(connectionTask).start();
    }

    private void updateConnectionStatus() {
        Platform.runLater(() -> {
            if (isDatabaseConnected) {
                connectionStatus.setText("🟢 Base de Datos Conectada");
                connectionStatus.setStyle("-fx-text-fill: white;");
                loginButton.setDisable(false);
            } else {
                connectionStatus.setText("🔴 Sin Conexión a BD");
                connectionStatus.setStyle("-fx-text-fill: #ffcccb;");
                loginButton.setDisable(true);
                showLoginError("No se puede conectar a la base de datos");
            }
        });
    }

    /**
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        // Enter en campo contraseña ejecuta login
        passwordField.setOnAction(e -> handleLogin());

        // Enter en campo usuario mueve a contraseña
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Limpiar error al escribir
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordVisibleField.setOnAction(e -> handleLogin());
    }

    /**
     * Alterna la visibilidad de la contraseña
     */
    private void togglePasswordVisibility() {
        boolean isVisible = passwordVisibleField.isVisible();
        if (isVisible) {
            // Ocultar contraseña
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            Platform.runLater(() -> {
                togglePasswordButton.setGraphic(null);
                ImageView icon = new ImageView(
                        new javafx.scene.image.Image(getClass().getResourceAsStream("/icons/eyelogin.png")));
                icon.setFitWidth(18);
                icon.setFitHeight(18);
                icon.setPreserveRatio(true);
                togglePasswordButton.setGraphic(icon);
            });
        } else {
            // Mostrar contraseña
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            Platform.runLater(() -> {
                togglePasswordButton.setGraphic(null);
                ImageView icon = new ImageView(
                        new javafx.scene.image.Image(getClass().getResourceAsStream("/icons/noteyelogin.png")));
                icon.setFitWidth(18);
                icon.setFitHeight(18);
                icon.setPreserveRatio(true);
                togglePasswordButton.setGraphic(icon);
            });
        }
    }

    // Eliminar el cierre de llave extra

    /**
     * Maneja el evento de inicio de sesión
     */
    @FXML
    private void handleLogin() {
        logger.info("Iniciando proceso de autenticación");

        // Validar campos
        if (!validateFields()) {
            return;
        }

        // Mostrar spinner y deshabilitar interfaz
        loginProgress.setVisible(true);
        setUIEnabled(false);

        // Crear tarea de autenticación
        Task<Usuario> loginTask = new Task<>() {
            @Override
            protected Usuario call() throws Exception {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();

                logger.debug("Intentando autenticar usuario: {}", username);
                return authService.authenticate(username, password);
            }
        };

        // Configurar callbacks
        loginTask.setOnSucceeded(e -> {
            loginProgress.setVisible(false);
            Usuario usuario = loginTask.getValue();
            if (usuario != null) {
                handleSuccessfulLogin(usuario);
            } else {
                // Mostrar mensaje de error por credenciales incorrectas
                showLoginError("Credenciales incorrectas", usernameField, passwordField);
                setUIEnabled(true);
                passwordField.clear();
                passwordField.requestFocus();
            }
        });

        loginTask.setOnFailed(e -> {
            loginProgress.setVisible(false);
            Throwable exception = loginTask.getException();
            logger.error("Error durante autenticación: {}", exception.getMessage(), exception);
            showLoginError("Error interno del sistema", usernameField, passwordField);
            setUIEnabled(true);
            passwordField.clear();
            passwordField.requestFocus();
        });

        // Ejecutar en hilo separado
        new Thread(loginTask).start();
    }

    /**
     * Valida los campos de entrada
     */
    private boolean validateFields() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showLoginError("Por favor ingrese su usuario", usernameField);
            usernameField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showLoginError("Por favor ingrese su contraseña", passwordField);
            passwordField.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            showLoginError("El usuario debe tener al menos 3 caracteres", usernameField);
            usernameField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Maneja un login exitoso
     */
    private void handleSuccessfulLogin(Usuario usuario) {
        logger.info("Login exitoso para usuario: {} ({})", usuario.getUsername(), usuario.getRol());

        // Asignar usuario globalmente para toda la sesión
        com.farmaciavictoria.proyectopharmavictoria.SessionManager.setUsuarioActual(usuario);

        Platform.runLater(() -> {
            showSuccess("¡Bienvenido " + usuario.getNombreCompleto() + "!");

            // Actualizar último login
            updateLastLogin(usuario);

            // Navegar al dashboard después de un breve delay
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> navigateToDashboard(usuario));
            delay.play();
        });
    }

    /**
     * Maneja un login fallido
     */
    private void handleFailedLogin(String message) {
        logger.warn("Login fallido: {}", message);

        Platform.runLater(() -> {
            showLoginError(message, usernameField, passwordField);
            setUIEnabled(true);
            passwordField.clear();
            passwordField.requestFocus();
        });
    }

    /**
     * Actualiza el último login del usuario
     */
    private void updateLastLogin(Usuario usuario) {
        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                usuarioRepository.updateLastLogin(usuario.getUsername());
                return null;
            }
        };

        updateTask.setOnFailed(
                e -> logger.warn("No se pudo actualizar último login: {}", updateTask.getException().getMessage()));

        new Thread(updateTask).start();
    }

    /**
     * Navega al dashboard principal
     */
    private void navigateToDashboard(Usuario usuario) {
        try {
            logger.info("[NAV] Navegando al dashboard principal");
            String fxmlPath;
            boolean esVendedor = false;
            if (usuario.getRol() != null) {
                String rol = usuario.getRol().name().toLowerCase();
                esVendedor = rol.contains("vendedor");
            }
            if (esVendedor) {
                fxmlPath = "/fxml/dashboard-vendedor.fxml";
            } else {
                fxmlPath = "/fxml/dashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (esVendedor) {
                DashboardVendedorController dashboardVendedorController = loader.getController();
                if (dashboardVendedorController != null) {
                    dashboardVendedorController.setUsuario(usuario);
                    logger.debug("[NAV] Usuario establecido en dashboard vendedor: {}", usuario.getNombreCompleto());
                }
            } else {
                DashboardController dashboardController = loader.getController();
                if (dashboardController != null) {
                    dashboardController.setUsuario(usuario);
                    logger.debug("[NAV] Usuario establecido en dashboard: {}", usuario.getNombreCompleto());
                }
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            logger.info("[NAV] Antes de cambiar escena: maximized={} width={} height={}", stage.isMaximized(),
                    stage.getWidth(), stage.getHeight());
            // Guardar tamaño de pantalla si está maximizado
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
            stage.setScene(scene);
            stage.setTitle("PharmaVictoria - Menú Principal");
            stage.setResizable(true);
            stage.centerOnScreen();
            Platform.runLater(() -> {
                stage.setMaximized(true);
                // Si sigue maximizado pero el tamaño es incorrecto, forzar tamaño
                if (stage.isMaximized() && (Math.abs(stage.getWidth() - screenWidth) > 10
                        || Math.abs(stage.getHeight() - screenHeight) > 10)) {
                    logger.warn("[NAV] Maximizado pero tamaño incorrecto, forzando tamaño: {}x{}", screenWidth,
                            screenHeight);
                    stage.setMaximized(false);
                    stage.setWidth(screenWidth);
                    stage.setHeight(screenHeight);
                    stage.setMaximized(true);
                }
                logger.info("[NAV] Después de workaround: maximized={} width={} height={}", stage.isMaximized(),
                        stage.getWidth(), stage.getHeight());
            });
            logger.info("[NAV] Navegación al dashboard completada");
        } catch (IOException e) {
            logger.error("Error al cargar dashboard: {}", e.getMessage(), e);
            showLoginError("Error al cargar la pantalla principal");
            setUIEnabled(true);
        }
    }

    /**
     * Maneja el evento de cancelar
     */
    @FXML
    private void handleCancel() {
        logger.info("Cancelando aplicación");
        Platform.exit();
    }

    /**
     * Muestra un mensaje de error de login o validación
     */
    private void showLoginError(String message, Control... fields) {
        logger.error("[LOGIN] showLoginError: {}", message);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.getStyleClass().removeAll("success-label");
        errorLabel.getStyleClass().add("error-label");
        if (fields != null) {
            for (Control field : fields) {
                addErrorStyle(field);
            }
        }
    }

    /**
     * Muestra un mensaje de éxito
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.getStyleClass().removeAll("error-label");
        errorLabel.getStyleClass().add("success-label");
    }

    /**
     * Limpia el mensaje de error
     */
    private void clearError() {
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            errorLabel.setText("");
            removeErrorStyles();
        });
    }

    /**
     * Añade estilo de error a un campo
     */
    private void addErrorStyle(Control control) {
        control.getStyleClass().add("error");
    }

    /**
     * Remueve estilos de error de todos los campos
     */
    private void removeErrorStyles() {
        usernameField.getStyleClass().removeAll("error", "success");
        passwordField.getStyleClass().removeAll("error", "success");
    }

    /**
     * Habilita o deshabilita la interfaz
     */
    private void setUIEnabled(boolean enabled) {
        usernameField.setDisable(!enabled);
        passwordField.setDisable(!enabled);
        loginButton.setDisable(!enabled || !isDatabaseConnected);
        // cancelButton.setDisable(!enabled); // Eliminado porque no existe en FXML

        if (enabled) {
            loginButton.setText("INICIAR SESIÓN");
        } else {
            loginButton.setText("VERIFICANDO...");
        }
    }
}