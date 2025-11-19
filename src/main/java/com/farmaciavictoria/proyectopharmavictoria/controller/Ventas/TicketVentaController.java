
package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepositoryJdbcImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TicketVentaController {
    private final VentaRepositoryJdbcImpl ventaRepository = new VentaRepositoryJdbcImpl();
    private final ComprobanteRepositoryJdbcImpl comprobanteRepository = new ComprobanteRepositoryJdbcImpl();

    public Venta registrarVentaTicket(List<DetalleVenta> carrito, String metodoPago, String usuario,
            String nombreFarmacia, Cliente cliente) {

        // Repositorios necesarios
        com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository productoRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository();
        com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository historialRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository();
        com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl detalleRepository = new com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepositoryJdbcImpl();

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta d : carrito) {
            if (d.getSubtotal() != null)
                subtotal = subtotal.add(d.getSubtotal());
        }
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal total = subtotal.add(igv);

        // Generar serie y número
        String serieTicket = "TKT1";
        int ultimoNumero = comprobanteRepository.obtenerUltimoNumeroPorSerieYTipo(serieTicket, "TICKET");
        String numeroTicket = String.format("%06d", ultimoNumero + 1);

        // Crear comprobante tipo TICKET
        Comprobante comprobante = new Comprobante();
        comprobante.setSerie(serieTicket);
        comprobante.setNumero(numeroTicket);
        comprobante.setTipo("TICKET");
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setEstadoSunat("GENERADO");
        comprobante.setHashSunat(null);

        // Crear venta
        Venta venta = new Venta();
        venta.setSubtotal(subtotal);
        venta.setIgvMonto(igv);
        venta.setTotal(total);
        venta.setTipoPago(metodoPago);
        venta.setTipoComprobante("TICKET");
        venta.setNumeroBoleta(numeroTicket);
        venta.setSerie(serieTicket);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setCreatedAt(LocalDateTime.now());
        venta.setEstado("REALIZADA");
        venta.setDetalles(carrito);
        venta.setComprobante(comprobante);
        venta.setObservaciones("Venta registrada como ticket simple");
        // Asignar usuario actual
        com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.SessionManager
                .getUsuarioActual();
        venta.setUsuario(usuarioActual);
        // Asignar cliente recibido
        if (cliente != null) {
            venta.setCliente(cliente);
        }
        // Asignar updatedAt igual a createdAt
        venta.setUpdatedAt(venta.getCreatedAt());

        // Guardar venta y comprobante
        Venta ventaGuardada = ventaRepository.save(venta);
        comprobante.setVenta(ventaGuardada);
        comprobanteRepository.save(comprobante);

        // Registrar detalle de venta y descontar stock
        for (DetalleVenta d : carrito) {
            // Asignar venta al detalle
            d.setVenta(ventaGuardada);
            detalleRepository.save(d);

            // Descontar stock
            Producto producto = d.getProducto();
            if (producto != null) {
                int stockAnterior = producto.getStockActual();
                int nuevoStock = stockAnterior - d.getCantidad();
                productoRepository.updateStock(producto.getId(), nuevoStock);

                // Registrar historial de cambio de stock (llamada estática)
                com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository
                        .registrarCambio(
                                producto.getId(),
                                "stock_actual",
                                String.valueOf(stockAnterior),
                                String.valueOf(nuevoStock),
                                usuario);
            }
        }

        // Retornar la venta registrada
        return ventaGuardada;
    }

    public void mostrarVistaPreviaTicket(String textoTicket) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/vista_previa_ticket.fxml"));
            javafx.scene.Parent root = loader.load();
            VistaPreviaTicketController controller = loader.getController();
            controller.setTicketTexto(textoTicket);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Vista previa del ticket");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para generar el texto del ticket adaptado
    public String generarTextoTicket(Venta venta, String nombreFarmacia) {
        StringBuilder sb = new StringBuilder();
        sb.append("┌────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append(String.format("│%-76s│\n", "PharmaVictoria"));
        sb.append(String.format("│RUC: %-52s│\n", "10468894501"));
        sb.append(String.format("│Dirección: %-48s│\n", "IMPERIAL, CAÑETE"));
        sb.append("│--------------------------------------------------------------│\n");
        sb.append(String.format("│%30s│\n", "TICKET DE VENTA"));
        sb.append(String.format("│%30s│\n", "SERIE: " + venta.getSerie() + " - N° " + venta.getNumeroBoleta()));
        sb.append("│--------------------------------------------------------------│\n");
        sb.append(String.format("│ FECHA: %s │\n", venta.getFechaVenta().toLocalDate()));
        sb.append(String.format("│ HORA: %s │\n", venta.getFechaVenta().toLocalTime().toString()));
        String cajero = "";
        if (venta.getUsuario() != null) {
            cajero = venta.getUsuario().getNombreCompleto();
        }
        sb.append(String.format("│ CAJERO: %s │\n", cajero));

        // Datos de cliente
        String clienteNombre = "CONSUMIDOR FINAL";
        String clienteDoc = "00000000";
        if (venta.getCliente() != null) {
            clienteNombre = venta.getCliente().getNombreCompleto();
            clienteDoc = venta.getCliente().getDocumento();
        }
        sb.append(String.format("│ CLIENTE: %s │\n", clienteNombre));
        if (venta.getCliente() != null && "NATURAL".equalsIgnoreCase(venta.getCliente().getTipoCliente())) {
            sb.append(String.format("│ DNI: %s │\n", clienteDoc));
        }
        sb.append("│--------------------------------------------------------------│\n");
        sb.append("│ CANT.  |  DESCRIPCIÓN                  | P.U.   | IMPORTE    │\n");
        sb.append("│--------------------------------------------------------------│\n");
        for (DetalleVenta d : venta.getDetalles()) {
            String prod = d.getProducto() != null ? d.getProducto().getNombre() : "";
            String linea = String.format("│ %-6d | %-28s | %-6.2f | %-9.2f │\n", d.getCantidad(), prod,
                    d.getPrecioUnitario(), d.getSubtotal());
            sb.append(linea);
        }
        sb.append("│--------------------------------------------------------------│\n");
        sb.append(String.format("│%40s%8.2f │\n", "SUBTOTAL:", venta.getSubtotal().doubleValue()));
        sb.append(String.format("│%40s%8.2f │\n", "IGV (18%):", venta.getIgvMonto().doubleValue()));
        sb.append("│--------------------------------------------------------------│\n");
        sb.append(String.format("│%40s%8.2f │\n", "TOTAL:", venta.getTotal().doubleValue()));
        sb.append("│--------------------------------------------------------------│\n");
        sb.append(String.format("│ MÉTODO DE PAGO: %s │\n",
                venta.getTipoPago() != null ? venta.getTipoPago() : "EFECTIVO"));
        sb.append("│--------------------------------------------------------------│\n");
        sb.append("│             ¡GRACIAS POR SU COMPRA! 💚                        │\n");
        sb.append("│   Consuma responsablemente. Medicamentos bajo receta médica.  │\n");
        sb.append("└──────────────────────────────────────────────────────────────┘\n");
        return sb.toString();
    }
}
