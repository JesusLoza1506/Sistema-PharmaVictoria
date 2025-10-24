package com.farmaciavictoria.proyectopharmavictoria.strategy.Proveedor;

import java.util.List;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

public interface ProveedorFilterStrategy {
    List<Proveedor> filtrar(List<Proveedor> proveedores, String criterio);
}
