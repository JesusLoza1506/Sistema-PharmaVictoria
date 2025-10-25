package com.farmaciavictoria.proyectopharmavictoria.controller.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioAcceso;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioAuditoria;

public class UsuarioDetallesController {
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblNombres;
    @FXML
    private Label lblApellidos;
    @FXML
    private Label lblDni;
    @FXML
    private Label lblTelefono;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblRol;
    // ...existing code...
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblUltimoAcceso;
    @FXML
    private TableView<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso> accesosTable;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso, String> colAccesoFecha;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso, String> colAccesoIp;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso, String> colAccesoDispositivo;
    @FXML
    private TableView<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio> auditoriaTable;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio, String> colAuditoriaFecha;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio, String> colAuditoriaAccion;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio, String> colAuditoriaUsuario;
    @FXML
    private TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio, String> colAuditoriaDetalle;

    private Usuario usuarioActual;

    public void cargarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        // Validación y visualización de datos
        lblUsername.setText((usuario != null && usuario.getUsername() != null && !usuario.getUsername().isEmpty())
                ? usuario.getUsername()
                : "-");
        lblNombres.setText((usuario != null && usuario.getNombres() != null && !usuario.getNombres().isEmpty())
                ? usuario.getNombres()
                : "-");
        lblApellidos.setText((usuario != null && usuario.getApellidos() != null && !usuario.getApellidos().isEmpty())
                ? usuario.getApellidos()
                : "-");
        lblDni.setText(
                (usuario != null && usuario.getDni() != null && !usuario.getDni().isEmpty()) ? usuario.getDni() : "-");
        lblTelefono.setText((usuario != null && usuario.getTelefono() != null && !usuario.getTelefono().isEmpty())
                ? usuario.getTelefono()
                : "-");
        lblEmail.setText(
                (usuario != null && usuario.getEmail() != null && !usuario.getEmail().isEmpty()) ? usuario.getEmail()
                        : "-");
        lblRol.setText((usuario != null && usuario.getRol() != null) ? usuario.getRol().name() : "-");
        // ...existing code...
        lblUltimoAcceso.setText(
                (usuario != null && usuario.getUltimoAcceso() != null) ? usuario.getUltimoAcceso().toString() : "-");

        // Estado visual robusto
        lblEstado.setText((usuario != null && usuario.isActivo()) ? "Activo" : "Inactivo");
        lblEstado.getStyleClass().removeAll("estado-activo", "estado-inactivo");
        if (usuario != null && usuario.isActivo()) {
            lblEstado.getStyleClass().add("estado-activo");
        } else {
            lblEstado.getStyleClass().add("estado-inactivo");
        }

        // Poblar tabla de accesos
        accesosTable.getItems().clear();
        if (usuario != null && usuario.getId() != null) {
            // Recargar historial desde el servicio para asegurar datos actualizados
            com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService usuarioService = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService
                    .getInstance();
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso> accesos = usuarioService
                    .obtenerHistorialAccesos(usuario.getId());
            if (accesos != null && !accesos.isEmpty()) {
                accesosTable.getItems().addAll(accesos);
            }
            java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio> auditoria = usuarioService
                    .obtenerHistorialCambios(usuario.getId());
            auditoriaTable.getItems().clear();
            if (auditoria != null && !auditoria.isEmpty()) {
                auditoriaTable.getItems().addAll(auditoria);
            }
        }
    }

    @FXML
    public void initialize() {
        // boton cerrar removido del FXML: no es necesario manejarlo aquí

        // Configurar columnas de accesos
        colAccesoFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFechaAcceso() != null ? data.getValue().getFechaAcceso().toString() : "-"));
        colAccesoIp.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getIpAddress() != null ? data.getValue().getIpAddress() : "-"));
        colAccesoDispositivo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getUserAgent() != null ? data.getValue().getUserAgent() : "-"));

        // Configurar columnas de auditoría
        colAuditoriaFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFecha() != null ? data.getValue().getFecha().toString() : "-"));
        colAuditoriaAccion.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCampo_modificado() != null ? data.getValue().getCampo_modificado() : "-"));
        colAuditoriaUsuario.setCellValueFactory(data -> {
            Integer id = data.getValue().getModificado_por();
            if (id == null)
                return new javafx.beans.property.SimpleStringProperty("-");
            com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository usuarioRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository();
            java.util.Optional<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> usuarioOpt = usuarioRepo
                    .findById(id.longValue());
            if (usuarioOpt.isPresent()) {
                com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario u = usuarioOpt.get();
                String nombre = (u.getNombres() != null ? u.getNombres() : "") + " "
                        + (u.getApellidos() != null ? u.getApellidos() : "");
                return new javafx.beans.property.SimpleStringProperty(
                        nombre.trim().isEmpty() ? u.getUsername() : nombre.trim());
            } else {
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(id));
            }
        });
        colAuditoriaDetalle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                (data.getValue().getValor_anterior() != null ? "Anterior: " + data.getValue().getValor_anterior() + "\n"
                        : "") +
                        (data.getValue().getValor_nuevo() != null ? "Nuevo: " + data.getValue().getValor_nuevo()
                                : "")));

        // Forzar estilo de los encabezados en tiempo de ejecución (override de hojas
        // CSS cargadas después)
        fixTableHeaders(accesosTable);
        fixTableHeaders(auditoriaTable);

        // Reemplazar los headers de las columnas por Labels con estilo inline (solución
        // definitiva)
        javafx.application.Platform.runLater(() -> {
            try {
                replaceColumnHeadersWithLabels(accesosTable);
                replaceColumnHeadersWithLabels(auditoriaTable);
            } catch (Exception ex) {
                System.err.println("Warning: no se pudieron reemplazar headers por labels: " + ex.getMessage());
            }
        });
    }

    // Método auxiliar que busca los nodos de header en una TableView y aplica
    // estilos inline
    private void fixTableHeaders(javafx.scene.control.TableView<?> table) {
        if (table == null)
            return;
        // Ejecutar más tarde para asegurar que la skin y los nodos estén creados
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.scene.Node header = table.lookup(".column-header");
                if (header != null) {
                    // buscar todas las labels dentro del header
                    java.util.List<javafx.scene.Node> labels = header.lookupAll(".label").stream().toList();
                    for (javafx.scene.Node node : labels) {
                        node.setStyle("-fx-text-fill: #2C3E50; -fx-font-weight: bold;");
                    }
                }
                // también intentar buscar columna header backgrounds
                javafx.scene.Node ch = table.lookup(".column-header-background");
                if (ch != null) {
                    ch.setStyle("-fx-background-color: white;");
                }
            } catch (Exception ex) {
                // No bloquear UI si algo falla; el CSS seguirá intentando aplicar
                System.err.println("Warning: no se pudieron ajustar headers runtime: " + ex.getMessage());
            }
        });
    }

    // Reemplaza el header text de cada TableColumn por un Label con estilo inline
    private void replaceColumnHeadersWithLabels(javafx.scene.control.TableView<?> table) {
        if (table == null)
            return;
        for (javafx.scene.control.TableColumn<?, ?> col : table.getColumns()) {
            try {
                String text = col.getText();
                if (text == null)
                    text = "";
                javafx.scene.control.Label lbl = new javafx.scene.control.Label(text);
                lbl.setStyle("-fx-text-fill: #2C3E50; -fx-font-weight: bold; -fx-font-size: 13px;");
                // mantener alignment similar al original
                lbl.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                col.setGraphic(lbl);
                col.setText("");
            } catch (Exception ex) {
                // ignora columnas que no se puedan modificar
            }
        }
    }
}
