package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import java.sql.*;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.util.*;

public class VentaHistorialCambioRepositoryJdbcImpl implements VentaHistorialCambioRepository {
    public VentaHistorialCambioRepositoryJdbcImpl() {
        // Sin argumentos, usa DatabaseConfig Singleton
    }

    @Override
    public VentaHistorialCambio findById(int id) {
        String sql = "SELECT * FROM venta_historial_cambios WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar historial de cambio por ID", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM venta_historial_cambios WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar historial de cambio", e);
        }
    }

    @Override
    public void save(VentaHistorialCambio historial) {
        // El campo id es autoincremental, no se debe incluir en el INSERT
        String sql = "INSERT INTO venta_historial_cambio (venta_id, tipo_cambio, motivo, usuario_id, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, historial.getVenta() != null ? historial.getVenta().getId() : 0);
            stmt.setString(2, historial.getTipoCambio());
            stmt.setString(3, historial.getMotivo());
            stmt.setLong(4, historial.getUsuario() != null ? historial.getUsuario().getId() : 0L);
            stmt.setObject(5, historial.getFecha());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar historial de cambio", e);
        }
    }

    @Override
    public List<VentaHistorialCambio> findByVentaId(int ventaId) {
        List<VentaHistorialCambio> historialList = new ArrayList<>();
        String sql = "SELECT * FROM venta_historial_cambios WHERE venta_id = ? ORDER BY fecha DESC";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historialList.add(mapFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar historial de cambios por ventaId", e);
        }
        return historialList;
    }

    // Utilidad para mapear ResultSet a VentaHistorialCambio
    private VentaHistorialCambio mapFromResultSet(ResultSet rs) throws SQLException {
        VentaHistorialCambio h = new VentaHistorialCambio();
        h.setId(rs.getInt("id"));
        // Solo se setea el ID de venta. Se recomienda cargar el objeto completo en el
        // Service si se requiere.
        // Venta venta = new Venta(); venta.setId(rs.getInt("venta_id"));
        // h.setVenta(venta);
        h.setTipoCambio(rs.getString("tipo_cambio"));
        h.setMotivo(rs.getString("motivo"));
        // Usuario usuario = new Usuario(); usuario.setId(rs.getLong("usuario_id"));
        // h.setUsuario(usuario);
        h.setFecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null);
        return h;
    }
}
