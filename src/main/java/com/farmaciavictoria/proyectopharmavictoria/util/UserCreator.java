package com.farmaciavictoria.proyectopharmavictoria.util;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Creador de Usuarios para PHARMAVICTORIA
 * 
 * Utilidad interactiva para crear usuarios directamente en la base de datos
 * con contraseñas personalizadas y verificación completa.
 * 
 * @author PHARMAVICTORIA Development Team
 */
public class UserCreator {
    
    public static void main(String[] args) {
        System.out.println("=== PHARMAVICTORIA - CREADOR DE USUARIOS ===");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Solicitar datos del usuario
            System.out.print("👤 Nombre de usuario: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("🔑 Contraseña: ");
            String password = scanner.nextLine().trim();
            
            System.out.print("🏷️ Rol (ADMIN/VENDEDOR): ");
            String rol = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("👨‍💼 Nombres: ");
            String nombres = scanner.nextLine().trim();
            
            System.out.print("👨‍💼 Apellidos: ");
            String apellidos = scanner.nextLine().trim();
            
            System.out.print("📧 Email: ");
            String email = scanner.nextLine().trim();
            
            // Validaciones básicas
            if (username.isEmpty() || password.isEmpty() || nombres.isEmpty()) {
                System.out.println("❌ Error: Campos obligatorios vacíos");
                return;
            }
            
            if (!rol.equals("ADMIN") && !rol.equals("VENDEDOR")) {
                System.out.println("❌ Error: Rol debe ser ADMIN o VENDEDOR");
                return;
            }
            
            // Generar hash BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            
            System.out.println();
            System.out.println("🔐 Generando hash BCrypt...");
            
            // Conectar a la base de datos
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            
            try (Connection conn = dbConfig.getConnection()) {
                
                // Verificar si el usuario ya existe
                String checkSQL = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                    checkStmt.setString(1, username);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("⚠️ Usuario '" + username + "' ya existe. ¿Actualizar? (s/n): ");
                            String response = scanner.nextLine().trim().toLowerCase();
                            if (!response.equals("s") && !response.equals("si")) {
                                System.out.println("❌ Operación cancelada");
                                return;
                            }
                            
                            // Actualizar usuario existente
                            String updateSQL = """
                                UPDATE usuarios SET 
                                    password_hash = ?, rol = ?, nombres = ?, apellidos = ?, email = ?
                                WHERE username = ?
                                """;
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                                updateStmt.setString(1, hashedPassword);
                                updateStmt.setString(2, rol);
                                updateStmt.setString(3, nombres);
                                updateStmt.setString(4, apellidos);
                                updateStmt.setString(5, email);
                                updateStmt.setString(6, username);
                                
                                int updated = updateStmt.executeUpdate();
                                System.out.println("✅ Usuario actualizado: " + updated);
                            }
                        } else {
                            // Crear nuevo usuario
                            String insertSQL = """
                                INSERT INTO usuarios (
                                    username, password_hash, rol, sucursal_id, nombres, apellidos, 
                                    dni, telefono, email, activo, created_at
                                ) VALUES (?, ?, ?, 1, ?, ?, '00000000', '000000000', ?, true, NOW())
                                """;
                            
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                                insertStmt.setString(1, username);
                                insertStmt.setString(2, hashedPassword);
                                insertStmt.setString(3, rol);
                                insertStmt.setString(4, nombres);
                                insertStmt.setString(5, apellidos);
                                insertStmt.setString(6, email);
                                
                                int inserted = insertStmt.executeUpdate();
                                System.out.println("✅ Usuario creado: " + inserted);
                            }
                        }
                    }
                }
                
                // Verificar autenticación
                System.out.println();
                System.out.println("🔐 VERIFICANDO AUTENTICACIÓN...");
                boolean isValid = BCrypt.checkpw(password, hashedPassword);
                System.out.println("   BCrypt check: " + (isValid ? "✅ EXITOSO" : "❌ FALLIDO"));
                
                System.out.println();
                System.out.println("🎯 CREDENCIALES PARA LOGIN:");
                System.out.println("   Usuario: " + username);
                System.out.println("   Contraseña: " + password);
                System.out.println("   Rol: " + rol);
                
                System.out.println();
                System.out.println("✅ ¡Usuario listo para usar en PHARMAVICTORIA!");
                
            } catch (SQLException e) {
                System.err.println("❌ Error SQL: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}