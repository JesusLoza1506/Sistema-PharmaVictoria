package com.farmaciavictoria.proyectopharmavictoria.model.Usuario;

import java.time.LocalDateTime;

/**
 * Alerta asociada a un usuario (inactividad, accesos fallidos, etc)
 */
public class UsuarioAlerta {
    private Long id;
    private Long usuarioId;
    private String tipoAlerta;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida;

    public UsuarioAlerta() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
