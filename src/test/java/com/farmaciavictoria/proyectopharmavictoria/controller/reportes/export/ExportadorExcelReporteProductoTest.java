package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorExcelReporteProductoTest {
    @Test
    void exportar_GeneraArchivoConDatos() {
        ExportadorExcelReporteProducto exportador = new ExportadorExcelReporteProducto();
        VentaPorProductoDTO dto = new VentaPorProductoDTO("Ibuprofeno", 10, 55.0);
        List<VentaPorProductoDTO> datos = Collections.singletonList(dto);
        File destino = new File("test_producto.xlsx");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_GeneraArchivoSinDatos() {
        ExportadorExcelReporteProducto exportador = new ExportadorExcelReporteProducto();
        List<VentaPorProductoDTO> datos = new ArrayList<>();
        File destino = new File("test_producto_vacio.xlsx");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_ManejaExcepcionArchivoInvalido() {
        ExportadorExcelReporteProducto exportador = new ExportadorExcelReporteProducto();
        List<VentaPorProductoDTO> datos = new ArrayList<>();
        File destino = new File(""); // nombre inválido
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
    }
}
