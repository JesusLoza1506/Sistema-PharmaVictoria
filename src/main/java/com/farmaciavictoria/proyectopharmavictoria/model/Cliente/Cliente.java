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
    private String nombres;

    // Permite setear el nombre completo (divide en nombres y apellidos si es
    // posible)
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
        if (createdAt == null)
            return false;
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        return createdAt.getYear() == ahora.getYear() && createdAt.getMonth() == ahora.getMonth();
    }

    // ✅ Campos de la tabla actual
    private Integer id;
    private String apellidos;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDate fechaNacimiento;
    // Soporte para clientes naturales y empresariales
    private String tipoCliente; // "NATURAL" o "EMPRESARIAL"
    private String razonSocial; // Solo para empresariales
    private String documento; // DNI o RUC según tipoCliente

    // Sistema de puntos
    private Integer puntosTotales;
    private Integer puntosUsados;
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

    public Cliente(String documento, String nombres, String apellidos) {
        this();
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    // ✅ MÉTODOS DE NEGOCIO

    /**
     * Obtiene el nombre completo o razón social del cliente
     */
    public String getNombreCompleto() {
        if (tipoCliente != null && tipoCliente.equalsIgnoreCase("Empresarial")) {
            if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                return razonSocial;
            }
            return ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim();
        } else {
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
    }

    /**
     * Devuelve el documento principal según tipo de cliente
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Calcula los puntos disponibles
     */
    public Integer getPuntosDisponibles() {
        if (puntosTotales == null)
            puntosTotales = 0;
        if (puntosUsados == null)
            puntosUsados = 0;
        return puntosTotales - puntosUsados;
    }

    /**
     * Agrega puntos al cliente
     */
    public void agregarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0) {
            if (puntosTotales == null)
                puntosTotales = 0;
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
            if (puntosUsados == null)
                puntosUsados = 0;
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
        return documento != null && documento.matches("^\\d{8}$");
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
    // ...existing code...

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

    // ...existing code...

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombres() {
        return nombres;
    }

    // ...existing code...

    // Removed duplicate getters and setters for nombres, apellidos, telefono, and
    // documento

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
                ", esFrecuente=" + esFrecuente +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Cliente that = (Cliente) obj;
        return documento != null ? documento.equals(that.documento) : that.documento == null;
    }

    @Override
    public int hashCode() {
        return documento != null ? documento.hashCode() : 0;
    }
}