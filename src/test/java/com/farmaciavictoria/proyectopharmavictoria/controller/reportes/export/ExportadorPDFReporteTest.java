package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.VentaReporteDTO;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto.ProductoDetalleDTO;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorPDFReporteTest {
    @Test
    void exportar_GeneraArchivoSinProductos() {
        ExportadorPDFReporte exportador = new ExportadorPDFReporte();
        VentaReporteDTO venta = new VentaReporteDTO();
        venta.setNumeroBoleta("B001");
        venta.setSerie("S01");
        venta.setCliente("Cliente Prueba");
        venta.setVendedor("Vendedor Prueba");
        venta.setEstado("ACTIVA");
        venta.setProductos(Collections.emptyList());
        List<VentaReporteDTO> datos = Collections.singletonList(venta);
        File destino = new File("test_sin_productos.pdf");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_GeneraArchivoConProductos() {
        ExportadorPDFReporte exportador = new ExportadorPDFReporte();
        VentaReporteDTO venta = new VentaReporteDTO();
        venta.setNumeroBoleta("B002");
        venta.setSerie("S02");
        venta.setCliente("Cliente Prueba");
        venta.setVendedor("Vendedor Prueba");
        venta.setEstado("ACTIVA");
        ProductoDetalleDTO prod = new ProductoDetalleDTO();
        prod.setNombre("Paracetamol");
        prod.setCantidad(2);
        prod.setPrecioUnitario(java.math.BigDecimal.valueOf(5.5));
        prod.setDescuento(java.math.BigDecimal.ZERO);
        prod.setSubtotal(java.math.BigDecimal.valueOf(11));
        List<ProductoDetalleDTO> productos = new ArrayList<>();
        productos.add(prod);
        venta.setProductos(productos);
        List<VentaReporteDTO> datos = Collections.singletonList(venta);
        File destino = new File("test_con_productos.pdf");
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
        assertTrue(destino.exists() && destino.length() > 0);
        destino.delete();
    }

    @Test
    void exportar_ManejaExcepcionArchivoInvalido() {
        ExportadorPDFReporte exportador = new ExportadorPDFReporte();
        VentaReporteDTO venta = new VentaReporteDTO();
        venta.setNumeroBoleta("B003");
        venta.setSerie("S03");
        venta.setCliente("Cliente Prueba");
        venta.setVendedor("Vendedor Prueba");
        venta.setEstado("ACTIVA");
        venta.setProductos(Collections.emptyList());
        List<VentaReporteDTO> datos = Collections.singletonList(venta);
        File destino = new File(""); // nombre inválido
        assertDoesNotThrow(() -> exportador.exportar(datos, destino));
    }
}
