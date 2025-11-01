package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import java.io.File;

public abstract class ExportadorReporteFactory {
    public abstract ExportadorReporte crearExportador();

    public static ExportadorReporte getExportador(String tipo, ReporteTipo reporteTipo) {
        // Solo implementa para VENTAS, PDF y Excel
        if (reporteTipo == ReporteTipo.VENTAS) {
            if ("PDF".equalsIgnoreCase(tipo)) {
                return new ExportadorPDFReporte();
            } else if ("EXCEL".equalsIgnoreCase(tipo)) {
                return new ExportadorExcelReporte();
            }
        }
        throw new IllegalArgumentException("Tipo de exportador o reporte no soportado: " + tipo + " - " + reporteTipo);
    }
}
