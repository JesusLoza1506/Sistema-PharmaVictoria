package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorSucursal implements UsuarioFilterStrategy {
    private final String sucursal;
    public FiltroPorSucursal(String sucursal) { this.sucursal = sucursal == null ? "" : sucursal; }
    @Override
    public boolean matches(Usuario usuario) {
        if (sucursal.isEmpty()) return true;
        String sucId = String.valueOf(usuario.getSucursalId());
        return sucursal.split(" ", 2)[0].equals(sucId);
    }
}
