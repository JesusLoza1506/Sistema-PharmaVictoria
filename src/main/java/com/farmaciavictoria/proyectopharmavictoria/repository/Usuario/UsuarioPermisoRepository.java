package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository para permisos granulares de usuario
 */
public class UsuarioPermisoRepository {
    private final DatabaseConfig databaseConfig;

    public UsuarioPermisoRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    public void save(UsuarioPermiso permiso) {
        String sql = "INSERT INTO usuario_permisos (usuario_id, permiso, valor, fecha_asignacion) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, permiso.getUsuarioId());
            stmt.setString(2, permiso.getPermiso());
            stmt.setBoolean(3, permiso.isValor());
            stmt.setTimestamp(4, Timestamp.valueOf(permiso.getFechaAsignacion()));
            stmt.executeUpdate();
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
