package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import java.time.LocalDateTime;

public class ClienteExportAudit {
    private Integer id;
    private String usuario;
    private LocalDateTime fecha;
    private String tipoArchivo; // Excel o PDF
    private String rutaArchivo;
    private Integer cantidadClientes;

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public Integer getCantidadClientes() { return cantidadClientes; }
    public void setCantidadClientes(Integer cantidadClientes) { this.cantidadClientes = cantidadClientes; }
}
