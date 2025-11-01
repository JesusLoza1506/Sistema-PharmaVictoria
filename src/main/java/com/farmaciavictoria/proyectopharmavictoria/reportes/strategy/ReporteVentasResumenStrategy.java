package com.farmaciavictoria.proyectopharmavictoria.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ReporteVentasResumenStrategy implements ReporteVentasStrategy {
    @Override
    public List<VentaReporteDTO> obtenerReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        // Aquí iría la lógica para consultar la vista vw_resumen_ventas y mapear a
        // VentaReporteDTO
        // Simulación de datos
        return new ArrayList<>();
    }
}
