package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorNombre implements UsuarioFilterStrategy {
    private final String nombre;
    public FiltroPorNombre(String nombre) { this.nombre = nombre == null ? "" : nombre.toLowerCase(); }
    @Override
    public boolean matches(Usuario usuario) {
        if (nombre.isEmpty()) return true;
        return usuario.getNombres() != null && usuario.getNombres().toLowerCase().contains(nombre);
    }
}
