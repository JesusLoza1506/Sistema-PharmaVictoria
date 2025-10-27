package com.farmaciavictoria.proyectopharmavictoria.repository.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ✅ REPOSITORY PATTERN - Repository para gestión de clientes
 * Implementa patrón Repository para operaciones CRUD de clientes
 * Incluye búsquedas especializadas y queries optimizadas
 * 
 * @author PHARMAVICTORIA Development Team
 * @version 1.0
 */
public class ClienteRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClienteRepository.class);
    private final DatabaseConfig databaseConfig;

    public ClienteRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    // ✅ CRUD OPERATIONS

    /**
     * Obtener todos los clientes (con opción de paginación)
     */
    public List<Cliente> findAll(int offset, int limit) {
        String sql = "SELECT * FROM clientes ORDER BY id ASC LIMIT ? OFFSET ?";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
            logger.debug("Clientes obtenidos: {}", clientes.size());
        } catch (SQLException e) {
            logger.error("Error al obtener clientes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener clientes", e);
        }
        return clientes;
    }

    /**
     * Buscar cliente por ID
     */
    public Optional<Cliente> findById(Integer id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapResultSetToCliente(rs);
                    logger.debug("Cliente encontrado por ID {}: {}", id, cliente.getNombreCompleto());
                    return Optional.of(cliente);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al buscar cliente por ID {}: {}", id, e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Buscar cliente por código interno
     */
    public Optional<Cliente> findByCodigo(String codigo) {
        String sql = "SELECT * FROM clientes WHERE codigo = ? AND activo = true";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapResultSetToCliente(rs);
                    logger.debug("Cliente encontrado por código {}: {}", codigo, cliente.getNombreCompleto());
                    return Optional.of(cliente);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al buscar cliente por código {}: {}", codigo, e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Buscar clientes por nombre o apellidos (búsqueda parcial)
     */
    public List<Cliente> findByNombreOApellido(String texto) {
        String sql = "SELECT * FROM clientes WHERE LOWER(nombres) LIKE ? OR LOWER(apellidos) LIKE ? ORDER BY nombres, apellidos";
        List<Cliente> clientes = new ArrayList<>();
        String pattern = "%" + texto.toLowerCase() + "%";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
            logger.debug("Clientes encontrados por nombre/apellido '{}': {}", texto, clientes.size());
        } catch (SQLException e) {
            logger.error("Error al buscar clientes por nombre/apellido '{}': {}", texto, e.getMessage(), e);
        }
        return clientes;
    }

    /**
     * Buscar clientes por categoría
     */
    public List<Cliente> findByCategoria(String categoria) {
        String sql = """
                SELECT * FROM clientes
                WHERE categoria_cliente = ? AND activo = true
                ORDER BY puntos_acumulados DESC
                """;

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }

            logger.debug("Clientes encontrados por categoría '{}': {}", categoria, clientes.size());

        } catch (SQLException e) {
            logger.error("Error al buscar clientes por categoría '{}': {}", categoria, e.getMessage(), e);
        }

        return clientes;
    }

    /**
     * Obtener clientes VIP (descuento >= 10% o categoría VIP)
     */
    public List<Cliente> findClientesVip() {
        String sql = """
                SELECT * FROM clientes
                WHERE (categoria_cliente = 'VIP' OR descuento_preferencial >= 10.0)
                AND activo = true
                ORDER BY puntos_acumulados DESC
                """;

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

            logger.debug("Clientes VIP encontrados: {}", clientes.size());

        } catch (SQLException e) {
            logger.error("Error al obtener clientes VIP: {}", e.getMessage(), e);
        }

        return clientes;
    }

    /**
     * Guardar cliente (crear o actualizar)
     */
    public boolean save(Cliente cliente) {
        if (cliente.getId() == null) {
            return insert(cliente);
        } else {
            return update(cliente);
        }
    }

    /**
     * Insertar nuevo cliente
     */
    private boolean insert(Cliente cliente) {
        String sql = "INSERT INTO clientes (documento, tipo_cliente, razon_social, nombres, apellidos, telefono, email, direccion, fecha_nacimiento, puntos_totales, puntos_usados, es_frecuente, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Documento principal
            String documento = cliente.getDocumento();
            String tipo = cliente.getTipoCliente();
            String razonSocial = "Empresarial".equalsIgnoreCase(tipo) ? cliente.getRazonSocial() : null;
            stmt.setString(1, documento); // documento
            stmt.setString(2, tipo); // tipo_cliente
            if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                stmt.setString(3, razonSocial); // razon_social
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR); // razon_social null para Natural
            }
            stmt.setString(4, cliente.getNombres());
            stmt.setString(5, cliente.getApellidos());
            stmt.setString(6, cliente.getTelefono());
            stmt.setString(7, cliente.getEmail());
            stmt.setString(8, cliente.getDireccion());
            if (cliente.getFechaNacimiento() != null) {
                stmt.setDate(9, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            stmt.setInt(10, cliente.getPuntosTotales() != null ? cliente.getPuntosTotales() : 0);
            stmt.setInt(11, cliente.getPuntosUsados() != null ? cliente.getPuntosUsados() : 0);
            stmt.setBoolean(12, cliente.isFrecuente());
            stmt.setTimestamp(13, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(14, new java.sql.Timestamp(System.currentTimeMillis()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Cliente creado exitosamente: {}", cliente.getNombreCompleto());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error al crear cliente: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Actualizar cliente existente
     */
    private boolean update(Cliente cliente) {
        String sql = "UPDATE clientes SET documento = ?, tipo_cliente = ?, razon_social = ?, nombres = ?, apellidos = ?, telefono = ?, email = ?, direccion = ?, fecha_nacimiento = ?, puntos_totales = ?, puntos_usados = ?, es_frecuente = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setString(idx++, cliente.getDocumento());
            stmt.setString(idx++, cliente.getTipoCliente());
            String tipo = cliente.getTipoCliente();
            String razonSocial = "Empresarial".equalsIgnoreCase(tipo) ? cliente.getRazonSocial() : null;
            if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                stmt.setString(idx++, razonSocial);
            } else {
                stmt.setNull(idx++, java.sql.Types.VARCHAR);
            }
            stmt.setString(idx++, cliente.getNombres());
            stmt.setString(idx++, cliente.getApellidos());
            stmt.setString(idx++, cliente.getTelefono());
            stmt.setString(idx++, cliente.getEmail());
            stmt.setString(idx++, cliente.getDireccion());
            if (cliente.getFechaNacimiento() != null) {
                stmt.setDate(idx++, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                stmt.setNull(idx++, java.sql.Types.DATE);
            }
            stmt.setInt(idx++, cliente.getPuntosTotales() != null ? cliente.getPuntosTotales() : 0);
            stmt.setInt(idx++, cliente.getPuntosUsados() != null ? cliente.getPuntosUsados() : 0);
            stmt.setBoolean(idx++, cliente.isFrecuente());
            stmt.setInt(idx++, cliente.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Cliente actualizado exitosamente: {}", cliente.getNombreCompleto());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar cliente: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Eliminar cliente (soft delete: elimina físicamente porque no hay campo
     * activo)
     */
    public boolean delete(Integer id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Cliente eliminado físicamente: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar cliente ID {}: {}", id, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Reactivar cliente
     */
    public boolean reactivate(Integer id) {
        String sql = "UPDATE clientes SET activo = true WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Cliente reactivado: ID {}", id);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error al reactivar cliente ID {}: {}", id, e.getMessage(), e);
        }

        return false;
    }

    // ✅ QUERIES ESPECIALIZADAS

    /**
     * Contar total de clientes activos
     */
    public int countClientesActivos() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = true";

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Error al contar clientes activos: {}", e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Contar clientes nuevos del mes actual
     */
    public int countClientesNuevosDelMes() {
        String sql = """
                SELECT COUNT(*) FROM clientes
                WHERE YEAR(fecha_registro) = YEAR(CURDATE())
                AND MONTH(fecha_registro) = MONTH(CURDATE())
                AND activo = true
                """;

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Error al contar clientes nuevos del mes: {}", e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Obtener clientes por categoría con conteo
     */
    public List<Object[]> getClientesPorCategoria() {
        String sql = """
                SELECT categoria_cliente, COUNT(*) as cantidad
                FROM clientes
                WHERE activo = true
                GROUP BY categoria_cliente
                ORDER BY cantidad DESC
                """;

        List<Object[]> resultado = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultado.add(new Object[] {
                        rs.getString("categoria_cliente"),
                        rs.getInt("cantidad")
                });
            }

        } catch (SQLException e) {
            logger.error("Error al obtener clientes por categoría: {}", e.getMessage(), e);
        }

        return resultado;
    }

    // ✅ MÉTODOS DE UTILIDAD

    /**
     * Establecer parámetros del cliente en PreparedStatement
     */
    private void setClienteParameters(PreparedStatement stmt, Cliente cliente) throws SQLException {
        // Ya no se usa, el update ahora setea los parámetros directamente
    }

    /**
     * Mapear ResultSet a objeto Cliente
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        // LOGS DE DEPURACIÓN AVANZADA
        try {
            String tipoRaw = rs.getString("tipo_cliente");
            String documentoRaw = rs.getString("documento");
            String razonSocialRaw = rs.getString("razon_social");
            logger.info("[DEBUG RAW] tipo_cliente={} | documento={} | razon_social={}", tipoRaw, documentoRaw,
                    razonSocialRaw);
            if (tipoRaw == null || tipoRaw.trim().isEmpty()) {
                logger.warn("[ERROR] tipo_cliente vacío o nulo en ResultSet");
            }
            if (documentoRaw == null || documentoRaw.trim().isEmpty()) {
                logger.warn("[ERROR] documento vacío o nulo en ResultSet");
            }
            // Solo advertir si el tipo es Empresarial y razon_social está vacío
            if ("Empresarial".equalsIgnoreCase(tipoRaw)
                    && (razonSocialRaw == null || razonSocialRaw.trim().isEmpty())) {
                logger.warn("[ADVERTENCIA] razon_social vacío o nulo en ResultSet para cliente Empresarial");
            }
        } catch (Exception ex) {
            logger.error("[EXCEPCION] Error al leer campos RAW del ResultSet: {}", ex.getMessage(), ex);
        }
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setDocumento(rs.getString("documento"));
        cliente.setNombres(rs.getString("nombres"));
        cliente.setApellidos(rs.getString("apellidos"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setDireccion(rs.getString("direccion"));
        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) {
            cliente.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }
        cliente.setPuntosTotales(rs.getInt("puntos_totales"));
        cliente.setPuntosUsados(rs.getInt("puntos_usados"));
        cliente.setEsFrecuente(rs.getBoolean("es_frecuente"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null)
            cliente.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null)
            cliente.setUpdatedAt(updated.toLocalDateTime());
        String tipoCliente = rs.getString("tipo_cliente");
        cliente.setTipoCliente(tipoCliente);
        // Solo asignar razon_social si el tipo es Empresarial
        if ("Empresarial".equalsIgnoreCase(tipoCliente)) {
            cliente.setRazonSocial(rs.getString("razon_social"));
        } else {
            cliente.setRazonSocial("");
        }
        logger.info("[MAPEO CLIENTE] tipo_cliente={} | documento={} | razon_social={}", cliente.getTipoCliente(),
                cliente.getDocumento(), cliente.getRazonSocial());
        return cliente;
    }
}