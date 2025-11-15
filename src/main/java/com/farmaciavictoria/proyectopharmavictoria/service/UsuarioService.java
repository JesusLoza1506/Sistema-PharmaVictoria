package com.farmaciavictoria.proyectopharmavictoria.service;

import java.time.LocalDateTime;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioAlerta;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialAcceso;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioHistorialCambio;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso;
import com.farmaciavictoria.proyectopharmavictoria.repository.*;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioAlertaRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioHistorialAccesoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioHistorialCambioRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioPermisoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service Layer para gestión de usuarios
 * Lógica de negocio, validaciones, auditoría y alertas
 */
public class UsuarioService {
    /**
     * Verifica si el usuario tiene el permiso granular especificado para el módulo
     * dado
     */
    public boolean tienePermiso(Long usuarioId, String modulo, String accion) {
        // Ejemplo: permiso = "proveedores.nuevo", "proveedores.exportar", etc.
        String clavePermiso = modulo + "." + accion;
        List<UsuarioPermiso> permisos = obtenerPermisos(usuarioId);
        for (UsuarioPermiso permiso : permisos) {
            if (permiso.getPermiso().equalsIgnoreCase(clavePermiso)) {
                return permiso.isValor();
            }
        }
        return false;
    }

    public UsuarioPermisoRepository getPermisoRepository() {
        return permisoRepository;
    }

    /**
     * Restablece la contraseña usando el token de recuperación.
     */
    public boolean restablecerPasswordConToken(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.buscarPorTokenRecuperacion(token);
        if (usuario == null)
            return false;
        if (usuario.getRecoveryTokenExpiry() == null
                || usuario.getRecoveryTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        String hash = com.farmaciavictoria.proyectopharmavictoria.util.PasswordUtil.hashPassword(nuevaPassword);
        boolean ok = usuarioRepository.actualizarPasswordYLimpiarToken(usuario.getId(), hash);
        return ok;
    }

    /**
     * Valida el token y actualiza la contraseña si es correcto y no expiró
     */
    public boolean validarYActualizarPassword(String email, String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null)
            return false;
        if (usuario.getRecoveryToken() == null || usuario.getRecoveryTokenExpiry() == null)
            return false;
        if (!usuario.getRecoveryToken().equals(token))
            return false;
        if (usuario.getRecoveryTokenExpiry().isBefore(java.time.LocalDateTime.now()))
            return false;
        // Actualizar contraseña (hash)
        String hash = hashPassword(nuevaPassword);
        boolean ok = usuarioRepository.setPassword(usuario.getId(), hash);
        if (ok) {
            // Limpiar token
            usuarioRepository.setRecoveryToken(usuario.getId(), null, null);
        }
        return ok;
    }

    // Utilidad para hashear contraseña (puedes adaptar a tu método real)
    private String hashPassword(String password) {
        // Ejemplo simple, reemplaza por tu lógica real (BCrypt, Argon2, etc.)
        return Integer.toHexString(password.hashCode());
    }

