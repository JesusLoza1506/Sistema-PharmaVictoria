package com.farmaciavictoria.proyectopharmavictoria.repository.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository para historial de cambios de clientes
 */
public class ClienteHistorialCambioRepository {
    private final DatabaseConfig databaseConfig;

    public ClienteHistorialCambioRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    // Guardar historial de cambio
    public boolean save(ClienteHistorialCambio historial) {
        String sql = "INSERT INTO cliente_historial_cambio (cliente_id, campo_modificado, valor_anterior, valor_nuevo, usuario, fecha) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historial.getClienteId());
            stmt.setString(2, historial.getCampoModificado());
            stmt.setString(3, historial.getValorAnterior());
            stmt.setString(4, historial.getValorNuevo());
            stmt.setString(5, historial.getUsuario());
            stmt.setTimestamp(6, Timestamp.valueOf(historial.getFecha()));
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el historial de cambios para un cliente específico
     */
    public List<ClienteHistorialCambio> findByClienteId(int clienteId) {
        String sql = "SELECT * FROM cliente_historial_cambio WHERE cliente_id = ? ORDER BY fecha DESC";
        List<ClienteHistorialCambio> lista = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClienteHistorialCambio h = new ClienteHistorialCambio();
                    h.setId(rs.getInt("id"));
                    h.setClienteId(rs.getInt("cliente_id"));
                    h.setCampoModificado(rs.getString("campo_modificado"));
                    h.setValorAnterior(rs.getString("valor_anterior"));
                    h.setValorNuevo(rs.getString("valor_nuevo"));
                    h.setUsuario(rs.getString("usuario"));
                    Timestamp fecha = rs.getTimestamp("fecha");
                    if (fecha != null) h.setFecha(fecha.toLocalDateTime());
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
