package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import java.sql.*;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.util.*;

public class DetalleVentaRepositoryJdbcImpl implements DetalleVentaRepository {
    public DetalleVentaRepositoryJdbcImpl() {
        // Sin argumentos, usa DatabaseConfig Singleton
    }

    @Override
    public DetalleVenta findById(int id) {
        String sql = "SELECT * FROM detalle_ventas WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDetalleVentaFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar detalle de venta por ID", e);
        }
        return null;
    }

    @Override
    public void update(DetalleVenta detalleVenta) {
        String sql = "UPDATE detalle_ventas SET venta_id=?, producto_id=?, cantidad=?, precio_unitario=?, descuento=?, subtotal=?, lote=?, fecha_vencimiento=? WHERE id=?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, detalleVenta.getVenta() != null ? detalleVenta.getVenta().getId() : null);
            stmt.setObject(2, detalleVenta.getProducto() != null ? detalleVenta.getProducto().getId() : null);
            stmt.setInt(3, detalleVenta.getCantidad());
            stmt.setBigDecimal(4, detalleVenta.getPrecioUnitario());
            stmt.setBigDecimal(5, detalleVenta.getDescuento());
            stmt.setBigDecimal(6, detalleVenta.getSubtotal());
            stmt.setString(7, detalleVenta.getLote());
            stmt.setObject(8, detalleVenta.getFechaVencimiento());
            stmt.setInt(9, detalleVenta.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar detalle de venta", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM detalle_ventas WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar detalle de venta", e);
        }
    }

    @Override
    public void save(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal, lote, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, detalle.getVenta() != null ? detalle.getVenta().getId() : null);
            stmt.setObject(2, detalle.getProducto() != null ? detalle.getProducto().getId() : null);
            stmt.setInt(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getDescuento());
            stmt.setBigDecimal(6, detalle.getSubtotal());
            stmt.setString(7, detalle.getLote());
            stmt.setObject(8, detalle.getFechaVencimiento());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar detalle de venta", e);
        }
    }

    @Override
    public List<DetalleVenta> findByVentaId(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ventas WHERE venta_id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapDetalleVentaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar detalles de venta por ventaId", e);
        }
        return detalles;
    }

    public void deleteByVentaId(int ventaId) {
        String sql = "DELETE FROM detalle_ventas WHERE venta_id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar detalles de venta por ventaId", e);
        }
    }

    // Utilidad para mapear ResultSet a DetalleVenta
    private DetalleVenta mapDetalleVentaFromResultSet(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(rs.getInt("id"));
        // Solo se setean los IDs de venta y producto. Se recomienda cargar los objetos completos en el Service si se requiere.
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setDescuento(rs.getBigDecimal("descuento"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        detalle.setLote(rs.getString("lote"));
        detalle.setFechaVencimiento(rs.getDate("fecha_vencimiento") != null ? rs.getDate("fecha_vencimiento").toLocalDate() : null);
        return detalle;
    }
}
