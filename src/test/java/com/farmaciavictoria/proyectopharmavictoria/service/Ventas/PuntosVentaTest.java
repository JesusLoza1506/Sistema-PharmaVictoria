package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PuntosVentaTest {
    @Test
    void devuelveCeroSiFaltaLaVentaOElTotal() {
        assertEquals(0, PuntosVenta.calcularGanados(null));
        Venta venta = new Venta();
        venta.setTotal(null);
        assertEquals(0, PuntosVenta.calcularGanados(venta));
    }

    @Test
    void conservaSoloLaParteEnteraDelTotal() {
        Venta venta = new Venta();
        venta.setTotal(new BigDecimal("24.99"));
        assertEquals(24, PuntosVenta.calcularGanados(venta));
    }
}
