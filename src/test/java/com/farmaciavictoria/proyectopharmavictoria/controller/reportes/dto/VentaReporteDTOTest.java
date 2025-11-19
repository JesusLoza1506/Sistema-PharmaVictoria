package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VentaReporteDTOTest {
    @Test
    void testGettersAndSetters() {
        VentaReporteDTO dto = new VentaReporteDTO();
        dto.setIdVenta(1);
        dto.setNumeroBoleta("B001-0001");
        dto.setSerie("B001");
        LocalDateTime fecha = LocalDateTime.now();
        dto.setFechaVenta(fecha);
        dto.setCliente("Juan Perez");
        dto.setVendedor("Ana Lopez");
        dto.setSubtotal(BigDecimal.valueOf(100));
        dto.setDescuento(BigDecimal.valueOf(10));
        dto.setTotal(BigDecimal.valueOf(90));
        dto.setTipoPago("EFECTIVO");
        dto.setEstado("REALIZADA");
        ProductoDetalleDTO prod1 = new ProductoDetalleDTO();
        prod1.setIdProducto(101);
        prod1.setNombre("Paracetamol");
        prod1.setCantidad(2);
        prod1.setPrecioUnitario(BigDecimal.valueOf(5));
        prod1.setDescuento(BigDecimal.valueOf(0));
        prod1.setSubtotal(BigDecimal.valueOf(10));
        ProductoDetalleDTO prod2 = new ProductoDetalleDTO();
        prod2.setIdProducto(102);
        prod2.setNombre("Ibuprofeno");
        prod2.setCantidad(1);
        prod2.setPrecioUnitario(BigDecimal.valueOf(8));
        prod2.setDescuento(BigDecimal.valueOf(0));
        prod2.setSubtotal(BigDecimal.valueOf(8));
        List<ProductoDetalleDTO> productos = Arrays.asList(prod1, prod2);
        dto.setProductos(productos);
        assertEquals(1, dto.getIdVenta());
        assertEquals("B001-0001", dto.getNumeroBoleta());
        assertEquals("B001", dto.getSerie());
        assertEquals(fecha, dto.getFechaVenta());
        assertEquals("Juan Perez", dto.getCliente());
        assertEquals("Ana Lopez", dto.getVendedor());
        assertEquals(BigDecimal.valueOf(100), dto.getSubtotal());
        assertEquals(BigDecimal.valueOf(10), dto.getDescuento());
        assertEquals(BigDecimal.valueOf(90), dto.getTotal());
        assertEquals("EFECTIVO", dto.getTipoPago());
        assertEquals("REALIZADA", dto.getEstado());
        assertEquals(productos, dto.getProductos());
    }
}
