package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.time.format.DateTimeFormatter;

public class SearchByVencimientoStrategy implements ProductSearchStrategy {
    @Override
    public List<Producto> buscar(List<Producto> productos, String filtro) {
        if (filtro == null || filtro.isEmpty()) return productos;
        String f = filtro.toLowerCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return productos.stream()
            .filter(p -> p.getFechaVencimiento() != null &&
                p.getFechaVencimiento().format(formatter).toLowerCase().contains(f))
            .collect(Collectors.toList());
    }
}
