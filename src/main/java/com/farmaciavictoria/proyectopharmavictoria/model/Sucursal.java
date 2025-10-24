package com.farmaciavictoria.proyectopharmavictoria.model;

import java.time.LocalDateTime;

/**
 * Entidad Sucursal que corresponde al esquema de base de datos actual
 */
public class Sucursal {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private Boolean activa;
    private LocalDateTime createdAt;

    // Constructor vacío
    public Sucursal() {
        this.activa = true;
    }

    // Constructor completo
    public Sucursal(String nombre, String direccion, String telefono) {
        this();
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sucursal sucursal = (Sucursal) obj;
        return id != null ? id.equals(sucursal.id) : sucursal.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}