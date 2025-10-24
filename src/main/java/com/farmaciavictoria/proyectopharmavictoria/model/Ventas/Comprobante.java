package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

import java.time.LocalDateTime;

public class Comprobante {
    private int id;
    private Venta venta;
    private String tipo; // BOLETA, FACTURA
    private String serie;
    private String numero;
    private String hashSunat;
    private String estadoSunat; // GENERADO, ENVIADO, ACEPTADO, RECHAZADO
    private LocalDateTime fechaEmision;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getHashSunat() { return hashSunat; }
    public void setHashSunat(String hashSunat) { this.hashSunat = hashSunat; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
}
