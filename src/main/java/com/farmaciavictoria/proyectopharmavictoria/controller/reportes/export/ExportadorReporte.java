package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;

import java.io.File;

public interface ExportadorReporte {
    void exportar(List<VentaReporteDTO> datos, File destino);
}
