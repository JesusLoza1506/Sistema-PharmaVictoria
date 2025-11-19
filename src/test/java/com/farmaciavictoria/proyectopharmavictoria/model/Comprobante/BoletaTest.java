package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoletaTest {
    @Test
    void testGettersRetornanValoresCorrectos() {
        Cliente cliente = mock(Cliente.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        List<DetalleVenta> productos = Arrays.asList(detalle);
        Boleta boleta = new Boleta(cliente, productos, 100.0, 18.0, 118.0);
        assertEquals(cliente, boleta.getCliente());
        assertEquals(productos, boleta.getProductos());
        assertEquals(100.0, boleta.getTotal() - boleta.getIgv());
        assertEquals(18.0, boleta.getIgv());
        assertEquals(118.0, boleta.getTotal());
    }

    @Test
    void testGenerarNoLanzaExcepcion() {
        Cliente cliente = mock(Cliente.class);
        List<DetalleVenta> productos = Arrays.asList(mock(DetalleVenta.class));
        Boleta boleta = new Boleta(cliente, productos, 50.0, 9.0, 59.0);
        assertDoesNotThrow(boleta::generar);
    }

    @Test
    void testImprimirNoLanzaExcepcion() {
        Cliente cliente = mock(Cliente.class);
        List<DetalleVenta> productos = Arrays.asList(mock(DetalleVenta.class));
        Boleta boleta = new Boleta(cliente, productos, 20.0, 3.6, 23.6);
        assertDoesNotThrow(boleta::imprimir);
    }
}
