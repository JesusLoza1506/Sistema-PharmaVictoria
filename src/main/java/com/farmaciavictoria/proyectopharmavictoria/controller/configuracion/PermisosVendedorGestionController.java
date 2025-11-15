package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso;
import java.time.LocalDateTime;

public class PermisosVendedorGestionController {
        // Checkboxes de Clientes
        @FXML
        private javafx.scene.control.CheckBox chkVerCliente;
        @FXML
        private javafx.scene.control.CheckBox chkNuevoCliente;
        @FXML
        private javafx.scene.control.CheckBox chkExportarCliente;
        @FXML
        private javafx.scene.control.CheckBox chkEditarCliente;
        @FXML
        private javafx.scene.control.CheckBox chkEliminarCliente;
        @FXML
        private javafx.scene.control.CheckBox chkDashboardTopClientes;
        @FXML
        private javafx.scene.control.CheckBox chkGraficaEdad;
        // Checkboxes de Proveedores
        @FXML
        private javafx.scene.control.CheckBox chkVerProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkNuevoProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkExportarProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkContactarProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkEditarProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkEliminarProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkActivarInactivarProveedor;
        @FXML
        private javafx.scene.control.CheckBox chkDashboardTopProveedores;
        @FXML
        private javafx.scene.control.CheckBox chkDashboardDistribucionEstado;
        @FXML
        private VBox root;
        @FXML
        private TitledPane inventarioPane;
        @FXML
        private TitledPane clientesPane;
        @FXML
        private TitledPane proveedoresPane;
        @FXML
        private Label lblTitulo;

        // Checkboxes de Inventario
        @FXML
        private javafx.scene.control.CheckBox chkVerInventario;
        @FXML
        private javafx.scene.control.CheckBox chkAgregarProducto;
        @FXML
        private javafx.scene.control.CheckBox chkEditarProducto;
        @FXML
        private javafx.scene.control.CheckBox chkEliminarProducto;
        @FXML
        private javafx.scene.control.CheckBox chkActivarInactivarProducto;
        @FXML
        private javafx.scene.control.CheckBox chkEdicionMasiva;
        @FXML
        private javafx.scene.control.CheckBox chkExportarInventario;

        private final UsuarioService usuarioService = UsuarioService.getInstance();
        // Ya no se usa vendedorId específico

        @FXML
        public void initialize() {
                inicializarPermisosInventario();
                agregarListenersPermisosInventario();
                inicializarPermisosProveedores();
                agregarListenersPermisosProveedores();
                inicializarPermisosClientes();
                agregarListenersPermisosClientes();
        }

        private void inicializarPermisosClientes() {
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                                .obtenerUsuarios()
                                .stream()
                                .filter(u -> u.getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                                .collect(java.util.stream.Collectors.toList());
                if (!vendedores.isEmpty()) {
                        Long primerVendedorId = vendedores.get(0).getId();
                        java.util.List<UsuarioPermiso> permisos = usuarioService.obtenerPermisos(primerVendedorId);
                        chkVerCliente.setSelected(true);
                        chkVerCliente.setDisable(true); // Siempre activo y no editable
                        chkNuevoCliente.setSelected(buscarPermiso(permisos, "clientes.nuevo"));
                        chkExportarCliente.setSelected(buscarPermiso(permisos, "clientes.exportar"));
                        chkEditarCliente.setSelected(buscarPermiso(permisos, "clientes.editar"));
                        chkEliminarCliente.setSelected(buscarPermiso(permisos, "clientes.eliminar"));
                        chkDashboardTopClientes.setSelected(buscarPermiso(permisos, "clientes.dashboard_top"));
                        chkGraficaEdad.setSelected(buscarPermiso(permisos, "clientes.grafica_edad"));
                }
        }

        private void agregarListenersPermisosClientes() {
                // "Ver cliente" siempre activo, no editable
                chkNuevoCliente.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.nuevo", newVal));
                chkExportarCliente.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.exportar", newVal));
                chkEditarCliente.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.editar", newVal));
                chkEliminarCliente.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.eliminar", newVal));
                chkDashboardTopClientes.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.dashboard_top", newVal));
                chkGraficaEdad.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("clientes.grafica_edad", newVal));
        }

