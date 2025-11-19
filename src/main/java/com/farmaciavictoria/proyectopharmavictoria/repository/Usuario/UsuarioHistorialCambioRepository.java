package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsuarioHistorialCambioRepository {
    private final DatabaseConfig databaseConfig;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioHistorialCambioRepository.class);

    public UsuarioHistorialCambioRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    public void save(UsuarioHistorialCambio cambio) {
        String sql = "INSERT INTO usuario_historial_cambio (usuario_id, campo_modificado, valor_anterior, valor_nuevo, modificado_por, fecha) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cambio.getUsuario_id());
            stmt.setString(2, cambio.getCampo_modificado());
            stmt.setString(3, cambio.getValor_anterior());
            stmt.setString(4, cambio.getValor_nuevo());
            if (cambio.getModificado_por() != null) {
                stmt.setInt(5, cambio.getModificado_por());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setTimestamp(6, cambio.getFecha() != null ? Timestamp.valueOf(cambio.getFecha())
                    : new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            logger.debug("Historial de cambio guardado para usuario_id={}", cambio.getUsuario_id());
        } catch (SQLException e) {
            logger.error("Error al guardar historial de cambio para usuario_id={}: {}", cambio.getUsuario_id(),
                    e.getMessage(), e);
            throw new RuntimeException("Error al guardar historial de cambio", e);
        }
    }

    public List<UsuarioHistorialCambio> findByUsuarioId(int usuarioId) {
        List<UsuarioHistorialCambio> cambios = new ArrayList<>();
        String sql = "SELECT * FROM usuario_historial_cambio WHERE usuario_id = ? ORDER BY fecha DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
                    cambio.setId(rs.getInt("id"));
                    cambio.setUsuario_id(rs.getInt("usuario_id"));
                    cambio.setCampo_modificado(rs.getString("campo_modificado"));
                    cambio.setValor_anterior(rs.getString("valor_anterior"));
                    cambio.setValor_nuevo(rs.getString("valor_nuevo"));
                    int modPor = rs.getInt("modificado_por");
                    cambio.setModificado_por(rs.wasNull() ? null : modPor);
                    Timestamp ts = rs.getTimestamp("fecha");
                    cambio.setFecha(ts != null ? ts.toLocalDateTime() : null);
                    cambios.add(cambio);
                }
                logger.debug("{} cambios recuperados para usuario_id={}", cambios.size(), usuarioId);
            }
        } catch (SQLException e) {
            logger.error("Error al leer historial de cambios para usuario_id={}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al leer historial de cambios", e);
        }
        return cambios;
    }
}
