package com.farmaciavictoria.proyectopharmavictoria.model.Usuario;

import java.time.LocalDateTime;

public class UsuarioAcceso {
    private LocalDateTime fechaAcceso;
    private String ip;
    private String dispositivo;

    public UsuarioAcceso(LocalDateTime fechaAcceso, String ip, String dispositivo) {
        this.fechaAcceso = fechaAcceso;
        this.ip = ip;
        this.dispositivo = dispositivo;
    }
    public LocalDateTime getFechaAcceso() { return fechaAcceso; }
    public String getIp() { return ip; }
    public String getDispositivo() { return dispositivo; }
}
