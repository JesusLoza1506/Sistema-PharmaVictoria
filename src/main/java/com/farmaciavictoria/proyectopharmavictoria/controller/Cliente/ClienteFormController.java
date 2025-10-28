package com.farmaciavictoria.proyectopharmavictoria.controller.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

public class ClienteFormController {
    // Callback para notificar edición instantánea (ahora acepta el cliente editado)
    private java.util.function.Consumer<Cliente> onClienteEditado;

    public void setOnClienteEditado(java.util.function.Consumer<Cliente> callback) {
        this.onClienteEditado = callback;
    }

    @FXML
    public void onCancelar() {
        // Cierra la ventana del formulario
        Stage stage = (Stage) txtDni.getScene().getWindow();
        stage.close();
    }

    @FXML
    private ComboBox<String> comboTipoCliente;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtRuc;
    @FXML
    private TextField txtRazonSocial;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtDireccion;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private Label lblError;

    private Cliente cliente;
    private boolean editMode = false;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            editMode = true;
            comboTipoCliente.setValue(cliente.getTipoCliente());
            txtNombres.setText(cliente.getNombres());
            txtApellidos.setText(cliente.getApellidos());
            txtTelefono.setText(cliente.getTelefono());
            txtEmail.setText(cliente.getEmail());
            txtDireccion.setText(cliente.getDireccion());
            if (cliente.getFechaNacimiento() != null) {
                dpFechaNacimiento.setValue(cliente.getFechaNacimiento());
            }
            chkActivo.setSelected(Boolean.TRUE.equals(cliente.isFrecuente()));
            if ("Empresa".equals(cliente.getTipoCliente())) {
                txtRuc.setText(cliente.getDocumento());
                txtRazonSocial.setText(cliente.getRazonSocial());
            } else {
                txtDni.setText(cliente.getDocumento());
            }
        }
        actualizarVisibilidadCampos();
    }

    @FXML
    public void initialize() {
        comboTipoCliente.getItems().addAll("Natural", "Empresa");
        comboTipoCliente.setValue("Natural");
        // Mostrar/ocultar campos según el tipo seleccionado
        comboTipoCliente.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Empresa".equalsIgnoreCase(newVal)) {
                txtRuc.setDisable(false);
                txtRuc.setVisible(true);
                txtRazonSocial.setDisable(false);
                txtRazonSocial.setVisible(true);
                txtDni.setDisable(true);
                txtDni.setVisible(false);
            } else {
                txtDni.setDisable(false);
                txtDni.setVisible(true);
                txtRuc.setDisable(true);
                txtRuc.setVisible(false);
                txtRazonSocial.setDisable(true);
                txtRazonSocial.setVisible(false);
            }
        });
        // Inicializar visibilidad al abrir el formulario
        if ("Empresa".equalsIgnoreCase(comboTipoCliente.getValue())) {
            txtRuc.setDisable(false);
            txtRuc.setVisible(true);
            txtRazonSocial.setDisable(false);
            txtRazonSocial.setVisible(true);
            txtDni.setDisable(true);
            txtDni.setVisible(false);
        } else {
            txtDni.setDisable(false);
            txtDni.setVisible(true);
            txtRuc.setDisable(true);
            txtRuc.setVisible(false);
            txtRazonSocial.setDisable(true);
            txtRazonSocial.setVisible(false);
        }
        actualizarVisibilidadCampos();
        comboTipoCliente.valueProperty().addListener((obs, oldVal, newVal) -> actualizarVisibilidadCampos());
    }

    private void actualizarVisibilidadCampos() {
        String tipo = comboTipoCliente.getValue();
        boolean esEmpresa = "Empresa".equals(tipo);
        boolean esNatural = "Natural".equals(tipo);

        // Campos para cliente natural
        txtDni.setVisible(esNatural);
        txtNombres.setVisible(esNatural);
        txtNombres.setDisable(esEmpresa);
        txtApellidos.setVisible(esNatural);
        txtApellidos.setDisable(esEmpresa);
        dpFechaNacimiento.setVisible(esNatural);
        dpFechaNacimiento.setDisable(esEmpresa);

        // Campos para empresa
        txtRuc.setVisible(esEmpresa);
        txtRuc.setDisable(!esEmpresa);
        txtRazonSocial.setVisible(esEmpresa);
        txtRazonSocial.setDisable(!esEmpresa);

        // Campos comunes (siempre visibles)
        txtTelefono.setVisible(true);
        txtEmail.setVisible(true);
        txtDireccion.setVisible(true);
    }

    @FXML
    public void onGuardarCliente() {
        String tipo = comboTipoCliente.getValue();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        clearError();
        if ("Natural".equals(tipo)) {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty() || nombres.isEmpty()) {
                showError("DNI y Nombres son obligatorios.");
                mostrarAlertaDatosIncompletos();
                return;
            }
            if (!dni.matches("\\d{8}")) {
                showError("El DNI debe tener 8 dígitos.");
                mostrarAlertaDatosIncompletos();
                return;
            }
            if (existeClienteConDni(dni)) {
                showError("El DNI ya está registrado. Ingrese uno diferente.");
                mostrarAlertaDatosIncompletos();
                return;
            }
        } else if ("Empresa".equals(tipo)) {
            String ruc = txtRuc.getText().trim();
            String razonSocial = txtRazonSocial.getText().trim();
            if (ruc.isEmpty() || razonSocial.isEmpty()) {
                showError("RUC y Razón Social son obligatorios.");
                mostrarAlertaDatosIncompletos();
                return;
            }
            if (!ruc.matches("\\d{11}")) {
                showError("El RUC debe tener 11 dígitos.");
                mostrarAlertaDatosIncompletos();
                return;
            }
            if (existeClienteConRuc(ruc)) {
                showError("El RUC ya está registrado. Ingrese uno diferente.");
                mostrarAlertaDatosIncompletos();
                return;
            }
        }
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("El email no tiene un formato válido.");
            mostrarAlertaDatosIncompletos();
            return;
        }
        if (!telefono.isEmpty() && !telefono.matches("^\\d{6,15}$")) {
            showError("El teléfono debe tener entre 6 y 15 dígitos.");
            mostrarAlertaDatosIncompletos();
            return;
        }
        if (existeClienteConNombre(nombres)) {
            showError("El nombre ya está registrado. Ingrese uno diferente.");
            mostrarAlertaDatosIncompletos();
            return;
        }
        if (existeClienteConEmail(email)) {
            showError("El email ya está registrado. Ingrese uno diferente.");
            mostrarAlertaDatosIncompletos();
            return;
        }
        if (existeClienteConTelefono(telefono)) {
            showError("El teléfono ya está registrado. Ingrese uno diferente.");
            mostrarAlertaDatosIncompletos();
            return;
        }
        try {
            Cliente clienteGuardado = getClienteFromForm();
            logger.info(
                    "[GUARDAR] Intentando guardar cliente: tipo={}, documento={}, razon_social={}, nombres={}, apellidos={}, telefono={}, email={}, direccion={}, fecha_nacimiento={}, es_frecuente={}",
                    clienteGuardado.getTipoCliente(), clienteGuardado.getDocumento(), clienteGuardado.getRazonSocial(),
                    clienteGuardado.getNombres(), clienteGuardado.getApellidos(), clienteGuardado.getTelefono(),
                    clienteGuardado.getEmail(), clienteGuardado.getDireccion(), clienteGuardado.getFechaNacimiento(),
                    clienteGuardado.isFrecuente());
            if (!chkActivo.isSelected()) {
                mostrarAlertaClienteInactivo();
            }
            mostrarNotificacionGuardado();
            if (onClienteEditado != null) {
                onClienteEditado.accept(clienteGuardado);
            }
            ((Stage) comboTipoCliente.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error("[ERROR GUARDAR CLIENTE] {}", e.getMessage(), e);
            showError("Error al guardar el cliente: " + e.getMessage());
        }
    }

    // ALERTAS Y NOTIFICACIONES
    private void mostrarAlertaDatosIncompletos() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Datos incompletos");
        alert.setHeaderText("El cliente tiene datos incompletos o inválidos.");
        alert.setContentText("Por favor, revise los campos obligatorios y el formato de los datos.");
        alert.showAndWait();
    }

    private void mostrarAlertaClienteInactivo() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Cliente inactivo");
        alert.setHeaderText("El cliente ha sido registrado como inactivo.");
        alert.setContentText("Recuerde que los clientes inactivos no podrán realizar compras ni acumular puntos.");
        alert.showAndWait();
    }

    private void mostrarNotificacionGuardado() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Cliente guardado");
        alert.setHeaderText(null);
        alert.setContentText("El cliente se ha guardado correctamente.");
        alert.showAndWait();
    }

    // Validación de repetidos usando el servicio (debe inyectarse la lista de
    // clientes)
    private java.util.List<Cliente> clientesExistentes = new java.util.ArrayList<>();

    public void setClientesExistentes(java.util.List<Cliente> lista) {
        this.clientesExistentes = lista != null ? lista : new java.util.ArrayList<>();
    }

    private boolean existeClienteConDni(String dni) {
        if (editMode && cliente != null && dni.equals(cliente.getDocumento())
                && "Natural".equals(cliente.getTipoCliente()))
            return false;
        return clientesExistentes.stream().anyMatch(c -> "Natural".equals(c.getTipoCliente())
                && c.getDocumento() != null && c.getDocumento().equalsIgnoreCase(dni));
    }

    private boolean existeClienteConNombre(String nombre) {
        if (editMode && cliente != null) {
            if ("Empresa".equals(cliente.getTipoCliente())) {
                String razonSocial = txtRazonSocial.getText().trim();
                if (razonSocial.equalsIgnoreCase(cliente.getRazonSocial()))
                    return false;
                return clientesExistentes.stream().anyMatch(c -> "Empresa".equals(c.getTipoCliente())
                        && c.getRazonSocial() != null && c.getRazonSocial().equalsIgnoreCase(razonSocial));
            } else {
                if (nombre.equalsIgnoreCase(cliente.getNombres()))
                    return false;
                return clientesExistentes.stream().anyMatch(c -> "Natural".equals(c.getTipoCliente())
                        && c.getNombres() != null && c.getNombres().equalsIgnoreCase(nombre));
            }
        }
        if ("Empresa".equals(comboTipoCliente.getValue())) {
            String razonSocial = txtRazonSocial.getText().trim();
            return clientesExistentes.stream().anyMatch(c -> "Empresa".equals(c.getTipoCliente())
                    && c.getRazonSocial() != null && c.getRazonSocial().equalsIgnoreCase(razonSocial));
        } else {
            return clientesExistentes.stream().anyMatch(c -> "Natural".equals(c.getTipoCliente())
                    && c.getNombres() != null && c.getNombres().equalsIgnoreCase(nombre));
        }
    }

    private boolean existeClienteConEmail(String email) {
        if (email.isEmpty())
            return false;
        if (editMode && cliente != null && email.equals(cliente.getEmail()))
            return false;
        return clientesExistentes.stream().anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email));
    }

    private boolean existeClienteConTelefono(String telefono) {
        if (telefono.isEmpty())
            return false;
        if (editMode && cliente != null && telefono.equals(cliente.getTelefono()))
            return false;
        return clientesExistentes.stream()
                .anyMatch(c -> c.getTelefono() != null && c.getTelefono().equalsIgnoreCase(telefono));
    }

    private boolean existeClienteConRuc(String ruc) {
        if (editMode && cliente != null && ruc.equals(cliente.getDocumento())
                && "Empresa".equals(cliente.getTipoCliente()))
            return false;
        return clientesExistentes.stream().anyMatch(c -> "Empresa".equals(c.getTipoCliente())
                && c.getDocumento() != null && c.getDocumento().equalsIgnoreCase(ruc));
    }

    public Cliente getClienteFromForm() {
        if (cliente == null)
            cliente = new Cliente();
        String tipo = comboTipoCliente.getValue();
        cliente.setTipoCliente(tipo);
        if ("Empresa".equals(tipo)) {
            cliente.setDocumento(txtRuc.getText());
            cliente.setRazonSocial(txtRazonSocial.getText());
        } else {
            cliente.setDocumento(txtDni.getText());
            cliente.setRazonSocial("");
        }
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setDireccion(txtDireccion.getText());
        if (dpFechaNacimiento.getValue() != null) {
            cliente.setFechaNacimiento(dpFechaNacimiento.getValue());
        }
        cliente.setEsFrecuente(chkActivo.isSelected());
        logger.info(
                "[FORM] Datos del cliente a guardar: tipo_cliente={}, documento={}, razon_social={}, nombres={}, apellidos={}, telefono={}, email={}, direccion={}, fecha_nacimiento={}, es_frecuente={}",
                cliente.getTipoCliente(), cliente.getDocumento(), cliente.getRazonSocial(), cliente.getNombres(),
                cliente.getApellidos(), cliente.getTelefono(), cliente.getEmail(), cliente.getDireccion(),
                cliente.getFechaNacimiento(), cliente.isFrecuente());
        return cliente;
    }

    @FXML
    public void onLimpiarCampos() {
        txtDni.clear();
        txtRuc.clear();
        txtRazonSocial.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        dpFechaNacimiento.setValue(null);
        clearError();
        comboTipoCliente.setValue("NATURAL");
        actualizarVisibilidadCampos();
    }

    public void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    public void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
    }

    private static final Logger logger = LoggerFactory.getLogger(ClienteFormController.class);
}
