package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import java.sql.*;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.util.*;

public class VentaRepositoryJdbcImpl implements VentaRepository {
    public List<Venta> findByClienteId(int clienteId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE cliente_id = ? ORDER BY fecha_venta DESC";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapVentaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas por cliente", e);
        }
        return ventas;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar venta", e);
        }
    }

    public VentaRepositoryJdbcImpl() {
        // Sin argumentos, usa DatabaseConfig Singleton
    }

    @Override
    public void save(Venta venta) {
        // Guardar venta sin id manual, obtener id generado
        String sql = "INSERT INTO ventas (cliente_id, usuario_id, subtotal, descuento_monto, igv_monto, total, tipo_pago, tipo_comprobante, numero_boleta, serie, fecha_venta, estado, observaciones, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // cliente_id
            if (venta.getCliente() != null && venta.getCliente().getId() != null) {
                stmt.setInt(1, venta.getCliente().getId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            // usuario_id
            if (venta.getUsuario() != null && venta.getUsuario().getId() != null) {
                stmt.setInt(2, venta.getUsuario().getId().intValue());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setBigDecimal(3, venta.getSubtotal());
            stmt.setBigDecimal(4, venta.getDescuentoMonto());
            stmt.setBigDecimal(5, venta.getIgvMonto());
            stmt.setBigDecimal(6, venta.getTotal());
            stmt.setString(7, venta.getTipoPago());
            stmt.setString(8, venta.getTipoComprobante());
            stmt.setString(9, venta.getNumeroBoleta());
            stmt.setString(10, venta.getSerie());
            stmt.setObject(11, venta.getFechaVenta());
            stmt.setString(12, venta.getEstado());
            stmt.setString(13, venta.getObservaciones());
            stmt.setObject(14, venta.getCreatedAt());
            stmt.setObject(15, venta.getUpdatedAt());
            stmt.executeUpdate();

            // Obtener el id generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venta.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("[VENTA ERROR] Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar venta", e);
        }
    }

    @Override
    public Venta findById(int id) {
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venta venta = mapVentaFromResultSet(rs);
                    return venta;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por ID", e);
        }
        return null;
    }

    @Override
    public List<Venta> findAll() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapVentaFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas", e);
        }
        return ventas;
    }

    @Override
    public void update(Venta venta) {
        String sql = "UPDATE ventas SET cliente_id=?, usuario_id=?, subtotal=?, descuento_monto=?, igv_monto=?, total=?, tipo_pago=?, tipo_comprobante=?, numero_boleta=?, serie=?, fecha_venta=?, estado=?, observaciones=?, created_at=?, updated_at=? WHERE id=?";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            // cliente_id
            if (venta.getCliente() != null && venta.getCliente().getId() != null) {
                stmt.setInt(1, venta.getCliente().getId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            // usuario_id
            if (venta.getUsuario() != null && venta.getUsuario().getId() != null) {
                stmt.setInt(2, venta.getUsuario().getId().intValue());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setBigDecimal(3, venta.getSubtotal());
            stmt.setBigDecimal(4, venta.getDescuentoMonto());
            stmt.setBigDecimal(5, venta.getIgvMonto());
            stmt.setBigDecimal(6, venta.getTotal());
            stmt.setString(7, venta.getTipoPago());
            stmt.setString(8, venta.getTipoComprobante());
            stmt.setString(9, venta.getNumeroBoleta());
            stmt.setString(10, venta.getSerie());
            stmt.setObject(11, venta.getFechaVenta());
            stmt.setString(12, venta.getEstado());
            stmt.setString(13, venta.getObservaciones());
            stmt.setObject(14, venta.getCreatedAt());
            stmt.setObject(15, venta.getUpdatedAt());
            stmt.setInt(16, venta.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[VENTA ERROR] Error al actualizar venta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar venta", e);
        }
    }

    // Utilidad para mapear ResultSet a Venta (sin detalles ni comprobante)
    private Venta mapVentaFromResultSet(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        // Cargar el objeto Cliente completo
        int clienteId = rs.getInt("cliente_id");
        com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository clienteRepo = new com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository();
        com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente cliente = clienteRepo.findById(clienteId)
                .orElse(null);
        venta.setCliente(cliente);
        // ...otros campos de Venta...
        venta.setSubtotal(rs.getBigDecimal("subtotal"));
        venta.setDescuentoMonto(rs.getBigDecimal("descuento_monto"));
        venta.setIgvMonto(rs.getBigDecimal("igv_monto"));
        venta.setTotal(rs.getBigDecimal("total"));
        venta.setTipoPago(rs.getString("tipo_pago"));
        venta.setTipoComprobante(rs.getString("tipo_comprobante"));
        venta.setNumeroBoleta(rs.getString("numero_boleta"));
        venta.setSerie(rs.getString("serie"));
        venta.setFechaVenta(
                rs.getTimestamp("fecha_venta") != null ? rs.getTimestamp("fecha_venta").toLocalDateTime() : null);
        venta.setEstado(rs.getString("estado"));
        venta.setObservaciones(rs.getString("observaciones"));
        venta.setCreatedAt(
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        venta.setUpdatedAt(
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return venta;
    }
}
