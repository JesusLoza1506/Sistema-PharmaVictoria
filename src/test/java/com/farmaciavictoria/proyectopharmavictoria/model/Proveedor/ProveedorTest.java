package com.farmaciavictoria.proyectopharmavictoria.model.Proveedor;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ProveedorTest {
    @Test
    void constructorVacioYSettersYGetters() {
        Proveedor p = new Proveedor();
        p.setId(1);
        p.setRazonSocial("Farmacia S.A.");
        p.setRuc("12345678901");
        p.setContacto("Juan Perez");
        p.setTelefono("999888777");
        p.setEmail("farmacia@email.com");
        p.setDireccion("Av. Principal 123");
        p.setCondicionesPago("Contado");
        p.setObservaciones("Sin observaciones");
        p.setTipo("Mayorista");
        p.setTipoProducto("Medicamentos");
        p.setActivo(true);
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedAt(now);
        assertEquals(1, p.getId());
        assertEquals("Farmacia S.A.", p.getRazonSocial());
        assertEquals("12345678901", p.getRuc());
        assertEquals("Juan Perez", p.getContacto());
        assertEquals("999888777", p.getTelefono());
        assertEquals("farmacia@email.com", p.getEmail());
        assertEquals("Av. Principal 123", p.getDireccion());
        assertEquals("Contado", p.getCondicionesPago());
        assertEquals("Sin observaciones", p.getObservaciones());
        assertEquals("Mayorista", p.getTipo());
        assertEquals("Medicamentos", p.getTipoProducto());
        assertTrue(p.getActivo());
        assertTrue(p.isActivo());
        assertEquals(now, p.getCreatedAt());
    }

    @Test
    void constructorCompleto() {
        Proveedor p = new Proveedor("Farmacia S.A.", "12345678901", "Juan Perez", "999888777", "farmacia@email.com",
                "Av. Principal 123", "Contado", "Sin observaciones");
        assertEquals("Farmacia S.A.", p.getRazonSocial());
        assertEquals("12345678901", p.getRuc());
        assertEquals("Juan Perez", p.getContacto());
        assertEquals("999888777", p.getTelefono());
        assertEquals("farmacia@email.com", p.getEmail());
        assertEquals("Av. Principal 123", p.getDireccion());
        assertEquals("Contado", p.getCondicionesPago());
        assertEquals("Sin observaciones", p.getObservaciones());
    }

    @Test
    void testToString() {
        Proveedor p = new Proveedor();
        p.setRazonSocial("Farmacia S.A.");
        assertEquals("Farmacia S.A.", p.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Proveedor p1 = new Proveedor();
        Proveedor p2 = new Proveedor();
        p1.setRuc("12345678901");
        p2.setRuc("12345678901");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        p2.setRuc("00000000000");
        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());
    }

    @Test
    void testIsActivoNull() {
        Proveedor p = new Proveedor();
        p.setActivo(null);
        assertFalse(p.isActivo());
    }
}
