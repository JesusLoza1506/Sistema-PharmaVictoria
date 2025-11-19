package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExportadorReporteFactoryTest {
    @Test
    void getExportadorPDFVentas() {
        ExportadorReporte exportador = ExportadorReporteFactory.getExportador("PDF", ReporteTipo.VENTAS);
        assertTrue(exportador instanceof ExportadorPDFReporte);
    }

    @Test
    void getExportadorExcelVentas() {
        ExportadorReporte exportador = ExportadorReporteFactory.getExportador("EXCEL", ReporteTipo.VENTAS);
        assertTrue(exportador instanceof ExportadorExcelReporte);
    }

    @Test
    void getExportadorPDFVentasProducto() {
        ExportadorReporte exportador = ExportadorReporteFactory.getExportador("PDF", ReporteTipo.VENTAS_PRODUCTO);
        assertTrue(exportador instanceof ExportadorPDFReporte);
    }

    @Test
    void getExportadorExcelVentasProducto() {
        ExportadorReporte exportador = ExportadorReporteFactory.getExportador("EXCEL", ReporteTipo.VENTAS_PRODUCTO);
        assertTrue(exportador instanceof ExportadorExcelReporte);
    }

    @Test
    void getExportadorProductoPDF() {
        Object exportador = ExportadorReporteFactory.getExportadorProducto("PDF");
        assertTrue(exportador instanceof ExportadorPDFReporteProducto);
    }

    @Test
    void getExportadorProductoExcel() {
        Object exportador = ExportadorReporteFactory.getExportadorProducto("EXCEL");
        assertTrue(exportador instanceof ExportadorExcelReporteProducto);
    }

    @Test
    void getExportadorTipoNoSoportado() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ExportadorReporteFactory.getExportador("WORD", ReporteTipo.VENTAS));
        assertTrue(ex.getMessage().contains("no soportado"));
    }

    @Test
    void getExportadorReporteNoSoportado() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ExportadorReporteFactory.getExportador("PDF", ReporteTipo.CLIENTES));
        assertTrue(ex.getMessage().contains("no soportado"));
    }

    @Test
    void getExportadorProductoTipoNoSoportado() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ExportadorReporteFactory.getExportadorProducto("WORD"));
        assertTrue(ex.getMessage().contains("no soportado"));
    }
}
