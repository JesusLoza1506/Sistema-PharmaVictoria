package com.farmaciavictoria.proyectopharmavictoria.reportes.strategy;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.time.LocalDate;
import java.util.List;

public interface ReporteVentasStrategy {
    List<VentaReporteDTO> obtenerReporte(LocalDate fechaInicio, LocalDate fechaFin);
}
