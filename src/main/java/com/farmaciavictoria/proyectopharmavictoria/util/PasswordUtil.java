package com.farmaciavictoria.proyectopharmavictoria.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static void main(String[] args) {
        String password = "123456"; // Cambia aquí la contraseña si lo deseas
        String hash = hashPassword(password);
        System.out.println("Hash bcrypt para '" + password + "': " + hash);
    }
}
