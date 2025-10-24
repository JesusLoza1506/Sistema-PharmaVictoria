package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import java.util.List;

public interface DetalleVentaRepository {
    DetalleVenta findById(int id);
    List<DetalleVenta> findByVentaId(int ventaId);
    void save(DetalleVenta detalleVenta);
    void update(DetalleVenta detalleVenta);
    void delete(int id);
    void deleteByVentaId(int ventaId);
    // Otros métodos según necesidades
}
