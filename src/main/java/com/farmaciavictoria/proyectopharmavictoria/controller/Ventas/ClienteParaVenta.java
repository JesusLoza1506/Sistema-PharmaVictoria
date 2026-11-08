package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

final class ClienteParaVenta {
    private ClienteParaVenta() {
    }

    static Cliente resolver(Object valorCombo, String tipoComprobante, ComboBox<Cliente> comboClientes,
            Label lblTotal, Consumer<String> mostrarMensaje) {
        // --- INTEGRACIÓN BOLETA/FACTURA RÁPIDA ---
        if ((valorCombo == null || valorCombo.toString().trim().isEmpty()) &&
                ("BOLETA".equalsIgnoreCase(tipoComprobante) || "FACTURA".equalsIgnoreCase(tipoComprobante))) {
            try {
                if ("BOLETA".equalsIgnoreCase(tipoComprobante)) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            ClienteParaVenta.class.getResource("/fxml/Ventas/BoletaRapidaDialog.fxml"));
                    DialogPane dialogPane = loader.load();
                    BoletaRapidaDialogController dialogController = loader.getController();
                    double montoTotal = 0.0;
                    try {
                        String txtTotal = lblTotal.getText().replace("Total: S/", "").replace(",", ".").trim();
                        montoTotal = Double.parseDouble(txtTotal);
                    } catch (Exception e) {
                        montoTotal = 0.0;
                    }
                    Dialog<Map<String, String>> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Datos rápidos para Boleta");
                    dialog.setHeaderText("Completa los datos mínimos para la boleta electrónica");
                    ButtonType okType = null;
                    for (ButtonType bt : dialogPane.getButtonTypes()) {
                        if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            okType = bt;
                            break;
                        }
                    }
                    final double finalMontoTotal = montoTotal;
                    dialog.setResultConverter(bt -> {
                        if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            if (!dialogController.validar(finalMontoTotal)) {
                                return null;
                            }
                            Map<String, String> datos = new HashMap<>();
                            datos.put("nombre", dialogController.getNombre());
                            datos.put("dni", dialogController.getDni());
                            return datos;
                        }
                        return null;
                    });
                    if (okType != null) {
                        Button okBtn = (Button) dialogPane.lookupButton(okType);
                        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                            if (!dialogController.validar(finalMontoTotal)) {
                                event.consume();
                            }
                        });
                    }
                    Optional<Map<String, String>> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String nombre = result.get().getOrDefault("nombre", "Cliente Genérico");
                        String dni = result.get().getOrDefault("dni", "");
                        Cliente clienteTemp = new Cliente();
                        clienteTemp.setNombreCompleto(nombre);
                        clienteTemp.setDni(dni);
                        clienteTemp.setTipoCliente("NATURAL");
                        clienteTemp.setDireccion("-");
                        clienteTemp.setEmail("");
                        clienteTemp.setId(null);
                        clienteTemp.setEsRapido(true); // <--- MARCAR COMO CLIENTE RÁPIDO
                        ClienteRepository clienteRepo = new ClienteRepository();
                        boolean guardado = clienteRepo.save(clienteTemp);
                        if (guardado && clienteTemp.getId() != null && clienteTemp.getId() > 0) {
                            return clienteTemp;
                        } else {
                            mostrarMensaje.accept("No se pudo registrar el cliente rápido. Operación cancelada.");
                            return null;
                        }
                    } else {
                        mostrarMensaje.accept("Operación cancelada por el usuario.");
                        return null;
                    }
                } else if ("FACTURA".equalsIgnoreCase(tipoComprobante)) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            ClienteParaVenta.class.getResource("/fxml/Ventas/FacturaRapidaDialog.fxml"));
                    DialogPane dialogPane = loader.load();
                    FacturaRapidaDialogController dialogController = loader
                            .getController();
                    Dialog<Map<String, String>> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Datos rápidos para Factura");
                    dialog.setHeaderText("Completa los datos mínimos para la factura electrónica");
                    ButtonType okType = null;
                    for (ButtonType bt : dialogPane.getButtonTypes()) {
                        if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            okType = bt;
                            break;
                        }
                    }
                    dialog.setResultConverter(bt -> {
                        if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            if (!dialogController.validar()) {
                                return null;
                            }
                            Map<String, String> datos = new HashMap<>();
                            datos.put("razonSocial", dialogController.getRazonSocial());
                            datos.put("ruc", dialogController.getRuc());
                            datos.put("direccion", dialogController.getDireccion());
                            return datos;
                        }
                        return null;
                    });
                    if (okType != null) {
                        Button okBtn = (Button) dialogPane.lookupButton(okType);
                        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                            if (!dialogController.validar()) {
                                event.consume();
                            }
                        });
                    }
                    Optional<Map<String, String>> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String razonSocial = result.get().getOrDefault("razonSocial", "Empresa Genérica");
                        String ruc = result.get().getOrDefault("ruc", "");
                        String direccion = result.get().getOrDefault("direccion", "");
                        Cliente clienteTemp = new Cliente();
                        clienteTemp.setTipoCliente("EMPRESA");
                        clienteTemp.setRuc(ruc);
                        clienteTemp.setRazonSocial(razonSocial);
                        clienteTemp.setDireccion(direccion);
                        clienteTemp.setEmail("");
                        clienteTemp.setNombres(""); // Para cumplir con NOT NULL
                        clienteTemp.setApellidos(""); // Para cumplir con NOT NULL
                        clienteTemp.setId(null);
                        clienteTemp.setEsRapido(true); // <--- MARCAR COMO CLIENTE RÁPIDO
                        ClienteRepository clienteRepo = new ClienteRepository();
                        boolean guardado = clienteRepo.save(clienteTemp);
                        if (guardado && clienteTemp.getId() != null && clienteTemp.getId() > 0) {
                            return clienteTemp;
                        } else {
                            mostrarMensaje.accept("No se pudo registrar el cliente rápido de factura. Operación cancelada.");
                            return null;
                        }
                    } else {
                        mostrarMensaje.accept("Operación cancelada por el usuario.");
                        return null;
                    }
                }
            } catch (Exception ex) {
                mostrarMensaje.accept("Error al mostrar el formulario rápido: " + ex.getMessage());
                return null;
            }
        } else if (valorCombo instanceof Cliente) {
            return (Cliente) valorCombo;
        } else if (valorCombo instanceof String) {
            String texto = ((String) valorCombo).trim().toLowerCase();
            Cliente encontrado = comboClientes.getItems().stream()
                    .filter(c -> c.getNombreCompleto().toLowerCase().equals(texto)
                            || c.getDocumento().toLowerCase().equals(texto)
                            || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().equals(texto)))
                    .findFirst()
                    .orElse(null);
            if (encontrado != null) {
                comboClientes.getSelectionModel().select(encontrado);
                return encontrado;
            } else {
                mostrarMensaje.accept("No se encontró un cliente que coincida con el texto ingresado.");
                return null;
            }
        } else {
            // Lógica para CONSUMIDOR FINAL: solo uno, nunca crear más de uno y siempre
            // esRapido=true
            ClienteRepository clienteRepo = new ClienteRepository();
            Optional<Cliente> optGenerico = clienteRepo
                    .findByDocumento("00000000");
            if (optGenerico.isPresent()) {
                Cliente clienteExistente = optGenerico.get();
                if (!clienteExistente.isEsRapido()) {
                    clienteExistente.setEsRapido(true);
                    clienteRepo.save(clienteExistente);
                }
                return clienteExistente;
            } else {
                // Crear cliente genérico automáticamente como 'CONSUMIDOR FINAL'
                Cliente clienteGenerico = new Cliente();
                clienteGenerico.setDocumento("00000000");
                clienteGenerico.setNombreCompleto("CONSUMIDOR FINAL");
                clienteGenerico.setTipoCliente("NATURAL");
                clienteGenerico.setRazonSocial("");
                clienteGenerico.setEsRapido(true); // Ahora sí es cliente rápido
                // Si hay otros campos requeridos, inicialízalos aquí
                boolean guardado = clienteRepo.save(clienteGenerico);
                if (guardado && clienteGenerico.getId() != null && clienteGenerico.getId() > 0) {
                    return clienteGenerico;
                } else {
                    mostrarMensaje.accept("No se pudo registrar el cliente genérico. Operación cancelada.");
                    return null;
                }
            }
        }
        return null;
    }
}
