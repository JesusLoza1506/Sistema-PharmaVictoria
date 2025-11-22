package com.farmaciavictoria.proyectopharmavictoria.controller.reportes.dto;

public class UsuarioVentaDTO {
    private String usuario;
    private String nombre;
    private double totalVentas;

    public UsuarioVentaDTO() {
    }

    public UsuarioVentaDTO(String usuario, String nombre, double totalVentas) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.totalVentas = totalVentas;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }
}
