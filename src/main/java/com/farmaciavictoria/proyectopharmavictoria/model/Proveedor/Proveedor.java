package com.farmaciavictoria.proyectopharmavictoria.model.Proveedor;

import java.time.LocalDateTime;

/**
 * Entidad Proveedor que corresponde al esquema de base de datos actual
 */
public class Proveedor {
    private Integer id;
    private String razonSocial;
    private String ruc;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private String condicionesPago;
    private String observaciones;
    private String tipo; // Nuevo campo para filtro por tipo
    private Boolean activo;
    private LocalDateTime createdAt;

    // Constructor vacío
        public Proveedor() {}

    // Constructor completo
    public Proveedor(String razonSocial, String ruc, String contacto, String telefono, 
                    String email, String direccion, String condicionesPago, String observaciones) {
        this();
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.condicionesPago = condicionesPago;
        this.observaciones = observaciones;
    }
        // Mejoras sugeridas
        private Integer sucursalId;      // FK a sucursales (puede ser null)
        private String tipoProducto;     // Opcional, para filtrar por tipo de producto

        public Integer getSucursalId() { return sucursalId; }
        public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

        public String getTipoProducto() { return tipoProducto; }
        public void setTipoProducto(String tipoProducto) { this.tipoProducto = tipoProducto; }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCondicionesPago() {
        return condicionesPago;
    }

    public void setCondicionesPago(String condicionesPago) {
        this.condicionesPago = condicionesPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }
    
    public boolean isActivo() {
        return activo != null ? activo : false;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return razonSocial;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proveedor proveedor = (Proveedor) obj;
        return ruc != null ? ruc.equals(proveedor.ruc) : proveedor.ruc == null;
    }

    @Override
    public int hashCode() {
        return ruc != null ? ruc.hashCode() : 0;
    }
}