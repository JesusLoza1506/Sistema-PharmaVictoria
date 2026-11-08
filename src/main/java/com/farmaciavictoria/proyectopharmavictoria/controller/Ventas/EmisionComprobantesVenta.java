package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.TransaccionPuntosRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.PuntosVenta;
import com.farmaciavictoria.proyectopharmavictoria.service.Ventas.TotalesCarrito;
import com.farmaciavictoria.proyectopharmavictoria.util.ComprobanteUtils;

import java.util.List;
import java.util.function.Consumer;

final class EmisionComprobantesVenta {
    record UsoPuntos(boolean aplicados, int cantidad) {
    }

    private final List<DetalleVenta> carrito;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ComprobanteRepositoryJdbcImpl comprobanteRepository;
    private final VentaHistorialCambioRepository historialCambioRepository;
    private final ProductoRepository productoRepository;
    private final TransaccionPuntosRepository transaccionPuntosRepository;
    private final PanelPagoMixto pagoMixto;
    private final Consumer<String> mostrarMensaje;
    private final Runnable actualizarTotales;

    EmisionComprobantesVenta(List<DetalleVenta> carrito, VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository, ComprobanteRepositoryJdbcImpl comprobanteRepository,
            VentaHistorialCambioRepository historialCambioRepository, ProductoRepository productoRepository,
            TransaccionPuntosRepository transaccionPuntosRepository, PanelPagoMixto pagoMixto,
            Consumer<String> mostrarMensaje, Runnable actualizarTotales) {
        this.carrito = carrito;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.historialCambioRepository = historialCambioRepository;
        this.productoRepository = productoRepository;
        this.transaccionPuntosRepository = transaccionPuntosRepository;
        this.pagoMixto = pagoMixto;
        this.mostrarMensaje = mostrarMensaje;
        this.actualizarTotales = actualizarTotales;
    }

