package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalesCarritoTest {
    @Test
    void calculaTotalesDeUnCarritoVacio() {
        assertEquals(
                new TotalesCarrito(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.00"), new BigDecimal("0.00")),
                TotalesCarrito.calcular(List.of()));
    }

    @Test
    void calculaIgvSobreElSubtotalYAplicaElDescuentoAlTotal() {
        TotalesCarrito totales = TotalesCarrito.calcular(List.of(
                detalle("10.00", "1.00"),
                detalle("2.50", "0.25")));

        assertEquals(new BigDecimal("12.50"), totales.subtotal());
        assertEquals(new BigDecimal("1.25"), totales.descuento());
        assertEquals(new BigDecimal("2.2500"), totales.igv());
        assertEquals(new BigDecimal("13.5000"), totales.total());
    }

    @Test
    void omiteSubtotalYDescuentoNulosDeFormaIndependiente() {
        TotalesCarrito totales = TotalesCarrito.calcular(List.of(
                detalle(null, "0.30"),
                detalle("2.00", null),
                detalle(null, null)));

        assertEquals(new BigDecimal("2.00"), totales.subtotal());
        assertEquals(new BigDecimal("0.30"), totales.descuento());
        assertEquals(new BigDecimal("0.3600"), totales.igv());
        assertEquals(new BigDecimal("2.0600"), totales.total());
    }

    @Test
    void conservaLaPrecisionAntesDeFormatearLosImportes() {
        TotalesCarrito totales = TotalesCarrito.calcular(List.of(detalle("0.01", "0.00")));

        assertEquals(new BigDecimal("0.0018"), totales.igv());
        assertEquals(new BigDecimal("0.0118"), totales.total());
    }

    private DetalleVenta detalle(String subtotal, String descuento) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setSubtotal(subtotal == null ? null : new BigDecimal(subtotal));
        detalle.setDescuento(descuento == null ? null : new BigDecimal(descuento));
        return detalle;
    }
}
