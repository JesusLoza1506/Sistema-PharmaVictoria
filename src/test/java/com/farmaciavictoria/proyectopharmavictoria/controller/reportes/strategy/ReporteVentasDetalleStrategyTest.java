package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.ProductoDetalleDTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteVentasDetalleStrategyTest {
    @Test
    void obtenerReporteMapeaVentaYProductos() throws Exception {
        // Mock de conexión y resultset para ventas
        Connection conn = mock(Connection.class);
        PreparedStatement psVentas = mock(PreparedStatement.class);
        ResultSet rsVentas = mock(ResultSet.class);
        // Mock de resultset para productos
        PreparedStatement psProductos = mock(PreparedStatement.class);
        ResultSet rsProductos = mock(ResultSet.class);
        // Simular una venta
        when(rsVentas.next()).thenReturn(true, false);
        when(rsVentas.getInt("id")).thenReturn(1);
        when(rsVentas.getString("numero_boleta")).thenReturn("B001-0001");
        when(rsVentas.getString("serie")).thenReturn("B001");
        when(rsVentas.getTimestamp("fecha_venta")).thenReturn(java.sql.Timestamp.valueOf("2025-11-16 10:00:00"));
        when(rsVentas.getString("cliente")).thenReturn("Juan Perez");
        when(rsVentas.getString("vendedor")).thenReturn("Ana Lopez");
        when(rsVentas.getBigDecimal("subtotal")).thenReturn(java.math.BigDecimal.valueOf(100));
        when(rsVentas.getBigDecimal("descuento_monto")).thenReturn(java.math.BigDecimal.valueOf(10));
        when(rsVentas.getBigDecimal("total")).thenReturn(java.math.BigDecimal.valueOf(90));
        when(rsVentas.getString("tipo_pago")).thenReturn("EFECTIVO");
        when(rsVentas.getString("estado")).thenReturn("REALIZADA");
        when(psVentas.executeQuery()).thenReturn(rsVentas);
        when(conn.prepareStatement(anyString())).thenReturn(psVentas);
        // Simular productos de la venta
        when(psProductos.executeQuery()).thenReturn(rsProductos);
        when(conn.prepareStatement(contains("FROM detalle_ventas"))).thenReturn(psProductos);
        when(rsProductos.next()).thenReturn(true, false);
        when(rsProductos.getInt("producto_id")).thenReturn(101);
        when(rsProductos.getString("nombre")).thenReturn("Paracetamol");
        when(rsProductos.getInt("cantidad")).thenReturn(2);
        when(rsProductos.getBigDecimal("precio_unitario")).thenReturn(java.math.BigDecimal.valueOf(5));
        when(rsProductos.getBigDecimal("descuento")).thenReturn(java.math.BigDecimal.ZERO);
        when(rsProductos.getBigDecimal("subtotal")).thenReturn(java.math.BigDecimal.valueOf(10));
        try (MockedStatic<com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig> dbConfigMock = mockStatic(
                com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.class)) {
            com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig dbConfig = mock(
                    com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.class);
            dbConfigMock.when(() -> com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance())
                    .thenReturn(dbConfig);
            when(dbConfig.getConnection()).thenReturn(conn);
            ReporteVentasDetalleStrategy strategy = new ReporteVentasDetalleStrategy();
            List<VentaReporteDTO> resultado = strategy.obtenerReporte(LocalDate.now(), LocalDate.now());
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            VentaReporteDTO venta = resultado.get(0);
            assertEquals("B001-0001", venta.getNumeroBoleta());
            assertEquals("Juan Perez", venta.getCliente());
            assertEquals(java.math.BigDecimal.valueOf(100), venta.getSubtotal());
            assertEquals("EFECTIVO", venta.getTipoPago());
            assertEquals("REALIZADA", venta.getEstado());
            assertNotNull(venta.getProductos());
            assertEquals(1, venta.getProductos().size());
            ProductoDetalleDTO prod = venta.getProductos().get(0);
            assertEquals(101, prod.getIdProducto());
            assertEquals("Paracetamol", prod.getNombre());
            assertEquals(2, prod.getCantidad());
            assertEquals(java.math.BigDecimal.valueOf(10), prod.getSubtotal());
        }
    }
}
