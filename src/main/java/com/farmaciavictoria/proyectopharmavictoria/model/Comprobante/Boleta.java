package com.farmaciavictoria.proyectopharmavictoria.model.Comprobante;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;

public class Boleta implements Comprobante {
    public double getIgv() {
        return igv;
    }
    public java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta> getProductos() {
        return productos;
    }
    private Cliente cliente;
    private List<DetalleVenta> productos;
    private double subtotal;
    private double igv;
    private double total;
    // Otros campos necesarios

    public Boleta(Cliente cliente, List<DetalleVenta> productos, double subtotal, double igv, double total) {
        this.cliente = cliente;
        this.productos = productos;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
    }

    @Override
    public void generar() {
        // Lógica para calcular totales, asignar datos, etc.
    }

    @Override
    public void imprimir() {
        // Lógica para imprimir en impresora predeterminada
    }

    public Cliente getCliente() {
        return cliente;
    }

    public double getTotal() {
        return total;
    }
}
