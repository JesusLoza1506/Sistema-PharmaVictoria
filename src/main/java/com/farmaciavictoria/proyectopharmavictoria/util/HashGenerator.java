package com.farmaciavictoria.proyectopharmavictoria.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Generador de Hashes BCrypt para PHARMAVICTORIA
 * 
 * Utilidad para generar hashes seguros de contraseñas
 * que son 100% compatibles con el sistema de autenticación.
 * 
 * USO:
 * 1. Cambiar la variable 'plainPassword' por tu contraseña deseada
 * 2. Ejecutar esta clase desde VS Code (Run Java)
 * 3. Copiar el hash generado para usar en la base de datos
 * 
 * @author PHARMAVICTORIA Development Team
 */
public class HashGenerator {
    
    public static void main(String[] args) {
        
        // 🔑 CAMBIA AQUÍ TU CONTRASEÑA PERSONALIZADA
        String plainPassword = "123456";  // ← Modifica esta contraseña
        
        System.out.println("=== PHARMAVICTORIA - GENERADOR DE HASH BCRYPT ===");
        System.out.println();
        
        // Generar hash con BCrypt (cost factor 10 = mismo que el sistema)
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
        
        System.out.println("🔐 CONTRASEÑA ORIGINAL:");
        System.out.println("   " + plainPassword);
        System.out.println();
        
        System.out.println("🔒 HASH BCRYPT GENERADO:");
        System.out.println("   " + hashedPassword);
        System.out.println();
        
        // Verificar que el hash funciona correctamente
        boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("✅ VERIFICACIÓN:");
        System.out.println("   " + (isValid ? "Hash válido y funcional" : "ERROR: Hash inválido"));
        System.out.println();
        
        System.out.println("📋 PARA USAR EN SQL:");
        System.out.println("   UPDATE usuarios SET password_hash = '" + hashedPassword + "' WHERE username = 'admin';");
        System.out.println();
        
        System.out.println("💡 INSTRUCCIONES:");
        System.out.println("   1. Copia el hash generado arriba");
        System.out.println("   2. Úsalo en tu INSERT o UPDATE SQL");
        System.out.println("   3. El hash es compatible al 100% con PHARMAVICTORIA");
        
        // Ejemplos con diferentes contraseñas
        System.out.println();
        System.out.println("🎯 EJEMPLOS DE OTROS HASHES:");
        
        String[] passwords = {"123456", "password", "vendedor123", "farmacia2024"};
        for (String pwd : passwords) {
            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt(10));
            System.out.println("   " + pwd + " → " + hash.substring(0, 25) + "...");
        }
    }
}