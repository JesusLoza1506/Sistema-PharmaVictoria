package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

import java.time.LocalDateTime;

public abstract class InventarioCommand {
    protected Producto producto;
    protected int cantidadAnterior;
    protected int cantidadNueva;
    protected String usuario;
    protected LocalDateTime fecha;
    protected String tipoMovimiento;
    protected String observacion;

    public InventarioCommand(Producto producto, int cantidadAnterior, int cantidadNueva, String usuario, String tipoMovimiento, String observacion) {
        this.producto = producto;
        this.cantidadAnterior = cantidadAnterior;
        this.cantidadNueva = cantidadNueva;
        this.usuario = usuario;
        this.fecha = LocalDateTime.now();
        this.tipoMovimiento = tipoMovimiento;
        this.observacion = observacion;
    }

    public abstract void ejecutar();
    public abstract void deshacer();

    public Producto getProducto() { return producto; }
    public int getCantidadAnterior() { return cantidadAnterior; }
    public int getCantidadNueva() { return cantidadNueva; }
    public String getUsuario() { return usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public String getObservacion() { return observacion; }
}
