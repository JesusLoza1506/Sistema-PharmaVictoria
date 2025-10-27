package com.farmaciavictoria.proyectopharmavictoria;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class SessionManager {
    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
}
