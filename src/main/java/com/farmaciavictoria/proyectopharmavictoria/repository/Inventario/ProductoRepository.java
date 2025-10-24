package com.farmaciavictoria.proyectopharmavictoria.repository.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.model.*; // Assuming CategoriaProducto and FormaFarmaceutica are here
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository para gestión de productos en la base de datos
 */
public class ProductoRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductoRepository.class);

    /**
     * Excepción personalizada para errores en el repositorio de productos
     */
    class ProductoRepositoryException extends RuntimeException {
        public ProductoRepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
        public ProductoRepositoryException(String message) {
            super(message);
        }
    }

    // -------------------------------------------------------------------------
    // BÚSQUEDA Y LECTURA (READ OPERATIONS)
    // -------------------------------------------------------------------------

    /**
     * Obtener todos los productos activos
     */
    public List<Producto> findAll() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.activo = TRUE 
            ORDER BY p.codigo
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener productos: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener productos", e);
        }
        return productos;
    }

    /**
     * Obtener productos paginados
     * @param page número de página (empezando en 0)
     * @param size cantidad de productos por página
     */
    public List<Producto> findAllPaged(int page, int size) {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.activo = TRUE 
            ORDER BY p.codigo
            LIMIT ? OFFSET ?
        """;
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, size);
            stmt.setInt(2, page * size);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener productos paginados: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener productos paginados", e);
        }
        return productos;
    }
    
    /**
     * Obtener todos los productos incluyendo inactivos
     */
    public List<Producto> findAllIncludingInactive() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            ORDER BY p.codigo
            """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Error al obtener todos los productos: {}", e.getMessage(), e);
            // Uso de ProductoRepositoryException consistente con findAll()
            throw new ProductoRepositoryException("Error al obtener todos los productos", e); 
        }
        
        return productos;
    }

    /**
     * Buscar producto por ID
     */
    public Optional<Producto> findById(Long id) {
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.id = ?
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar producto por ID: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al buscar producto por ID", e);
        }
        return Optional.empty();
    }

    /**
     * Buscar producto por código
     */
    public Optional<Producto> findByCodigo(String codigo) {
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.codigo = ?
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar producto por código: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al buscar producto por código", e);
        }
        return Optional.empty();
    }

    /**
     * Buscar productos por nombre
     */
    public List<Producto> findByNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.nombre LIKE ? AND p.activo = TRUE 
            ORDER BY p.nombre
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar productos por nombre: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al buscar productos por nombre", e);
        }
        return productos;
    }

    /**
     * Buscar productos por categoría (usa columna 'categoria' en lugar de 'categoria_id')
     */
    public List<Producto> findByCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.categoria = ? AND p.activo = TRUE 
            ORDER BY p.nombre
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar productos por categoría: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al buscar productos por categoría", e);
        }
        return productos;
    }
    
    /**
     * Buscar productos por filtros avanzados: categoría, proveedor, rango de precios
     * NOTA: El filtro usa `categoria_id` en el SQL, pero el mapeo usa la columna `categoria` (String/Enum).
     * Se mantiene el uso original del código.
     */
    public List<Producto> findByFiltros(Long categoriaId, Long proveedorId, BigDecimal precioMin, BigDecimal precioMax) {
        List<Producto> productos = new ArrayList<>();
        // Asumiendo que `categoria_id` en el WHERE es la ID numérica de la categoría, 
        // aunque `findByCategoria` usa el nombre (String). Mantenemos la lógica original.
        StringBuilder sql = new StringBuilder("SELECT p.*, pr.razon_social as proveedor_nombre FROM productos p LEFT JOIN proveedores pr ON p.proveedor_id = pr.id WHERE p.activo = TRUE");
        
        if (categoriaId != null) sql.append(" AND p.categoria_id = ?"); 
        if (proveedorId != null) sql.append(" AND p.proveedor_id = ?");
        if (precioMin != null) sql.append(" AND p.precio_venta >= ?");
        if (precioMax != null) sql.append(" AND p.precio_venta <= ?");
        sql.append(" ORDER BY p.nombre");

        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            
            int idx = 1;
            if (categoriaId != null) stmt.setLong(idx++, categoriaId);
            if (proveedorId != null) stmt.setLong(idx++, proveedorId);
            if (precioMin != null) stmt.setBigDecimal(idx++, precioMin);
            if (precioMax != null) stmt.setBigDecimal(idx++, precioMax);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar productos por filtros: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al buscar productos por filtros", e);
        }
        return productos;
    }

    // -------------------------------------------------------------------------
    // ESTADÍSTICAS E INFORMES (REPORTING)
    // -------------------------------------------------------------------------

    /**
     * Obtener productos vencidos
     */
    public List<Producto> findVencidos() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.fecha_vencimiento < CURDATE() AND p.activo = TRUE 
            ORDER BY p.fecha_vencimiento
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener productos vencidos: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener productos vencidos", e);
        }
        return productos;
    }

    /**
     * Obtener productos próximos a vencer (30 días)
     */
    public List<Producto> findProximosVencer() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) 
            AND p.activo = TRUE 
            ORDER BY p.fecha_vencimiento
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener productos próximos a vencer: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener productos próximos a vencer", e);
        }
        return productos;
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> findStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.razon_social as proveedor_nombre 
            FROM productos p 
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id 
            WHERE p.stock_actual <= p.stock_minimo AND p.activo = TRUE 
            ORDER BY (p.stock_actual - p.stock_minimo)
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener productos con stock bajo: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener productos con stock bajo", e);
        }
        return productos;
    }

    /**
     * Contar productos por estado
     */
    public Map<String, Integer> countByEstado() {
        Map<String, Integer> estadisticas = new HashMap<>();
        
        // Inicializar contadores
        estadisticas.put("TOTAL", 0);
        estadisticas.put("VENCIDOS", 0);
        estadisticas.put("PROXIMOS_VENCER", 0);
        estadisticas.put("STOCK_BAJO", 0);
        estadisticas.put("SIN_STOCK", 0);
        
        String sql = """
            SELECT 
                COUNT(*) as total,
                COUNT(CASE WHEN fecha_vencimiento < CURDATE() THEN 1 END) as vencidos,
                COUNT(CASE WHEN fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 1 END) as proximos_vencer,
                COUNT(CASE WHEN stock_actual <= stock_minimo AND stock_actual > 0 THEN 1 END) as stock_bajo,
                COUNT(CASE WHEN stock_actual = 0 THEN 1 END) as sin_stock
            FROM productos 
            WHERE activo = TRUE
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.put("TOTAL", rs.getInt("total"));
                estadisticas.put("VENCIDOS", rs.getInt("vencidos"));
                estadisticas.put("PROXIMOS_VENCER", rs.getInt("proximos_vencer"));
                estadisticas.put("STOCK_BAJO", rs.getInt("stock_bajo"));
                estadisticas.put("SIN_STOCK", rs.getInt("sin_stock"));
            }
        } catch (SQLException e) {
            logger.error("Error al contar productos por estado: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al contar productos por estado", e);
        }
        
        return estadisticas;
    }

    // -------------------------------------------------------------------------
    // CREACIÓN Y ACTUALIZACIÓN (WRITE OPERATIONS)
    // -------------------------------------------------------------------------

    /**
     * Guardar nuevo producto
     */
    public boolean save(Producto producto) {
        String sql = """
            INSERT INTO productos (codigo, nombre, categoria, descripcion, principio_activo, 
                                 concentracion, forma_farmaceutica, precio_compra, precio_venta, stock_actual, stock_minimo, 
                                 fecha_vencimiento, lote, ubicacion, laboratorio, fecha_fabricacion, proveedor_id, activo, 
                                 requiere_receta, es_controlado, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getCategoria().name());
            stmt.setString(4, producto.getDescripcion());
            stmt.setString(5, producto.getPrincipioActivo());
            stmt.setString(6, producto.getConcentracion());
            stmt.setString(7, producto.getFormaFarmaceutica() != null ? producto.getFormaFarmaceutica().name() : "");
            stmt.setBigDecimal(8, producto.getPrecioCompra() != null ? producto.getPrecioCompra() : BigDecimal.ZERO);
            stmt.setBigDecimal(9, producto.getPrecioVenta());
            stmt.setInt(10, producto.getStockActual());
            stmt.setInt(11, producto.getStockMinimo());
            stmt.setDate(12, Date.valueOf(producto.getFechaVencimiento()));
            stmt.setString(13, producto.getLote());
            stmt.setString(14, producto.getUbicacion());
            stmt.setString(15, producto.getLaboratorio() != null ? producto.getLaboratorio() : "");
            if (producto.getFechaFabricacion() != null) {
                stmt.setDate(16, Date.valueOf(producto.getFechaFabricacion()));
            } else {
                stmt.setNull(16, java.sql.Types.DATE);
            }
            stmt.setInt(17, producto.getProveedorId());
            stmt.setBoolean(18, true); // activo por defecto
            stmt.setBoolean(19, producto.getRequiereReceta() != null ? producto.getRequiereReceta() : false);
            stmt.setBoolean(20, producto.getEsControlado() != null ? producto.getEsControlado() : false);
            stmt.setTimestamp(21, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Se asume que el ID es un Integer basado en cómo se usa en el objeto Producto
                        producto.setId(generatedKeys.getInt(1)); 
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error al guardar producto: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al guardar producto", e);
        }
        return false;
    }

    /**
     * Actualizar producto existente
     */
    public boolean update(Producto producto) {
        String sql = """
            UPDATE productos SET 
                codigo = ?, nombre = ?, categoria = ?, descripcion = ?, principio_activo = ?, 
                concentracion = ?, forma_farmaceutica = ?, precio_compra = ?, precio_venta = ?, stock_actual = ?, stock_minimo = ?, stock_maximo = ?,
                fecha_vencimiento = ?, lote = ?, ubicacion = ?, laboratorio = ?, fecha_fabricacion = ?, proveedor_id = ?, activo = ?, 
                requiere_receta = ?, es_controlado = ?, updated_at = ? 
            WHERE id = ?
        """;
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getCategoria().name());
            stmt.setString(4, producto.getDescripcion());
            stmt.setString(5, producto.getPrincipioActivo());
            stmt.setString(6, producto.getConcentracion());
            stmt.setString(7, producto.getFormaFarmaceutica() != null ? producto.getFormaFarmaceutica().name() : "");
            stmt.setBigDecimal(8, producto.getPrecioCompra() != null ? producto.getPrecioCompra() : BigDecimal.ZERO);
            stmt.setBigDecimal(9, producto.getPrecioVenta());
            stmt.setInt(10, producto.getStockActual());
            stmt.setInt(11, producto.getStockMinimo());
            stmt.setInt(12, producto.getStockMaximo());
            stmt.setDate(13, Date.valueOf(producto.getFechaVencimiento()));
            stmt.setString(14, producto.getLote());
            stmt.setString(15, producto.getUbicacion());
            stmt.setString(16, producto.getLaboratorio() != null ? producto.getLaboratorio() : "");
            if (producto.getFechaFabricacion() != null) {
                stmt.setDate(17, Date.valueOf(producto.getFechaFabricacion()));
            } else {
                stmt.setNull(17, java.sql.Types.DATE);
            }
            stmt.setInt(18, producto.getProveedorId());
            stmt.setBoolean(19, producto.getActivo());
            stmt.setBoolean(20, producto.getRequiereReceta() != null ? producto.getRequiereReceta() : false);
            stmt.setBoolean(21, producto.getEsControlado() != null ? producto.getEsControlado() : false);
            stmt.setTimestamp(22, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(23, producto.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error al actualizar producto: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al actualizar producto", e);
        }
    }

    // -------------------------------------------------------------------------
    // ELIMINACIÓN (DELETE OPERATIONS)
    // -------------------------------------------------------------------------

    /**
     * Eliminar producto (soft delete)
     */
    public boolean delete(Integer id) {
        String sql = "UPDATE productos SET activo = FALSE, updated_at = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id); // Se asume que el ID es Integer
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error al eliminar producto (soft delete): {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al eliminar producto (soft delete)", e);
        }
    }

    /**
     * ✅ ELIMINACIÓN DEFINITIVA: Hard delete del producto (IRREVERSIBLE)
     * ⚠️ USAR CON PRECAUCIÓN: Esta operación elimina físicamente el registro
     */
    public boolean deleteDefinitivamente(Integer id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error al eliminar definitivamente producto: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al eliminar definitivamente producto", e);
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE UTILIDAD
    // -------------------------------------------------------------------------

    /**
     * Obtener último número de código para un prefijo
     */
    public int getUltimoNumeroCodigo(String prefijo) {
        // La columna `codigo` es un String, se asume que el formato es `XXXNNNNN...`
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(codigo, 4) AS UNSIGNED)), 0) as ultimo_numero FROM productos WHERE codigo LIKE ?";
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, prefijo + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ultimo_numero");
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener último número de código: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al obtener último número de código", e);
        }
        return 0;
    }

    /**
     * Verifica si ya existe un producto con el mismo nombre (case-insensitive)
     */
    public boolean existsByNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM productos WHERE LOWER(nombre) = LOWER(?)";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar unicidad de nombre de producto: {}", e.getMessage(), e);
            throw new ProductoRepositoryException("Error al verificar unicidad de nombre", e);
        }
        return false;
    }

    /**
     * Mapear ResultSet a Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        
        producto.setId(rs.getInt("id"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        
        // Mapeo seguro de categoria
        try {
            // Asume que CategoriaProducto es un Enum
            String categoriaStr = rs.getString("categoria");
            if (categoriaStr != null) {
                 producto.setCategoria(CategoriaProducto.valueOf(categoriaStr));
            }
        } catch (IllegalArgumentException e) {
            producto.setCategoria(CategoriaProducto.OTROS); // Valor por defecto
        }
        
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrincipioActivo(rs.getString("principio_activo"));
        producto.setConcentracion(rs.getString("concentracion"));
        
        // Mapeo seguro de forma farmaceutica
        try {
            // Asume que FormaFarmaceutica es un Enum
            String formaFarmaceuticaStr = rs.getString("forma_farmaceutica");
            if (formaFarmaceuticaStr != null) {
                producto.setFormaFarmaceutica(FormaFarmaceutica.valueOf(formaFarmaceuticaStr));
            }
        } catch (IllegalArgumentException e) {
            producto.setFormaFarmaceutica(FormaFarmaceutica.TABLETA); // Valor por defecto
        }
        
        producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        producto.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        // Calcular margen de ganancia al mapear
        if (producto.getPrecioCompra() != null && producto.getPrecioVenta() != null && producto.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = producto.getPrecioVenta()
                .subtract(producto.getPrecioCompra())
                .divide(producto.getPrecioCompra(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
            producto.setMargenGanancia(margen);
        } else {
            producto.setMargenGanancia(BigDecimal.ZERO);
        }
        producto.setStockActual(rs.getInt("stock_actual"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        
        // El campo stock_maximo fue agregado en el mapeo aunque no aparece en todos los SELECTs
        try {
            producto.setStockMaximo(rs.getInt("stock_maximo"));
        } catch (SQLException e) {
            // Se ignora si la columna no existe en el ResultSet (no está en el SELECT)
        }
        
        Date vencimiento = rs.getDate("fecha_vencimiento");
        if (vencimiento != null) {
            producto.setFechaVencimiento(vencimiento.toLocalDate());
        }
        
        producto.setLote(rs.getString("lote"));
        producto.setUbicacion(rs.getString("ubicacion"));
        
        producto.setLaboratorio(rs.getString("laboratorio"));
        
        Date fechaFabricacion = rs.getDate("fecha_fabricacion");
        if (fechaFabricacion != null) {
            producto.setFechaFabricacion(fechaFabricacion.toLocalDate());
        }
        
        producto.setProveedorId(rs.getInt("proveedor_id"));
        // El campo proveedor_nombre viene del JOIN
        producto.setProveedorNombre(rs.getString("proveedor_nombre")); 
        
        producto.setRequiereReceta(rs.getBoolean("requiere_receta"));
        producto.setEsControlado(rs.getBoolean("es_controlado"));
        producto.setActivo(rs.getBoolean("activo"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            producto.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            producto.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return producto;
    }

    /**
     * Método para crear productos inactivos de prueba
     */
    public void crearProductosInactivosDePrueba() {
        // Método para crear productos de prueba si es necesario
        System.out.println("Método crearProductosInactivosDePrueba() ejecutado");
    }

        /**
         * Actualiza solo el stock_actual de un producto por su ID
         */
        public boolean updateStock(Integer id, int nuevoStock) {
            String sql = "UPDATE productos SET stock_actual = ? WHERE id = ?";
            try (Connection connection = DatabaseConfig.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, nuevoStock);
                stmt.setInt(2, id);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.error("Error al actualizar stock del producto {}: {}", id, e.getMessage(), e);
                throw new ProductoRepositoryException("Error al actualizar stock del producto", e);
            }
        }

    /**
     * Método para corregir prefijos de configuración
     */
    public void corregirPrefijosConfiguracion() {
        // Método para corregir prefijos de configuración
        System.out.println("Método corregirPrefijosConfiguracion() ejecutado");
    }
}