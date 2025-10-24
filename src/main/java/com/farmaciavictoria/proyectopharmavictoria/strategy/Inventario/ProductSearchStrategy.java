package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

public interface ProductSearchStrategy {
    List<Producto> buscar(List<Producto> productos, String filtro);
}