    void emitirFactura(Cliente clienteActual, String metodoPago, TotalesCarrito importes) {
        // Validar que el cliente seleccionado tenga datos completos de empresa
        if (clienteActual == null || clienteActual.getRuc() == null || clienteActual.getRazonSocial() == null
                || clienteActual.getDireccion() == null || clienteActual.getEmail() == null
                || clienteActual.getRuc().isEmpty() || clienteActual.getRazonSocial().isEmpty()
                || clienteActual.getDireccion().isEmpty()) {
            mostrarMensaje.accept(
                    "Para emitir factura electrónica, selecciona un cliente tipo Empresa con RUC, razón social y dirección.");
            return;
        }
        // Ir directo a la vista previa usando los datos del cliente seleccionado
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/vista_previa_comprobante.fxml"));
            javafx.scene.Parent root = loader.load();
            VistaPreviaComprobanteController previewController = loader.getController();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Vista Previa de Factura Electrónica");
            stage.setScene(new javafx.scene.Scene(root));
            // Convertir Cliente y DetalleVenta locales a tipos PharmavictoriaApplication
            ComprobanteUtils.Cliente clienteJsonPreview = new ComprobanteUtils.Cliente();
            clienteJsonPreview.tipo_documento = "6";
            clienteJsonPreview.numero_documento = clienteActual.getRuc();
            clienteJsonPreview.razon_social = clienteActual.getRazonSocial();
            clienteJsonPreview.direccion = clienteActual.getDireccion();
            clienteJsonPreview.direccion = clienteActual.getEmail();
            var detallesPreview = detallesParaVistaPrevia();
            // Obtener el siguiente número de factura para la serie
            String serieFactura = "FFF1";
            String numeroFactura = obtenerSiguienteNumeroFactura(serieFactura);
            previewController.setDatos(clienteJsonPreview, "FACTURA", serieFactura, numeroFactura,
                    importes.total().toPlainString(), detallesPreview);
            previewController.setStage(stage);
            previewController.setOnConfirmar(() -> {
                try {
                    // Armado de JSON igual al ejemplo de PharmavictoriaApplication.java
                    java.util.LinkedHashMap<String, Object> factura = new java.util.LinkedHashMap<>();
                    factura.put("operacion", "generar_comprobante");
                    factura.put("tipo_de_comprobante", 1);
                    factura.put("serie", serieFactura);
                    factura.put("numero", Integer.parseInt(numeroFactura));
                    factura.put("sunat_transaction", 1);
                    factura.put("cliente_tipo_de_documento", 6);
                    factura.put("cliente_numero_de_documento", clienteActual.getRuc());
                    factura.put("cliente_denominacion", clienteActual.getRazonSocial());
                    factura.put("cliente_direccion", clienteActual.getDireccion());
                    factura.put("cliente_email", clienteActual.getEmail() != null ? clienteActual.getEmail() : "");
                    factura.put("cliente_email_1", "");
                    factura.put("cliente_email_2", "");
                    factura.put("fecha_de_emision", java.time.LocalDate.now().toString());
                    factura.put("fecha_de_vencimiento", "");
                    factura.put("moneda", 1);
                    factura.put("tipo_de_cambio", "");
                    factura.put("porcentaje_de_igv", 18.00);
                    factura.put("descuento_global", "");
                    factura.put("total_descuento", "");
                    factura.put("total_anticipo", "");
                    factura.put("total_gravada", importes.subtotal().doubleValue());
                    factura.put("total_inafecta", "");
                    factura.put("total_exonerada", "");
                    factura.put("total_igv", importes.igv().doubleValue());
                    factura.put("total_gratuita", "");
                    factura.put("total_otros_cargos", "");
                    factura.put("total", importes.total().doubleValue());
                    factura.put("percepcion_tipo", "");
                    factura.put("percepcion_base_imponible", "");
                    factura.put("total_percepcion", "");
                    factura.put("total_incluido_percepcion", "");
                    factura.put("retencion_tipo", "");
                    factura.put("retencion_base_imponible", "");
                    factura.put("total_retencion", "");
                    factura.put("total_impuestos_bolsas", "");
                    factura.put("detraccion", false);
                    factura.put("observaciones", "");
                    factura.put("documento_que_se_modifica_tipo", "");
                    factura.put("documento_que_se_modifica_serie", "");
                    factura.put("documento_que_se_modifica_numero", "");
                    factura.put("tipo_de_nota_de_credito", "");
                    factura.put("tipo_de_nota_de_debito", "");
                    factura.put("enviar_automaticamente_a_la_sunat", true);
                    factura.put("enviar_automaticamente_al_cliente", false);
                    factura.put("condiciones_de_pago", "");
                    factura.put("medio_de_pago", "");
                    factura.put("placa_vehiculo", "");
                    factura.put("orden_compra_servicio", "");
                    factura.put("formato_de_pdf", "");
                    factura.put("generado_por_contingencia", "");
                    factura.put("bienes_region_selva", "");
                    factura.put("servicios_region_selva", "");
                    // Items
                    java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                    for (DetalleVenta d : carrito) {
                        java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                        item.put("unidad_de_medida", "NIU");
                        item.put("codigo", d.getProducto().getCodigo());
                        item.put("codigo_producto_sunat", "10000000");
                        item.put("descripcion", d.getProducto().getNombre());
                        item.put("cantidad", d.getCantidad());
                        double valorUnitario = d.getPrecioUnitario().doubleValue();
                        item.put("valor_unitario", valorUnitario);
                        double precioUnitarioConIGV = Math.round(valorUnitario * 1.18 * 100.0) / 100.0;
                        item.put("precio_unitario", precioUnitarioConIGV);
                        item.put("descuento", "");
                        item.put("subtotal", valorUnitario * d.getCantidad());
                        item.put("tipo_de_igv", 1);
                        double igvDetalle = Math.round(valorUnitario * d.getCantidad() * 0.18 * 100.0) / 100.0;
                        item.put("igv", igvDetalle);
                        double totalLinea = Math.round(precioUnitarioConIGV * d.getCantidad() * 100.0) / 100.0;
                        item.put("total", totalLinea);
                        item.put("anticipo_regularizacion", false);
                        item.put("anticipo_documento_serie", "");
                        item.put("anticipo_documento_numero", "");
                        items.add(item);
                    }
                    factura.put("items", items);
                    factura.put("guias", new java.util.ArrayList<>());
                    factura.put("venta_al_credito", new java.util.ArrayList<>());
                    String jsonFactura = new com.google.gson.Gson().toJson(factura);
                    System.out.println("[DEBUG JSON FACTURA] JSON generado para NubeFacT:\n" + jsonFactura);
                    java.util.Properties nubefactProps = new java.util.Properties();
                    try (java.io.InputStream input = EmisionComprobantesVenta.class
                            .getResourceAsStream("/venta_nubefact.properties")) {
                        if (input == null) {
                            throw new java.io.FileNotFoundException(
                                    "No se encontró el archivo de configuración: venta_nubefact.properties");
                        }
                        nubefactProps.load(input);
                    }
                    String apiUrl = nubefactProps.getProperty("api.url");
                    String apiToken = nubefactProps.getProperty("api.token");
                    String respuesta = ComprobanteUtils.enviarFacturaNubeFact(jsonFactura, apiUrl, apiToken);
                    String hashSunat = "";
                    String estadoSunat = "";
                    String pdfUrl = "";
                    String xmlUrl = "";
                    String cdrUrl = "";
                    try {
                        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(respuesta)
                                .getAsJsonObject();
                        hashSunat = json.has("hash") ? json.get("hash").getAsString() : "";
                        estadoSunat = json.has("sunat_description")
                                ? json.get("sunat_description").getAsString()
                                : "";
                        pdfUrl = json.has("enlace_del_pdf") ? json.get("enlace_del_pdf").getAsString() : "";
                        xmlUrl = json.has("enlace_xml") ? json.get("enlace_xml").getAsString() : "";
                        cdrUrl = json.has("enlace_cdr") ? json.get("enlace_cdr").getAsString() : "";
                        System.out.println("[DEBUG] Respuesta NubeFacT: " + respuesta);
                        System.out.println("[DEBUG] pdfUrl extraído: " + pdfUrl);
                    } catch (Exception ex) {
                        mostrarMensaje.accept("Error procesando respuesta de NubeFacT: " + ex.getMessage());
                        System.err.println("[ERROR NubeFacT] " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.ComprobanteFactory comprobanteFactory = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.FacturaFactory();
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante comprobante = comprobanteFactory
                            .crearComprobante();
                    comprobante.setSerie(serieFactura);
                    comprobante.setNumero(numeroFactura);
                    comprobante.setHashSunat(hashSunat);
                    comprobante.setEstadoSunat("GENERADO");
                    comprobante.setFechaEmision(java.time.LocalDateTime.now());

                    com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                            .getUsuarioActual();
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder ventaBuilder = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder()
                            .conCliente(clienteActual)
                            .conUsuario(usuarioActual)
                            .conSubtotal(importes.subtotal())
                            .conDescuentoMonto(importes.descuento())
                            .conIgvMonto(importes.igv())
                            .conTotal(importes.total())
                            .conTipoPago(metodoPago)
                            .conTipoComprobante("FACTURA")
                            .conNumeroBoleta(numeroFactura)
                            .conSerie(serieFactura)
                            .conFechaVenta(now)
                            .conEstado("REALIZADA")
                            .conDetalles(new java.util.ArrayList<>(carrito))
                            .conComprobante(comprobante)
                            .conObservaciones("")
                            .conCreatedAt(now)
                            .conUpdatedAt(now);
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta venta = ventaBuilder.build();
                    Venta ventaGuardada = ventaRepository.save(venta);
                    if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                        mostrarMensaje.accept("Error al guardar la venta. No se pudo obtener el ID generado.");
                        return;
                    }
                    for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
                        detalle.setVenta(ventaGuardada);
                        try {
                            detalleVentaRepository.save(detalle);
                        } catch (Exception ex) {
                            mostrarMensaje.accept("Error al guardar detalle de venta: " + ex.getMessage());
                            System.err.println("[ERROR DETALLE VENTA] " + ex.getMessage());
                        }
                        var producto = detalle.getProducto();
                        Integer cantidadVendida = detalle.getCantidad();
                        if (producto != null && producto.getId() != null && cantidadVendida != null) {
                            int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                            int nuevoStock = stockActual - cantidadVendida;
                            if (nuevoStock < 0) {
                                mostrarMensaje.accept("Error: El stock del producto '" + producto.getNombre()
                                        + "' no puede ser negativo. Venta no registrada correctamente.");
                                System.err.println("[ERROR STOCK] Stock negativo para producto: "
                                        + producto.getNombre() + " (ID: " + producto.getId() + ")");
                                continue;
                            }
                            producto.setStockActual(nuevoStock);
                            try {
                                // Usar el método correcto del repositorio para actualizar el stock
                                productoRepository.updateStock(producto.getId(), nuevoStock);
                            } catch (Exception ex) {
                                mostrarMensaje.accept("Error al actualizar el stock de '" + producto.getNombre() + "': "
                                        + ex.getMessage());
                                System.err.println("[ERROR STOCK] " + ex.getMessage());
                            }
                        }
                    }
                    // Guardar los pagos mixtos en la tabla detalle_pago_venta
                    if ("MIXTO".equalsIgnoreCase(metodoPago)) {
                        PagosMixtosVenta.guardar(pagoMixto, ventaGuardada, mostrarMensaje);
                    }
                    // Diagnóstico: mostrar tipo de cliente y total de venta antes de registrar
                    // puntos
                    System.out.println("[DEBUG DIAGNOSTICO PUNTOS] clienteActual="
                            + (clienteActual != null ? clienteActual.getTipoCliente() : "null") + " | totalVenta="
                            + (ventaGuardada != null && ventaGuardada.getTotal() != null ? ventaGuardada.getTotal()
                                    : "null"));
                    // Registrar puntos GANADOS por la venta
                    if (clienteActual != null && "NATURAL".equalsIgnoreCase(clienteActual.getTipoCliente())) {
                        int puntosGanados = PuntosVenta.calcularGanados(ventaGuardada);
                        if (puntosGanados > 0) {
                            com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos movGanado = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                            movGanado.setClienteId(clienteActual.getId());
                            movGanado.setVentaId(ventaGuardada.getId());
                            movGanado.setTipo("GANADO");
                            movGanado.setPuntos(puntosGanados);
                            movGanado.setDescripcion("Puntos ganados por compra");
                            movGanado.setFecha(java.time.LocalDateTime.now());
                            movGanado.setUsuarioId(usuarioActual != null
                                    ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                    : null);
                            System.out.println(
                                    "[DEBUG FLUJO PUNTOS] Se va a guardar transacción de puntos: " + movGanado);
                            boolean puntosGuardados = transaccionPuntosRepository.save(movGanado);
                            if (!puntosGuardados) {
                                System.err.println(
                                        "[ERROR PUNTOS] No se pudo guardar la transacción de puntos GANADO: "
                                                + movGanado);
                                mostrarMensaje.accept(
                                        "Error al registrar los puntos ganados. Verifique los datos del cliente y la venta.");
                            }
                        }
                    }
                    // 2. Crear comprobante y asociar la venta guardada
                    comprobante.setVenta(ventaGuardada);
                    comprobanteRepository.save(comprobante);

                    // Registrar historial de venta
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio historial = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio();
                    historial.setVenta(ventaGuardada);
                    historial.setTipoCambio("CREACION"); // ENUM correcto
                    historial.setMotivo("Venta registrada");
                    historial.setUsuario(usuarioActual);
                    historial.setFecha(java.time.LocalDateTime.now());
                    try {
                        historialCambioRepository.save(historial);
                    } catch (Exception ex) {
                        mostrarMensaje.accept("Error al registrar historial de venta: " + ex.getMessage());
                        System.err.println("[ERROR HISTORIAL VENTA] " + ex.getMessage());
                    }
                    StringBuilder msg = new StringBuilder();
                    msg.append("Factura electrónica enviada a SUNAT (demo)\n");
                    msg.append("Estado SUNAT: ").append(estadoSunat).append("\n");
                    if (!pdfUrl.isEmpty())
                        msg.append("PDF: ").append(pdfUrl).append("\n");
                    if (!xmlUrl.isEmpty())
                        msg.append("XML: ").append(xmlUrl).append("\n");
                    if (!cdrUrl.isEmpty())
                        msg.append("CDR: ").append(cdrUrl).append("\n");
                    mostrarMensaje.accept(msg.toString());
                    // Abrir ventana de acciones con PDF y datos del cliente (FACTURA/BOLETA)
                    try {
                        javafx.fxml.FXMLLoader accionesLoader = new javafx.fxml.FXMLLoader(
                                getClass().getResource("/fxml/comprobante_acciones.fxml"));
                        javafx.scene.Parent accionesRoot = accionesLoader.load();
                        ComprobanteAccionesController accionesController = accionesLoader.getController();
                        accionesController.setDatos(pdfUrl, clienteActual.getNombreCompleto(),
                                clienteActual.getEmail(), clienteActual.getTelefono());
                        javafx.stage.Stage accionesStage = new javafx.stage.Stage();
                        accionesController.setStage(accionesStage);
                        accionesStage.setTitle("Acciones sobre Comprobante Electrónico");
                        accionesStage.setScene(new javafx.scene.Scene(accionesRoot));
                        accionesStage.show();
                    } catch (Exception ex) {
                        mostrarMensaje.accept("No se pudo abrir la ventana de acciones: " + ex.getMessage());
                    }
                    carrito.clear();
                    actualizarTotales.run();
                } catch (Exception ex) {
                    mostrarMensaje.accept("Error en emisión de factura electrónica: " + ex.getMessage());
                    System.err.println("[ERROR Emisión Factura] " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            stage.showAndWait();
        } catch (Exception ex) {
            mostrarMensaje.accept("Error en emisión de factura electrónica: " + ex.getMessage());
            System.err.println("[ERROR Emisión Factura] " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void emitirBoleta(Cliente clienteActual, String metodoPago, TotalesCarrito importes, UsoPuntos usoPuntos) {
        // Validar que el cliente seleccionado sea NATURAL y tenga datos completos
        if (clienteActual == null || clienteActual.getDni() == null || clienteActual.getNombreCompleto() == null
                || clienteActual.getDireccion() == null || clienteActual.getDni().isEmpty()
                || clienteActual.getNombreCompleto().isEmpty() || clienteActual.getDireccion().isEmpty()) {
            mostrarMensaje.accept(
                    "Para emitir boleta electrónica, selecciona un cliente tipo NATURAL con DNI, nombre y dirección.");
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/vista_previa_comprobante.fxml"));
            javafx.scene.Parent root = loader.load();
            VistaPreviaComprobanteController previewController = loader.getController();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Vista Previa de Boleta Electrónica");
            stage.setScene(new javafx.scene.Scene(root));
            ComprobanteUtils.Cliente clienteJsonPreview = new ComprobanteUtils.Cliente();
            clienteJsonPreview.tipo_documento = "1";
            clienteJsonPreview.numero_documento = clienteActual.getDni();
            clienteJsonPreview.razon_social = clienteActual.getNombreCompleto();
            clienteJsonPreview.direccion = clienteActual.getDireccion();
            clienteJsonPreview.direccion = clienteActual.getEmail();
            var detallesPreview = detallesParaVistaPrevia();
            String serieBoleta = "BBB1";
            String numeroBoleta = obtenerSiguienteNumeroBoleta(serieBoleta);
            previewController.setDatos(clienteJsonPreview, "BOLETA", serieBoleta, numeroBoleta,
                    importes.total().toPlainString(), detallesPreview);
            previewController.setStage(stage);
            previewController.setOnConfirmar(() -> {
                try {
                    // Armado de JSON igual al ejemplo de PharmavictoriaApplication.java
                    java.util.LinkedHashMap<String, Object> boleta = new java.util.LinkedHashMap<>();
                    boleta.put("operacion", "generar_comprobante");
                    boleta.put("tipo_de_comprobante", 2);
                    boleta.put("serie", serieBoleta);
                    boleta.put("numero", Integer.parseInt(numeroBoleta));
                    boleta.put("sunat_transaction", 1);
                    boleta.put("cliente_tipo_de_documento", 1);
                    boleta.put("cliente_numero_de_documento", clienteActual.getDni());
                    boleta.put("cliente_denominacion", clienteActual.getNombreCompleto());
                    boleta.put("cliente_direccion", clienteActual.getDireccion());
                    boleta.put("cliente_email", clienteActual.getEmail() != null ? clienteActual.getEmail() : "");
                    boleta.put("cliente_email_1", "");
                    boleta.put("cliente_email_2", "");
                    boleta.put("fecha_de_emision", java.time.LocalDate.now().toString());
                    boleta.put("fecha_de_vencimiento", "");
                    boleta.put("moneda", 1);
                    boleta.put("tipo_de_cambio", "");
                    boleta.put("porcentaje_de_igv", 18.00);
                    boleta.put("descuento_global", "");
                    boleta.put("total_descuento", "");
                    boleta.put("total_anticipo", "");
                    boleta.put("total_gravada", importes.subtotal().doubleValue());
                    boleta.put("total_inafecta", "");
                    boleta.put("total_exonerada", "");
                    boleta.put("total_igv", importes.igv().doubleValue());
                    boleta.put("total_gratuita", "");
                    boleta.put("total_otros_cargos", "");
                    boleta.put("total", importes.total().doubleValue());
                    boleta.put("percepcion_tipo", "");
                    boleta.put("percepcion_base_imponible", "");
                    boleta.put("total_percepcion", "");
                    boleta.put("total_incluido_percepcion", "");
                    boleta.put("retencion_tipo", "");
                    boleta.put("retencion_base_imponible", "");
                    boleta.put("total_retencion", "");
                    boleta.put("total_impuestos_bolsas", "");
                    boleta.put("detraccion", false);
                    boleta.put("observaciones", "");
                    boleta.put("documento_que_se_modifica_tipo", "");
                    boleta.put("documento_que_se_modifica_serie", "");
                    boleta.put("documento_que_se_modifica_numero", "");
                    boleta.put("tipo_de_nota_de_credito", "");
                    boleta.put("tipo_de_nota_de_debito", "");
                    boleta.put("enviar_automaticamente_a_la_sunat", true);
                    boleta.put("enviar_automaticamente_al_cliente", false);
                    boleta.put("condiciones_de_pago", "");
                    boleta.put("medio_de_pago", "");
                    boleta.put("placa_vehiculo", "");
                    boleta.put("orden_compra_servicio", "");
                    boleta.put("formato_de_pdf", "");
                    boleta.put("generado_por_contingencia", "");
                    boleta.put("bienes_region_selva", "");
                    boleta.put("servicios_region_selva", "");
                    // Items
                    java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                    for (DetalleVenta d : carrito) {
                        java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                        item.put("unidad_de_medida", "NIU");
                        item.put("codigo", d.getProducto().getCodigo());
                        item.put("codigo_producto_sunat", "10000000");
                        item.put("descripcion", d.getProducto().getNombre());
                        item.put("cantidad", d.getCantidad());
                        double valorUnitario = d.getPrecioUnitario().doubleValue();
                        item.put("valor_unitario", valorUnitario);
                        double precioUnitarioConIGV = Math.round(valorUnitario * 1.18 * 100.0) / 100.0;
                        item.put("precio_unitario", precioUnitarioConIGV);
                        item.put("descuento", "");
                        item.put("subtotal", valorUnitario * d.getCantidad());
                        item.put("tipo_de_igv", 1);
                        double igvDetalle = Math.round(valorUnitario * d.getCantidad() * 0.18 * 100.0) / 100.0;
                        item.put("igv", igvDetalle);
                        double totalLinea = Math.round(precioUnitarioConIGV * d.getCantidad() * 100.0) / 100.0;
                        item.put("total", totalLinea);
                        item.put("anticipo_regularizacion", false);
                        item.put("anticipo_documento_serie", "");
                        item.put("anticipo_documento_numero", "");
                        items.add(item);
                    }
                    boleta.put("items", items);
                    boleta.put("guias", new java.util.ArrayList<>());
                    boleta.put("venta_al_credito", new java.util.ArrayList<>());
                    String jsonBoleta = new com.google.gson.Gson().toJson(boleta);
                    System.out.println("[DEBUG JSON BOLETA] JSON generado para NubeFacT:\n" + jsonBoleta);
                    java.util.Properties nubefactProps = new java.util.Properties();
                    try (java.io.InputStream input = EmisionComprobantesVenta.class
                            .getResourceAsStream("/venta_nubefact.properties")) {
                        if (input == null) {
                            throw new java.io.FileNotFoundException(
                                    "No se encontró el archivo de configuración: venta_nubefact.properties");
                        }
                        nubefactProps.load(input);
                    }
                    String apiUrl = nubefactProps.getProperty("api.url");
                    String apiToken = nubefactProps.getProperty("api.token");
                    String respuesta = com.farmaciavictoria.proyectopharmavictoria.util.ComprobanteUtils
                            .enviarFacturaNubeFact(jsonBoleta, apiUrl, apiToken);
                    // Mostrar JSON y respuesta NubeFacT solo en terminal para diagnóstico
                    System.out.println("[DEBUG JSON enviado a NubeFacT]\n" + jsonBoleta);
                    System.out.println("[DEBUG Respuesta completa de NubeFacT]\n" + respuesta);
                    String hashSunat = "";
                    String estadoSunat = "";
                    String pdfUrl = "";
                    String xmlUrl = "";
                    String cdrUrl = "";
                    try {
                        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(respuesta)
                                .getAsJsonObject();
                        if (json != null && !json.isJsonNull()) {
                            hashSunat = json.has("hash") && !json.get("hash").isJsonNull()
                                    ? json.get("hash").getAsString()
                                    : "";
                            estadoSunat = json.has("sunat_description")
                                    && !json.get("sunat_description").isJsonNull()
                                            ? json.get("sunat_description").getAsString()
                                            : "";
                            pdfUrl = json.has("enlace_del_pdf") && !json.get("enlace_del_pdf").isJsonNull()
                                    ? json.get("enlace_del_pdf").getAsString()
                                    : "";
                            xmlUrl = json.has("enlace_xml") && !json.get("enlace_xml").isJsonNull()
                                    ? json.get("enlace_xml").getAsString()
                                    : "";
                            cdrUrl = json.has("enlace_cdr") && !json.get("enlace_cdr").isJsonNull()
                                    ? json.get("enlace_cdr").getAsString()
                                    : "";
                            System.out.println("[DEBUG] Respuesta NubeFacT: " + respuesta);
                            System.out.println("[DEBUG] pdfUrl extraído: " + pdfUrl);
                        } else {
                            mostrarMensaje.accept("Boleta registrada, pero no se recibió respuesta completa de NubeFacT.");
                        }
                    } catch (Exception ex) {
                        mostrarMensaje.accept("Boleta registrada, pero no se recibió respuesta completa de NubeFacT.");
                        System.err.println("[ERROR NubeFacT] " + ex.getMessage());
                    }
                    // 1. Crear comprobante usando Factory
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.ComprobanteFactory comprobanteFactory = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.BoletaFactory();
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante comprobante = comprobanteFactory
                            .crearComprobante();
                    comprobante.setSerie(serieBoleta);
                    comprobante.setNumero(numeroBoleta);
                    comprobante.setHashSunat(hashSunat);
                    comprobante.setEstadoSunat("GENERADO");
                    comprobante.setFechaEmision(java.time.LocalDateTime.now());

                    // 2. Construir venta usando Builder
                    com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                            .getUsuarioActual();
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder ventaBuilder = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaBuilder()
                            .conCliente(clienteActual)
                            .conUsuario(usuarioActual)
                            .conSubtotal(importes.subtotal())
                            .conDescuentoMonto(importes.descuento())
                            .conIgvMonto(importes.igv())
                            .conTotal(importes.total())
                            .conTipoPago(metodoPago)
                            .conTipoComprobante("BOLETA")
                            .conNumeroBoleta(numeroBoleta)
                            .conSerie(serieBoleta)
                            .conFechaVenta(now)
                            .conEstado("REALIZADA")
                            .conDetalles(new java.util.ArrayList<>(carrito))
                            .conComprobante(comprobante)
                            .conObservaciones("")
                            .conCreatedAt(now)
                            .conUpdatedAt(now);
                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta venta = ventaBuilder.build();
                    Venta ventaGuardada = ventaRepository.save(venta);
                    if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                        mostrarMensaje.accept("Error al guardar la venta. No se pudo obtener el ID generado.");
                        return;
                    }
                    for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
                        detalle.setVenta(ventaGuardada);
                        try {
                            detalleVentaRepository.save(detalle);
                        } catch (Exception ex) {
                            mostrarMensaje.accept("Error al guardar detalle de venta: " + ex.getMessage());
                            System.err.println("[ERROR DETALLE VENTA] " + ex.getMessage());
                        }
                        var producto = detalle.getProducto();
                        Integer cantidadVendida = detalle.getCantidad();
                        if (producto != null && producto.getId() != null && cantidadVendida != null) {
                            int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
                            int nuevoStock = stockActual - cantidadVendida;
                            if (nuevoStock < 0) {
                                mostrarMensaje.accept("Error: El stock del producto '" + producto.getNombre()
                                        + "' no puede ser negativo. Venta no registrada correctamente.");
                                System.err.println("[ERROR STOCK] Stock negativo para producto: "
                                        + producto.getNombre() + " (ID: " + producto.getId() + ")");
                                continue;
                            }
                            producto.setStockActual(nuevoStock);
                            try {
                                productoRepository.updateStock(producto.getId(), nuevoStock);
                            } catch (Exception ex) {
                                mostrarMensaje.accept("Error al actualizar el stock de '" + producto.getNombre() + "': "
                                        + ex.getMessage());
                                System.err.println("[ERROR STOCK] " + ex.getMessage());
                            }
                        }
                    }
                    // Registrar puntos GANADOS por la venta SOLO para clientes NATURAL (boleta)
                    System.out.println("[DEBUG DIAGNOSTICO PUNTOS] clienteActual="
                            + (clienteActual != null ? clienteActual.getTipoCliente() : "null") + " | totalVenta="
                            + (ventaGuardada != null && ventaGuardada.getTotal() != null ? ventaGuardada.getTotal()
                                    : "null"));
                    // Guardar pagos mixtos en detalle_pago_venta para BOLETA
                    if ("MIXTO".equalsIgnoreCase(metodoPago)) {
                        PagosMixtosVenta.guardar(pagoMixto, ventaGuardada, mostrarMensaje);
                    }
                    if (clienteActual != null && "NATURAL".equalsIgnoreCase(clienteActual.getTipoCliente())) {
                        int puntosGanados = PuntosVenta.calcularGanados(ventaGuardada);
                        if (puntosGanados > 0) {
                            com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos movGanado = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                            movGanado.setClienteId(clienteActual.getId());
                            movGanado.setVentaId(ventaGuardada.getId());
                            movGanado.setTipo("GANADO");
                            movGanado.setPuntos(puntosGanados);
                            movGanado.setDescripcion("Puntos ganados por compra");
                            movGanado.setFecha(java.time.LocalDateTime.now());
                            movGanado.setUsuarioId(usuarioActual != null
                                    ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                    : null);
                            System.out.println(
                                    "[DEBUG FLUJO PUNTOS] Se va a guardar transacción de puntos: " + movGanado);
                            boolean puntosGuardados = transaccionPuntosRepository.save(movGanado);
                            if (!puntosGuardados) {
                                System.err.println(
                                        "[ERROR PUNTOS] No se pudo guardar la transacción de puntos GANADO: "
                                                + movGanado);
                                mostrarMensaje.accept(
                                        "Error al registrar los puntos ganados. Verifique los datos del cliente y la venta.");
                            }
                        }
                    }
                    // Registrar el uso de puntos si se usaron en la venta
                    if (usoPuntos.aplicados() && usoPuntos.cantidad() > 0) {
                        com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos mov = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.TransaccionPuntos();
                        mov.setClienteId(clienteActual.getId());
                        mov.setVentaId(ventaGuardada.getId());
                        mov.setTipo("USADO");
                        mov.setPuntos(usoPuntos.cantidad());
                        mov.setDescripcion("Redención de puntos en venta");
                        mov.setFecha(java.time.LocalDateTime.now());
                        mov.setUsuarioId(usuarioActual != null
                                ? (usuarioActual.getId() != null ? usuarioActual.getId().intValue() : null)
                                : null);
                        boolean puntosUsadosGuardados = transaccionPuntosRepository.save(mov);
                        if (!puntosUsadosGuardados) {
                            System.err.println(
                                    "[ERROR PUNTOS] No se pudo guardar la transacción de puntos USADO: " + mov);
                            mostrarMensaje.accept(
                                    "Error al registrar el uso de puntos. Verifique los datos del cliente y la venta.");
                        }
                    }
                    comprobante.setVenta(ventaGuardada);
                    comprobanteRepository.save(comprobante);

                    com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio historial = new com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio();
                    historial.setVenta(ventaGuardada);
                    historial.setTipoCambio("CREACION");
                    historial.setMotivo("Venta registrada");
                    historial.setUsuario(usuarioActual);
                    historial.setFecha(java.time.LocalDateTime.now());
                    try {
                        historialCambioRepository.save(historial);
                    } catch (Exception ex) {
                        mostrarMensaje.accept("Error al registrar historial de venta: " + ex.getMessage());
                        System.err.println("[ERROR HISTORIAL VENTA] " + ex.getMessage());
                    }
                    StringBuilder msg = new StringBuilder();
                    msg.append("Boleta electrónica enviada a SUNAT (demo)\n");
                    msg.append("Estado SUNAT: ").append(estadoSunat).append("\n");
                    if (!pdfUrl.isEmpty())
                        msg.append("PDF: ").append(pdfUrl).append("\n");
                    if (!xmlUrl.isEmpty())
                        msg.append("XML: ").append(xmlUrl).append("\n");
                    if (!cdrUrl.isEmpty())
                        msg.append("CDR: ").append(cdrUrl).append("\n");
                    mostrarMensaje.accept(msg.toString());
                    carrito.clear();
                    actualizarTotales.run();
                    // Abrir ventana de acciones con PDF y datos del cliente (BOLETA)
                    try {
                        javafx.fxml.FXMLLoader accionesLoader = new javafx.fxml.FXMLLoader(
                                getClass().getResource("/fxml/comprobante_acciones.fxml"));
                        javafx.scene.Parent accionesRoot = accionesLoader.load();
                        ComprobanteAccionesController accionesController = accionesLoader.getController();
                        accionesController.setDatos(pdfUrl, clienteActual.getNombreCompleto(),
                                clienteActual.getEmail(), clienteActual.getTelefono());
                        javafx.stage.Stage accionesStage = new javafx.stage.Stage();
                        accionesController.setStage(accionesStage);
                        accionesStage.setTitle("Acciones sobre Comprobante Electrónico");
                        accionesStage.setScene(new javafx.scene.Scene(accionesRoot));
                        accionesStage.show();
                    } catch (Exception ex) {
                        System.err.println(
                                "[ERROR BOLETA] No se pudo abrir la ventana de acciones: " + ex.getMessage());
                        mostrarMensaje.accept("No se pudo abrir la ventana de acciones: " + ex.getMessage());
                    }
                } catch (Exception ex) {
                    mostrarMensaje.accept("Error en emisión de boleta electrónica: " + ex.getMessage());
                    System.err.println("[ERROR Emisión Boleta] " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            stage.showAndWait();
        } catch (Exception ex) {
            mostrarMensaje.accept("Error en emisión de boleta electrónica: " + ex.getMessage());
            System.err.println("[ERROR Emisión Boleta] " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private javafx.collections.ObservableList<ComprobanteUtils.DetalleVenta> detallesParaVistaPrevia() {
        javafx.collections.ObservableList<ComprobanteUtils.DetalleVenta> detallesPreview = javafx.collections.FXCollections
                .observableArrayList();
        for (DetalleVenta d : carrito) {
            ComprobanteUtils.DetalleVenta det = new ComprobanteUtils.DetalleVenta();
            det.unidad_de_medida = "NIU";
            det.codigo_producto = d.getProducto().getCodigo();
            det.descripcion = d.getProducto().getNombre();
            det.cantidad = d.getCantidad();
            det.valor_unitario = d.getPrecioUnitario().doubleValue();
            det.precio_unitario = d.getPrecioUnitario().doubleValue();
            det.subtotal = d.getSubtotal().doubleValue();
            det.tipo_de_igv = 1;
            double igvDetalle = d.getSubtotal().doubleValue() * 0.18;
            det.igv = igvDetalle;
            det.total = d.getSubtotal().doubleValue() + igvDetalle;
            detallesPreview.add(det);
        }
        return detallesPreview;
    }

    private String obtenerSiguienteNumeroFactura(String serie) {
        int ultimoNumero = comprobanteRepository.obtenerUltimoNumeroPorSerieYTipo(serie, "FACTURA");
        int siguienteNumero = ultimoNumero + 1;
        return String.format("%06d", siguienteNumero);
    }
    private String obtenerSiguienteNumeroBoleta(String serie) {
        int ultimoNumero = comprobanteRepository.obtenerUltimoNumeroPorSerieYTipo(serie, "BOLETA");
        return String.format("%06d", ultimoNumero + 1);
    }
}
