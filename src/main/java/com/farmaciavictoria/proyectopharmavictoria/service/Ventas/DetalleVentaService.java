package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import java.util.List;

public interface DetalleVentaService {
    DetalleVenta obtenerPorId(int id);
    List<DetalleVenta> listarPorVentaId(int ventaId);
    void registrarDetalle(DetalleVenta detalleVenta);
    void actualizarDetalle(DetalleVenta detalleVenta);
    void eliminarDetalle(int id);
    // Otros métodos según necesidades
}
