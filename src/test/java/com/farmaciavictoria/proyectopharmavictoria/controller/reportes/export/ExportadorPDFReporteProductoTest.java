package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorPDFReporteProductoTest {
    @Test
    void exportar_GeneraArchivoConDatos() {
        ExportadorPDFReporteProducto exportador = new ExportadorPDFReporteProducto();
        VentaPorProductoDTO dto = new VentaPorProductoDTO("Ibuprofeno", 10, 55.0);
        List<VentaPorProductoDTO> datos = Collections.singletonList(dto);
        File destino = new File("test_producto.pdf");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_GeneraArchivoSinDatos() {
        ExportadorPDFReporteProducto exportador = new ExportadorPDFReporteProducto();
        List<VentaPorProductoDTO> datos = new ArrayList<>();
        File destino = new File("test_producto_vacio.pdf");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_ManejaExcepcionArchivoInvalido() {
        ExportadorPDFReporteProducto exportador = new ExportadorPDFReporteProducto();
        List<VentaPorProductoDTO> datos = new ArrayList<>();
        File destino = new File(""); // nombre inválido
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
    }
}
