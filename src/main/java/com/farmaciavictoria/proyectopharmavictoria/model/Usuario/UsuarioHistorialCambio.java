package com.farmaciavictoria.proyectopharmavictoria.model.Usuario;

import java.time.LocalDateTime;

public class UsuarioHistorialCambio {
    private int id;
    private int usuario_id;
    private String campo_modificado;
    private String valor_anterior;
    private String valor_nuevo;
    private Integer modificado_por;
    private LocalDateTime fecha;

    public UsuarioHistorialCambio() {
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getCampo_modificado() {
        return campo_modificado;
    }

    public void setCampo_modificado(String campo_modificado) {
        this.campo_modificado = campo_modificado;
    }

    public String getValor_anterior() {
        return valor_anterior;
    }

    public void setValor_anterior(String valor_anterior) {
        this.valor_anterior = valor_anterior;
    }

    public String getValor_nuevo() {
        return valor_nuevo;
    }

    public void setValor_nuevo(String valor_nuevo) {
        this.valor_nuevo = valor_nuevo;
    }

    public Integer getModificado_por() {
        return modificado_por;
    }

    public void setModificado_por(Integer modificado_por) {
        this.modificado_por = modificado_por;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
