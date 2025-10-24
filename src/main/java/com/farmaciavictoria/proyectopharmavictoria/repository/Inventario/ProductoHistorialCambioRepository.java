package com.farmaciavictoria.proyectopharmavictoria.repository.Inventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoHistorialCambioRepository {
    public static void registrarCambio(int productoId, String campo, String valorAnterior, String valorNuevo, String usuario) {
        try (Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection()) {
            // Verificar si ya existe un registro idéntico en los últimos 5 segundos
            String checkSql = "SELECT COUNT(*) FROM producto_historial_cambio WHERE producto_id = ? AND campo_modificado = ? AND valor_anterior = ? AND valor_nuevo = ? AND usuario = ? AND fecha >= NOW() - INTERVAL 5 SECOND";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, productoId);
                checkStmt.setString(2, campo);
                checkStmt.setString(3, valorAnterior);
                checkStmt.setString(4, valorNuevo);
                checkStmt.setString(5, usuario);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Ya existe, no insertar duplicado
                    return;
                }
            }
            String sql = "INSERT INTO producto_historial_cambio (producto_id, campo_modificado, valor_anterior, valor_nuevo, usuario) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, productoId);
                stmt.setString(2, campo);
                stmt.setString(3, valorAnterior);
                stmt.setString(4, valorNuevo);
                stmt.setString(5, usuario);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<HistorialCambioDTO> obtenerCambiosPorProducto(int productoId) {
        List<HistorialCambioDTO> lista = new ArrayList<>();
        try (Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection()) {
            String sql = "SELECT campo_modificado, valor_anterior, valor_nuevo, usuario, fecha FROM producto_historial_cambio WHERE producto_id = ? ORDER BY fecha DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, productoId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new HistorialCambioDTO(
                            rs.getString("campo_modificado"),
                            rs.getString("valor_anterior"),
                            rs.getString("valor_nuevo"),
                            rs.getString("usuario"),
                            rs.getTimestamp("fecha")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static class HistorialCambioDTO {
        public final String campo;
        public final String anterior;
        public final String nuevo;
        public final String usuario;
        public final Timestamp fecha;
        public HistorialCambioDTO(String campo, String anterior, String nuevo, String usuario, Timestamp fecha) {
            this.campo = campo;
            this.anterior = anterior;
            this.nuevo = nuevo;
            this.usuario = usuario;
            this.fecha = fecha;
        }
    }
}
