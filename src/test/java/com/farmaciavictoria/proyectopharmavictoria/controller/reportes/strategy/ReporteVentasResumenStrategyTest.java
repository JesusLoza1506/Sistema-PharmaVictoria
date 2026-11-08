package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReporteVentasResumenStrategyTest {
    @Test
    void obtenerReporteRetornaListaVacia() {
        ReporteVentasResumenStrategy strategy = new ReporteVentasResumenStrategy();
        LocalDate fecha = LocalDate.of(2025, 11, 16);

        List<VentaReporteDTO> resultado = strategy.obtenerReporte(
                fecha.atStartOfDay(), fecha.atTime(23, 59, 59));

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