    /**
     * Genera un token alfanumérico de 6 caracteres
     */
    public String generarTokenRecuperacion() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            token.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return token.toString();
    }

    /**
     * Inicia el proceso de recuperación: genera y guarda el token
     */
    public boolean iniciarRecuperacion(String email) {
        System.out.println("[Recuperación] Iniciando recuperación para: " + email);
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            System.out.println("[Recuperación] Usuario no encontrado para el email: " + email);
            return false;
        }
        String token = generarTokenRecuperacion();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
        boolean ok = usuarioRepository.setRecoveryToken(usuario.getId(), token, expiry);
        if (ok) {
            usuario.setRecoveryToken(token);
            usuario.setRecoveryTokenExpiry(expiry);
            System.out.println("[Recuperación] Token generado: " + token + " Expira: " + expiry);
            try {
                new com.farmaciavictoria.proyectopharmavictoria.util.MailService()
                        .sendRecoveryTokenEmail(usuario.getEmail(), token);
            } catch (jakarta.mail.MessagingException e) {
                System.out.println("[Recuperación] Error al enviar correo:");
                e.printStackTrace();
            }
        } else {
            System.out.println("[Recuperación] No se pudo guardar el token en la base de datos.");
        }
        return ok;
    }

    // Actualizar estado activo/inactivo (alias para compatibilidad con controlador)
    public void actualizarEstado(Long usuarioId, boolean activo, String usuarioActual) {
        // adminId: usar valor por defecto si no se puede obtener
        Long adminId = 1L; // Puedes cambiar esto por la lógica real si tienes el usuario actual
        cambiarEstadoUsuario(usuarioId, activo, adminId);
    }

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHistorialCambioRepository historialCambioRepository;
    private final UsuarioHistorialAccesoRepository historialAccesoRepository;
    private final UsuarioAlertaRepository alertaRepository;
    private final UsuarioPermisoRepository permisoRepository;
    private static UsuarioService instance;

    private UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.historialCambioRepository = new UsuarioHistorialCambioRepository();
        this.historialAccesoRepository = new UsuarioHistorialAccesoRepository();
        this.alertaRepository = new UsuarioAlertaRepository();
        this.permisoRepository = new UsuarioPermisoRepository();
    }
    // Eliminada inicialización de SucursalRepository

    public static UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }

    // Registrar usuario
    public String registrarUsuario(Usuario usuario, Long adminId) {
        // Validaciones: username único, email único, DNI único
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return "Ya existe un usuario con ese nombre de usuario.";
        }
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                return "Ya existe un usuario con ese email.";
            }
        }
        if (usuario.getDni() != null && !usuario.getDni().isEmpty()) {
            if (usuarioRepository.existsByDni(usuario.getDni())) {
                return "Ya existe un usuario con ese DNI.";
            }
        }
        Usuario guardado = usuarioRepository.save(usuario);
        if (guardado == null) {
            return "No se pudo guardar el usuario en la base de datos.";
        }
        UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
        cambio.setUsuario_id((int) guardado.getId().longValue());
        cambio.setCampo_modificado("CREACION");
        cambio.setValor_anterior(null);
        cambio.setValor_nuevo("Usuario creado");
        cambio.setModificado_por(adminId != null ? adminId.intValue() : null);
        cambio.setFecha(java.time.LocalDateTime.now());
        historialCambioRepository.save(cambio);
        return null;
    }

    // Editar usuario
    public void editarUsuario(Usuario usuario, String campo, String valorAnterior, String valorNuevo, Long adminId) {
        usuarioRepository.update(usuario);
        UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
        cambio.setUsuario_id((int) usuario.getId().longValue());
        cambio.setCampo_modificado(campo);
        cambio.setValor_anterior(valorAnterior);
        cambio.setValor_nuevo(valorNuevo);
        cambio.setModificado_por(adminId != null ? adminId.intValue() : null);
        cambio.setFecha(java.time.LocalDateTime.now());
        historialCambioRepository.save(cambio);
    }

    // Eliminar usuario
    public void eliminarUsuario(Long usuarioId, Long adminId) {
        usuarioRepository.delete(usuarioId);
        UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
        cambio.setUsuario_id(usuarioId != null ? usuarioId.intValue() : 0);
        cambio.setCampo_modificado("ELIMINACION");
        cambio.setValor_anterior("Usuario activo");
        cambio.setValor_nuevo("Usuario eliminado");
        cambio.setModificado_por(adminId != null ? adminId.intValue() : null);
        cambio.setFecha(java.time.LocalDateTime.now());
        historialCambioRepository.save(cambio);
    }

    // Cambiar estado activo/inactivo
    public void cambiarEstadoUsuario(Long usuarioId, boolean activo, Long adminId) {
        usuarioRepository.setActivo(usuarioId, activo);
        UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
        cambio.setUsuario_id(usuarioId != null ? usuarioId.intValue() : 0);
        cambio.setCampo_modificado("ESTADO");
        cambio.setValor_anterior(activo ? "Inactivo" : "Activo");
        cambio.setValor_nuevo(activo ? "Activo" : "Inactivo");
        cambio.setModificado_por(adminId != null ? adminId.intValue() : null);
        cambio.setFecha(java.time.LocalDateTime.now());
        historialCambioRepository.save(cambio);
    }

    // Cambiar contraseña
    public void cambiarContrasena(Long usuarioId, String nuevaHash, Long adminId) {
        usuarioRepository.setPassword(usuarioId, nuevaHash);
        UsuarioHistorialCambio cambio = new UsuarioHistorialCambio();
        cambio.setUsuario_id(usuarioId != null ? usuarioId.intValue() : 0);
        cambio.setCampo_modificado("CONTRASENA");
        cambio.setValor_anterior("-");
        cambio.setValor_nuevo("Hash actualizado");
        cambio.setModificado_por(adminId != null ? adminId.intValue() : null);
        cambio.setFecha(java.time.LocalDateTime.now());
        historialCambioRepository.save(cambio);
    }
    // Devuelve nombre y apellido del usuario por su ID
    // Eliminado: obtenerNombreCompletoUsuario

    // Registrar acceso
    public void registrarAcceso(Long usuarioId, boolean exito, String ip, String userAgent, String motivoFallo) {
        UsuarioHistorialAcceso acceso = new UsuarioHistorialAcceso();
        acceso.setUsuarioId(usuarioId);
        acceso.setFechaAcceso(java.time.LocalDateTime.now());
        acceso.setIpAddress(ip);
        acceso.setUserAgent(userAgent);
        acceso.setExito(exito);
        acceso.setMotivoFallo(motivoFallo);
        historialAccesoRepository.save(acceso);
        if (!exito) {
            UsuarioAlerta alerta = new UsuarioAlerta();
            alerta.setUsuarioId(usuarioId);
            alerta.setTipoAlerta("ACCESO_FALLIDO");
            alerta.setMensaje("Intento fallido: " + motivoFallo);
            alerta.setFecha(java.time.LocalDateTime.now());
            alerta.setLeida(false);
            alertaRepository.save(alerta);
        }
    }

    // Obtener usuarios, historial, alertas, permisos
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<UsuarioHistorialCambio> obtenerHistorialCambios(Long usuarioId) {
        return historialCambioRepository.findByUsuarioId(usuarioId != null ? usuarioId.intValue() : 0);
    }

    public List<UsuarioHistorialAcceso> obtenerHistorialAccesos(Long usuarioId) {
        return historialAccesoRepository.findByUsuarioId(usuarioId);
    }

    public List<UsuarioAlerta> obtenerAlertas(Long usuarioId) {
        return alertaRepository.findByUsuarioId(usuarioId);
    }

    public List<UsuarioPermiso> obtenerPermisos(Long usuarioId) {
        return permisoRepository.findByUsuarioId(usuarioId);
    }

    // Devuelve lista de nombres de sucursales activas
    public List<String> obtenerNombresSucursales() {
        List<String> nombres = new java.util.ArrayList<>();

        // Método obtenerNombresSucursales eliminado
        return nombres;
    }

    // Devuelve todos los usuarios
    public List<Usuario> obtenerTodos() {
        return obtenerUsuarios();
    }
}
