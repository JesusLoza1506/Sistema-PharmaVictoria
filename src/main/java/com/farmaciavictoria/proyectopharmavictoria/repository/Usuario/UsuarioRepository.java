// ...existing code...
package com.farmaciavictoria.proyectopharmavictoria.repository.Usuario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Usuario
 * Maneja todas las operaciones de base de datos relacionadas con usuarios
 */
public class UsuarioRepository {
    /**
     * Busca usuario por token de recuperación
     */
    public Usuario buscarPorTokenRecuperacion(String token) {
        String sql = "SELECT * FROM usuarios WHERE recovery_token = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por token de recuperación: {}", token, e);
        }
        return null;
    }

    /**
     * Actualiza la contraseña y limpia el token de recuperación
     */
    public boolean actualizarPasswordYLimpiarToken(Long id, String passwordHash) {
        String sql = "UPDATE usuarios SET password_hash = ?, recovery_token = NULL, recovery_token_expiry = NULL WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setLong(2, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al actualizar password y limpiar token para usuario con id: {}", id, e);
            return false;
        }
    }

    /**
     * Guarda el token de recuperación y su expiración para el usuario
     */
    public boolean setRecoveryToken(Long id, String token, LocalDateTime expiry) {
        String sql = "UPDATE usuarios SET recovery_token = ?, recovery_token_expiry = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setTimestamp(2, Timestamp.valueOf(expiry));
            stmt.setLong(3, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al guardar token de recuperación para usuario con id: {}", id, e);
            return false;
        }
    }

    /**
     * Busca y retorna el usuario por email
     */
    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
        }
        return null;
    }

    /**
     * Busca un usuario por su ID
     */
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por id: {}", id, e);
        }
        return Optional.empty();
    }

    /**
     * Verifica si existe un usuario con el email dado
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar existencia de email: {}", email, e);
        }
        return false;
    }

    /**
     * Verifica si existe un usuario con el DNI dado
     */
    public boolean existsByDni(String dni) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE dni = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar existencia de DNI: {}", dni, e);
        }
        return false;
    }

    private static final Logger logger = LoggerFactory.getLogger(UsuarioRepository.class);
    private final DatabaseConfig databaseConfig;

    public UsuarioRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    /**
     * Actualiza los datos de un usuario existente
     */
    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, password_hash = ?, rol = ?, sucursal_id = ?, nombres = ?, apellidos = ?, dni = ?, telefono = ?, email = ?, activo = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getRol().name());
            stmt.setLong(4, usuario.getSucursalId());
            stmt.setString(5, usuario.getNombres());
            stmt.setString(6, usuario.getApellidos());
            stmt.setString(7, usuario.getDni());
            stmt.setString(8, usuario.getTelefono());
            stmt.setString(9, usuario.getEmail());
            stmt.setBoolean(10, usuario.isActivo());
            stmt.setLong(11, usuario.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al actualizar usuario: {}", usuario.getUsername(), e);
            return false;
        }
    }

    /**
     * Elimina un usuario por su ID
     */
    public boolean delete(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al eliminar usuario con id: {}", id, e);
            return false;
        }
    }

    /**
     * Cambia el estado activo/inactivo de un usuario
     */
    public boolean setActivo(Long id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, activo);
            stmt.setLong(2, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al cambiar estado activo para usuario con id: {}", id, e);
            return false;
        }
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public boolean setPassword(Long id, String passwordHash) {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setLong(2, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al cambiar contraseña para usuario con id: {}", id, e);
            return false;
        }
    }

    /**
     * Busca un usuario por username
     */
    public Optional<Usuario> findByUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = true";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error al buscar usuario por username: {}", username, e);
        }

        return Optional.empty();
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombres, apellidos";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            logger.error("Error al obtener todos los usuarios", e);
        }

        return usuarios;
    }

    /**
     * Guarda un nuevo usuario
     */
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password_hash, rol, sucursal_id, nombres, apellidos, dni, telefono, email, activo) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getRol().name());
            stmt.setLong(4, usuario.getSucursalId());
            stmt.setString(5, usuario.getNombres());
            stmt.setString(6, usuario.getApellidos());
            stmt.setString(7, usuario.getDni());
            stmt.setString(8, usuario.getTelefono());
            stmt.setString(9, usuario.getEmail());
            stmt.setBoolean(10, usuario.isActivo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getLong(1));
                        logger.info("Usuario creado exitosamente: {}", usuario.getUsername());
                        return usuario;
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error al guardar usuario: {}", usuario.getUsername(), e);
        }

        return null;
    }

    /**
     * Actualiza el último login del usuario
     */
    public void updateLastLogin(String username) {
        String sql = "UPDATE usuarios SET last_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.debug("Último login actualizado para usuario: {}", username);
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar último login para usuario: {}", username, e);
        }
    }

    /**
     * Verifica si existe un usuario con el username dado
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al verificar existencia de usuario: {}", username, e);
        }

        return false;
    }

    /**
     * Valida credenciales de usuario
     */
    public boolean validateCredentials(String username, String passwordHash) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ? AND password_hash = ? AND activo = true";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al validar credenciales para usuario: {}", username, e);
        }

        return false;
    }

    /**
     * Mapea ResultSet a Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
        usuario.setSucursalId(rs.getLong("sucursal_id"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setDni(rs.getString("dni"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setEmail(rs.getString("email"));
        usuario.setActivo(rs.getBoolean("activo"));

        // Manejar fechas que pueden ser null
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            usuario.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            usuario.setLastLogin(lastLogin.toLocalDateTime());
        }

        // Token de recuperación
        usuario.setRecoveryToken(rs.getString("recovery_token"));
        Timestamp tokenExpiry = rs.getTimestamp("recovery_token_expiry");
        if (tokenExpiry != null) {
            usuario.setRecoveryTokenExpiry(tokenExpiry.toLocalDateTime());
        }
        return usuario;
    }
}