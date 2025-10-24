package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

import java.time.LocalDateTime;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

public class VentaHistorialCambio {
    private int id;
    private Venta venta;
    private String tipoCambio; // ANULACION, EDICION
    private String motivo;
    private Usuario usuario;
    private LocalDateTime fecha;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public String getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(String tipoCambio) { this.tipoCambio = tipoCambio; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
