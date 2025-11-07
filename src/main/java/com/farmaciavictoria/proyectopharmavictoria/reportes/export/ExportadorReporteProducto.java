package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.view.VentaPorProductoDTO;
import java.util.List;
import java.io.File;

public interface ExportadorReporteProducto {
    void exportar(List<VentaPorProductoDTO> datos, File destino);
}
