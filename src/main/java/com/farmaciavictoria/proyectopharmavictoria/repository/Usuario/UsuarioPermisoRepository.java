package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioPermisoRepository {
    private final DatabaseConfig databaseConfig;

    public UsuarioPermisoRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    public void save(UsuarioPermiso permiso) {
        // Primero intentar actualizar, si no existe, insertar
        String updateSql = "UPDATE usuario_permisos SET valor = ?, fecha_asignacion = ? WHERE usuario_id = ? AND permiso = ?";
        String insertSql = "INSERT INTO usuario_permisos (usuario_id, permiso, valor, fecha_asignacion) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection()) {
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setBoolean(1, permiso.isValor());
                updateStmt.setTimestamp(2, Timestamp.valueOf(permiso.getFechaAsignacion()));
                updateStmt.setLong(3, permiso.getUsuarioId());
                updateStmt.setString(4, permiso.getPermiso());
                int rows = updateStmt.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setLong(1, permiso.getUsuarioId());
                        insertStmt.setString(2, permiso.getPermiso());
                        insertStmt.setBoolean(3, permiso.isValor());
                        insertStmt.setTimestamp(4, Timestamp.valueOf(permiso.getFechaAsignacion()));
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UsuarioPermiso> findByUsuarioId(Long usuarioId) {
        List<UsuarioPermiso> permisos = new ArrayList<>();
        String sql = "SELECT * FROM usuario_permisos WHERE usuario_id = ? ORDER BY fecha_asignacion DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioPermiso permiso = new UsuarioPermiso();
                    permiso.setId(rs.getLong("id"));
                    permiso.setUsuarioId(rs.getLong("usuario_id"));
                    permiso.setPermiso(rs.getString("permiso"));
                    permiso.setValor(rs.getBoolean("valor"));
                    permiso.setFechaAsignacion(rs.getTimestamp("fecha_asignacion").toLocalDateTime());
                    permisos.add(permiso);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permisos;
    }
}
