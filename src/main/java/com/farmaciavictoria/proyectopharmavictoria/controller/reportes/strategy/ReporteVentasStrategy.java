package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import java.time.LocalDate;
import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;

public interface ReporteVentasStrategy {
    List<VentaReporteDTO> obtenerReporte(LocalDate fechaInicio, LocalDate fechaFin);
}
