package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

public class FiltroPorEstado implements ProveedorFilterStrategy {
    @Override
    public List<Proveedor> filtrar(List<Proveedor> proveedores, String criterio) {
        if (criterio == null || criterio.isBlank()) return proveedores;
        String crit = criterio.trim().toLowerCase();
        return proveedores.stream()
                .filter(p -> {
                    boolean activo = p.getActivo() != null && p.getActivo();
                    if (crit.equals("activo")) return activo;
                    if (crit.equals("inactivo")) return !activo;
                    return true;
                })
                .collect(Collectors.toList());
    }
}
