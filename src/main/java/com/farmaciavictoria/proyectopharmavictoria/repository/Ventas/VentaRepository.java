package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import java.util.List;

public interface VentaRepository {
    Venta findById(int id);

    List<Venta> findAll();

    Venta save(Venta venta);

    void update(Venta venta);

    void delete(int id);
    // Otros métodos según necesidades
}
