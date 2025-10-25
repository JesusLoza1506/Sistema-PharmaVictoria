package com.farmaciavictoria.proyectopharmavictoria.model.Usuario;

import java.time.LocalDateTime;

/**
 * Entidad Usuario del sistema
 * Representa a los usuarios que pueden acceder al sistema (Admin y Vendedor)
 */
public class Usuario {
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    private Long id;
    private String username;
    private String passwordHash;
    private Rol rol;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    // NUEVO: historial de accesos y auditoría
    private java.util.List<UsuarioAcceso> accesos = new java.util.ArrayList<>();
    private java.util.List<UsuarioAuditoria> auditoria = new java.util.ArrayList<>();

    // Recuperación de contraseña
    private String recoveryToken;
    private LocalDateTime recoveryTokenExpiry;

    // Enum para roles
    public enum Rol {
        ADMIN("Administrador"),
        VENDEDOR("Vendedor");

        private final String descripcion;

        Rol(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Constructor vacío
    public Usuario() {
        this.activo = true;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor completo
    public Usuario(String username, String passwordHash, Rol rol,
            String nombres, String apellidos, String dni) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
    }

    // Getters y Setters
    public String getRecoveryToken() {
        return recoveryToken;
    }

    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    public LocalDateTime getRecoveryTokenExpiry() {
        return recoveryTokenExpiry;
    }

    public void setRecoveryTokenExpiry(LocalDateTime recoveryTokenExpiry) {
        this.recoveryTokenExpiry = recoveryTokenExpiry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // NUEVO: para panel de detalles
    public LocalDateTime getUltimoAcceso() {
        return lastLogin;
    }

    public java.util.List<UsuarioAcceso> getAccesos() {
        return accesos;
    }

    public void setAccesos(java.util.List<UsuarioAcceso> accesos) {
        this.accesos = accesos;
    }

    public java.util.List<UsuarioAuditoria> getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(java.util.List<UsuarioAuditoria> auditoria) {
        this.auditoria = auditoria;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean isAdmin() {
        return rol == Rol.ADMIN;
    }

    public boolean isVendedor() {
        return rol == Rol.VENDEDOR;
    }

    public void actualizarUltimoLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // toString para debugging
    @Override
    public String toString() {
        return String.format("Usuario{id=%d, username='%s', rol=%s, nombres='%s', apellidos='%s', activo=%s}",
                id, username, rol, nombres, apellidos, activo);
    }

    // equals y hashCode basados en username (que es único)
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Usuario usuario = (Usuario) obj;
        return username != null && username.equals(usuario.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}