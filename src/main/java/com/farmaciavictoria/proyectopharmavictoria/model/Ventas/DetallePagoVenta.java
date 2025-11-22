package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

public class DetallePagoVenta {
    private int id;
    private int ventaId;
    private String tipoPago; // EFECTIVO, TARJETA, TRANSFERENCIA
    private double monto;

    public DetallePagoVenta() {
    }

    public DetallePagoVenta(int ventaId, String tipoPago, double monto) {
        this.ventaId = ventaId;
        this.tipoPago = tipoPago;
        this.monto = monto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
