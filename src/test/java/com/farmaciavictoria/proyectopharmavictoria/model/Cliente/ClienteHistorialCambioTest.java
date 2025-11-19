package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteHistorialCambioTest {
    @Test
    void constructorVacioYGettersSetters() {
        ClienteHistorialCambio cambio = new ClienteHistorialCambio();
        cambio.setId(1);
        cambio.setClienteId(2);
        cambio.setCampoModificado("email");
        cambio.setValorAnterior("old@mail.com");
        cambio.setValorNuevo("new@mail.com");
        cambio.setUsuario("admin");
        LocalDateTime fecha = LocalDateTime.now();
        cambio.setFecha(fecha);
        cambio.setFechaFormateada("16/11/2025");
        assertEquals(1, cambio.getId());
        assertEquals(2, cambio.getClienteId());
        assertEquals("email", cambio.getCampoModificado());
        assertEquals("old@mail.com", cambio.getValorAnterior());
        assertEquals("new@mail.com", cambio.getValorNuevo());
        assertEquals("admin", cambio.getUsuario());
        assertEquals(fecha, cambio.getFecha());
        assertEquals("16/11/2025", cambio.getFechaFormateada());
    }

    @Test
    void constructorCompleto() {
        LocalDateTime fecha = LocalDateTime.of(2025, 11, 16, 10, 0);
        ClienteHistorialCambio cambio = new ClienteHistorialCambio(3, "telefono", "123", "456", "user", fecha);
        assertEquals(3, cambio.getClienteId());
        assertEquals("telefono", cambio.getCampoModificado());
        assertEquals("123", cambio.getValorAnterior());
        assertEquals("456", cambio.getValorNuevo());
        assertEquals("user", cambio.getUsuario());
        assertEquals(fecha, cambio.getFecha());
    }
}
