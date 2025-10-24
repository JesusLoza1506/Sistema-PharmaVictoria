package com.farmaciavictoria.proyectopharmavictoria.repository.Inventario;


import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.InventarioCommand;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioAuditoriaRepository {
    public static void registrarMovimiento(InventarioCommand cmd) {
        try (Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection()) {
            Integer idProd = cmd.getProducto() != null ? cmd.getProducto().getId() : null;
            if (idProd == null) {
                System.err.println("[AUDITORIA] El producto no tiene ID válido, no se registra movimiento.");
                // com.farmaciavictoria.proyectopharmavictoria.service.NotificationService.mostrarErrorStatic("No se pudo registrar el movimiento: producto sin ID válido.");
                // Si quieres notificar, usa el NotificationService de instancia desde el controlador
                return;
            }
            String sql = "INSERT INTO audit_log (accion, tabla_afectada, registro_id, valores_anteriores, valores_nuevos, usuario_id, timestamp) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "AJUSTE_STOCK");
                stmt.setString(2, "productos");
                stmt.setInt(3, idProd);
                stmt.setString(4, String.format("{\"stock\":%d}", cmd.getCantidadAnterior()));
                stmt.setString(5, String.format("{\"stock\":%d}", cmd.getCantidadNueva()));
                stmt.setObject(6, null); // Si tienes usuario_id, ponlo aquí
                int res = stmt.executeUpdate();
                if (res == 0) {
                    System.err.println("[AUDITORIA] No se insertó ningún registro en audit_log.");
                    // com.farmaciavictoria.proyectopharmavictoria.service.NotificationService.mostrarErrorStatic("No se pudo registrar el movimiento en la auditoría.");
                    // Si quieres notificar, hazlo desde el controlador con NotificationService
                } else {
                    System.out.println("[AUDITORIA] Movimiento registrado correctamente en audit_log.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[AUDITORIA] Error al registrar movimiento en auditoría: " + e.getMessage());
            // Si quieres notificar, hazlo desde el controlador con NotificationService
        }
    }

    public static List<InventarioCommand> obtenerHistorialPorProducto(Producto producto) {
        List<InventarioCommand> lista = new ArrayList<>();
        try (Connection conn = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig.getInstance().getConnection()) {
            String sql = "SELECT accion, valores_anteriores, valores_nuevos, usuario_id, timestamp FROM audit_log WHERE tabla_afectada = 'productos' AND registro_id = ? ORDER BY timestamp DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, producto.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int anterior = extraerStock(rs.getString("valores_anteriores"));
                        int nuevo = extraerStock(rs.getString("valores_nuevos"));
                        String usuario = rs.getObject("usuario_id") != null ? rs.getObject("usuario_id").toString() : "Desconocido";
                        String tipo = rs.getString("accion");
                        java.time.LocalDateTime fecha = rs.getTimestamp("timestamp").toLocalDateTime();
                        InventarioCommand cmd = new com.farmaciavictoria.proyectopharmavictoria.model.Inventario.AjusteStockCommand(
                            producto,
                            anterior,
                            nuevo,
                            usuario,
                            ""
                        );
                        java.lang.reflect.Field fFecha = InventarioCommand.class.getDeclaredField("fecha");
                        fFecha.setAccessible(true);
                        fFecha.set(cmd, fecha);
                        java.lang.reflect.Field fTipo = InventarioCommand.class.getDeclaredField("tipoMovimiento");
                        fTipo.setAccessible(true);
                        fTipo.set(cmd, tipo);
                        lista.add(cmd);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private static int extraerStock(String json) {
        // Espera formato: {"stock":123}
        if (json == null) return 0;
        try {
            int idx = json.indexOf(":");
            int idx2 = json.indexOf("}");
            if (idx > 0 && idx2 > idx) {
                return Integer.parseInt(json.substring(idx + 1, idx2));
            }
        } catch (Exception e) {
            // Si falla, retorna 0
        }
        return 0;
    }
}
