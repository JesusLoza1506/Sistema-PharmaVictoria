package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteExportAuditTest {
    @Test
    void gettersSetters() {
        ClienteExportAudit audit = new ClienteExportAudit();
        audit.setId(1);
        audit.setUsuario("admin");
        LocalDateTime fecha = LocalDateTime.of(2025, 11, 16, 12, 0);
        audit.setFecha(fecha);
        audit.setTipoArchivo("Excel");
        audit.setRutaArchivo("/ruta/archivo.xlsx");
        audit.setCantidadClientes(50);
        assertEquals(1, audit.getId());
        assertEquals("admin", audit.getUsuario());
        assertEquals(fecha, audit.getFecha());
        assertEquals("Excel", audit.getTipoArchivo());
        assertEquals("/ruta/archivo.xlsx", audit.getRutaArchivo());
        assertEquals(50, audit.getCantidadClientes());
    }
}
