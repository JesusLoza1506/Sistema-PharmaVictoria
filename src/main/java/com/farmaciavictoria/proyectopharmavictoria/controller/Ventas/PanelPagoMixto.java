package com.farmaciavictoria.proyectopharmavictoria.controller.Ventas;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

record PanelPagoMixto(HBox panelPagoMixto, TextField txtMontoEfectivo, TextField txtMontoTarjeta,
        TextField txtMontoTransferencia, CheckBox chkEfectivo, CheckBox chkTarjeta,
        CheckBox chkTransferencia, Label lblTotalMixto, Label lblTotal) {

    void configurar(ComboBox<String> comboPago) {
        // --- MIXTO: Inicialización de controles (Ocultar al inicio) ---
        panelPagoMixto.setVisible(false);
        panelPagoMixto.setManaged(false);
        txtMontoEfectivo.setVisible(false);
        txtMontoEfectivo.setManaged(false);
        txtMontoTarjeta.setVisible(false);
        txtMontoTarjeta.setManaged(false);
        txtMontoTransferencia.setVisible(false);
        txtMontoTransferencia.setManaged(false);

        // Listener para mostrar/ocultar el panel de Pago Mixto
        comboPago.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("MIXTO".equals(newVal)) {
                panelPagoMixto.setVisible(true);
                panelPagoMixto.setManaged(true);
                // Esto asume que lblTotal ya tiene el valor correcto antes de seleccionar MIXTO
                lblTotalMixto.setText(lblTotal.getText());
            } else {
                panelPagoMixto.setVisible(false);
                panelPagoMixto.setManaged(false);
                // Limpiar y ocultar todos los campos mixtos
                chkEfectivo.setSelected(false);
                chkTarjeta.setSelected(false);
                chkTransferencia.setSelected(false);
                txtMontoEfectivo.clear();
                txtMontoTarjeta.clear();
                txtMontoTransferencia.clear();
                txtMontoEfectivo.setVisible(false);
                txtMontoEfectivo.setManaged(false);
                txtMontoTarjeta.setVisible(false);
                txtMontoTarjeta.setManaged(false);
                txtMontoTransferencia.setVisible(false);
                txtMontoTransferencia.setManaged(false);
            }
        });

        // Listeners para mostrar/ocultar los campos de monto al seleccionar checkbox
        chkEfectivo.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtMontoEfectivo.setVisible(newVal);
            txtMontoEfectivo.setManaged(newVal);
            if (!newVal)
                txtMontoEfectivo.clear();
            if (panelPagoMixto.isVisible())
                actualizarMontosMixto("CHECKBOX_CHANGE");
        });
        chkTarjeta.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtMontoTarjeta.setVisible(newVal);
            txtMontoTarjeta.setManaged(newVal);
            if (!newVal)
                txtMontoTarjeta.clear();
            if (panelPagoMixto.isVisible())
                actualizarMontosMixto("CHECKBOX_CHANGE");
        });
        chkTransferencia.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtMontoTransferencia.setVisible(newVal);
            txtMontoTransferencia.setManaged(newVal);
            if (!newVal)
                txtMontoTransferencia.clear();
            if (panelPagoMixto.isVisible())
                actualizarMontosMixto("CHECKBOX_CHANGE");
        });

        // Listener para autocompletar solo al presionar ENTER en los campos de pago
        // mixto
        txtMontoEfectivo.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                // Formatear el valor ingresado a dos decimales
                try {
                    double valor = parseMonto(txtMontoEfectivo.getText());
                    txtMontoEfectivo.setText(String.format("%.2f", valor));
                } catch (Exception ex) {
                }
                actualizarMontosMixto("EFECTIVO");
            }
        });
        txtMontoTarjeta.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                try {
                    double valor = parseMonto(txtMontoTarjeta.getText());
                    txtMontoTarjeta.setText(String.format("%.2f", valor));
                } catch (Exception ex) {
                }
                actualizarMontosMixto("TARJETA");
            }
        });
        txtMontoTransferencia.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                try {
                    double valor = parseMonto(txtMontoTransferencia.getText());
                    txtMontoTransferencia.setText(String.format("%.2f", valor));
                } catch (Exception ex) {
                }
                actualizarMontosMixto("TRANSFERENCIA");
            }
        });
    }

    private void actualizarMontosMixto(String tipoEditado) {
        // Obtiene el total de la venta desde el label principal
        double totalVenta = 0.0;
        try {
            String txtTotal = lblTotal.getText().replace("Total: S/", "").replace(",", ".").trim();
            totalVenta = Double.parseDouble(txtTotal);
        } catch (Exception e) {
            totalVenta = 0.0;
        }

        // Leer montos actuales
        double montoEfectivo = chkEfectivo.isSelected() ? parseMonto(txtMontoEfectivo.getText()) : 0.0;
        double montoTarjeta = chkTarjeta.isSelected() ? parseMonto(txtMontoTarjeta.getText()) : 0.0;
        double montoTransferencia = chkTransferencia.isSelected() ? parseMonto(txtMontoTransferencia.getText()) : 0.0;

        // Contar cuántos métodos están activos
        int activos = 0;
        if (chkEfectivo.isSelected())
            activos++;
        if (chkTarjeta.isSelected())
            activos++;
        if (chkTransferencia.isSelected())
            activos++;

        // Si solo hay uno activo, poner el total ahí
        if (activos == 1) {
            if (chkEfectivo.isSelected())
                txtMontoEfectivo.setText(String.format("%.2f", totalVenta));
            if (chkTarjeta.isSelected())
                txtMontoTarjeta.setText(String.format("%.2f", totalVenta));
            if (chkTransferencia.isSelected())
                txtMontoTransferencia.setText(String.format("%.2f", totalVenta));
            montoEfectivo = chkEfectivo.isSelected() ? totalVenta : 0.0;
            montoTarjeta = chkTarjeta.isSelected() ? totalVenta : 0.0;
            montoTransferencia = chkTransferencia.isSelected() ? totalVenta : 0.0;
        } else if (activos == 2) {
            // Si hay dos activos, ajustar el que NO se está editando
            double montoEditado = 0.0;
            if ("EFECTIVO".equals(tipoEditado)) {
                montoEditado = montoEfectivo;
                if (chkTarjeta.isSelected() && chkTransferencia.isSelected()) {
                    // Si ambos están activos, no se puede saber cuál ajustar, dejar ambos editables
                } else if (chkTarjeta.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoTarjeta.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoTarjeta = Math.max(restante, 0.0);
                } else if (chkTransferencia.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoTransferencia.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoTransferencia = Math.max(restante, 0.0);
                }
                // Formatear los montos ingresados a dos decimales si el campo está activo y el
                // texto no está vacío
                if (chkEfectivo.isSelected() && !txtMontoEfectivo.getText().isEmpty()) {
                    txtMontoEfectivo.setText(String.format("%.2f", montoEfectivo));
                }
                if (chkTarjeta.isSelected() && !txtMontoTarjeta.getText().isEmpty()) {
                    txtMontoTarjeta.setText(String.format("%.2f", montoTarjeta));
                }
                if (chkTransferencia.isSelected() && !txtMontoTransferencia.getText().isEmpty()) {
                    txtMontoTransferencia.setText(String.format("%.2f", montoTransferencia));
                }
            } else if ("TARJETA".equals(tipoEditado)) {
                montoEditado = montoTarjeta;
                if (chkEfectivo.isSelected() && chkTransferencia.isSelected()) {
                    // Si ambos están activos, no se puede saber cuál ajustar, dejar ambos editables
                } else if (chkEfectivo.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoEfectivo.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoEfectivo = Math.max(restante, 0.0);
                } else if (chkTransferencia.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoTransferencia.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoTransferencia = Math.max(restante, 0.0);
                }
            } else if ("TRANSFERENCIA".equals(tipoEditado)) {
                montoEditado = montoTransferencia;
                if (chkEfectivo.isSelected() && chkTarjeta.isSelected()) {
                    // Si ambos están activos, no se puede saber cuál ajustar, dejar ambos editables
                } else if (chkEfectivo.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoEfectivo.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoEfectivo = Math.max(restante, 0.0);
                } else if (chkTarjeta.isSelected()) {
                    double restante = totalVenta - montoEditado;
                    txtMontoTarjeta.setText(String.format("%.2f", Math.max(restante, 0.0)));
                    montoTarjeta = Math.max(restante, 0.0);
                }
            } else if ("CHECKBOX_CHANGE".equals(tipoEditado)) {
                // Si se activa/desactiva un método, limpiar los montos y recalcular
                if (activos == 2) {
                    // Poner 0 en ambos y el total en el primero
                    if (chkEfectivo.isSelected() && chkTarjeta.isSelected()) {
                        txtMontoEfectivo.setText(String.format("%.2f", totalVenta));
                        txtMontoTarjeta.setText("0.00");
                    } else if (chkEfectivo.isSelected() && chkTransferencia.isSelected()) {
                        txtMontoEfectivo.setText(String.format("%.2f", totalVenta));
                        txtMontoTransferencia.setText("0.00");
                    } else if (chkTarjeta.isSelected() && chkTransferencia.isSelected()) {
                        txtMontoTarjeta.setText(String.format("%.2f", totalVenta));
                        txtMontoTransferencia.setText("0.00");
                    }
                }
            }
        } else if (activos == 3) {
            // Lógica para tres métodos activos
            if ("EFECTIVO".equals(tipoEditado)) {
                // El usuario editó EFECTIVO, mantener TARJETA y recalcular TRANSFERENCIA
                double nuevoTransferencia = totalVenta - montoEfectivo - montoTarjeta;
                txtMontoTransferencia.setText(String.format("%.2f", Math.max(nuevoTransferencia, 0.0)));
                montoTransferencia = Math.max(nuevoTransferencia, 0.0);
            } else if ("TARJETA".equals(tipoEditado)) {
                // El usuario editó TARJETA, mantener EFECTIVO y recalcular TRANSFERENCIA
                double nuevoTransferencia = totalVenta - montoEfectivo - montoTarjeta;
                txtMontoTransferencia.setText(String.format("%.2f", Math.max(nuevoTransferencia, 0.0)));
                montoTransferencia = Math.max(nuevoTransferencia, 0.0);
            } else if ("TRANSFERENCIA".equals(tipoEditado)) {
                // El usuario editó TRANSFERENCIA, mantener EFECTIVO y recalcular TARJETA
                double nuevoTarjeta = totalVenta - montoEfectivo - montoTransferencia;
                txtMontoTarjeta.setText(String.format("%.2f", Math.max(nuevoTarjeta, 0.0)));
                montoTarjeta = Math.max(nuevoTarjeta, 0.0);
            } else if ("CHECKBOX_CHANGE".equals(tipoEditado)) {
                // Si se activa/desactiva un método, limpiar los montos y recalcular
                txtMontoEfectivo.setText("");
                txtMontoTarjeta.setText("");
                txtMontoTransferencia.setText("");
            }
        }
        // Recalcular suma final
        double sumaFinal = montoEfectivo + montoTarjeta + montoTransferencia;
        lblTotalMixto.setText(String.format("Total: S/ %.2f", sumaFinal));
    }

    static double parseMonto(String txt) {
        try {
            return Double.parseDouble(txt.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
