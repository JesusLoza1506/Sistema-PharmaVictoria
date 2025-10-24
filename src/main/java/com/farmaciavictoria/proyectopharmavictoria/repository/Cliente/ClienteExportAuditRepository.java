package com.farmaciavictoria.proyectopharmavictoria.repository.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteExportAudit;

import java.sql.*;

public class ClienteExportAuditRepository {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public void save(ClienteExportAudit audit) {
        String sql = "INSERT INTO cliente_export_audit (usuario, fecha, tipo_archivo, ruta_archivo, cantidad_clientes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, audit.getUsuario());
            stmt.setTimestamp(2, Timestamp.valueOf(audit.getFecha()));
            stmt.setString(3, audit.getTipoArchivo());
            stmt.setString(4, audit.getRutaArchivo());
            stmt.setInt(5, audit.getCantidadClientes());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
