package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import javafx.collections.FXCollections;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ProveedorFormController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorFormController.class);

    // FXML Components - Información Básica
    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtRazonSocial;
    @FXML
    private TextField txtRuc;
    @FXML
    private TextField txtContacto;

    // FXML Components - Información de Contacto
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtDireccion;

    // FXML Components - Información Comercial
    @FXML
    private ComboBox<String> cmbCondicionesPago;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private ComboBox<String> cmbTipoProducto;
    @FXML
    private CheckBox chkActivo;

    // FXML Components - Botones
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    // Botón Cancelar eliminado

    private final ProveedorService proveedorService;

    private Proveedor proveedorActual;
    private boolean modoEdicion = false;
    private Consumer<Proveedor> onProveedorGuardado;

    // Patrones de validación flexibles
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PATTERN_RUC = Pattern.compile("^\\d{8,11}$");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^[0-9+\\-\\s\\(\\)]{7,20}$");

    public ProveedorFormController() {
        try {
            ServiceContainer container = ServiceContainer.getInstance();
            this.proveedorService = container.getProveedorService();

            logger.info("ProveedorFormController inicializado con enterprise patterns");
        } catch (Exception e) {
            logger.error("Error al inicializar ProveedorFormController: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo crítico en inicialización del formulario", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponents();
        setupEventHandlers();
        logger.debug("ProveedorFormController inicializado correctamente");
    }

    private void initializeComponents() {
        // Configurar ComboBox de condiciones de pago
        cmbCondicionesPago.setItems(FXCollections.observableArrayList(
                "Contado",
                "15 días",
                "30 días",
                "45 días",
                "60 días",
                "90 días",
                "Contra entrega"));
        cmbCondicionesPago.setValue("30 días");

        // ComboBox Tipo de Producto
        cmbTipoProducto.setItems(FXCollections.observableArrayList(
                "Medicamentos",
                "Insumos",
                "Material Médico",
                "Otros"));
        cmbTipoProducto.setPromptText("Seleccionar tipo");

    }

    private void setupEventHandlers() {
        // Validación flexible de RUC (solo números, no longitud fija)
        txtRuc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txtRuc.setText(oldValue);
            }
        });

        // Validación flexible de teléfono
        txtTelefono.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 20) {
                txtTelefono.setText(newValue.substring(0, 20));
            }
        });
    }

    // Configurar proveedor para edición
    public void setProveedor(Proveedor proveedor) {
        this.proveedorActual = proveedor;
        this.modoEdicion = true;
        cargarDatos();
        lblTitulo.setText("EDITAR PROVEEDOR");
        btnGuardar.setText("💾 Actualizar Proveedor");
    }

    // Callback para notificar cuando se guarda
    public void setOnProveedorGuardado(Consumer<Proveedor> callback) {
        this.onProveedorGuardado = callback;
    }

    private void cargarDatos() {
        if (proveedorActual == null)
            return;

        txtRazonSocial.setText(proveedorActual.getRazonSocial());
        txtRuc.setText(proveedorActual.getRuc() != null ? proveedorActual.getRuc() : "");
        txtContacto.setText(proveedorActual.getContacto() != null ? proveedorActual.getContacto() : "");
        txtTelefono.setText(proveedorActual.getTelefono() != null ? proveedorActual.getTelefono() : "");
        txtEmail.setText(proveedorActual.getEmail() != null ? proveedorActual.getEmail() : "");
        txtDireccion.setText(proveedorActual.getDireccion() != null ? proveedorActual.getDireccion() : "");
        cmbCondicionesPago.setValue(
                proveedorActual.getCondicionesPago() != null ? proveedorActual.getCondicionesPago() : "30 días");
        txtObservaciones.setText(proveedorActual.getObservaciones() != null ? proveedorActual.getObservaciones() : "");
        if (proveedorActual.getTipoProducto() != null) {
            cmbTipoProducto.setValue(proveedorActual.getTipoProducto());
        }
        chkActivo.setSelected(proveedorActual.getActivo() != null ? proveedorActual.getActivo() : true);

    }

    @FXML
    private void guardar(ActionEvent event) {
        if (validarCampos()) {
            try {
                Proveedor proveedor = modoEdicion ? proveedorActual : new Proveedor();

                // Establecer datos básicos
                proveedor.setRazonSocial(txtRazonSocial.getText().trim());
                proveedor.setRuc(txtRuc.getText().trim().isEmpty() ? null : txtRuc.getText().trim());
                proveedor.setContacto(txtContacto.getText().trim().isEmpty() ? null : txtContacto.getText().trim());

                // Datos de contacto
                proveedor.setTelefono(txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim());
                proveedor.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
                proveedor.setDireccion(txtDireccion.getText().trim().isEmpty() ? null : txtDireccion.getText().trim());

                // Datos comerciales
                proveedor.setCondicionesPago(cmbCondicionesPago.getValue());
                proveedor.setObservaciones(
                        txtObservaciones.getText().trim().isEmpty() ? null : txtObservaciones.getText().trim());
                proveedor.setTipoProducto(cmbTipoProducto.getValue());
                proveedor.setActivo(chkActivo.isSelected());

                // Validar unicidad de razón social (nombre)
                boolean existeNombre = proveedorService.existePorRazonSocial(proveedor.getRazonSocial(),
                        modoEdicion ? proveedor.getId() : null);
                if (existeNombre) {
                    showWarning("Validación",
                            "Ya existe un proveedor con el mismo nombre. No se puede registrar ni actualizar.");
                    return;
                }

                Proveedor proveedorGuardado = modoEdicion
                        ? proveedorService.actualizar(proveedor, System.getProperty("user.name", "sistema"))
                        : proveedorService.guardar(proveedor);

                showInfo("Éxito",
                        modoEdicion ? "Proveedor actualizado exitosamente" : "Proveedor registrado exitosamente");

                if (onProveedorGuardado != null) {
                    onProveedorGuardado.accept(proveedorGuardado);
                }

                // Cerrar el formulario automáticamente
                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }

            } catch (Exception e) {
                logger.error("Error al guardar proveedor: {}", e.getMessage(), e);
                showError("Error al guardar", e.getMessage());
            }
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Limpiar Campos");
        alert.setHeaderText("¿Limpiar todos los campos?");
        alert.setContentText("Esta acción borrará toda la información ingresada.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            limpiarCampos();
        }
    }

    // Método cancelar y lógica eliminados

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        // Validar razón social (OBLIGATORIO)
        String razonSocial = txtRazonSocial.getText().trim();
        if (razonSocial.isEmpty()) {
            errores.append("• La razón social es obligatoria\n");
        } else if (razonSocial.length() < 3) {
            errores.append("• La razón social debe tener al menos 3 caracteres\n");
        }

        // Validar RUC (OPCIONAL pero si se ingresa debe ser válido)
        String ruc = txtRuc.getText().trim();
        if (!ruc.isEmpty() && !PATTERN_RUC.matcher(ruc).matches()) {
            errores.append("• El RUC debe tener entre 8 y 11 dígitos\n");
        }

        // Validar email (OPCIONAL pero si se ingresa debe ser válido)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !PATTERN_EMAIL.matcher(email).matches()) {
            errores.append("• El formato del email no es válido\n");
        }

        // Validar teléfono (OPCIONAL pero si se ingresa debe ser válido)
        String telefono = txtTelefono.getText().trim();
        if (!telefono.isEmpty() && !PATTERN_TELEFONO.matcher(telefono).matches()) {
            errores.append("• El formato del teléfono no es válido\n");
        }

        // Validar tipo de producto (opcional)
        // Validar sucursalId (opcional pero si se ingresa debe ser número)
        if (errores.length() > 0) {
            showWarning("Errores de validación", errores.toString());
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        // Información básica
        txtRazonSocial.clear();
        txtRuc.clear();
        txtContacto.clear();

        // Información de contacto
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();

        // Información comercial
        cmbCondicionesPago.setValue("30 días");
        txtObservaciones.clear();
        cmbTipoProducto.setValue(null);
        chkActivo.setSelected(true);

    }

    // Método cerrarVentana eliminado completamente

    // Métodos de utilidad para mostrar alertas
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}