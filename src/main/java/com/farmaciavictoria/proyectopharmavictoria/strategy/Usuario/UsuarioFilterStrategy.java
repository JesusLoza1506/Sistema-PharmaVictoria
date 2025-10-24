package com.farmaciavictoria.proyectopharmavictoria.strategy.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public interface UsuarioFilterStrategy {
    boolean matches(Usuario usuario);
}
