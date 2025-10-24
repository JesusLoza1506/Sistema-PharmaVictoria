package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

public class FiltroPorRazonSocial implements ProveedorFilterStrategy {
    @Override
    public List<Proveedor> filtrar(List<Proveedor> proveedores, String criterio) {
        if (criterio == null || criterio.isBlank()) return proveedores;
        String crit = criterio.trim().toLowerCase();
        return proveedores.stream()
                .filter(p -> p.getRazonSocial() != null && p.getRazonSocial().toLowerCase().contains(crit))
                .collect(Collectors.toList());
    }
}
