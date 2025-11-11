package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;

import java.io.File;

public interface ExportadorReporteProducto {
    void exportar(List<VentaPorProductoDTO> datos, File destino);
}
