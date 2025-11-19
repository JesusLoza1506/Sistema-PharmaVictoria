package com.farmaciavictoria.proyectopharmavictoria;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {
    @Test
    void testSetAndGetUsuarioActual() {
        Usuario usuario = new Usuario();
        SessionManager.setUsuarioActual(usuario);
        assertEquals(usuario, SessionManager.getUsuarioActual());
    }

    @Test
    void testUsuarioActualNull() {
        SessionManager.setUsuarioActual(null);
        assertNull(SessionManager.getUsuarioActual());
    }
}
