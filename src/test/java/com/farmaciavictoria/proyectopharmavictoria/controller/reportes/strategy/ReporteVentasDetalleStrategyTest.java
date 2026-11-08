package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.ProductoDetalleDTO;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ReporteVentasDetalleStrategyTest {
    @Test
    void obtenerReporteMapeaVentaYProductos() throws Exception {
        LocalDate fechaVenta = LocalDate.of(2025, 11, 16);
        Connection conn = mock(Connection.class);
        PreparedStatement psVentas = mock(PreparedStatement.class);
        ResultSet rsVentas = mock(ResultSet.class);
        PreparedStatement psProductos = mock(PreparedStatement.class);
        ResultSet rsProductos = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(psVentas);
        when(conn.prepareStatement(contains("FROM detalle_ventas"))).thenReturn(psProductos);
        when(psVentas.executeQuery()).thenReturn(rsVentas);
        when(psProductos.executeQuery()).thenReturn(rsProductos);

        when(rsVentas.next()).thenReturn(true, false);
        when(rsVentas.getInt("id")).thenReturn(1);
        when(rsVentas.getString("numero_boleta")).thenReturn("B001-0001");
        when(rsVentas.getString("serie")).thenReturn("B001");
        when(rsVentas.getTimestamp("fecha_venta")).thenReturn(Timestamp.valueOf(fechaVenta.atTime(10, 0)));
        when(rsVentas.getString("cliente")).thenReturn("Juan Perez");
        when(rsVentas.getString("vendedor")).thenReturn("Ana Lopez");
        when(rsVentas.getBigDecimal("subtotal")).thenReturn(BigDecimal.valueOf(100));
        when(rsVentas.getBigDecimal("descuento_monto")).thenReturn(BigDecimal.valueOf(10));
        when(rsVentas.getBigDecimal("total")).thenReturn(BigDecimal.valueOf(90));
        when(rsVentas.getString("tipo_pago")).thenReturn("EFECTIVO");
        when(rsVentas.getString("estado")).thenReturn("REALIZADA");

        when(rsProductos.next()).thenReturn(true, false);
        when(rsProductos.getInt("producto_id")).thenReturn(101);
        when(rsProductos.getString("nombre")).thenReturn("Paracetamol");
        when(rsProductos.getInt("cantidad")).thenReturn(2);
        when(rsProductos.getBigDecimal("precio_unitario")).thenReturn(BigDecimal.valueOf(5));
        when(rsProductos.getBigDecimal("descuento")).thenReturn(BigDecimal.ZERO);
        when(rsProductos.getBigDecimal("subtotal")).thenReturn(BigDecimal.valueOf(10));

        try (MockedStatic<DatabaseConfig> dbConfigMock = mockStatic(DatabaseConfig.class)) {
            DatabaseConfig dbConfig = mock(DatabaseConfig.class);
            dbConfigMock.when(DatabaseConfig::getInstance).thenReturn(dbConfig);
            when(dbConfig.getConnection()).thenReturn(conn);

            ReporteVentasDetalleStrategy strategy = new ReporteVentasDetalleStrategy();
            List<VentaReporteDTO> resultado = strategy.obtenerReporte(
                    fechaVenta.atStartOfDay(), fechaVenta.atTime(23, 59, 59));

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            VentaReporteDTO venta = resultado.get(0);
            assertEquals("B001-0001", venta.getNumeroBoleta());
            assertEquals("Juan Perez", venta.getCliente());
            assertEquals(BigDecimal.valueOf(100), venta.getSubtotal());
            assertEquals("EFECTIVO", venta.getTipoPago());
            assertEquals("REALIZADA", venta.getEstado());
            assertNotNull(venta.getProductos());
            assertEquals(1, venta.getProductos().size());

            ProductoDetalleDTO producto = venta.getProductos().get(0);
            assertEquals(101, producto.getIdProducto());
            assertEquals("Paracetamol", producto.getNombre());
            assertEquals(2, producto.getCantidad());
            assertEquals(BigDecimal.valueOf(10), producto.getSubtotal());
        }
    }
}
