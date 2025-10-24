package com.farmaciavictoria.proyectopharmavictoria.model.Cliente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * ✅ ENTITY MODEL - Cliente
 * Modelo simplificado para trabajar con la tabla clientes actual
 * Mantiene patrones enterprise con campos básicos
 * 
 * @author PHARMAVICTORIA Development Team
 * @version 1.0 (Tabla Básica)
 */
public class Cliente {
    // Permite setear el nombre completo (divide en nombres y apellidos si es posible)
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            this.nombres = "";
            this.apellidos = "";
        } else {
            String[] partes = nombreCompleto.trim().split(" ", 2);
            this.nombres = partes[0];
            this.apellidos = partes.length > 1 ? partes[1] : "";
        }
    }

    // Devuelve true si es frecuente
    public boolean isFrecuente() {
        return Boolean.TRUE.equals(esFrecuente);
    }

    // Devuelve true si el cliente fue creado este mes
    public boolean esNuevoEsteMes() {
        if (createdAt == null) return false;
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        return createdAt.getYear() == ahora.getYear() && createdAt.getMonth() == ahora.getMonth();
    }
    
    // ✅ Campos de la tabla actual
    private Integer id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDate fechaNacimiento;
    
    // Sistema de puntos
    private Integer puntosTotales;
    private Integer puntosUsados;
    private Integer puntosDisponibles; // Campo calculado
    private Boolean esFrecuente;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // ✅ CONSTRUCTORES
    public Cliente() {
        this.puntosTotales = 0;
        this.puntosUsados = 0;
        this.esFrecuente = false;
    this.createdAt = LocalDateTime.now();
    }
    
    public Cliente(String dni, String nombres, String apellidos) {
        this();
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }
    
    // ✅ MÉTODOS DE NEGOCIO
    
    /**
     * Obtiene el nombre completo del cliente
     */
    public String getNombreCompleto() {
        if (nombres == null && apellidos == null) {
            return "Sin nombre";
        }
        if (nombres == null) {
            return apellidos;
        }
        if (apellidos == null) {
            return nombres;
        }
        return nombres + " " + apellidos;
    }
    
    /**
     * Calcula los puntos disponibles
     */
    public Integer getPuntosDisponibles() {
        if (puntosTotales == null) puntosTotales = 0;
        if (puntosUsados == null) puntosUsados = 0;
        return puntosTotales - puntosUsados;
    }
    
    /**
     * Agrega puntos al cliente
     */
    public void agregarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0) {
            if (puntosTotales == null) puntosTotales = 0;
            puntosTotales += puntos;
            
            // Marcar como frecuente si tiene más de 500 puntos
            if (getPuntosDisponibles() >= 500) {
                esFrecuente = true;
            }
        }
    }
    
    /**
     * Redime puntos del cliente
     */
    public boolean redimirPuntos(Integer puntos) {
        if (puntos == null || puntos <= 0) {
            return false;
        }
        
        if (getPuntosDisponibles() >= puntos) {
            if (puntosUsados == null) puntosUsados = 0;
            puntosUsados += puntos;
            return true;
        }
        
        return false;
    }
    
    /**
     * Calcula la edad del cliente
     */
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
    
    /**
     * Verifica si es un cliente VIP
     */
    public boolean esClienteVip() {
        return getPuntosDisponibles() >= 1000;
    }
    
    /**
     * Valida si el DNI tiene formato correcto
     */
    public boolean tieneDniValido() {
        return dni != null && dni.matches("^\\d{8}$");
    }
    
    /**
     * Valida si el email tiene formato correcto
     */
    public boolean tieneEmailValido() {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email opcional
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    
    // ✅ GETTERS Y SETTERS
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public String getNombres() {
        return nombres;
    }
    
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    
    public String getApellidos() {
        return apellidos;
    }
    
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public Integer getPuntosTotales() {
        return puntosTotales;
    }
    
    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }
    
    public Integer getPuntosUsados() {
        return puntosUsados;
    }
    
    public void setPuntosUsados(Integer puntosUsados) {
        this.puntosUsados = puntosUsados;
    }
    
    public Boolean getEsFrecuente() {
        return esFrecuente;
    }
    
    public void setEsFrecuente(Boolean esFrecuente) {
        this.esFrecuente = esFrecuente;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ✅ MÉTODOS AUXILIARES
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", puntosDisponibles=" + getPuntosDisponibles() +
                ", esFrecuente=" + esFrecuente +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cliente that = (Cliente) obj;
        return dni != null ? dni.equals(that.dni) : that.dni == null;
    }
    
    @Override
    public int hashCode() {
        return dni != null ? dni.hashCode() : 0;
    }
}