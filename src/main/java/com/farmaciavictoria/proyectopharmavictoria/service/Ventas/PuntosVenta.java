package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;

public final class PuntosVenta {
    private PuntosVenta() {
    }

    public static int calcularGanados(Venta venta) {
        if (venta == null || venta.getTotal() == null)
            return 0;
        return venta.getTotal().intValue();
    }
}
