package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.SessionManager;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.util.ComprobanteUtils;

import java.util.function.Consumer;

public final class AnulacionVentaService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final TransaccionPuntosRepository transaccionPuntosRepository;
    private final VentaHistorialCambioRepository historialCambioRepository;
    private final Consumer<String> mostrarMensaje;

    public AnulacionVentaService(VentaRepository ventaRepository, ProductoRepository productoRepository,
            TransaccionPuntosRepository transaccionPuntosRepository,
            VentaHistorialCambioRepository historialCambioRepository, Consumer<String> mostrarMensaje) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.transaccionPuntosRepository = transaccionPuntosRepository;
        this.historialCambioRepository = historialCambioRepository;
        this.mostrarMensaje = mostrarMensaje;
    }

    public void anular(Venta venta) {
        if (venta == null || "ANULADA".equalsIgnoreCase(venta.getEstado())) {
            mostrarMensaje.accept("La venta ya está anulada.");
            return;
        }
        boolean esTicket = "TICKET".equalsIgnoreCase(venta.getTipoComprobante());
        try {
            // Cambiar estado de la venta
            venta.setEstado("ANULADA");
            // Asignar usuario actual para auditoría
            Usuario usuarioActual = SessionManager
                    .getUsuarioActual();
            venta.setUsuario(usuarioActual);
            ventaRepository.update(venta);

            // Restar puntos ganados por la venta anulada
            if (venta.getCliente() != null) {
                int puntosGanados = PuntosVenta.calcularGanados(venta);
                if (puntosGanados > 0) {
                    TransaccionPuntos movExpirado = new TransaccionPuntos();
                    movExpirado.setClienteId(venta.getCliente().getId());
                    movExpirado.setVentaId(venta.getId());
                    movExpirado.setTipo("EXPIRADO");
                    movExpirado.setPuntos(puntosGanados);
                    movExpirado.setDescripcion("Puntos expirados por anulación de venta");
                    movExpirado.setFecha(java.time.LocalDateTime.now());
                    movExpirado.setUsuarioId(usuarioActual != null
                            ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                            : null);
                    transaccionPuntosRepository.save(movExpirado);
                }
            }
            // Restaurar stock de cada producto consultando el stock real en BD y mostrando
            // logs
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    Producto producto = detalle.getProducto();
                    if (producto != null && producto.getId() != null) {
                        try {
                            // Stock antes de la anulación
                            java.util.Optional<Producto> productoBDAntes = productoRepository
                                    .findById(producto.getId().longValue());
                            int stockAntes = productoBDAntes.isPresent()
                                    && productoBDAntes.get().getStockActual() != null
                                            ? productoBDAntes.get().getStockActual()
                                            : 0;
                            int cantidadVendida = detalle.getCantidad();
                            int nuevoStock = stockAntes + cantidadVendida;
                            System.out.println("[ANULACION STOCK] Producto: " + producto.getNombre() + " (ID: "
                                    + producto.getId() + ") | Stock antes BD: " + stockAntes + " | Cantidad anulada: "
                                    + cantidadVendida + " | Nuevo stock: " + nuevoStock);
                            boolean actualizado = productoRepository.updateStock(producto.getId(), nuevoStock);
                            // Stock después de la anulación
                            java.util.Optional<Producto> productoBDAfter = productoRepository
                                    .findById(producto.getId().longValue());
                            int stockDespues = productoBDAfter.isPresent()
                                    && productoBDAfter.get().getStockActual() != null
                                            ? productoBDAfter.get().getStockActual()
                                            : 0;
                            if (!actualizado) {
                                System.err.println(
                                        "[ERROR UPDATE STOCK] No se actualizó el stock en BD para producto ID: "
                                                + producto.getId() + " | Cantidad: " + cantidadVendida
                                                + " | Nuevo stock: " + nuevoStock);
                            } else {
                                System.out.println(
                                        "[UPDATE STOCK OK] Stock actualizado correctamente en BD para producto ID: "
                                                + producto.getId() + " | Stock después BD: " + stockDespues);
                            }
                        } catch (Exception ex) {
                            System.err.println("[ERROR ANULACION STOCK] Producto: "
                                    + (producto.getNombre() != null ? producto.getNombre() : "(sin nombre)") + " (ID: "
                                    + producto.getId() + ") | Detalle: " + ex.getMessage());
                        }
                    } else {
                        System.err.println("[ERROR ANULACION STOCK] Producto nulo o sin ID en detalle venta");
                    }
                }
            }

            // Guardar historial de cambio de anulación
            VentaHistorialCambio historial = new VentaHistorialCambio();
            historial.setVenta(venta);
            historial.setTipoCambio("ANULACION");
            historial.setMotivo("Venta anulada desde historial");
            historial.setUsuario(usuarioActual);
            historial.setFecha(java.time.LocalDateTime.now());
            historialCambioRepository.save(historial);

            // Enviar anulación a NubeFacT/SUNAT
            if (esTicket) {
                mostrarMensaje.accept("Venta tipo ticket anulada correctamente.");
            } else {
                java.util.Properties nubefactProps = new java.util.Properties();
                try (java.io.InputStream input = AnulacionVentaService.class
                        .getResourceAsStream("/venta_nubefact.properties")) {
                    if (input == null) {
                        throw new java.io.FileNotFoundException(
                                "No se encontró el archivo de configuración: venta_nubefact.properties");
                    }
                    nubefactProps.load(input);
                }
                String apiUrl = nubefactProps.getProperty("api.url");
                String apiToken = nubefactProps.getProperty("api.token");
                java.util.LinkedHashMap<String, Object> jsonAnulacion = new java.util.LinkedHashMap<>();
                jsonAnulacion.put("operacion", "generar_anulacion");
                int tipoComprobante = "FACTURA".equalsIgnoreCase(venta.getTipoComprobante()) ? 1 : 2;
                jsonAnulacion.put("tipo_de_comprobante", tipoComprobante);
                String serie = "";
                if (venta.getComprobante() != null && venta.getComprobante().getSerie() != null
                        && !venta.getComprobante().getSerie().isEmpty()) {
                    serie = venta.getComprobante().getSerie();
                } else if (venta.getSerie() != null && !venta.getSerie().isEmpty()) {
                    serie = venta.getSerie();
                } else {
                    mostrarMensaje.accept("No se encontró la serie del comprobante para la anulación. Verifique la venta.");
                    return;
                }
                String numero = null;
                if (venta.getComprobante() != null && venta.getComprobante().getNumero() != null
                        && !venta.getComprobante().getNumero().isEmpty()) {
                    numero = venta.getComprobante().getNumero();
                } else if (venta.getNumeroBoleta() != null && !venta.getNumeroBoleta().isEmpty()) {
                    numero = venta.getNumeroBoleta();
                } else {
                    mostrarMensaje.accept("No se encontró el número de comprobante para la anulación. Verifique la venta.");
                    return;
                }
                if (numero != null) {
                    numero = numero.replaceFirst("^0+", "");
                }
                jsonAnulacion.put("serie", serie);
                jsonAnulacion.put("numero", numero);
                jsonAnulacion.put("motivo", "ERROR DEL SISTEMA");
                jsonAnulacion.put("codigo_unico", "");
                String json = new com.google.gson.Gson().toJson(jsonAnulacion);
                String respuestaNubeFact = ComprobanteUtils.enviarFacturaNubeFact(json, apiUrl, apiToken);
                if (respuestaNubeFact != null) {
                    System.out.println("[NUBEFACT ANULACION] Respuesta: " + respuestaNubeFact);
                    com.google.gson.JsonObject jsonResp = null;
                    try {
                        jsonResp = new com.google.gson.JsonParser().parse(respuestaNubeFact).getAsJsonObject();
                    } catch (Exception e) {
                        mostrarMensaje.accept("No se pudo procesar la respuesta de NubeFacT. Respuesta: " + respuestaNubeFact);
                        return;
                    }
                    boolean aceptadaPorSunat = false;
                    if (jsonResp.has("aceptada_por_sunat")) {
                        aceptadaPorSunat = jsonResp.get("aceptada_por_sunat").getAsBoolean();
                    }
                    String enlace = jsonResp.has("enlace") ? jsonResp.get("enlace").getAsString() : "";
                    String ticket = "";
                    if (jsonResp.has("sunat_ticket_numero") && !jsonResp.get("sunat_ticket_numero").isJsonNull()) {
                        try {
                            ticket = jsonResp.get("sunat_ticket_numero").getAsString();
                        } catch (Exception ex) {
                            ticket = "";
                        }
                    }
                    String estadoSunat = aceptadaPorSunat ? "Aceptada"
                            : (ticket.isEmpty() ? "Pendiente" : "En proceso");
                    String mensaje = "Venta anulada correctamente en NubeFacT. Estado SUNAT: " + estadoSunat
                            + ".\nEnlace: "
                            + enlace;
                    if (!ticket.isEmpty()) {
                        mensaje += "\nTicket SUNAT: " + ticket;
                    }
                    mostrarMensaje.accept(mensaje);
                } else {
                    mostrarMensaje.accept("No se pudo anular la venta en SUNAT/NubeFacT. Respuesta: null");
                }
            }
        } catch (Exception ex) {
            mostrarMensaje.accept("Error al anular la venta: " + ex.getMessage());
        }
    }
}
