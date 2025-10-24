package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorEstado implements UsuarioFilterStrategy {
    private final String estado;
    public FiltroPorEstado(String estado) { this.estado = estado == null ? "" : estado; }
    @Override
    public boolean matches(Usuario usuario) {
        if (estado.isEmpty()) return true;
        return estado.equals("Activo") ? usuario.isActivo() : !usuario.isActivo();
    }
}
