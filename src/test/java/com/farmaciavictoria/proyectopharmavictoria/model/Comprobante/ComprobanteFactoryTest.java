package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComprobanteFactoryTest {
    @Test
    void testCrearBoletaRetornaBoleta() {
        BoletaBuilder builder = mock(BoletaBuilder.class);
        Boleta boleta = mock(Boleta.class);
        when(builder.build()).thenReturn(boleta);
        Comprobante comprobante = ComprobanteFactory.crear("BOLETA", builder);
        assertEquals(boleta, comprobante);
    }

    @Test
    void testCrearTipoNoSoportadoLanzaExcepcion() {
        BoletaBuilder builder = mock(BoletaBuilder.class);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            ComprobanteFactory.crear("FACTURA", builder);
        });
        assertTrue(ex.getMessage().contains("Tipo de comprobante no soportado"));
    }
}
