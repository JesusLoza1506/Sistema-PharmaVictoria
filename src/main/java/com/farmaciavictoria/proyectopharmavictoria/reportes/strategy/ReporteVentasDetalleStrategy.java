package com.farmaciavictoria.proyectopharmavictoria.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ReporteVentasDetalleStrategy implements ReporteVentasStrategy {
    @Override
    public List<VentaReporteDTO> obtenerReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        List<VentaReporteDTO> ventas = new ArrayList<>();
        String sqlVentas = "SELECT v.id, v.numero_boleta, v.serie, v.fecha_venta, " +
                "CASE WHEN c.tipo_cliente = 'Empresa' THEN IFNULL(c.razon_social, 'EMPRESA SIN NOMBRE') ELSE CONCAT(IFNULL(c.nombres,'SIN'),' ',IFNULL(c.apellidos,'CLIENTE')) END AS cliente, "
                +
                "concat(u.nombres,' ',u.apellidos) as vendedor, v.subtotal, v.descuento_monto, v.total, v.tipo_pago, v.estado "
                +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                "JOIN usuarios u ON v.usuario_id = u.id " +
                "WHERE v.fecha_venta BETWEEN ? AND ? ";

        try (java.sql.Connection conn = obtenerConexion();
                java.sql.PreparedStatement ps = conn.prepareStatement(sqlVentas)) {
            ps.setObject(1, fechaInicio);
            ps.setObject(2, fechaFin);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaReporteDTO venta = new VentaReporteDTO();
                    venta.setIdVenta(rs.getInt("id"));
                    venta.setNumeroBoleta(rs.getString("numero_boleta"));
                    venta.setSerie(rs.getString("serie"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_venta").toLocalDateTime());
                    venta.setCliente(rs.getString("cliente"));
                    venta.setVendedor(rs.getString("vendedor"));
                    venta.setSubtotal(rs.getBigDecimal("subtotal"));
                    venta.setDescuento(rs.getBigDecimal("descuento_monto"));
                    venta.setTotal(rs.getBigDecimal("total"));
                    venta.setTipoPago(rs.getString("tipo_pago"));
                    venta.setEstado(rs.getString("estado"));
                    venta.setProductos(obtenerProductosPorVenta(conn, venta.getIdVenta()));
                    ventas.add(venta);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ventas;
    }

    private java.sql.Connection obtenerConexion() throws Exception {
        // Usar el pool de conexiones centralizado de la aplicación
        return com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection();
    }

    private List<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO> obtenerProductosPorVenta(
            java.sql.Connection conn, int ventaId) {
        List<com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO> productos = new ArrayList<>();
        String sql = "SELECT dv.producto_id, p.nombre, dv.cantidad, dv.precio_unitario, dv.descuento, dv.subtotal " +
                "FROM detalle_ventas dv JOIN productos p ON dv.producto_id = p.id WHERE dv.venta_id = ?";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO prod = new com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO();
                    prod.setIdProducto(rs.getInt("producto_id"));
                    prod.setNombre(rs.getString("nombre"));
                    prod.setCantidad(rs.getInt("cantidad"));
                    prod.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    prod.setDescuento(rs.getBigDecimal("descuento"));
                    prod.setSubtotal(rs.getBigDecimal("subtotal"));
                    productos.add(prod);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }
}
