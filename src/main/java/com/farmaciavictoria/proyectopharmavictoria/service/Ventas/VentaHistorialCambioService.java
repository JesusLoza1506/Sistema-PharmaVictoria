package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import java.util.List;

public interface VentaHistorialCambioService {
    VentaHistorialCambio obtenerPorId(int id);
    List<VentaHistorialCambio> listarPorVentaId(int ventaId);
    void registrarCambio(VentaHistorialCambio historialCambio);
    void eliminarCambio(int id);
    // Otros métodos según necesidades
}
