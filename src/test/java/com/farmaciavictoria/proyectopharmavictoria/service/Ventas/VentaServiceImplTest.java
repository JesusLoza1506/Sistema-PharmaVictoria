package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VentaServiceImplTest {
    VentaRepository ventaRepo;
    DetalleVentaRepository detalleRepo;
    ComprobanteRepository comprobanteRepo;
    VentaHistorialCambioRepository historialRepo;
    ProductoRepository productoRepo;
    VentaServiceImpl service;

    @BeforeEach
    void setup() {
        ventaRepo = mock(VentaRepository.class);
        detalleRepo = mock(DetalleVentaRepository.class);
        comprobanteRepo = mock(ComprobanteRepository.class);
        historialRepo = mock(VentaHistorialCambioRepository.class);
        productoRepo = mock(ProductoRepository.class);
        service = new VentaServiceImpl(ventaRepo, detalleRepo, comprobanteRepo, historialRepo, productoRepo);
    }

    @Test
    void testObtenerVentaPorId() {
        Venta venta = mock(Venta.class);
        when(ventaRepo.findById(1)).thenReturn(venta);
        assertEquals(venta, service.obtenerVentaPorId(1));
    }

    @Test
    void testListarVentas() {
        List<Venta> ventas = Arrays.asList(mock(Venta.class));
        when(ventaRepo.findAll()).thenReturn(ventas);
        assertEquals(ventas, service.listarVentas());
    }

    @Test
    void testRegistrarVentaGuardaTodo() {
        Venta venta = mock(Venta.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        Comprobante comprobante = mock(Comprobante.class);
        when(venta.getId()).thenReturn(10);
        when(venta.getDetalles()).thenReturn(Collections.singletonList(detalle));
        when(detalle.getProducto()).thenReturn(null);
        when(venta.getComprobante()).thenReturn(comprobante);
        when(venta.getUsuario()).thenReturn(null);
        // Los métodos save/update ya son mocks, no hace falta doNothing()
        service.registrarVenta(venta);
        verify(ventaRepo).save(venta);
        verify(detalleRepo).save(detalle);
        verify(comprobanteRepo).save(comprobante);
        verify(historialRepo).save(any(VentaHistorialCambio.class));
    }

    @Test
    void testActualizarVenta() {
        Venta venta = mock(Venta.class);
        service.actualizarVenta(venta);
        verify(ventaRepo).update(venta);
    }

    @Test
    void testAnularVentaActualizaEstadoYStock() {
        Venta venta = mock(Venta.class);
        DetalleVenta detalle = mock(DetalleVenta.class);
        com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto producto = mock(
                com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto.class);
        when(producto.getId()).thenReturn(1);
        when(detalle.getProducto()).thenReturn(producto);
        when(ventaRepo.findById(5)).thenReturn(venta);
        when(venta.getDetalles()).thenReturn(Collections.singletonList(detalle));
        // Los métodos save/update ya son mocks, no hace falta doNothing()
        service.anularVenta(5, "motivo", 1);
        verify(venta).setEstado("ANULADA");
        verify(ventaRepo).update(venta);
        verify(historialRepo).save(any(VentaHistorialCambio.class));
    }
}
