package com.farmaciavictoria.proyectopharmavictoria.util;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utilidad para actualizar la estructura de la base de datos
 */
public class DatabaseUpdater {
    
    public static void updateProductsTable() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verificar si las columnas ya existen
            boolean laboratorioExists = false;
            boolean fechaFabricacionExists = false;
            
            try (var rs = stmt.executeQuery("SHOW COLUMNS FROM productos LIKE 'laboratorio'")) {
                laboratorioExists = rs.next();
            }
            
            try (var rs = stmt.executeQuery("SHOW COLUMNS FROM productos LIKE 'fecha_fabricacion'")) {
                fechaFabricacionExists = rs.next();
            }
            
            // Agregar columnas si no existen
            if (!laboratorioExists) {
                stmt.executeUpdate("ALTER TABLE productos ADD COLUMN laboratorio VARCHAR(100) DEFAULT ''");
                System.out.println("✅ Columna 'laboratorio' agregada exitosamente");
            } else {
                System.out.println("ℹ️ Columna 'laboratorio' ya existe");
            }
            
            if (!fechaFabricacionExists) {
                stmt.executeUpdate("ALTER TABLE productos ADD COLUMN fecha_fabricacion DATE DEFAULT NULL");
                System.out.println("✅ Columna 'fecha_fabricacion' agregada exitosamente");
            } else {
                System.out.println("ℹ️ Columna 'fecha_fabricacion' ya existe");
            }
            
            System.out.println("🎉 Actualización de base de datos completada");
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea las tablas de historial de usuarios si no existen
     */
    public static void ensureUsuarioHistorialTables() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            // Crear tabla de accesos
            String sqlAccesos = "CREATE TABLE IF NOT EXISTS usuario_historial_acceso (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "usuario_id BIGINT NOT NULL, " +
                    "fecha_acceso TIMESTAMP NOT NULL, " +
                    "ip_address VARCHAR(100), " +
                    "user_agent VARCHAR(255), " +
                    "exito BOOLEAN, " +
                    "motivo_fallo TEXT" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(sqlAccesos);

            // Crear tabla de cambios/auditoría
            String sqlCambios = "CREATE TABLE IF NOT EXISTS usuario_historial_cambio (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "usuario_id INT NOT NULL, " +
                    "campo_modificado VARCHAR(100), " +
                    "valor_anterior TEXT, " +
                    "valor_nuevo TEXT, " +
                    "modificado_por INT NULL, " +
                    "fecha TIMESTAMP NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(sqlCambios);

            System.out.println("✅ Tablas de historial de usuario aseguradas (creadas si no existían)");
        } catch (Exception e) {
            System.err.println("❌ Error al crear tablas de historial: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Actualizar tabla proveedores para hacer RUC opcional
     */
    public static void updateProveedoresTable() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verificar si RUC ya permite NULL
            boolean rucAllowsNull = false;
            
            try (var rs = stmt.executeQuery("SHOW COLUMNS FROM proveedores LIKE 'ruc'")) {
                if (rs.next()) {
                    String nullable = rs.getString("Null");
                    rucAllowsNull = "YES".equalsIgnoreCase(nullable);
                }
            }
            
            // Modificar columna RUC para permitir NULL si es necesario
            if (!rucAllowsNull) {
                stmt.executeUpdate("ALTER TABLE proveedores MODIFY COLUMN ruc VARCHAR(11) UNIQUE NULL");
                System.out.println("✅ Columna 'ruc' modificada para permitir valores NULL");
            } else {
                System.out.println("ℹ️ Columna 'ruc' ya permite valores NULL");
            }
            
            // Verificar si columna observaciones existe
            boolean observacionesExists = false;
            
            try (var rs = stmt.executeQuery("SHOW COLUMNS FROM proveedores LIKE 'observaciones'")) {
                observacionesExists = rs.next();
            }
            
            // Agregar columna observaciones si no existe
            if (!observacionesExists) {
                stmt.executeUpdate("ALTER TABLE proveedores ADD COLUMN observaciones TEXT DEFAULT NULL");
                System.out.println("✅ Columna 'observaciones' agregada exitosamente");
            } else {
                System.out.println("ℹ️ Columna 'observaciones' ya existe");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar tabla proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }
}