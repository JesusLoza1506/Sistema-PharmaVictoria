package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;

public class BoletaBuilder {
    private Cliente cliente;
    private List<DetalleVenta> productos;
    private double subtotal;
    private double igv;
    private double total;

    public BoletaBuilder conCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public BoletaBuilder conProductos(List<DetalleVenta> productos) {
        this.productos = productos;
        return this;
    }

    public BoletaBuilder conTotales(double subtotal, double igv, double total) {
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        return this;
    }

    public Boleta build() {
        return new Boleta(cliente, productos, subtotal, igv, total);
    }
}