        private void inicializarPermisosProveedores() {
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                                .obtenerUsuarios()
                                .stream()
                                .filter(u -> u
                                                .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                                .collect(java.util.stream.Collectors.toList());
                if (!vendedores.isEmpty()) {
                        Long primerVendedorId = vendedores.get(0).getId();
                        java.util.List<UsuarioPermiso> permisos = usuarioService.obtenerPermisos(primerVendedorId);
                        chkVerProveedor.setSelected(true);
                        chkVerProveedor.setDisable(true); // Siempre activo y no editable
                        chkNuevoProveedor.setSelected(buscarPermiso(permisos, "proveedores.nuevo"));
                        chkExportarProveedor.setSelected(buscarPermiso(permisos, "proveedores.exportar"));
                        chkContactarProveedor.setSelected(buscarPermiso(permisos, "proveedores.contactar"));
                        chkEditarProveedor.setSelected(buscarPermiso(permisos, "proveedores.editar"));
                        chkEliminarProveedor.setSelected(buscarPermiso(permisos, "proveedores.eliminar"));
                        chkActivarInactivarProveedor
                                        .setSelected(buscarPermiso(permisos, "proveedores.activar_inactivar"));
                        chkDashboardTopProveedores.setSelected(buscarPermiso(permisos, "proveedores.dashboard_top"));
                        chkDashboardDistribucionEstado
                                        .setSelected(buscarPermiso(permisos, "proveedores.dashboard_estado"));
                }
        }

        private void agregarListenersPermisosProveedores() {
                // No agregar listener, siempre activo y no editable
                chkNuevoProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.nuevo", newVal));
                chkExportarProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.exportar", newVal));
                chkContactarProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.contactar", newVal));
                chkEditarProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.editar", newVal));
                chkEliminarProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.eliminar", newVal));
                chkActivarInactivarProveedor.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.activar_inactivar",
                                                newVal));
                chkDashboardTopProveedores.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.dashboard_top",
                                                newVal));
                chkDashboardDistribucionEstado.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("proveedores.dashboard_estado",
                                                newVal));
        }

        private void inicializarPermisosInventario() {
                // Cargar permisos globales para vendedores
                // Tomar el primer usuario con rol VENDEDOR para mostrar el estado
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                                .obtenerUsuarios()
                                .stream()
                                .filter(u -> u
                                                .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                                .collect(java.util.stream.Collectors.toList());
                if (!vendedores.isEmpty()) {
                        Long primerVendedorId = vendedores.get(0).getId();
                        java.util.List<UsuarioPermiso> permisos = usuarioService.obtenerPermisos(primerVendedorId);
                        chkVerInventario.setSelected(true); // Siempre activo y no editable
                        chkAgregarProducto.setSelected(buscarPermiso(permisos, "inventario.agregar"));
                        chkEditarProducto.setSelected(buscarPermiso(permisos, "inventario.editar"));
                        chkEliminarProducto.setSelected(buscarPermiso(permisos, "inventario.eliminar"));
                        chkActivarInactivarProducto
                                        .setSelected(buscarPermiso(permisos, "inventario.activar_inactivar"));
                        chkEdicionMasiva.setSelected(buscarPermiso(permisos, "inventario.edicion_masiva"));
                        chkExportarInventario.setSelected(buscarPermiso(permisos, "inventario.exportar"));
                }
        }

        private boolean buscarPermiso(java.util.List<UsuarioPermiso> permisos, String clave) {
                return permisos.stream().anyMatch(p -> clave.equals(p.getPermiso()) && p.isValor());
        }

        private void agregarListenersPermisosInventario() {
                // "Ver inventario" siempre activo, no editable
                chkAgregarProducto.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.agregar", newVal));
                chkEditarProducto.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.editar", newVal));
                chkEliminarProducto.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.eliminar", newVal));
                chkActivarInactivarProducto.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.activar_inactivar",
                                                newVal));
                chkEdicionMasiva.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.edicion_masiva",
                                                newVal));
                chkExportarInventario.selectedProperty()
                                .addListener((obs, oldVal, newVal) -> guardarPermiso("inventario.exportar", newVal));
        }

        private void guardarPermiso(String clave, boolean valor) {
                // Guardar el permiso para todos los usuarios con rol VENDEDOR
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                                .obtenerUsuarios()
                                .stream()
                                .filter(u -> u
                                                .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                                .collect(java.util.stream.Collectors.toList());
                for (com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario vendedor : vendedores) {
                        UsuarioPermiso permiso = new UsuarioPermiso();
                        permiso.setUsuarioId(vendedor.getId());
                        permiso.setPermiso(clave);
                        permiso.setValor(valor);
                        permiso.setFechaAsignacion(LocalDateTime.now());
                        usuarioService.getPermisoRepository().save(permiso);
                }
        }
}
