package com.farmaciavictoria.proyectopharmavictoria.controller.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClienteDetallesController {
        @FXML
        private javafx.scene.layout.GridPane empresaContactoGrid;
        @FXML
        private javafx.scene.layout.GridPane empresaAdicionalGrid;
        @FXML
        private javafx.scene.layout.GridPane naturalContactoGrid;
        @FXML
        private javafx.scene.layout.GridPane naturalAdicionalGrid;
        @FXML
        private javafx.scene.control.TableView<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio> historialTable;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio, String> colFecha;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio, String> colUsuario;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio, String> colCampo;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio, String> colAnterior;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio, String> colNuevo;

        // Tabla de historial de puntos
        @FXML
        private javafx.scene.control.TableView<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos> puntosTable;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos, String> colPuntosFecha;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos, String> colPuntosTipo;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos, Integer> colPuntosValor;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos, String> colPuntosDescripcion;

        // Tabla de historial de compras (ahora con modelo Venta)
        @FXML
        private javafx.scene.control.TableView<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta> comprasTable;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraFecha;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, Integer> colCompraVentaId;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraBoleta;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraTotal;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraEstado;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraPago;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraSucursal;
        @FXML
        private javafx.scene.control.TableColumn<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta, String> colCompraObs;

        private final com.farmaciavictoria.proyectopharmavictoria.service.ClienteService clienteService = new com.farmaciavictoria.proyectopharmavictoria.service.ClienteService(
                        new com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository());
        // Eliminado comprasService porque no se usa
        @FXML
        private Label subtituloLabel;
        @FXML
        private Label dniLabel;
        @FXML
        private Label nombresLabel;
        @FXML
        private Label apellidosLabel;
        @FXML
        private Label estadoLabel;
        @FXML
        private Label telefonoLabel;
        @FXML
        private Label emailLabel;
        @FXML
        private Label direccionLabel;
        @FXML
        private Label fechaNacimientoLabel;
        @FXML
        private Label puntosTotalesLabel;
        @FXML
        private Label frecuenteLabel;
        @FXML
        private Label fechaRegistroLabel;
        @FXML
        private Button btnCerrar;
        @FXML
        private javafx.scene.layout.GridPane naturalGrid;
        @FXML
        private javafx.scene.layout.GridPane empresaGrid;
        @FXML
        private Label rucLabel;
        @FXML
        private Label razonSocialLabel;
        @FXML
        private Label estadoEmpresaLabel;
        @FXML
        private Label telefonoEmpresaLabel;
        @FXML
        private Label emailEmpresaLabel;
        @FXML
        private Label direccionEmpresaLabel;
        @FXML
        private Label puntosEmpresaLabel;
        @FXML
        private Label frecuenteEmpresaLabel;
        @FXML
        private Label fechaRegistroEmpresaLabel;

        public void setCliente(Cliente cliente) {
                String tipo = cliente.getTipoCliente();
                if ("Empresa".equalsIgnoreCase(tipo)) {
                        // Ocultar bloques de Natural
                        naturalGrid.setVisible(false);
                        naturalGrid.setManaged(false);
                        naturalContactoGrid.setVisible(false);
                        naturalContactoGrid.setManaged(false);
                        naturalAdicionalGrid.setVisible(false);
                        naturalAdicionalGrid.setManaged(false);
                        // Mostrar bloques de Empresa
                        empresaGrid.setVisible(true);
                        empresaGrid.setManaged(true);
                        empresaContactoGrid.setVisible(true);
                        empresaContactoGrid.setManaged(true);
                        empresaAdicionalGrid.setVisible(true);
                        empresaAdicionalGrid.setManaged(true);
                        // Asignar datos de Empresa
                        rucLabel.setText(cliente.getDocumento());
                        razonSocialLabel.setText(cliente.getRazonSocial());
                        estadoEmpresaLabel.setText("ACTIVO");
                        estadoEmpresaLabel.getStyleClass().clear();
                        estadoEmpresaLabel.getStyleClass().add("estado-activo");
                        telefonoEmpresaLabel.setText(cliente.getTelefono());
                        emailEmpresaLabel.setText(cliente.getEmail());
                        direccionEmpresaLabel.setText(cliente.getDireccion());
                        puntosEmpresaLabel.setText(
                                        cliente.getPuntosTotales() != null ? String.valueOf(cliente.getPuntosTotales())
                                                        : "0");
                        frecuenteEmpresaLabel.setText(cliente.isFrecuente() ? "Sí" : "No");
                        fechaRegistroEmpresaLabel.setText(
                                        cliente.getCreatedAt() != null ? cliente.getCreatedAt().toString() : "-");
                } else {
                        // Ocultar bloques de Empresa
                        empresaGrid.setVisible(false);
                        empresaGrid.setManaged(false);
                        empresaContactoGrid.setVisible(false);
                        empresaContactoGrid.setManaged(false);
                        empresaAdicionalGrid.setVisible(false);
                        empresaAdicionalGrid.setManaged(false);
                        // Mostrar bloques de Natural
                        naturalGrid.setVisible(true);
                        naturalGrid.setManaged(true);
                        naturalContactoGrid.setVisible(true);
                        naturalContactoGrid.setManaged(true);
                        naturalAdicionalGrid.setVisible(true);
                        naturalAdicionalGrid.setManaged(true);
                        // Asignar datos de Natural
                        dniLabel.setText(cliente.getDocumento());
                        nombresLabel.setText(cliente.getNombres());
                        apellidosLabel.setText(cliente.getApellidos());
                        estadoLabel.setText("ACTIVO");
                        estadoLabel.getStyleClass().clear();
                        estadoLabel.getStyleClass().add("estado-activo");
                        telefonoLabel.setText(cliente.getTelefono());
                        emailLabel.setText(cliente.getEmail());
                        direccionLabel.setText(cliente.getDireccion());
                        fechaNacimientoLabel.setText(
                                        cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString()
                                                        : "-");
                        puntosTotalesLabel.setText(
                                        cliente.getPuntosTotales() != null ? String.valueOf(cliente.getPuntosTotales())
                                                        : "0");
                        frecuenteLabel.setText(cliente.isFrecuente() ? "Sí" : "No");
                        fechaRegistroLabel.setText(
                                        cliente.getCreatedAt() != null ? cliente.getCreatedAt().toString() : "-");
                }
                // Cargar historial de cambios
                cargarHistorial(cliente.getId());
                // Cargar historial de puntos
                cargarHistorialPuntos(cliente.getId());
                // Cargar historial de compras
                cargarHistorialCompras(cliente.getId());
        }

        private void cargarHistorial(Integer clienteId) {
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio> historial = clienteService
                                .obtenerHistorialDeCambios(clienteId);
                javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio> items = javafx.collections.FXCollections
                                .observableArrayList();
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter
                                .ofPattern("dd/MM/yyyy HH:mm");
                com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository usuarioRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository();
                for (com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio cambio : historial) {
                        // Formatear fecha y campos igual que proveedores
                        cambio.setFechaFormateada(cambio.getFecha() != null ? cambio.getFecha().format(dtf) : "");
                        // Buscar nombre completo del usuario
                        String nombreCompleto = null;
                        if (cambio.getUsuario() != null && !cambio.getUsuario().isEmpty()) {
                                com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = usuarioRepo
                                                .findByUsername(cambio.getUsuario()).orElse(null);
                                if (usuario != null && usuario.getNombres() != null && usuario.getApellidos() != null) {
                                        nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
                                }
                        }
                        cambio.setUsuario(nombreCompleto != null ? nombreCompleto : cambio.getUsuario());
                        items.add(cambio);
                }
                colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getFechaFormateada() != null ? data.getValue().getFechaFormateada()
                                                : ""));
                colUsuario.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsuario()));
                colCampo.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getCampoModificado()));
                colAnterior.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getValorAnterior()));
                colNuevo.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getValorNuevo()));
                historialTable.setItems(items);
        }

        private void cargarHistorialPuntos(Integer clienteId) {
                com.farmaciavictoria.proyectopharmavictoria.service.TransaccionPuntosService puntosService = new com.farmaciavictoria.proyectopharmavictoria.service.TransaccionPuntosService();
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos> movimientos = puntosService
                                .obtenerHistorialPuntosPorCliente(clienteId);
                javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos> items = javafx.collections.FXCollections
                                .observableArrayList(movimientos);
                colPuntosFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getFecha() != null ? data.getValue().getFecha().toString() : ""));
                colPuntosTipo
                                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getTipo()));
                colPuntosValor.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPuntos())
                                                .asObject());
                colPuntosDescripcion.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getDescripcion()));
                puntosTable.setItems(items);
        }

        private void cargarHistorialCompras(Integer clienteId) {
                // Usar VentaServiceImpl para obtener ventas reales del cliente
                com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl ventaService = new com.farmaciavictoria.proyectopharmavictoria.service.Ventas.VentaServiceImpl(
                                new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl(),
                                null, null,
                                null, null);
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta> ventas = ventaService
                                .listarVentasPorCliente(clienteId);
                javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta> items = javafx.collections.FXCollections
                                .observableArrayList(ventas);
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter
                                .ofPattern("dd/MM/yyyy HH:mm");
                colCompraFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getFechaVenta() != null ? data.getValue().getFechaVenta().format(dtf)
                                                : ""));
                colCompraVentaId.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId())
                                                .asObject());
                colCompraBoleta.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getNumeroBoleta()));
                colCompraTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getTotal() != null ? data.getValue().getTotal().toPlainString() : "0"));
                colCompraEstado.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
                colCompraPago.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoPago()));
                colCompraSucursal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                                data.getValue().getSucursal() != null ? data.getValue().getSucursal().getNombre()
                                                : ""));
                colCompraObs.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().getObservaciones()));
                comprasTable.setItems(items);
        }

        @FXML
        private void cerrarVentana() {
                Stage stage = (Stage) btnCerrar.getScene().getWindow();
                stage.close();
        }
}
