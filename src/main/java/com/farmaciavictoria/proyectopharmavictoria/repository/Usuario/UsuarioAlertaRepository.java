package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioAlerta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAlertaRepository {
    private final DatabaseConfig databaseConfig;

    public UsuarioAlertaRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    public void save(UsuarioAlerta alerta) {
        String sql = "INSERT INTO usuario_alertas (usuario_id, tipo_alerta, mensaje, fecha, leida) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, alerta.getUsuarioId());
            stmt.setString(2, alerta.getTipoAlerta());
            stmt.setString(3, alerta.getMensaje());
            stmt.setTimestamp(4, Timestamp.valueOf(alerta.getFecha()));
            stmt.setBoolean(5, alerta.isLeida());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UsuarioAlerta> findByUsuarioId(Long usuarioId) {
        List<UsuarioAlerta> alertas = new ArrayList<>();
        String sql = "SELECT * FROM usuario_alertas WHERE usuario_id = ? ORDER BY fecha DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioAlerta alerta = new UsuarioAlerta();
                    alerta.setId(rs.getLong("id"));
                    alerta.setUsuarioId(rs.getLong("usuario_id"));
                    alerta.setTipoAlerta(rs.getString("tipo_alerta"));
                    alerta.setMensaje(rs.getString("mensaje"));
                    alerta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    alerta.setLeida(rs.getBoolean("leida"));
                    alertas.add(alerta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertas;
    }
}
