package com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

public interface ClienteFilterStrategy {
    List<Cliente> filtrar(List<Cliente> clientes, String criterio);
}
