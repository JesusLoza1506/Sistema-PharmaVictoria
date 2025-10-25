package com.farmaciavictoria.proyectopharmavictoria.repository;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository para gestión de proveedores en la base de datos
 */
public class ProveedorRepository {

    /**
     * Obtener todos los proveedores activos
     */
    public List<Proveedor> findAll() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE activo = TRUE ORDER BY id ASC";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    /**
     * Buscar proveedores por texto
     */
    public List<Proveedor> buscarProveedores(String texto) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = """
                    SELECT * FROM proveedores
                    WHERE activo = TRUE AND (
                        razon_social LIKE ? OR
                        ruc LIKE ? OR
                        contacto LIKE ?
                    )
                    ORDER BY id ASC
                """;

        String patron = "%" + texto + "%";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapResultSetToProveedor(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    /**
     * Obtener proveedor por ID
     */
    public Proveedor findById(Integer id) {
        String sql = "SELECT * FROM proveedores WHERE id = ?";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProveedor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtener proveedor por RUC
     */
    public Proveedor findByRuc(String ruc) {
        String sql = "SELECT * FROM proveedores WHERE ruc = ?";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, ruc);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProveedor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor por RUC: " + e.getMessage());
        }
        return null;
    }

    /**
     * Guardar proveedor (insertar o actualizar)
     */
    public boolean save(Proveedor proveedor) {
        if (proveedor.getId() == null) {
            return insert(proveedor);
        } else {
            return update(proveedor);
        }
    }

    /**
     * Insertar nuevo proveedor
     */
    private boolean insert(Proveedor proveedor) {
        String sql = """
                    INSERT INTO proveedores (razon_social, ruc, contacto, telefono, email,
                                           direccion, condiciones_pago, observaciones, activo, tipo_producto)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, proveedor.getRazonSocial());
            stmt.setString(2, proveedor.getRuc());
            stmt.setString(3, proveedor.getContacto());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEmail());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setString(7, proveedor.getCondicionesPago());
            stmt.setString(8, proveedor.getObservaciones());
            stmt.setBoolean(9, proveedor.getActivo());
            stmt.setString(10, proveedor.getTipoProducto());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proveedor.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualizar proveedor existente
     */
    public boolean update(Proveedor proveedor) {
        String sql = """
                    UPDATE proveedores SET razon_social=?, ruc=?, contacto=?, telefono=?,
                                         email=?, direccion=?, condiciones_pago=?, observaciones=?, activo=?, tipo_producto=?
                    WHERE id=?
                """;
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, proveedor.getRazonSocial());
            stmt.setString(2, proveedor.getRuc());
            stmt.setString(3, proveedor.getContacto());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEmail());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setString(7, proveedor.getCondicionesPago());
            stmt.setString(8, proveedor.getObservaciones());
            stmt.setBoolean(9, proveedor.getActivo());
            stmt.setString(10, proveedor.getTipoProducto());
            stmt.setInt(11, proveedor.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar proveedor (marcar como inactivo)
     */
    public boolean delete(Integer id) {
        String sql = "UPDATE proveedores SET activo = FALSE WHERE id = ?";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mapear ResultSet a objeto Proveedor
     */
    private Proveedor mapResultSetToProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(rs.getInt("id"));
        proveedor.setRazonSocial(rs.getString("razon_social"));
        proveedor.setRuc(rs.getString("ruc"));
        proveedor.setContacto(rs.getString("contacto"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setEmail(rs.getString("email"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setCondicionesPago(rs.getString("condiciones_pago"));
        proveedor.setObservaciones(rs.getString("observaciones"));
        proveedor.setActivo(rs.getBoolean("activo"));
        proveedor.setTipoProducto(rs.getString("tipo_producto"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            proveedor.setCreatedAt(createdAt.toLocalDateTime());
        }
        return proveedor;
    }

    // Métodos para historial de cambios
    public boolean registrarCambioProveedor(int proveedorId, String campo, String valorAnterior, String valorNuevo,
            String usuario) {
        String sql = "INSERT INTO proveedor_historial_cambio (proveedor_id, campo_modificado, valor_anterior, valor_nuevo, usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, proveedorId);
            stmt.setString(2, campo);
            stmt.setString(3, valorAnterior);
            stmt.setString(4, valorNuevo);
            stmt.setString(5, usuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar cambio de proveedor: " + e.getMessage());
        }
        return false;
    }

    public List<HistorialCambioDTO> obtenerHistorialCambios(int proveedorId) {
        List<HistorialCambioDTO> historial = new ArrayList<>();
        String sql = "SELECT h.campo_modificado, h.valor_anterior, h.valor_nuevo, h.usuario, h.fecha, u.nombres, u.apellidos "
                +
                "FROM proveedor_historial_cambio h " +
                "LEFT JOIN usuarios u ON h.usuario = u.username " +
                "WHERE h.proveedor_id = ? ORDER BY h.fecha DESC";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, proveedorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nombreCompleto = rs.getString("nombres") != null
                            ? rs.getString("nombres") + " " + rs.getString("apellidos")
                            : rs.getString("usuario");
                    historial.add(new HistorialCambioDTO(
                            rs.getString("campo_modificado"),
                            rs.getString("valor_anterior"),
                            rs.getString("valor_nuevo"),
                            nombreCompleto,
                            rs.getTimestamp("fecha")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de cambios: " + e.getMessage());
        }
        return historial;
    }

    public static class HistorialCambioDTO {
        public final String campo;
        public final String anterior;
        public final String nuevo;
        public final String usuario;
        public final Timestamp fecha;

        public HistorialCambioDTO(String campo, String anterior, String nuevo, String usuario, Timestamp fecha) {
            this.campo = campo;
            this.anterior = anterior;
            this.nuevo = nuevo;
            this.usuario = usuario;
            this.fecha = fecha;
        }
    }

    /**
     * ✅ ELIMINACIÓN DEFINITIVA: Hard delete del proveedor (IRREVERSIBLE)
     * ⚠️ USAR CON PRECAUCIÓN: Esta operación elimina físicamente el registro
     */
    public boolean deleteDefinitivamente(Integer id) {
        String sql = "DELETE FROM proveedores WHERE id = ?";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar definitivamente proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtener todos los proveedores incluyendo inactivos
     */
    public List<Proveedor> findAllIncludingInactive() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id ASC";

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    /**
     * Verifica si existe un proveedor con la misma razón social (excluyendo el id
     * si se indica)
     */
    public boolean existePorRazonSocial(String razonSocial, Integer idExcluir) {
        String sql = "SELECT COUNT(*) FROM proveedores WHERE LOWER(razon_social) = LOWER(?)"
                + (idExcluir != null ? " AND id <> ?" : "");
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, razonSocial);
            if (idExcluir != null)
                stmt.setInt(2, idExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar razón social: " + e.getMessage());
        }
        return false;
    }
}