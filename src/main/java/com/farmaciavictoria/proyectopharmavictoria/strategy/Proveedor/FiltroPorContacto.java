package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

public class FiltroPorContacto implements ProveedorFilterStrategy {
    @Override
    public List<Proveedor> filtrar(List<Proveedor> proveedores, String criterio) {
        if (criterio == null || criterio.isBlank()) return proveedores;
        String crit = criterio.trim().toLowerCase();
        return proveedores.stream()
                .filter(p -> p.getContacto() != null && p.getContacto().toLowerCase().contains(crit))
                .collect(Collectors.toList());
    }
}
