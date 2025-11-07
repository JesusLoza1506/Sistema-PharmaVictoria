package com.farmaciavictoria.proyectopharmavictoria.reportes.view;

public class VentaPorProductoDTO {
    private String producto;
    private int cantidadVendida;
    private double totalVentas;

    public VentaPorProductoDTO(String producto, int cantidadVendida, double totalVentas) {
        this.producto = producto;
        this.cantidadVendida = cantidadVendida;
        this.totalVentas = totalVentas;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }
}
