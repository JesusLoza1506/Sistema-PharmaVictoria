package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.repository;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.controller.reportes.view.VentaPorProductoDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteVentasPorProductoRepository {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public List<VentaPorProductoDTO> obtenerRankingProductos(LocalDate desde, LocalDate hasta) {
        List<VentaPorProductoDTO> lista = new ArrayList<>();
        String sql = "SELECT p.nombre, SUM(dv.cantidad), SUM(dv.subtotal) " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE v.estado = 'REALIZADA' AND v.fecha_venta BETWEEN ? AND ? " +
                "GROUP BY p.id, p.nombre " +
                "ORDER BY SUM(dv.cantidad) DESC";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, desde);
            stmt.setObject(2, hasta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaPorProductoDTO dto = new VentaPorProductoDTO(
                            rs.getString(1),
                            rs.getInt(2),
                            rs.getDouble(3));
                    lista.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
