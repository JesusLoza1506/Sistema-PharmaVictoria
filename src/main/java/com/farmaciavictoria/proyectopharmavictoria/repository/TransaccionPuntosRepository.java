package com.farmaciavictoria.proyectopharmavictoria.repository;

import com.farmaciavictoria.proyectopharmavictoria.model.TransaccionPuntos;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.sql.*;
import java.util.*;

public class TransaccionPuntosRepository {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public List<TransaccionPuntos> findByClienteId(int clienteId) {
        List<TransaccionPuntos> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacciones_puntos WHERE cliente_id = ? ORDER BY fecha DESC";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TransaccionPuntos t = new TransaccionPuntos();
                    t.setId(rs.getInt("id"));
                    t.setClienteId(rs.getInt("cliente_id"));
                    t.setVentaId(rs.getInt("venta_id"));
                    t.setTipo(rs.getString("tipo"));
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
