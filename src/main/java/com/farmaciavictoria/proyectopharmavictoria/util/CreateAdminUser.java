package com.farmaciavictoria.proyectopharmavictoria.util;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Utilidad para crear usuarios administradores
 * Garantiza compatibilidad total con BCrypt del sistema
 */
public class CreateAdminUser {
    
    public static void main(String[] args) {
        System.out.println("=== PHARMAVICTORIA - CREAR USUARIO ADMIN ===");
        
        try {
            // Generar hash BCrypt exactamente como el sistema
            String password = "admin123";
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            
            System.out.println("✅ Hash BCrypt generado: " + hashedPassword.substring(0, 20) + "...");
            
            // Conectar a la base de datos
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            
            try (Connection conn = dbConfig.getConnection()) {
                
                // Eliminar usuario admin si existe
                String deleteSQL = "DELETE FROM usuarios WHERE username = 'admin'";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
                    int deleted = deleteStmt.executeUpdate();
                    System.out.println("🗑️ Usuarios eliminados: " + deleted);
                }
                
                // Crear nuevo usuario admin
                String insertSQL = """
                    INSERT INTO usuarios (
                        username, password_hash, rol, sucursal_id, nombres, apellidos, 
                        dni, telefono, email, activo, created_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                    """;
                
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, hashedPassword);
                    insertStmt.setString(3, "ADMIN");
                    insertStmt.setLong(4, 1);
                    insertStmt.setString(5, "Administrador");
                    insertStmt.setString(6, "Sistema");
                    insertStmt.setString(7, "12345678");
                    insertStmt.setString(8, "999888777");
                    insertStmt.setString(9, "admin@pharmavictoria.com");
                    insertStmt.setBoolean(10, true);
                    
                    int inserted = insertStmt.executeUpdate();
                    System.out.println("👤 Usuario admin creado: " + inserted);
                }
                
                // Verificar autenticación
                System.out.println("\n🔐 VERIFICANDO AUTENTICACIÓN...");
                boolean isValid = BCrypt.checkpw(password, hashedPassword);
                System.out.println("   BCrypt check: " + (isValid ? "✅ EXITOSO" : "❌ FALLIDO"));
                
                System.out.println("\n🎯 CREDENCIALES PARA LOGIN:");
                System.out.println("   Usuario: admin");
                System.out.println("   Contraseña: admin123");
                System.out.println("\n✅ ¡Usuario administrador listo para usar!");
                
            } catch (SQLException e) {
                System.err.println("❌ Error SQL: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}