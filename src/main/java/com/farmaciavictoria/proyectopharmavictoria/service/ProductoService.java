package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import java.util.Optional;

/**
 * ✅ SERVICE LAYER PATTERN - Servicio de negocio para productos
 * Centraliza toda la lógica de negocio relacionada con productos
 * Incluye validaciones, cálculos y reglas de inventario
 */
public class ProductoService {
    /**
     * Actualiza un campo individual del producto en la base de datos
     */
    public void actualizarCampo(Integer id, String campo, Object valor) {
        String sql = "UPDATE producto SET " + campo + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (valor instanceof BigDecimal) {
                stmt.setBigDecimal(1, (BigDecimal) valor);
            } else if (valor instanceof Integer) {
                stmt.setInt(1, (Integer) valor);
            } else if (valor instanceof LocalDate) {
                stmt.setDate(1, Date.valueOf((LocalDate) valor));
            } else if (valor instanceof String) {
                stmt.setString(1, (String) valor);
            } else {
                stmt.setObject(1, valor);
            }
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            // Puedes loguear el error si lo deseas
        }
    }

    // Singleton para exportación
    private static ProductoService instance;

    /**
     * Valida que el código del producto corresponda al prefijo de la categoría
     * seleccionada
     */
    private void validarCodigoConCategoria(Producto producto) {
        if (producto == null || producto.getCategoria() == null || producto.getCodigo() == null) {
            throw new IllegalArgumentException("Producto, categoría o código no pueden ser nulos");
        }
        String categoria = producto.getCategoria().name();
        String codigo = producto.getCodigo();
        String prefijoEsperado = codigoGeneratorService.getPrefijoPorCategoria(categoria);
        if (!codigo.startsWith(prefijoEsperado)) {
            throw new IllegalArgumentException(
                    "El código del producto (" + codigo + ") no corresponde a la categoría seleccionada (" + categoria
                            + "). Debe comenzar con: " + prefijoEsperado);
        }
    }

    public static ProductoService getInstance() {
        if (instance == null) {
            // Aquí deberías inyectar los repositorios reales
            instance = new ProductoService(
                    new com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository(),
                    new com.farmaciavictoria.proyectopharmavictoria.service.CodigoGeneratorService());
        }
        return instance;
    }

    public List<String> obtenerCategorias() {
        // Devuelve la lista de nombres de categorías
        return java.util.Arrays
                .stream(com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto.values())
                .map(com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto::getNombre)
                .toList();
    }

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final CodigoGeneratorService codigoGeneratorService;
    private final SystemEventManager eventManager;

    // Constantes de negocio
    private static final int STOCK_MINIMO_DEFAULT = 5;
    private static final int STOCK_MAXIMO_DEFAULT = 100;
    private static int DIAS_VENCIMIENTO_ALERTA = cargarDiasVencimientoAlerta();

    private static int cargarDiasVencimientoAlerta() {
        try {
            java.util.Properties prop = new java.util.Properties();
            java.io.InputStream input = ProductoService.class.getClassLoader()
                    .getResourceAsStream("alerta-config.properties");
            if (input != null) {
                prop.load(input);
                String valor = prop.getProperty("alerta.vencimiento.dias");
                if (valor != null && !valor.isEmpty()) {
                    return Integer.parseInt(valor);
                }
            }
        } catch (Exception e) {
            // Si falla, usar el valor por defecto
        }
        return 30;
    }

    // Permite cambiar el valor en tiempo real desde la UI
    public static void setDiasVencimientoAlerta(int dias) {
        DIAS_VENCIMIENTO_ALERTA = dias;
        // Guardar en archivo de configuración
        try {
            java.util.Properties prop = new java.util.Properties();
            prop.setProperty("alerta.vencimiento.dias", String.valueOf(dias));
            java.io.OutputStream output = new java.io.FileOutputStream("src/main/resources/alerta-config.properties");
            prop.store(output, null);
            output.close();
        } catch (Exception e) {
            // Silenciar error de guardado
        }
        // Limpiar todas las marcas de notificación de vencimiento
        com.farmaciavictoria.proyectopharmavictoria.util.AlertaCorreoPersistente.limpiar();
    }

    public static int getDiasVencimientoAlerta() {
        return DIAS_VENCIMIENTO_ALERTA;
    }

    public ProductoService(ProductoRepository productoRepository, CodigoGeneratorService codigoGeneratorService) {
        this.productoRepository = productoRepository;
        this.codigoGeneratorService = codigoGeneratorService;
        this.eventManager = SystemEventManager.getInstance();
    }

    /**
     * ✅ SERVICE LAYER: Obtener todos los productos
     */
    public List<Producto> obtenerTodos() {
        try {
            logger.debug("Obteniendo todos los productos");
            List<Producto> productos = productoRepository.findAllIncludingInactive();

            // Verificar alertas de stock y vencimiento
            verificarAlertas(productos);

            logger.info("Productos obtenidos exitosamente: {}", productos.size());
            return productos;
        } catch (Exception e) {
            logger.error("Error al obtener productos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener la lista de productos", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener solo productos activos
     */
    public List<Producto> obtenerActivos() {
        try {
            logger.debug("Obteniendo productos activos");
            List<Producto> productos = productoRepository.findAll(); // Usar findAll que existe
            logger.info("Productos activos obtenidos: {}", productos.size());
            return productos;
        } catch (Exception e) {
            logger.error("Error al obtener productos activos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener productos activos", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Buscar productos por texto
     */
    public List<Producto> buscarPorTexto(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return obtenerActivos();
            }

            logger.debug("Buscando productos por texto: {}", texto);
            List<Producto> resultados = productoRepository.findByNombre(texto.trim()); // Usar método existente
            logger.info("Búsqueda completada. Resultados: {}", resultados.size());
            return resultados;
        } catch (Exception e) {
            logger.error("Error en búsqueda de productos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar productos", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener producto por ID
     */
    public Producto obtenerPorId(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de producto inválido");
            }

            logger.debug("Obteniendo producto por ID: {}", id);
            Optional<Producto> productoOpt = productoRepository.findById(Long.valueOf(id));

            if (!productoOpt.isPresent()) {
                logger.warn("Producto no encontrado con ID: {}", id);
                throw new RuntimeException("Producto no encontrado");
            }

            return productoOpt.get();
        } catch (Exception e) {
            logger.error("Error al obtener producto por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el producto", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener producto por código
     */
    public Producto obtenerPorCodigo(String codigo) {
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException("Código de producto requerido");
            }

            logger.debug("Obteniendo producto por código: {}", codigo);
            Optional<Producto> productoOpt = productoRepository.findByCodigo(codigo.trim());

            if (!productoOpt.isPresent()) {
                logger.warn("Producto no encontrado con código: {}", codigo);
                throw new RuntimeException("Producto no encontrado");
            }

            return productoOpt.get();
        } catch (Exception e) {
            logger.error("Error al obtener producto por código {}: {}", codigo, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el producto", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Guardar producto (crear o actualizar)
     */
    public Producto guardar(Producto producto) {
        try {
            logger.debug("Iniciando proceso de guardado de producto");
            // Validaciones de negocio
            validarProducto(producto);
            validarCodigoConCategoria(producto);
            // Validar nombre único
            if (productoRepository.existsByNombre(producto.getNombre())) {
                throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
            }
            // Generar código si es nuevo
            if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
                String codigo = codigoGeneratorService.generarSiguienteCodigo(producto.getCategoria().name());
                producto.setCodigo(codigo);
            }
            // Verificar código único
            verificarCodigoUnico(producto);
            // Calcular margen de ganancia
            calcularMargenGanancia(producto);
            // Preparar para guardar
            prepararProductoParaGuardar(producto);
            // Guardar en base de datos
            boolean guardado = productoRepository.save(producto);
            if (!guardado) {
                throw new RuntimeException("No se pudo guardar el producto en la base de datos");
            }
            logger.info("Producto guardado exitosamente: {} (Código: {})",
                    producto.getNombre(), producto.getCodigo());
            // ✅ OBSERVER PATTERN: Notificar evento
            eventManager.publishEvent(SystemEvent.EventType.PRODUCTO_CREADO,
                    "Nuevo producto creado: " + producto.getNombre(),
                    this);
            return producto;
        } catch (Exception e) {
            logger.error("Error al guardar producto: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Actualizar producto existente
     */
    public Producto actualizar(Producto producto) {
        try {
            if (producto.getId() == null) {
                throw new IllegalArgumentException("ID de producto requerido para actualización");
            }
            logger.debug("Actualizando producto ID: {}", producto.getId());
            // Verificar que existe
            Producto existente = obtenerPorId(producto.getId());
            // Validaciones
            validarProducto(producto);
            validarCodigoConCategoria(producto);
            // Validar nombre único (excluyendo el actual)
            if (!producto.getNombre().equalsIgnoreCase(existente.getNombre())
                    && productoRepository.existsByNombre(producto.getNombre())) {
                throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
            }
            // Verificar código único (excluyendo el actual)
            verificarCodigoUnicoParaActualizacion(producto);
            // Calcular margen
            calcularMargenGanancia(producto);
            // Mantener fecha de creación
            producto.setCreatedAt(existente.getCreatedAt());
            producto.setUpdatedAt(LocalDateTime.now());

            // Si la fecha de vencimiento cambió, eliminar la marca de notificado
            if (producto.getFechaVencimiento() != null && existente.getFechaVencimiento() != null
                    && !producto.getFechaVencimiento().isEqual(existente.getFechaVencimiento())) {
                com.farmaciavictoria.proyectopharmavictoria.util.AlertaCorreoPersistente
                        .eliminarNotificadoVencimiento(producto.getNombre());
            }

            // Actualizar en base de datos
            boolean actualizado = productoRepository.update(producto);
            if (!actualizado) {
                throw new RuntimeException("No se pudo actualizar el producto");
            }
            logger.info("Producto actualizado exitosamente: {}", producto.getNombre());
            // ✅ OBSERVER PATTERN: Notificar evento
            eventManager.publishEvent(SystemEvent.EventType.PRODUCTO_ACTUALIZADO,
                    "Producto actualizado: " + producto.getNombre(),
                    this);
            return producto;
        } catch (Exception e) {
            logger.error("Error al actualizar producto: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Eliminar producto
     */
    public boolean eliminar(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de producto inválido");
            }

            logger.debug("Eliminando producto ID: {}", id);

            // Verificar que existe
            Producto producto = obtenerPorId(id);

            // - No se puede eliminar si tiene ventas registradas
            // - Evaluar si desactivar en lugar de eliminar

            boolean eliminado = productoRepository.delete(id);

            if (!eliminado) {
                throw new RuntimeException("No se pudo eliminar el producto");
            }

            logger.info("Producto eliminado exitosamente: {}", producto.getNombre());

            // ✅ OBSERVER PATTERN: Notificar evento
            eventManager.publishEvent(SystemEvent.EventType.PRODUCTO_ELIMINADO,
                    "Producto eliminado: " + producto.getNombre(),
                    this);

            return true;

        } catch (Exception e) {
            logger.error("Error al eliminar producto ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Validaciones de negocio centralizadas
     */
    public void validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser nulo");
        }

        // Validar nombre
        validarNombre(producto.getNombre());

        // Validar precios
        validarPrecios(producto.getPrecioCompra(), producto.getPrecioVenta());

        // Validar stock
        validarStock(producto.getStockActual(), producto.getStockMinimo(), producto.getStockMaximo());
        // Validar que el stock actual no supere el stock máximo
        if (producto.getStockMaximo() != null && producto.getStockActual() != null
                && producto.getStockActual() > producto.getStockMaximo()) {
            throw new IllegalArgumentException("El stock actual (" + producto.getStockActual()
                    + ") no puede superar el stock máximo permitido (" + producto.getStockMaximo() + ").");
        }

        // Validar fecha de vencimiento
        validarFechaVencimiento(producto.getFechaVencimiento());

        // Validar categoría
        validarCategoria(producto.getCategoria());

        logger.debug("Validaciones de producto completadas exitosamente");
    }

    /**
     * ✅ SERVICE LAYER: Validar nombre del producto
     */
    public void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (nombre.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
        }

        if (nombre.trim().length() > 150) {
            throw new IllegalArgumentException("El nombre no puede exceder 150 caracteres");
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar precios
     */
    public void validarPrecios(BigDecimal precioCompra, BigDecimal precioVenta) {
        if (precioCompra == null || precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor a cero");
        }

        if (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a cero");
        }

        if (precioVenta.compareTo(precioCompra) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor al precio de compra");
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar stock
     */
    public void validarStock(Integer stockActual, Integer stockMinimo, Integer stockMaximo) {
        if (stockActual == null || stockActual < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo");
        }

        if (stockMinimo == null || stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }

        if (stockMaximo == null || stockMaximo < 0) {
            throw new IllegalArgumentException("El stock máximo no puede ser negativo");
        }

        if (stockMaximo < stockMinimo) {
            throw new IllegalArgumentException("El stock máximo debe ser mayor o igual al stock mínimo");
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar fecha de vencimiento
     */
    public void validarFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar categoría
     */
    public void validarCategoria(CategoriaProducto categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria");
        }
    }

    /**
     * ✅ SERVICE LAYER: Verificar código único
     */
    private void verificarCodigoUnico(Producto producto) {
        Optional<Producto> existenteOpt = productoRepository.findByCodigo(producto.getCodigo());
        if (existenteOpt.isPresent()) {
            logger.warn("Intento de crear producto con código duplicado: {}", producto.getCodigo());
            throw new IllegalArgumentException("Ya existe un producto con el código: " + producto.getCodigo());
        }
    }

    /**
     * ✅ SERVICE LAYER: Verificar código único para actualización
     */
    private void verificarCodigoUnicoParaActualizacion(Producto producto) {
        Optional<Producto> existenteOpt = productoRepository.findByCodigo(producto.getCodigo());
        if (existenteOpt.isPresent() && !existenteOpt.get().getId().equals(producto.getId())) {
            logger.warn("Intento de actualizar con código duplicado: {}", producto.getCodigo());
            throw new IllegalArgumentException("Ya existe otro producto con el código: " + producto.getCodigo());
        }
    }

    /**
     * ✅ SERVICE LAYER: Calcular margen de ganancia
     */
    private void calcularMargenGanancia(Producto producto) {
        if (producto.getPrecioCompra() != null && producto.getPrecioVenta() != null) {
            BigDecimal margen = producto.getPrecioVenta()
                    .subtract(producto.getPrecioCompra())
                    .divide(producto.getPrecioCompra(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));

            producto.setMargenGanancia(margen);
        }
    }

    /**
     * ✅ SERVICE LAYER: Preparar producto para guardar
     */
    private void prepararProductoParaGuardar(Producto producto) {
        // Limpiar y normalizar datos
        producto.setNombre(producto.getNombre().trim());

        if (producto.getDescripcion() != null) {
            producto.setDescripcion(producto.getDescripcion().trim());
        }

        // Establecer valores por defecto
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(STOCK_MINIMO_DEFAULT);
        }

        if (producto.getStockMaximo() == null) {
            producto.setStockMaximo(STOCK_MAXIMO_DEFAULT);
        }

        if (producto.getStockActual() == null) {
            producto.setStockActual(0);
        }

        // Establecer fechas
        if (producto.getCreatedAt() == null) {
            producto.setCreatedAt(LocalDateTime.now());
        }
        producto.setUpdatedAt(LocalDateTime.now());

        // Por defecto activo
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
    }

    /**
     * ✅ OBSERVER PATTERN: Verificar alertas de stock y vencimiento
     */
    private void verificarAlertas(List<Producto> productos) {
        int diasVencimientoAlerta = DIAS_VENCIMIENTO_ALERTA;
        for (Producto producto : productos) {
            // Alerta de stock bajo
            if (producto.getStockActual() <= producto.getStockMinimo()) {
                eventManager.publishEvent(SystemEvent.EventType.STOCK_BAJO,
                        "Stock bajo en producto: " + producto.getNombre(),
                        this);
            }

            // Alerta de vencimiento próximo
            if (producto.getFechaVencimiento() != null) {
                LocalDate hoy = LocalDate.now();
                LocalDate fechaLimite = hoy.plusDays(diasVencimientoAlerta);
                if (producto.getFechaVencimiento().isAfter(hoy) &&
                        (producto.getFechaVencimiento().isEqual(fechaLimite)
                                || producto.getFechaVencimiento().isBefore(fechaLimite))) {
                    eventManager.publishEvent(SystemEvent.EventType.PRODUCTO_VENCIMIENTO_PROXIMO,
                            "Producto próximo a vencer: " + producto.getNombre(),
                            this);
                }
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener productos con stock bajo
     */
    public List<Producto> obtenerProductosStockBajo() {
        try {
            logger.debug("Obteniendo productos con stock bajo");
            List<Producto> productos = productoRepository.findStockBajo();
            logger.info("Productos con stock bajo: {}", productos.size());
            return productos;
        } catch (Exception e) {
            logger.error("Error al obtener productos con stock bajo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener productos con stock bajo", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener productos próximos a vencer
     */
    public List<Producto> obtenerProductosProximosVencer() {
        try {
            logger.debug("Obteniendo productos próximos a vencer");
            int dias = getDiasVencimientoAlerta();
            List<Producto> productos = productoRepository.findProximosVencer(dias);
            logger.info("Productos próximos a vencer: {}", productos.size());
            return productos;
        } catch (Exception e) {
            logger.error("Error al obtener productos próximos a vencer: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener productos próximos a vencer", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Eliminación DEFINITIVA de producto (hard delete)
     * ⚠️ USAR CON PRECAUCIÓN: Esta operación es IRREVERSIBLE
     */
    public boolean eliminarDefinitivamente(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de producto inválido");
            }
            logger.warn("ELIMINACIÓN DEFINITIVA solicitada para producto ID: {}", id);
            // Verificar que existe antes de eliminar
            Producto producto = obtenerPorId(id);
            String nombreProducto = producto.getNombre();
            String codigoProducto = producto.getCodigo();

            // 1. Eliminar hijos en producto_historial_cambio
            try (java.sql.Connection connection = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig
                    .getInstance().getConnection();
                    java.sql.PreparedStatement stmt = connection
                            .prepareStatement("DELETE FROM producto_historial_cambio WHERE producto_id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (Exception ex) {
                logger.error("Error al eliminar historial de cambios del producto {}: {}", id, ex.getMessage(), ex);
                throw new RuntimeException(
                        "No se pudo eliminar el historial de cambios del producto: " + ex.getMessage(), ex);
            }

            // 2. Eliminar el producto
            boolean eliminado = productoRepository.deleteDefinitivamente(id);
            if (!eliminado) {
                throw new RuntimeException("No se pudo eliminar definitivamente el producto");
            }

            // ✅ OBSERVER PATTERN: Notificar eliminación definitiva
            SystemEventManager.getInstance().publishEvent(new SystemEvent(
                    SystemEvent.EventType.PRODUCTO_ELIMINADO,
                    String.format("Producto '%s' eliminado definitivamente", nombreProducto),
                    LocalDateTime.now()));
            logger.warn("Producto ELIMINADO DEFINITIVAMENTE: {} ({})", nombreProducto, codigoProducto);
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar definitivamente producto ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar definitivamente el producto: " + e.getMessage(), e);
        }
    }
}