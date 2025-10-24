package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import java.util.List;

public interface ComprobanteRepository {
    Comprobante findById(int id);
    List<Comprobante> findByVentaId(int ventaId);
    void save(Comprobante comprobante);
    void update(Comprobante comprobante);
    void delete(int id);
    // Otros métodos según necesidades
}
