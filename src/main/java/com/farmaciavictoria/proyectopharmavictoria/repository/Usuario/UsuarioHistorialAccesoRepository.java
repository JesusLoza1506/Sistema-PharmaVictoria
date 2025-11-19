package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsuarioHistorialAccesoRepository {
    private final DatabaseConfig databaseConfig;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioHistorialAccesoRepository.class);

    public UsuarioHistorialAccesoRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    public void save(UsuarioHistorialAcceso acceso) {
        String sql = "INSERT INTO usuario_historial_acceso (usuario_id, fecha_acceso, ip_address, user_agent, exito, motivo_fallo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, acceso.getUsuarioId());
            stmt.setTimestamp(2, Timestamp.valueOf(acceso.getFechaAcceso()));
            stmt.setString(3, acceso.getIpAddress());
            stmt.setString(4, acceso.getUserAgent());
            stmt.setBoolean(5, acceso.isExito());
            stmt.setString(6, acceso.getMotivoFallo());
            stmt.executeUpdate();
            logger.debug("Historial de acceso guardado para usuarioId={}", acceso.getUsuarioId());
        } catch (SQLException e) {
            logger.error("Error al guardar historial de acceso para usuarioId={}: {}", acceso.getUsuarioId(),
                    e.getMessage(), e);
            throw new RuntimeException("Error al guardar historial de acceso", e);
        }
    }

    public List<UsuarioHistorialAcceso> findByUsuarioId(Long usuarioId) {
        List<UsuarioHistorialAcceso> accesos = new ArrayList<>();
        String sql = "SELECT * FROM usuario_historial_acceso WHERE usuario_id = ? ORDER BY fecha_acceso DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioHistorialAcceso acceso = new UsuarioHistorialAcceso();
                    acceso.setId(rs.getLong("id"));
                    acceso.setUsuarioId(rs.getLong("usuario_id"));
                    acceso.setFechaAcceso(rs.getTimestamp("fecha_acceso").toLocalDateTime());
                    acceso.setIpAddress(rs.getString("ip_address"));
                    acceso.setUserAgent(rs.getString("user_agent"));
                    acceso.setExito(rs.getBoolean("exito"));
                    acceso.setMotivoFallo(rs.getString("motivo_fallo"));
                    accesos.add(acceso);
                }
                logger.debug("{} accesos recuperados para usuarioId={}", accesos.size(), usuarioId);
            }
        } catch (SQLException e) {
            logger.error("Error al leer historial de accesos para usuarioId={}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al leer historial de accesos", e);
        }
        return accesos;
    }
}
