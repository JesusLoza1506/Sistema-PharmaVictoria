
package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {
    @Test
    void constructorVacioYGettersSetters() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombres("Juan");
        cliente.setApellidos("Perez");
        cliente.setDireccion("Av. Siempre Viva 123");
        cliente.setTelefono("555-1234");
        cliente.setEmail("juan@correo.com");
        cliente.setTipoCliente("NATURAL");
        cliente.setDocumento("12345678");
        cliente.setRazonSocial("No aplica");
        cliente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        cliente.setPuntosTotales(100);
        cliente.setPuntosUsados(10);
        assertEquals(1, cliente.getId());
        assertEquals("Juan", cliente.getNombres());
        assertEquals("Perez", cliente.getApellidos());
        assertEquals("Av. Siempre Viva 123", cliente.getDireccion());
        assertEquals("555-1234", cliente.getTelefono());
        assertEquals("juan@correo.com", cliente.getEmail());
        assertEquals("NATURAL", cliente.getTipoCliente());
        assertEquals("12345678", cliente.getDocumento());
        assertEquals("No aplica", cliente.getRazonSocial());
        assertEquals(LocalDate.of(1990, 1, 1), cliente.getFechaNacimiento());
        assertEquals(100, cliente.getPuntosTotales());
        assertEquals(10, cliente.getPuntosUsados());
    }

    @Test
    void constructorConDocumentoNombresApellidos() {
        Cliente cliente = new Cliente("87654321", "Ana", "Lopez");
        assertEquals("87654321", cliente.getDocumento());
        assertEquals("Ana", cliente.getNombres());
        assertEquals("Lopez", cliente.getApellidos());
    }

    @Test
    void toStringYEqualsYHashCode() {
        Cliente c1 = new Cliente("12345678", "Carlos", "Gomez");
        Cliente c2 = new Cliente("12345678", "Carlos", "Gomez");
        Cliente c3 = new Cliente("87654321", "Otro", "Apellido");
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotNull(c1.toString());
    }

    @Test
    void testMetodosNegocio() {
        Cliente cliente = new Cliente("12345678", "Juan", "Perez");
        cliente.setTipoCliente("NATURAL");
        cliente.setEmail("juan@correo.com");
        cliente.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        cliente.setPuntosTotales(1000);
        cliente.setPuntosUsados(200);
        assertEquals("Juan Perez", cliente.getNombreCompleto());
        assertEquals(800, cliente.getPuntosDisponibles());
        cliente.agregarPuntos(200);
        assertEquals(1000, cliente.getPuntosDisponibles());
        assertTrue(cliente.esClienteVip());
        assertTrue(cliente.tieneDniValido());
        assertTrue(cliente.tieneEmailValido());
        assertEquals(Period.between(LocalDate.of(2000, 1, 1), LocalDate.now()).getYears(), cliente.getEdad());
        assertTrue(cliente.redimirPuntos(500));
        assertEquals(500, cliente.getPuntosDisponibles());
        assertFalse(cliente.redimirPuntos(1000));
    }

    @Test
    void testFechasCreatedUpdated() {
        Cliente cliente = new Cliente();
        LocalDateTime now = LocalDateTime.now();
        cliente.setCreatedAt(now.minusDays(1));
        cliente.setUpdatedAt(now);
        assertEquals(now.minusDays(1), cliente.getCreatedAt());
        assertEquals(now, cliente.getUpdatedAt());
    }

    @Test
    void testSetGetDniRuc() {
        Cliente cliente = new Cliente();
        cliente.setTipoCliente("NATURAL");
        cliente.setDni("12345678");
        assertEquals("12345678", cliente.getDni());
        cliente.setTipoCliente("EMPRESA");
        cliente.setRuc("20123456789");
        assertEquals("20123456789", cliente.getRuc());
    }

    @Test
    void testEsNuevoEsteMes() {
        Cliente cliente = new Cliente();
        cliente.setCreatedAt(LocalDateTime.now());
        assertTrue(cliente.esNuevoEsteMes());
        cliente.setCreatedAt(LocalDateTime.now().minusMonths(1));
        assertFalse(cliente.esNuevoEsteMes());
    }

    @Test
    void testSetNombreCompletoYGetNombreCompleto() {
        Cliente cliente = new Cliente();
        cliente.setNombreCompleto("Juan Perez");
        assertEquals("Juan Perez", cliente.getNombreCompleto());
        cliente.setNombreCompleto("SoloNombre");
        assertEquals("SoloNombre", cliente.getNombreCompleto().trim());
        cliente.setNombreCompleto("");
        assertEquals(" ", cliente.getNombreCompleto());
        cliente.setNombres(null);
        cliente.setApellidos(null);
        assertEquals("Sin nombre", cliente.getNombreCompleto());
    }
}
