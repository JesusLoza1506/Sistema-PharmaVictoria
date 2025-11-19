package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductoDetalleDTOTest {
    @Test
    void testGettersAndSetters() {
        ProductoDetalleDTO dto = new ProductoDetalleDTO();
        dto.setIdProducto(101);
        dto.setNombre("Paracetamol");
        dto.setCantidad(2);
        dto.setPrecioUnitario(BigDecimal.valueOf(5));
        dto.setDescuento(BigDecimal.valueOf(1));
        dto.setSubtotal(BigDecimal.valueOf(9));
        assertEquals(101, dto.getIdProducto());
        assertEquals("Paracetamol", dto.getNombre());
        assertEquals(2, dto.getCantidad());
        assertEquals(BigDecimal.valueOf(5), dto.getPrecioUnitario());
        assertEquals(BigDecimal.valueOf(1), dto.getDescuento());
        assertEquals(BigDecimal.valueOf(9), dto.getSubtotal());
    }
}
