package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorApellido implements UsuarioFilterStrategy {
    private final String apellido;
    public FiltroPorApellido(String apellido) { this.apellido = apellido == null ? "" : apellido.toLowerCase(); }
    @Override
    public boolean matches(Usuario usuario) {
        if (apellido.isEmpty()) return true;
        return usuario.getApellidos() != null && usuario.getApellidos().toLowerCase().contains(apellido);
    }
}
