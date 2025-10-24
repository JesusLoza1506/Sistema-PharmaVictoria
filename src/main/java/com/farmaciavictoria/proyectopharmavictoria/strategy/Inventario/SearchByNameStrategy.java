package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

public class SearchByNameStrategy implements ProductSearchStrategy {
    @Override
    public List<Producto> buscar(List<Producto> productos, String filtro) {
        if (filtro == null || filtro.isEmpty()) return productos;
        String f = filtro.toLowerCase();
        return productos.stream()
            .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(f))
            .collect(Collectors.toList());
    }
}
