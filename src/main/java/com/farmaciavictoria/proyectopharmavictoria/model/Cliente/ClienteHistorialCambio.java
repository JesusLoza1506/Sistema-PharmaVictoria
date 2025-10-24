package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import java.time.LocalDateTime;

/**
 * Modelo para historial de cambios de clientes (auditoría)
 */
public class ClienteHistorialCambio {
    private Integer id;
    private Integer clienteId;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private String usuario;
    private LocalDateTime fecha;
    private String fechaFormateada;

    public ClienteHistorialCambio() {}

    public ClienteHistorialCambio(Integer clienteId, String campoModificado, String valorAnterior, String valorNuevo, String usuario, LocalDateTime fecha) {
        this.clienteId = clienteId;
        this.campoModificado = campoModificado;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getCampoModificado() { return campoModificado; }
    public void setCampoModificado(String campoModificado) { this.campoModificado = campoModificado; }
    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getFechaFormateada() { return fechaFormateada; }
    public void setFechaFormateada(String fechaFormateada) { this.fechaFormateada = fechaFormateada; }
}
