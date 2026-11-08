package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;

import java.math.BigDecimal;

public record TotalesCarrito(BigDecimal subtotal, BigDecimal descuento, BigDecimal igv, BigDecimal total) {
    private static final BigDecimal TASA_IGV = new BigDecimal("0.18");

    public static TotalesCarrito calcular(Iterable<DetalleVenta> carrito) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuento = BigDecimal.ZERO;

        for (DetalleVenta detalle : carrito) {
            if (detalle.getSubtotal() != null) {
                subtotal = subtotal.add(detalle.getSubtotal());
            }
            if (detalle.getDescuento() != null) {
                descuento = descuento.add(detalle.getDescuento());
            }
        }

        BigDecimal igv = subtotal.multiply(TASA_IGV);
        BigDecimal total = subtotal.subtract(descuento).add(igv);
        return new TotalesCarrito(subtotal, descuento, igv, total);
    }
}
