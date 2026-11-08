package com.farmaciavictoria.proyectopharmavictoria.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static void main(String[] args) {
        java.io.Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Ejecuta el generador de hash desde una terminal interactiva.");
        }
        char[] password = console.readPassword("Contraseña: ");
        if (password == null) {
            return;
        }
        try {
            System.out.println(hashPassword(new String(password)));
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
