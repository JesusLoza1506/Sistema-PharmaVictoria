package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorRol implements UsuarioFilterStrategy {
    private final String rol;
    public FiltroPorRol(String rol) { this.rol = rol == null ? "" : rol; }
    @Override
    public boolean matches(Usuario usuario) {
        if (rol.isEmpty()) return true;
        return usuario.getRol() != null && usuario.getRol().name().equalsIgnoreCase(rol);
    }
}
