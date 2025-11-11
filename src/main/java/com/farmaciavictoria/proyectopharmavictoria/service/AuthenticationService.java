package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación para PHARMAVICTORIA
 * Maneja la validación de credenciales y sesiones de usuario
 */
public class AuthenticationService {
    // Usuario autenticado actual
    private static Usuario usuarioActual;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsuarioRepository usuarioRepository;

    public AuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica un usuario con username y password
     * 
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado o null si las credenciales son incorrectas
     */
    public Usuario authenticate(String username, String password) {
        try {
            logger.debug("Iniciando autenticación para usuario: {}", username);

            // Validar parámetros
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Username vacío o nulo");
                return null;
            }

            if (password == null || password.isEmpty()) {
                logger.warn("Password vacío o nulo");
                return null;
            }

            // Buscar usuario en la base de datos
            Usuario usuario = usuarioRepository.findByUsername(username.trim()).orElse(null);

            if (usuario == null) {
                logger.warn("Usuario no encontrado: {}", username);
                return null;
            }

            // Verificar si el usuario está activo
            if (!usuario.isActivo()) {
                logger.warn("Intento de login con usuario inactivo: {}", username);
                return null;
            }

            // Verificar contraseña
            if (!verifyPassword(password, usuario.getPasswordHash())) {
                logger.warn("Contraseña incorrecta para usuario: {}", username);
                return null;
            }

            logger.info("Autenticación exitosa para usuario: {} ({})", username, usuario.getRol());
            // Guardar usuario autenticado como actual
            usuarioActual = usuario;

            // Registrar acceso exitoso en el historial a través del UsuarioService
            try {
                String ip;
                try {
                    ip = java.net.InetAddress.getLocalHost().getHostAddress();
                } catch (Exception e) {
                    ip = "127.0.0.1";
                }
                String userAgent = System.getProperty("user.name") + " - " + System.getProperty("os.name") + " "
                        + System.getProperty("os.version");
                com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService.getInstance()
                        .registrarAcceso(usuario.getId(), true, ip, userAgent, null);
            } catch (Exception ex) {
                logger.warn("No se pudo registrar el acceso en el historial via UsuarioService: {}", ex.getMessage());
            }

            return usuario;

        } catch (Exception e) {
            logger.error("Error durante autenticación de usuario {}: {}", username, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Devuelve el usuario autenticado actual
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado
     * 
     * @param plainPassword  Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la base de datos
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            if (plainPassword == null || hashedPassword == null) {
                return false;
            }

            // Verificar usando BCrypt
            return BCrypt.checkpw(plainPassword, hashedPassword);

        } catch (Exception e) {
            logger.error("Error al verificar contraseña: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Genera un hash de contraseña usando BCrypt
     * 
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public String hashPassword(String plainPassword) {
        try {
            if (plainPassword == null || plainPassword.isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }

            // Generar salt y hash con BCrypt (factor de trabajo 12)
            return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

        } catch (Exception e) {
            logger.error("Error al generar hash de contraseña: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar contraseña", e);
        }
    }

    /**
     * Valida la fortaleza de una contraseña
     * 
     * @param password Contraseña a validar
     * @return true si la contraseña cumple los criterios de seguridad
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Verifica si un usuario tiene permisos de administrador
     * 
     * @param usuario Usuario a verificar
     * @return true si es administrador
     */
    public boolean isAdmin(Usuario usuario) {
        return usuario != null && usuario.getRol() == Usuario.Rol.ADMIN;
    }

    /**
     * Verifica si un usuario tiene permisos de vendedor
     * 
     * @param usuario Usuario a verificar
     * @return true si es vendedor
     */
    public boolean isVendedor(Usuario usuario) {
        return usuario != null && usuario.getRol() == Usuario.Rol.VENDEDOR;
    }

    /**
     * Verifica si una sesión de usuario sigue siendo válida
     * 
     * @param usuario              Usuario de la sesión
     * @param maxInactivityMinutes Minutos máximos de inactividad permitidos
     * @return true si la sesión es válida
     */
    public boolean isSessionValid(Usuario usuario, int maxInactivityMinutes) {
        if (usuario == null || usuario.getLastLogin() == null) {
            return false;
        }

        LocalDateTime lastActivity = usuario.getLastLogin();
        LocalDateTime now = LocalDateTime.now();

        return lastActivity.plusMinutes(maxInactivityMinutes).isAfter(now);
    }

    /**
     * Registra un intento de login fallido
     * 
     * @param username Usuario que intentó hacer login
     * @param reason   Razón del fallo
     */
    public void logFailedLogin(String username, String reason) {
        logger.warn("Login fallido - Usuario: {}, Razón: {}, Timestamp: {}",
                username, reason, LocalDateTime.now());
    }

    /**
     * Registra un login exitoso
     * 
     * @param usuario Usuario que hizo login exitosamente
     */
    public void logSuccessfulLogin(Usuario usuario) {
        logger.info("Login exitoso - Usuario: {}, Rol: {}, Timestamp: {}",
                usuario.getUsername(), usuario.getRol(), LocalDateTime.now());
    }

    // ...existing code...
}