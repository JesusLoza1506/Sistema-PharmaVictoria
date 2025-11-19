package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReporteVentasResumenStrategyTest {
    @Test
    void obtenerReporteRetornaListaVacia() {
        ReporteVentasResumenStrategy strategy = new ReporteVentasResumenStrategy();
        List<VentaReporteDTO> resultado = strategy.obtenerReporte(LocalDate.now(), LocalDate.now());
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
