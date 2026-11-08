package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetallePagoVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetallePagoVentaRepositoryJdbcImpl;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

final class PagosMixtosVenta {
    private PagosMixtosVenta() {
    }

    static void guardar(PanelPagoMixto panel, Venta venta, Consumer<String> mostrarMensaje) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getInstance().getConnection();
            DetallePagoVentaRepositoryJdbcImpl repository = new DetallePagoVentaRepositoryJdbcImpl(conn);
            int ventaId = venta.getId();
            System.out.println("[DEBUG MIXTO] Guardando pagos mixtos para ventaId=" + ventaId);
            guardarSiSeleccionado(repository, ventaId, "EFECTIVO", panel.chkEfectivo(), panel.txtMontoEfectivo(), mostrarMensaje);
            guardarSiSeleccionado(repository, ventaId, "TARJETA", panel.chkTarjeta(), panel.txtMontoTarjeta(), mostrarMensaje);
            guardarSiSeleccionado(repository, ventaId, "TRANSFERENCIA", panel.chkTransferencia(),
                    panel.txtMontoTransferencia(), mostrarMensaje);
        } catch (Exception ex) {
            System.err.println("[ERROR MIXTO] Error al guardar pagos mixtos: " + ex.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void guardarSiSeleccionado(DetallePagoVentaRepositoryJdbcImpl repository, int ventaId, String tipo,
            CheckBox seleccionado, TextField campoMonto, Consumer<String> mostrarMensaje) {
        if (!seleccionado.isSelected()) {
            return;
        }
        double monto = PanelPagoMixto.parseMonto(campoMonto.getText());
        System.out.println("[DEBUG MIXTO] Intentando guardar pago: venta_id=" + ventaId
                + ", tipo_pago=" + tipo + ", monto=" + monto);
        if (!(ventaId > 0 && monto > 0)) {
            System.err.println("[ERROR MIXTO] Datos inválidos para " + tipo + ": venta_id=" + ventaId + ", monto=" + monto);
            return;
        }
        boolean ok = repository.save(new DetallePagoVenta(ventaId, tipo, monto));
        System.out.println("[DEBUG MIXTO] Pago " + tipo + " guardado: " + ok);
        if (!ok) {
            mostrarMensaje.accept("Error al guardar el pago " + tipo
                    + " en la tabla detalle_pago_venta. Verifica la conexión y los datos.");
        }
    }
}
