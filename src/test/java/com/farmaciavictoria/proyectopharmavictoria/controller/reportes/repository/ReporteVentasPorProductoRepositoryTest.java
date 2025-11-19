package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.repository;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReporteVentasPorProductoRepositoryTest {
    @Test
    void testObtenerRankingProductosReturnsEmptyListOnException() throws Exception {
        ReporteVentasPorProductoRepository repo = new ReporteVentasPorProductoRepository();
        try (MockedStatic<com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig> dbConfigMock = mockStatic(
                com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.class)) {
            dbConfigMock.when(() -> com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance())
                    .thenThrow(new RuntimeException("DB error"));
            List<VentaPorProductoDTO> result = repo.obtenerRankingProductos(LocalDate.now(), LocalDate.now());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testObtenerRankingProductosMapping() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        // Simular un resultado
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("Paracetamol");
        when(rs.getInt(2)).thenReturn(10);
        when(rs.getDouble(3)).thenReturn(100.0);
        when(stmt.executeQuery()).thenReturn(rs);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        doNothing().when(rs).close();
        doNothing().when(stmt).close();
        doNothing().when(conn).close();
        try (MockedStatic<com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig> dbConfigMock = mockStatic(
                com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.class)) {
            com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig dbConfig = mock(
                    com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.class);
            dbConfigMock.when(() -> com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance())
                    .thenReturn(dbConfig);
            when(dbConfig.getConnection()).thenReturn(conn);
            // Inicializar el repositorio dentro del mock
            ReporteVentasPorProductoRepository repo = new ReporteVentasPorProductoRepository();
            List<VentaPorProductoDTO> result = repo.obtenerRankingProductos(LocalDate.now(), LocalDate.now());
            assertNotNull(result);
            assertEquals(1, result.size());
            VentaPorProductoDTO dto = result.get(0);
            assertEquals("Paracetamol", dto.getProducto());
            assertEquals(10, dto.getCantidadVendida());
            assertEquals(100.0, dto.getTotalVentas());
        }
    }
}
