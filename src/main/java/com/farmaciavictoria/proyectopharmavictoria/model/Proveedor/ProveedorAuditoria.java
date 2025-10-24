package com.farmaciavictoria.proyectopharmavictoria.model.Proveedor;

import java.time.LocalDateTime;

public class ProveedorAuditoria {
    private LocalDateTime fecha;
    private String usuario;
    private String accion;
    private String detalles;

    public ProveedorAuditoria(LocalDateTime fecha, String usuario, String accion, String detalles) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.accion = accion;
        this.detalles = detalles;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getAccion() { return accion; }
    public String getDetalles() { return detalles; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", fecha, usuario, accion, detalles);
    }
}
