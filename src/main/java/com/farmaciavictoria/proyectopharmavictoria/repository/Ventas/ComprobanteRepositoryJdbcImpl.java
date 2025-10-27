package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;

public class ComprobanteRepositoryJdbcImpl implements ComprobanteRepository {
    /**
     * Obtiene el siguiente número de boleta para una serie dada.
     */
    public String obtenerSiguienteNumeroBoleta(String serie) {
        String ultimoNumero = "000001";
        String sql = "SELECT MAX(CAST(numero AS UNSIGNED)) FROM comprobantes WHERE tipo='BOLETA' AND serie=?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serie);
            try (ResultSet rs = stmt.executeQuery()) {
                int num = 0;
                if (rs.next()) {
                    num = rs.getInt(1);
                }
                num++;
                ultimoNumero = String.format("%06d", num);
            }
        } catch (Exception ex) {
            System.err.println("Error obteniendo número de boleta: " + ex.getMessage());
            ultimoNumero = "000001";
        }
        return ultimoNumero;
    }

    /**
     * Obtiene el último número de comprobante para una serie dada (FACTURA o
     * BOLETA).
     * Devuelve 0 si no hay comprobantes previos.
     */
    public int obtenerUltimoNumeroPorSerieYTipo(String serie, String tipo) {
        int ultimoNumero = 0;
        String sql = "SELECT MAX(CAST(numero AS UNSIGNED)) FROM comprobantes WHERE serie=? AND tipo=?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serie);
            stmt.setString(2, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ultimoNumero = rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error obteniendo último número por serie y tipo: " + ex.getMessage());
            ultimoNumero = 0;
        }
        return ultimoNumero;
    }

    public ComprobanteRepositoryJdbcImpl() {
        // Sin argumentos, usa DatabaseConfig Singleton
    }

    @Override
    public Comprobante findById(int id) {
        String sql = "SELECT * FROM comprobantes WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapComprobanteFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar comprobante por ID", e);
        }
        return null;
    }

    @Override
    public void update(Comprobante comprobante) {
        String sql = "UPDATE comprobantes SET venta_id=?, tipo=?, serie=?, numero=?, hash_sunat=?, estado_sunat=?, fecha_emision=? WHERE id=?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, comprobante.getVenta() != null ? comprobante.getVenta().getId() : null);
            stmt.setString(2, comprobante.getTipo());
            stmt.setString(3, comprobante.getSerie());
            stmt.setString(4, comprobante.getNumero());
            stmt.setString(5, comprobante.getHashSunat());
            stmt.setString(6, comprobante.getEstadoSunat());
            stmt.setObject(7, comprobante.getFechaEmision());
            stmt.setInt(8, comprobante.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar comprobante", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM comprobantes WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar comprobante", e);
        }
    }

    @Override
    public void save(Comprobante comprobante) {
        String sql = "INSERT INTO comprobantes (venta_id, tipo, serie, numero, hash_sunat, estado_sunat, fecha_emision) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, comprobante.getVenta() != null ? comprobante.getVenta().getId() : null);
            stmt.setString(2, comprobante.getTipo());
            stmt.setString(3, comprobante.getSerie());
            stmt.setString(4, comprobante.getNumero());
            stmt.setString(5, comprobante.getHashSunat());
            stmt.setString(6, comprobante.getEstadoSunat());
            stmt.setObject(7, comprobante.getFechaEmision());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar comprobante", e);
        }
    }

    @Override
    public List<Comprobante> findByVentaId(int ventaId) {
        List<Comprobante> comprobantes = new ArrayList<>();
        String sql = "SELECT * FROM comprobantes WHERE venta_id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comprobantes.add(mapComprobanteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar comprobantes por ventaId", e);
        }
        return comprobantes;
    }

    // Utilidad para mapear ResultSet a Comprobante
    private Comprobante mapComprobanteFromResultSet(ResultSet rs) throws SQLException {
        Comprobante comprobante = new Comprobante();
        comprobante.setId(rs.getInt("id"));
        // Solo se setea el ID de venta. Se recomienda cargar el objeto completo en el
        // Service si se requiere.
        comprobante.setTipo(rs.getString("tipo"));
        comprobante.setSerie(rs.getString("serie"));
        comprobante.setNumero(rs.getString("numero"));
        comprobante.setHashSunat(rs.getString("hash_sunat"));
        comprobante.setEstadoSunat(rs.getString("estado_sunat"));
        comprobante.setFechaEmision(
                rs.getTimestamp("fecha_emision") != null ? rs.getTimestamp("fecha_emision").toLocalDateTime() : null);
        return comprobante;
    }
}
