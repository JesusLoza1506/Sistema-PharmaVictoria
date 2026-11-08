package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.repository;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ReporteVentasPorProductoRepositoryTest {
    @Test
    void testObtenerRankingProductosReturnsEmptyListOnException() throws Exception {
        LocalDate fecha = LocalDate.of(2025, 11, 16);

        try (MockedStatic<DatabaseConfig> dbConfigMock = mockStatic(DatabaseConfig.class)) {
            DatabaseConfig dbConfig = mock(DatabaseConfig.class);
            dbConfigMock.when(DatabaseConfig::getInstance).thenReturn(dbConfig);
            when(dbConfig.getConnection()).thenThrow(new SQLException("DB error"));

            ReporteVentasPorProductoRepository repo = new ReporteVentasPorProductoRepository();
            List<VentaPorProductoDTO> resultado = repo.obtenerRankingProductos(
                    fecha.atStartOfDay(), fecha.atTime(23, 59, 59));

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    void testObtenerRankingProductosMapping() throws Exception {
        LocalDate fecha = LocalDate.of(2025, 11, 16);
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("Paracetamol");
        when(rs.getInt(2)).thenReturn(10);
        when(rs.getDouble(3)).thenReturn(100.0);

        try (MockedStatic<DatabaseConfig> dbConfigMock = mockStatic(DatabaseConfig.class)) {
            DatabaseConfig dbConfig = mock(DatabaseConfig.class);
            dbConfigMock.when(DatabaseConfig::getInstance).thenReturn(dbConfig);
            when(dbConfig.getConnection()).thenReturn(conn);

            ReporteVentasPorProductoRepository repo = new ReporteVentasPorProductoRepository();
            List<VentaPorProductoDTO> resultado = repo.obtenerRankingProductos(
                    fecha.atStartOfDay(), fecha.atTime(23, 59, 59));

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            VentaPorProductoDTO dto = resultado.get(0);
            assertEquals("Paracetamol", dto.getProducto());
            assertEquals(10, dto.getCantidadVendida());
            assertEquals(100.0, dto.getTotalVentas());
        }
    }
}
