package com.farmaciavictoria.proyectopharmavictoria.model.Proveedor;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ProveedorAuditoriaTest {
    @Test
    void constructorYGetters() {
        LocalDateTime fecha = LocalDateTime.of(2025, 11, 16, 22, 30);
        String usuario = "admin";
        String accion = "CREAR";
        String detalles = "Proveedor creado correctamente";
        ProveedorAuditoria pa = new ProveedorAuditoria(fecha, usuario, accion, detalles);
        assertEquals(fecha, pa.getFecha());
        assertEquals(usuario, pa.getUsuario());
        assertEquals(accion, pa.getAccion());
        assertEquals(detalles, pa.getDetalles());
    }

    @Test
    void testToString() {
        LocalDateTime fecha = LocalDateTime.of(2025, 11, 16, 22, 30);
        ProveedorAuditoria pa = new ProveedorAuditoria(fecha, "admin", "CREAR", "Proveedor creado correctamente");
        String expected = String.format("[%s] %s - %s: %s", fecha, "admin", "CREAR", "Proveedor creado correctamente");
        assertEquals(expected, pa.toString());
    }
}
