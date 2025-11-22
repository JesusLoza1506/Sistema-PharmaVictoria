package com.farmaciavictoria.proyectopharmavictoria.repository.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetallePagoVenta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetallePagoVentaRepositoryJdbcImpl {
    private final Connection connection;

    public DetallePagoVentaRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    public boolean save(DetallePagoVenta detalle) {
        String sql = "INSERT INTO detalle_pago_venta (venta_id, tipo_pago, monto) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getVentaId());
            stmt.setString(2, detalle.getTipoPago());
            stmt.setDouble(3, detalle.getMonto());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
