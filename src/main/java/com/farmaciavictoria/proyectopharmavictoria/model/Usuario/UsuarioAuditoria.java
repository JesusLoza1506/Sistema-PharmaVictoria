package com.farmaciavictoria.proyectopharmavictoria.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioAuditoria {
    private LocalDateTime fecha;
    private String accion;
    private String realizadoPor;
    private String detalle;

    public UsuarioAuditoria(LocalDateTime fecha, String accion, String realizadoPor, String detalle) {
        this.fecha = fecha;
        this.accion = accion;
        this.realizadoPor = realizadoPor;
        this.detalle = detalle;
    }
    public LocalDateTime getFecha() { return fecha; }
    public String getAccion() { return accion; }
    public String getRealizadoPor() { return realizadoPor; }
    public String getDetalle() { return detalle; }
}
