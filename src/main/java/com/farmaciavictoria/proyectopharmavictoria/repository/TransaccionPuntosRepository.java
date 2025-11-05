package com.farmaciavictoria.proyectopharmavictoria.repository;

import com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.sql.*;
import java.util.*;

public class TransaccionPuntosRepository {
    public boolean save(TransaccionPuntos mov) {
        String sql = "INSERT INTO cliente_puntos_historial (cliente_id, venta_id, tipo_movimiento, puntos, descripcion, fecha, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("[DEBUG PUNTOS] Intentando guardar transacción: " +
                    "cliente_id=" + mov.getClienteId() +
                    ", venta_id=" + mov.getVentaId() +
                    ", tipo_movimiento=" + mov.getTipo() +
                    ", puntos=" + mov.getPuntos() +
                    ", descripcion=" + mov.getDescripcion() +
                    ", fecha=" + mov.getFecha() +
                    ", usuario_id=" + mov.getUsuarioId());

            stmt.setInt(1, mov.getClienteId());
            stmt.setInt(2, mov.getVentaId());
            stmt.setString(3, mov.getTipo()); // tipo -> tipo_movimiento
            stmt.setInt(4, mov.getPuntos());
            stmt.setString(5, mov.getDescripcion());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(mov.getFecha()));
            if (mov.getUsuarioId() != null) {
                stmt.setInt(7, mov.getUsuarioId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            int rows = stmt.executeUpdate();
            System.out.println("[DEBUG PUNTOS] Filas afectadas: " + rows);
            // Sincronizar puntos en la tabla clientes según el tipo de movimiento
            if (rows > 0) {
                String tipo = mov.getTipo();
                int puntos = mov.getPuntos();
                int clienteId = mov.getClienteId();
                String updateSql = null;
                if ("GANADO".equalsIgnoreCase(tipo) || "AJUSTE".equalsIgnoreCase(tipo)) {
                    // Sumar puntos ganados o ajustados
                    updateSql = "UPDATE clientes SET puntos_totales = COALESCE(puntos_totales,0) + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                } else if ("USADO".equalsIgnoreCase(tipo) || "EXPIRADO".equalsIgnoreCase(tipo)) {
                    // Sumar puntos usados o expirados
                    updateSql = "UPDATE clientes SET puntos_usados = COALESCE(puntos_usados,0) + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                }
                if (updateSql != null) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, puntos);
                        updateStmt.setInt(2, clienteId);
                        int updateRows = updateStmt.executeUpdate();
                        System.out.println("[DEBUG PUNTOS] Cliente actualizado, filas afectadas: " + updateRows);
                    } catch (Exception e) {
                        System.err.println("[ERROR PUNTOS] Error al actualizar puntos del cliente: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("[ERROR PUNTOS] Excepción al guardar transacción: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    /**
     * Recalcula y actualiza los puntos de todos los clientes según el historial de
     * movimientos.
     * Suma los puntos GANADO/AJUSTE y los USADO/EXPIRADO para cada cliente y
     * actualiza la tabla clientes.
     */
    public void sincronizarPuntosClientes() {
        String clientesSql = "SELECT id FROM clientes";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement clientesStmt = conn.prepareStatement(clientesSql);
                ResultSet clientesRs = clientesStmt.executeQuery()) {
            while (clientesRs.next()) {
                int clienteId = clientesRs.getInt("id");
                int puntosTotales = 0;
                int puntosUsados = 0;
                // Sumar puntos GANADO y AJUSTE
                String sumGanadosSql = "SELECT COALESCE(SUM(puntos),0) FROM cliente_puntos_historial WHERE cliente_id = ? AND (tipo_movimiento = 'GANADO' OR tipo_movimiento = 'AJUSTE')";
                try (PreparedStatement sumGanadosStmt = conn.prepareStatement(sumGanadosSql)) {
                    sumGanadosStmt.setInt(1, clienteId);
                    try (ResultSet rs = sumGanadosStmt.executeQuery()) {
                        if (rs.next()) {
                            puntosTotales = rs.getInt(1);
                        }
                    }
                }
                // Sumar puntos USADO y EXPIRADO
                String sumUsadosSql = "SELECT COALESCE(SUM(puntos),0) FROM cliente_puntos_historial WHERE cliente_id = ? AND (tipo_movimiento = 'USADO' OR tipo_movimiento = 'EXPIRADO')";
                try (PreparedStatement sumUsadosStmt = conn.prepareStatement(sumUsadosSql)) {
                    sumUsadosStmt.setInt(1, clienteId);
                    try (ResultSet rs = sumUsadosStmt.executeQuery()) {
                        if (rs.next()) {
                            puntosUsados = rs.getInt(1);
                        }
                    }
                }
                // Actualizar la tabla clientes
                String updateSql = "UPDATE clientes SET puntos_totales = ?, puntos_usados = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, puntosTotales);
                    updateStmt.setInt(2, puntosUsados);
                    updateStmt.setInt(3, clienteId);
                    int updateRows = updateStmt.executeUpdate();
                    System.out.println("[SYNC PUNTOS] Cliente " + clienteId + " actualizado: totales=" + puntosTotales
                            + ", usados=" + puntosUsados + ", filas=" + updateRows);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR SYNC PUNTOS] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<TransaccionPuntos> findByClienteId(int clienteId) {
        List<TransaccionPuntos> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente_puntos_historial WHERE cliente_id = ? ORDER BY fecha DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TransaccionPuntos t = new TransaccionPuntos();
                    t.setId(rs.getInt("id"));
                    t.setClienteId(rs.getInt("cliente_id"));
                    t.setVentaId(rs.getInt("venta_id"));
                    t.setTipo(rs.getString("tipo_movimiento"));
                    t.setPuntos(rs.getInt("puntos"));
                    t.setDescripcion(rs.getString("descripcion"));
                    t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    t.setUsuarioId(rs.getInt("usuario_id"));
                    lista.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
