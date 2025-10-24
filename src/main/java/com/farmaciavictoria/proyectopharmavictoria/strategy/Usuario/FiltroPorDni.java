package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class FiltroPorDni implements UsuarioFilterStrategy {
    private final String dni;
    public FiltroPorDni(String dni) { this.dni = dni == null ? "" : dni.toLowerCase(); }
    @Override
    public boolean matches(Usuario usuario) {
        if (dni.isEmpty()) return true;
        return usuario.getDni() != null && usuario.getDni().toLowerCase().contains(dni);
    }
}
