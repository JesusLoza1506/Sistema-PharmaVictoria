package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import java.sql.*;
import java.math.BigDecimal;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class VentaRepositoryJdbcImplTest {
    private VentaRepositoryJdbcImpl repo;
    private DatabaseConfig dbConfigMock;
    private Connection connMock;
    private PreparedStatement stmtMock;
    private ResultSet rsMock;

    @BeforeEach
    void setUp() throws Exception {
        repo = new VentaRepositoryJdbcImpl();
        dbConfigMock = Mockito.mock(DatabaseConfig.class);
        connMock = Mockito.mock(Connection.class);
        stmtMock = Mockito.mock(PreparedStatement.class);
        rsMock = Mockito.mock(ResultSet.class);
    }

    @Test
    void testConstructor() {
        assertNotNull(repo);
    }

    @Test
    void testFindByClienteIdThrows() throws Exception {
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.findByClienteId(-1));
        }
    }

    @Test
    void testDeleteThrows() throws Exception {
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.delete(-1));
        }
    }

    @Test
    void testSaveThrows() throws Exception {
        Venta venta = new Venta();
        venta.setSubtotal(BigDecimal.ZERO);
        venta.setDescuentoMonto(BigDecimal.ZERO);
        venta.setIgvMonto(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);
        venta.setTipoPago("");
        venta.setTipoComprobante("");
        venta.setNumeroBoleta("");
        venta.setSerie("");
        venta.setEstado("");
        venta.setObservaciones("");
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.save(venta));
        }
    }

    @Test
    void testFindByIdThrows() throws Exception {
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.findById(-1));
        }
    }

    @Test
    void testFindAllThrows() throws Exception {
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.findAll());
        }
    }

    @Test
    void testUpdateThrows() throws Exception {
        Venta venta = new Venta();
        venta.setId(-1);
        venta.setSubtotal(BigDecimal.ZERO);
        venta.setDescuentoMonto(BigDecimal.ZERO);
        venta.setIgvMonto(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);
        venta.setTipoPago("");
        venta.setTipoComprobante("");
        venta.setNumeroBoleta("");
        venta.setSerie("");
        venta.setEstado("");
        venta.setObservaciones("");
        try (MockedStatic<DatabaseConfig> dbStatic = Mockito.mockStatic(DatabaseConfig.class)) {
            dbStatic.when(DatabaseConfig::getInstance).thenReturn(dbConfigMock);
            Mockito.when(dbConfigMock.getConnection()).thenThrow(new SQLException("Fallo de conexión"));
            assertThrows(RuntimeException.class, () -> repo.update(venta));
        }
    }
}
