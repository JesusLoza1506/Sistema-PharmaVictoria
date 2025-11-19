package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas.command;

import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import static org.mockito.Mockito.*;

class RestablecerStockCommandTest {
    @Test
    void testRestableceStockCorrectamente() {
        ProductoRepository repo = mock(ProductoRepository.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        Producto producto = mock(Producto.class);
        when(detalle.getProducto()).thenReturn(producto);
        when(detalle.getCantidad()).thenReturn(5);
        when(producto.getId()).thenReturn(10);
        when(producto.getStockActual()).thenReturn(20);
        when(repo.findById(10L)).thenReturn(Optional.of(producto));
        RestablecerStockCommand cmd = new RestablecerStockCommand(repo, detalle);
        cmd.execute();
        verify(repo).updateStock(10, 25); // 20 + 5
    }

    @Test
    void testNoHaceNadaSiProductoNoExiste() {
        ProductoRepository repo = mock(ProductoRepository.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        Producto producto = mock(Producto.class);
        when(detalle.getProducto()).thenReturn(producto);
        when(producto.getId()).thenReturn(99);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        RestablecerStockCommand cmd = new RestablecerStockCommand(repo, detalle);
        cmd.execute();
        verify(repo, never()).updateStock(anyInt(), anyInt());
    }

    @Test
    void testStockActualNuloSeTomaComoCero() {
        ProductoRepository repo = mock(ProductoRepository.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        Producto producto = mock(Producto.class);
        when(detalle.getProducto()).thenReturn(producto);
        when(detalle.getCantidad()).thenReturn(7);
        when(producto.getId()).thenReturn(5);
        when(producto.getStockActual()).thenReturn(null);
        when(repo.findById(5L)).thenReturn(Optional.of(producto));
        RestablecerStockCommand cmd = new RestablecerStockCommand(repo, detalle);
        cmd.execute();
        verify(repo).updateStock(5, 7); // 0 + 7
    }
}
