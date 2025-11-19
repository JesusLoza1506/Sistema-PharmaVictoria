package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoletaBuilderTest {
    @Test
    void testConstruyeBoletaCorrectamente() {
        Cliente cliente = mock(Cliente.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        List<DetalleVenta> productos = Arrays.asList(detalle);
        Boleta boleta = new BoletaBuilder()
                .conCliente(cliente)
                .conProductos(productos)
                .conTotales(100.0, 18.0, 118.0)
                .build();
        assertEquals(cliente, boleta.getCliente());
        assertEquals(productos, boleta.getProductos());
        assertEquals(100.0, boleta.getTotal() - boleta.getIgv());
        assertEquals(18.0, boleta.getIgv());
        assertEquals(118.0, boleta.getTotal());
    }

    @Test
    void testComprobanteFactoryCreaBoleta() {
        BoletaBuilder builder = mock(BoletaBuilder.class);
        Boleta boleta = mock(Boleta.class);
        when(builder.build()).thenReturn(boleta);
        Comprobante comprobante = ComprobanteFactory.crear("BOLETA", builder);
        assertEquals(boleta, comprobante);
    }

    @Test
    void testComprobanteFactoryTipoInvalido() {
        BoletaBuilder builder = mock(BoletaBuilder.class);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            ComprobanteFactory.crear("FACTURA", builder);
        });
        assertTrue(ex.getMessage().contains("Tipo de comprobante no soportado"));
    }
}
