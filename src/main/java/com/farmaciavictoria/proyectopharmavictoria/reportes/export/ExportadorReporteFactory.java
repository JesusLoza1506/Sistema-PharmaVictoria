package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import java.io.File;

public abstract class ExportadorReporteFactory {
    public abstract ExportadorReporte crearExportador();

    public static ExportadorReporte getExportador(String tipo, ReporteTipo reporteTipo) {
        // Soporta VENTAS y VENTAS_PRODUCTO, PDF y Excel
        if (reporteTipo == ReporteTipo.VENTAS || reporteTipo == ReporteTipo.VENTAS_PRODUCTO) {
            if ("PDF".equalsIgnoreCase(tipo)) {
                return new ExportadorPDFReporte();
            } else if ("EXCEL".equalsIgnoreCase(tipo)) {
                return new ExportadorExcelReporte();
            }
        }
        throw new IllegalArgumentException("Tipo de exportador o reporte no soportado: " + tipo + " - " + reporteTipo);
    }

    public static Object getExportadorProducto(String tipo) {
        if ("PDF".equalsIgnoreCase(tipo)) {
            return new ExportadorPDFReporteProducto();
        } else if ("EXCEL".equalsIgnoreCase(tipo)) {
            return new ExportadorExcelReporteProducto();
        }
        throw new IllegalArgumentException("Tipo de exportador no soportado: " + tipo);
    }
}
