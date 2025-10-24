package com.farmaciavictoria.proyectopharmavictoria.repository;

import com.farmaciavictoria.proyectopharmavictoria.model.Sucursal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SucursalRepository {
    public SucursalRepository() {
        // no-op: connections are obtained per-method from DatabaseConfig
    }

    public List<Sucursal> findAllActivas() {
        List<Sucursal> sucursales = new ArrayList<>();
        String sql = "SELECT id, nombre, direccion, telefono, activa, created_at FROM sucursales WHERE activa = 1 ORDER BY nombre";
       try (java.sql.Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setId(rs.getInt("id"));
                sucursal.setNombre(rs.getString("nombre"));
                sucursal.setDireccion(rs.getString("direccion"));
                sucursal.setTelefono(rs.getString("telefono"));
                sucursal.setActiva(rs.getBoolean("activa"));
                sucursal.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                sucursales.add(sucursal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sucursales;
    }

    public String findNombreById(int id) {
        String sql = "SELECT nombre FROM sucursales WHERE id = ?";
        try (java.sql.Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
