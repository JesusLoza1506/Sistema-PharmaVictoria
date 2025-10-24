package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

public class FiltroPorRuc implements ProveedorFilterStrategy {
    @Override
    public List<Proveedor> filtrar(List<Proveedor> proveedores, String criterio) {
        if (criterio == null || criterio.isBlank()) return proveedores;
        String crit = criterio.trim();
        return proveedores.stream()
                .filter(p -> p.getRuc() != null && p.getRuc().contains(crit))
                .collect(Collectors.toList());
    }
}
