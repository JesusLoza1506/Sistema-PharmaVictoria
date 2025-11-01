package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.util.List;
import java.io.File;

public interface ExportadorReporte {
    void exportar(List<VentaReporteDTO> datos, File destino);
}
