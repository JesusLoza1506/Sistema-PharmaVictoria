package com.farmaciavictoria.proyectopharmavictoria.controller.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

public class ClienteFormController {
    // Callback para notificar edición instantánea
    private Runnable onClienteEditado;

    public void setOnClienteEditado(Runnable callback) {
        this.onClienteEditado = callback;
    }
    @FXML
    public void onCancelar() {
        // Cierra la ventana del formulario
        Stage stage = (Stage) txtDni.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onGuardarCliente() {
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        // Validaciones obligatorias
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
        // Validación de repetidos
        if (existeClienteConDni(dni)) {
            showError("El DNI ya está registrado. Ingrese uno diferente.");
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
        clearError();
        // Llenar el objeto cliente
        if (cliente == null) cliente = new Cliente();
        cliente.setDni(dni);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setDireccion(txtDireccion.getText());
        if (dpFechaNacimiento.getValue() != null) {
            cliente.setFechaNacimiento(dpFechaNacimiento.getValue());
        }
        cliente.setEsFrecuente(chkActivo.isSelected());
        // Alerta si el cliente está inactivo
        if (!chkActivo.isSelected()) {
            mostrarAlertaClienteInactivo();
        }
        // Notificación automática al guardar
        mostrarNotificacionGuardado();
        // Callback para refrescar tabla instantáneamente
        if (onClienteEditado != null) {
            onClienteEditado.run();
        }
        // Cerrar ventana
        ((Stage) txtDni.getScene().getWindow()).close();
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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Cliente inactivo");
        alert.setHeaderText("El cliente ha sido registrado como inactivo.");
        alert.setContentText("Recuerde que los clientes inactivos no podrán realizar compras ni acumular puntos.");
        alert.showAndWait();
    }

    private void mostrarNotificacionGuardado() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Cliente guardado");
        alert.setHeaderText(null);
        alert.setContentText("El cliente se ha guardado correctamente.");
        alert.showAndWait();
    }

    // Validación de repetidos usando el servicio (debe inyectarse la lista de clientes)
    private java.util.List<Cliente> clientesExistentes = new java.util.ArrayList<>();

    public void setClientesExistentes(java.util.List<Cliente> lista) {
        this.clientesExistentes = lista != null ? lista : new java.util.ArrayList<>();
    }

    private boolean existeClienteConDni(String dni) {
        if (editMode && cliente != null && dni.equals(cliente.getDni())) return false;
        return clientesExistentes.stream().anyMatch(c -> c.getDni() != null && c.getDni().equalsIgnoreCase(dni));
    }
    private boolean existeClienteConNombre(String nombre) {
        if (editMode && cliente != null && nombre.equals(cliente.getNombres())) return false;
        return clientesExistentes.stream().anyMatch(c -> c.getNombres() != null && c.getNombres().equalsIgnoreCase(nombre));
    }
    private boolean existeClienteConEmail(String email) {
        if (email.isEmpty()) return false;
        if (editMode && cliente != null && email.equals(cliente.getEmail())) return false;
        return clientesExistentes.stream().anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email));
    }
    private boolean existeClienteConTelefono(String telefono) {
        if (telefono.isEmpty()) return false;
        if (editMode && cliente != null && telefono.equals(cliente.getTelefono())) return false;
        return clientesExistentes.stream().anyMatch(c -> c.getTelefono() != null && c.getTelefono().equalsIgnoreCase(telefono));
    }

    @FXML
    public void onLimpiarCampos() {
        txtDni.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        dpFechaNacimiento.setValue(null);
        clearError();
    }
    private static final Logger logger = LoggerFactory.getLogger(ClienteFormController.class);

    @FXML private TextField txtDni;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
        @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblError;

    private Cliente cliente;
    private boolean editMode = false;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            editMode = true;
            txtDni.setText(cliente.getDni());
            txtNombres.setText(cliente.getNombres());
            txtApellidos.setText(cliente.getApellidos());
            txtTelefono.setText(cliente.getTelefono());
            txtEmail.setText(cliente.getEmail());
            txtDireccion.setText(cliente.getDireccion());
            if (cliente.getFechaNacimiento() != null) {
                dpFechaNacimiento.setValue(cliente.getFechaNacimiento());
            }
            chkActivo.setSelected(Boolean.TRUE.equals(cliente.getEsFrecuente()));
        }
    }

    public Cliente getClienteFromForm() {
        if (cliente == null) cliente = new Cliente();
        cliente.setDni(txtDni.getText());
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setDireccion(txtDireccion.getText());
        if (dpFechaNacimiento.getValue() != null) {
            cliente.setFechaNacimiento(dpFechaNacimiento.getValue());
        }
    cliente.setEsFrecuente(chkActivo.isSelected());
        return cliente;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    public void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
    }
}
