package com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente;

import java.util.List;
import java.util.stream.Collectors;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

public class FiltroPorNombreRazonSocial implements ClienteFilterStrategy {
    @Override
    public List<Cliente> filtrar(List<Cliente> clientes, String criterio) {
        if (criterio == null || criterio.isBlank())
            return clientes;
        String crit = criterio.trim().toLowerCase();
        return clientes.stream()
                .filter(c -> (c.getNombres() != null && c.getNombres().toLowerCase().contains(crit)) ||
                        (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(crit)))
                .collect(Collectors.toList());
    }
}
