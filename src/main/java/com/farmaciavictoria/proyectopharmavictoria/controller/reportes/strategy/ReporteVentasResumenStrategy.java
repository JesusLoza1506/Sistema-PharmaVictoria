package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.strategy;

import java.time.LocalDate;
import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;

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
