package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import java.util.List;

public interface VentaHistorialCambioRepository {
    VentaHistorialCambio findById(int id);
    List<VentaHistorialCambio> findByVentaId(int ventaId);
    void save(VentaHistorialCambio historialCambio);
    void delete(int id);
    // Otros métodos según necesidades
}
