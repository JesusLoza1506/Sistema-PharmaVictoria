package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ✅ SERVICE LAYER PATTERN - Servicio de negocio para proveedores
 * Centraliza toda la lógica de negocio relacionada con proveedores
 * Aplicando principios de separación de responsabilidades
 */
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    // Patrones de validación centralizados
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PATTERN_RUC = Pattern.compile("^\\d{11}$");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^\\d{7,15}$");

    private final ProveedorRepository proveedorRepository;
    private final SystemEventManager eventManager;

    // Temporal: Constructor con parámetro hasta implementar DI
    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
        this.eventManager = SystemEventManager.getInstance();
    }

    /**
     * ✅ SERVICE LAYER: Obtener todos los proveedores activos
     * Encapsula la lógica de negocio para listado de proveedores
     */
    public List<Proveedor> obtenerTodos() {
        try {
            logger.debug("Obteniendo todos los proveedores activos");
            List<Proveedor> proveedores = proveedorRepository.findAll();
            logger.info("Proveedores obtenidos exitosamente: {}", proveedores.size());
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener la lista de proveedores", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Buscar proveedores por texto
     * Aplica lógica de búsqueda de negocio
     */
    public List<Proveedor> buscarPorTexto(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return obtenerTodos();
            }

            logger.debug("Buscando proveedores por texto: {}", texto);
            List<Proveedor> resultados = proveedorRepository.buscarProveedores(texto.trim());
            logger.info("Búsqueda completada. Resultados encontrados: {}", resultados.size());
            return resultados;
        } catch (Exception e) {
            logger.error("Error en búsqueda de proveedores: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar proveedores", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener proveedor por ID
     */
    public Proveedor obtenerPorId(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de proveedor inválido");
            }

            logger.debug("Obteniendo proveedor por ID: {}", id);
            Proveedor proveedor = proveedorRepository.findById(id);

            if (proveedor == null) {
                logger.warn("Proveedor no encontrado con ID: {}", id);
                throw new RuntimeException("Proveedor no encontrado");
            }

            return proveedor;
        } catch (Exception e) {
            logger.error("Error al obtener proveedor por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el proveedor", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Guardar proveedor (crear o actualizar)
     * Aplica todas las validaciones de negocio antes de persistir
     */
    public Proveedor guardar(Proveedor proveedor) {
        try {
            logger.debug("Iniciando proceso de guardado de proveedor");

            // Aplicar validaciones de negocio
            validarProveedor(proveedor);

            // Verificar duplicados por RUC
            verificarRucUnico(proveedor);

            // Preparar datos para persistencia
            prepararProveedorParaGuardar(proveedor);

            // Guardar en base de datos
            boolean guardado = proveedorRepository.save(proveedor);

            if (!guardado) {
                throw new RuntimeException("No se pudo guardar el proveedor en la base de datos");
            }

            logger.info("Proveedor guardado exitosamente: {} (RUC: {})",
                    proveedor.getRazonSocial(), proveedor.getRuc());

            // ✅ OBSERVER PATTERN: Notificar evento de creación
            eventManager.publishEvent(SystemEvent.EventType.PROVEEDOR_CREADO,
                    "Nuevo proveedor creado: " + proveedor.getRazonSocial(),
                    this);

            return proveedor;

        } catch (Exception e) {
            logger.error("Error al guardar proveedor: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el proveedor: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Actualizar proveedor existente
     */
    public Proveedor actualizar(Proveedor proveedor, String usuario) {
        try {
            if (proveedor.getId() == null) {
                throw new IllegalArgumentException("ID de proveedor requerido para actualización");
            }
            logger.debug("Actualizando proveedor ID: {}", proveedor.getId());
            Proveedor existente = obtenerPorId(proveedor.getId());
            validarProveedor(proveedor);
            verificarRucUnicoParaActualizacion(proveedor);
            proveedor.setCreatedAt(existente.getCreatedAt());
            // Registrar cambios en historial si hay modificaciones
            compararYRegistrarCambios(existente, proveedor, usuario);
            boolean actualizado = proveedorRepository.update(proveedor);
            if (!actualizado) {
                throw new RuntimeException("No se pudo actualizar el proveedor");
            }
            logger.info("Proveedor actualizado exitosamente: {}", proveedor.getRazonSocial());
            eventManager.publishEvent(SystemEvent.EventType.PROVEEDOR_ACTUALIZADO,
                    "Proveedor actualizado: " + proveedor.getRazonSocial(),
                    this);
            return proveedor;
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el proveedor: " + e.getMessage(), e);
        }
    }

    /**
     * Compara los campos relevantes y registra los cambios en el historial
     */
    private void compararYRegistrarCambios(Proveedor anterior, Proveedor nuevo, String usuario) {
        String usuarioLogueado = com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService
                .getUsuarioActual() != null
                        ? com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService.getUsuarioActual()
                                .getUsername()
                        : usuario;
        if (!safeEquals(anterior.getRazonSocial(), nuevo.getRazonSocial())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "razon_social", anterior.getRazonSocial(),
                    nuevo.getRazonSocial(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getRuc(), nuevo.getRuc())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "ruc", anterior.getRuc(), nuevo.getRuc(),
                    usuarioLogueado);
        }
        if (!safeEquals(anterior.getContacto(), nuevo.getContacto())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "contacto", anterior.getContacto(),
                    nuevo.getContacto(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getTelefono(), nuevo.getTelefono())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "telefono", anterior.getTelefono(),
                    nuevo.getTelefono(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getEmail(), nuevo.getEmail())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "email", anterior.getEmail(), nuevo.getEmail(),
                    usuarioLogueado);
        }
        if (!safeEquals(anterior.getDireccion(), nuevo.getDireccion())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "direccion", anterior.getDireccion(),
                    nuevo.getDireccion(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getCondicionesPago(), nuevo.getCondicionesPago())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "condiciones_pago",
                    anterior.getCondicionesPago(), nuevo.getCondicionesPago(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getObservaciones(), nuevo.getObservaciones())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "observaciones", anterior.getObservaciones(),
                    nuevo.getObservaciones(), usuarioLogueado);
        }
        if (!safeEquals(anterior.getSucursalId(), nuevo.getSucursalId())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "sucursal_id",
                    String.valueOf(anterior.getSucursalId()), String.valueOf(nuevo.getSucursalId()), usuarioLogueado);
        }
        if (!safeEquals(anterior.getTipoProducto(), nuevo.getTipoProducto())) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "tipo_producto", anterior.getTipoProducto(),
                    nuevo.getTipoProducto(), usuarioLogueado);
        }
        if (anterior.getActivo() != nuevo.getActivo()) {
            proveedorRepository.registrarCambioProveedor(nuevo.getId(), "activo", String.valueOf(anterior.getActivo()),
                    String.valueOf(nuevo.getActivo()), usuarioLogueado);
        }
    }

    private boolean safeEquals(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    /**
     * Permite consultar el historial de cambios de un proveedor
     */
    public List<ProveedorRepository.HistorialCambioDTO> obtenerHistorialCambios(Integer proveedorId) {
        return proveedorRepository.obtenerHistorialCambios(proveedorId);
    }

    /**
     * ✅ SERVICE LAYER: Eliminar proveedor
     * Aplica reglas de negocio para eliminación
     */
    public boolean eliminar(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de proveedor inválido");
            }

            logger.debug("Eliminando proveedor ID: {}", id);

            // Verificar que existe
            Proveedor proveedor = obtenerPorId(id);

            // - No se puede eliminar si tiene productos asociados
            // - No se puede eliminar si tiene compras registradas
            // Por ahora permitimos la eliminación

            boolean eliminado = proveedorRepository.delete(id);

            if (!eliminado) {
                throw new RuntimeException("No se pudo eliminar el proveedor");
            }

            logger.info("Proveedor eliminado exitosamente: {}", proveedor.getRazonSocial());

            // ✅ OBSERVER PATTERN: Notificar evento de eliminación
            eventManager.publishEvent(SystemEvent.EventType.PROVEEDOR_ELIMINADO,
                    "Proveedor eliminado: " + proveedor.getRazonSocial(),
                    this);

            return true;

        } catch (Exception e) {
            logger.error("Error al eliminar proveedor ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el proveedor: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Validaciones de negocio centralizadas
     * Todas las reglas de validación en un solo lugar
     */
    public void validarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor no puede ser nulo");
        }

        // Validar razón social
        validarRazonSocial(proveedor.getRazonSocial());

        // Validar RUC
        validarRuc(proveedor.getRuc());

        // Validar email (opcional)
        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            validarEmail(proveedor.getEmail());
        }

        // Validar teléfono (opcional)
        if (proveedor.getTelefono() != null && !proveedor.getTelefono().trim().isEmpty()) {
            validarTelefono(proveedor.getTelefono());
        }

        logger.debug("Validaciones de proveedor completadas exitosamente");
    }

    /**
     * ✅ SERVICE LAYER: Validar razón social
     */
    public void validarRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }

        if (razonSocial.trim().length() < 3) {
            throw new IllegalArgumentException("La razón social debe tener al menos 3 caracteres");
        }

        if (razonSocial.trim().length() > 150) {
            throw new IllegalArgumentException("La razón social no puede exceder 150 caracteres");
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar RUC (OPCIONAL)
     */
    public void validarRuc(String ruc) {
        // RUC es OPCIONAL - solo validar formato si se proporciona
        if (ruc != null && !ruc.trim().isEmpty()) {
            if (!PATTERN_RUC.matcher(ruc.trim()).matches()) {
                throw new IllegalArgumentException("El RUC debe tener exactamente 11 dígitos numéricos");
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar email
     */
    public void validarEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!PATTERN_EMAIL.matcher(email.trim()).matches()) {
                throw new IllegalArgumentException("El formato del email es inválido");
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Validar teléfono
     */
    public void validarTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (!PATTERN_TELEFONO.matcher(telefono.trim()).matches()) {
                throw new IllegalArgumentException("El teléfono debe tener entre 7 y 15 dígitos numéricos");
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Verificar que el RUC sea único (solo si se proporciona)
     */
    private void verificarRucUnico(Proveedor proveedor) {
        // Solo verificar unicidad si se proporciona RUC
        if (proveedor.getRuc() != null && !proveedor.getRuc().trim().isEmpty()) {
            Proveedor existente = proveedorRepository.findByRuc(proveedor.getRuc());
            if (existente != null) {
                logger.warn("Intento de crear proveedor con RUC duplicado: {}", proveedor.getRuc());
                throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Verificar RUC único para actualización (solo si se
     * proporciona)
     */
    private void verificarRucUnicoParaActualizacion(Proveedor proveedor) {
        // Solo verificar unicidad si se proporciona RUC
        if (proveedor.getRuc() != null && !proveedor.getRuc().trim().isEmpty()) {
            Proveedor existente = proveedorRepository.findByRuc(proveedor.getRuc());
            if (existente != null && !existente.getId().equals(proveedor.getId())) {
                logger.warn("Intento de actualizar con RUC duplicado: {}", proveedor.getRuc());
                throw new IllegalArgumentException("Ya existe otro proveedor con el RUC: " + proveedor.getRuc());
            }
        }
    }

    /**
     * ✅ SERVICE LAYER: Preparar proveedor para guardar
     */
    private void prepararProveedorParaGuardar(Proveedor proveedor) {
        // Limpiar y normalizar datos
        proveedor.setRazonSocial(proveedor.getRazonSocial().trim());

        // RUC es opcional - solo normalizar si se proporciona
        if (proveedor.getRuc() != null && !proveedor.getRuc().trim().isEmpty()) {
            proveedor.setRuc(proveedor.getRuc().trim());
        } else {
            proveedor.setRuc(null); // Asegurar que esté en null si está vacío
        }

        if (proveedor.getEmail() != null) {
            proveedor.setEmail(proveedor.getEmail().trim().toLowerCase());
        }

        // Establecer fecha de creación si es nuevo
        if (proveedor.getCreatedAt() == null) {
            proveedor.setCreatedAt(LocalDateTime.now());
        }

        // Por defecto está activo (si no se especifica)
        // proveedor.setActivo(true); // Ya viene del formulario
    }

    /**
     * ✅ ELIMINACIÓN DEFINITIVA: Hard delete del proveedor (IRREVERSIBLE)
     * ⚠️ USAR CON PRECAUCIÓN: Esta operación es IRREVERSIBLE
     */
    public boolean eliminarDefinitivamente(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de proveedor inválido");
            }

            logger.warn("ELIMINACIÓN DEFINITIVA solicitada para proveedor ID: {}", id);

            // Verificar que existe antes de eliminar
            Proveedor proveedor = obtenerPorId(id);
            String nombreProveedor = proveedor.getRazonSocial();
            String rucProveedor = proveedor.getRuc();

            // ELIMINACIÓN DEFINITIVA (hard delete)
            boolean eliminado = proveedorRepository.deleteDefinitivamente(id);

            if (!eliminado) {
                throw new RuntimeException("No se pudo eliminar definitivamente el proveedor");
            }

            logger.warn("Proveedor ELIMINADO DEFINITIVAMENTE: {} (RUC: {})", nombreProveedor, rucProveedor);

            // ✅ OBSERVER PATTERN: Notificar evento de eliminación definitiva
            eventManager.publishEvent(SystemEvent.EventType.PROVEEDOR_ELIMINADO,
                    "Proveedor ELIMINADO DEFINITIVAMENTE: " + nombreProveedor,
                    this);

            return true;

        } catch (Exception e) {
            logger.error("Error al eliminar definitivamente proveedor ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar definitivamente el proveedor: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener todos los proveedores incluyendo inactivos
     */
    public List<Proveedor> obtenerTodosIncluyendoInactivos() {
        try {
            logger.debug("Obteniendo todos los proveedores incluyendo inactivos");
            List<Proveedor> proveedores = proveedorRepository.findAllIncludingInactive();
            logger.info("Proveedores obtenidos (incluye inactivos): {}", proveedores.size());
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores incluyendo inactivos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener la lista completa de proveedores", e);
        }
    }

    /**
     * Verifica si existe un proveedor con la misma razón social (excluyendo el id
     * si se indica)
     */
    public boolean existePorRazonSocial(String razonSocial, Integer idExcluir) {
        return proveedorRepository.existePorRazonSocial(razonSocial, idExcluir);
    }
}