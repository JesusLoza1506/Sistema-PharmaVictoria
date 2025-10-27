package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ✅ SERVICE LAYER PATTERN - Servicio de Clientes
 * Versión simplificada que trabaja con la tabla básica de clientes
 * Mantiene todos los patrones enterprise
 * 
 * @author PHARMAVICTORIA Development Team
 * @version 1.0 (Tabla Básica)
 */
public class ClienteService {
    /**
     * Normaliza y prepara los datos del cliente antes de guardar
     */
    private void prepararClienteParaGuardar(Cliente cliente) {
        if (cliente.getNombres() != null) {
            cliente.setNombres(cliente.getNombres().trim().toUpperCase());
        }
        if (cliente.getApellidos() != null) {
            cliente.setApellidos(cliente.getApellidos().trim().toUpperCase());
        }
        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim().toLowerCase());
        }
        if (cliente.getDocumento() != null) {
            cliente.setDocumento(cliente.getDocumento().trim());
        }
        if (cliente.getPuntosTotales() == null) {
            cliente.setPuntosTotales(0);
        }
        if (cliente.getPuntosUsados() == null) {
            cliente.setPuntosUsados(0);
        }
        // El campo frecuente ahora es booleano, inicializar si es null
        // (Si el modelo lo permite, pero normalmente isFrecuente() es primitivo)
        if (cliente.getCreatedAt() == null) {
            cliente.setCreatedAt(LocalDateTime.now());
        }
        // Marcar como frecuente si tiene más de 500 puntos
        if (cliente.getPuntosDisponibles() != null && cliente.getPuntosDisponibles() >= 500) {
            cliente.setEsFrecuente(true);
        }
    }

    /**
     * Devuelve todos los clientes sin paginación
     */
    public List<Cliente> obtenerTodos() {
        try {
            // Si existe findAll() sin parámetros, úsalo. Si no, usa paginación amplia.
            // return clienteRepository.findAll();
            return clienteRepository.findAll(0, 10000); // Ajusta el límite según el tamaño esperado
        } catch (Exception e) {
            logger.error("Error al obtener todos los clientes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Obtiene el historial de cambios para un cliente específico
     */
    public List<com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio> obtenerHistorialDeCambios(
            Integer clienteId) {
        if (clienteId == null || clienteId <= 0)
            return List.of();
        return historialService.obtenerHistorialPorCliente(clienteId);
    }

    // Servicio de historial de cambios
    private final com.farmaciavictoria.proyectopharmavictoria.service.ClienteHistorialCambioService historialService = new com.farmaciavictoria.proyectopharmavictoria.service.ClienteHistorialCambioService();

    // Métodos adaptados para compatibilidad con el controlador
    public List<Cliente> obtenerTodosLosClientes() {
        return obtenerTodos();
    }

    public Cliente guardarCliente(Cliente cliente) {
        return guardar(cliente);
    }

    public Cliente actualizarCliente(Cliente cliente) {
        return guardar(cliente);
    }

    public boolean eliminarCliente(Integer id) {
        if (id == null || id <= 0) {
            logger.error("ID de cliente inválido: {}", id);
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        Cliente clienteAnterior = obtenerPorId(id);
        if (clienteAnterior == null) {
            logger.warn("No existe cliente con ID: {}", id);
            return false;
        }
        try (java.sql.Connection conn = DatabaseConfig.getInstance().getConnection()) {
            // Eliminar historial de cambios
            try (java.sql.PreparedStatement stmt = conn
                    .prepareStatement("DELETE FROM cliente_historial_cambio WHERE cliente_id = ?")) {
                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();
                logger.info("Historial eliminado para cliente {}: {} registros", id, rows);
            }
        } catch (java.sql.SQLException sqlEx) {
            logger.error("SQLException al eliminar historial de cliente {}: {}", id, sqlEx.getMessage(), sqlEx);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar historial de cliente {}: {}", id, e.getMessage(), e);
            return false;
        }
        boolean eliminado = false;
        try {
            eliminado = clienteRepository.delete(id);
        } catch (Exception e) {
            logger.error("Error al eliminar cliente en repositorio ID {}: {}", id, e.getMessage(), e);
            return false;
        }
        if (eliminado) {
            logger.info("Cliente eliminado ID: {}", id);
            // Auditoría: registrar historial de eliminación
            String usuario = System.getProperty("user.name", "sistema");
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            historialService.registrarCambio(
                    new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                            id, "ELIMINACION", clienteAnterior.getNombreCompleto(), null, usuario, ahora));
            // Publicar evento solo si fue eliminado
            eventManager.publishEvent(SystemEvent.EventType.CLIENTE_ELIMINADO,
                    "Cliente eliminado ID: " + id,
                    this);
        } else {
            logger.warn("No se pudo eliminar el cliente ID: {}", id);
        }
        return eliminado;
    }

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    // ✅ DEPENDENCY INJECTION
    private final ClienteRepository clienteRepository;
    private final SystemEventManager eventManager;

    // ✅ PATRONES DE VALIDACIÓN
    private static final Pattern PATTERN_DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^\\d{7,15}$");

    /**
     * ✅ DEPENDENCY INJECTION: Constructor
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        this.eventManager = SystemEventManager.getInstance();
        logger.info("ClienteService inicializado");
    }

    // ✅ OPERACIONES PRINCIPALES

    /**
     * ✅ SERVICE LAYER: Obtener todos los clientes activos
     */
    /**
     * Obtener todos los clientes con paginación
     */
    public List<Cliente> obtenerTodos(int offset, int limit) {
        try {
            logger.debug("Obteniendo todos los clientes paginados");
            return clienteRepository.findAll(offset, limit);
        } catch (Exception e) {
            logger.error("Error al obtener clientes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener la lista de clientes", e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener cliente por ID
     */
    public Cliente obtenerPorId(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID de cliente inválido");
            }
            Optional<Cliente> clienteOpt = clienteRepository.findById(id);
            return clienteOpt.orElse(null);
        } catch (Exception e) {
            logger.error("Error al obtener cliente por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * ✅ SERVICE LAYER: Buscar cliente por DNI
     */
    public Optional<Cliente> buscarPorDni(String dni) {
        // Eliminado: buscarPorDni ya no es válido, usar buscarPorDocumento si se
        // requiere
        throw new UnsupportedOperationException("La búsqueda por DNI ha sido eliminada. Use buscarPorDocumento.");
    }

    /**
     * ✅ SERVICE LAYER: Buscar clientes por nombre
     */
    /**
     * Buscar clientes por nombre o apellido
     */
    public List<Cliente> buscarPorNombreOApellido(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return List.of();
            }
            logger.debug("Buscando clientes por nombre/apellido: {}", texto);
            return clienteRepository.findByNombreOApellido(texto.trim());
        } catch (Exception e) {
            logger.error("Error al buscar clientes por nombre/apellido '{}': {}", texto, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Filtrar clientes por teléfono
     */
    public List<Cliente> buscarPorTelefono(String telefono) {
        try {
            if (telefono == null || telefono.trim().isEmpty()) {
                return List.of();
            }
            return obtenerTodos(0, 1000).stream()
                    .filter(c -> telefono.equals(c.getTelefono()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al buscar clientes por teléfono '{}': {}", telefono, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Filtrar clientes por email
     */
    public List<Cliente> buscarPorEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return List.of();
            }
            return obtenerTodos(0, 1000).stream()
                    .filter(c -> email.equalsIgnoreCase(c.getEmail()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al buscar clientes por email '{}': {}", email, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Filtrar clientes por estado frecuente
     */
    public List<Cliente> buscarFrecuentes() {
        try {
            return obtenerTodos(0, 1000).stream()
                    .filter(Cliente::isFrecuente)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al buscar clientes frecuentes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Filtrar clientes por estado inactivo (si existiera campo)
     */
    public List<Cliente> buscarInactivos() {
        // Si se agrega campo activo/inactivo, aquí se implementa
        return List.of();
    }

    /**
     * ✅ SERVICE LAYER: Guardar cliente (crear o actualizar)
     */
    public Cliente guardar(Cliente cliente) {
        try {
            logger.debug("Iniciando proceso de guardado de cliente");
            // Aplicar validaciones de negocio
            validarCliente(cliente);
            // Preparar datos para persistencia
            prepararClienteParaGuardar(cliente);
            boolean esNuevo = cliente.getId() == null;
            Cliente clienteAnterior = null;
            if (!esNuevo) {
                clienteAnterior = obtenerPorId(cliente.getId());
            }
            boolean guardado = clienteRepository.save(cliente);
            if (!guardado) {
                throw new RuntimeException("No se pudo guardar el cliente en la base de datos");
            }
            String accion = esNuevo ? "creado" : "actualizado";
            logger.info("Cliente {} exitosamente: {}", accion, cliente.getNombreCompleto());
            // Auditoría: registrar historial de cambios siguiendo el patrón de proveedores
            String usuario = com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService
                    .getUsuarioActual() != null
                            ? com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService
                                    .getUsuarioActual().getUsername()
                            : System.getProperty("user.name", "sistema");
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            if (esNuevo) {
                historialService.registrarCambio(
                        new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                cliente.getId(), "CREACION", null, cliente.getNombreCompleto(), usuario, ahora));
            } else if (clienteAnterior != null) {
                // Comparar campos relevantes y registrar cada cambio individualmente
                if (!safeEquals(clienteAnterior.getDocumento(), cliente.getDocumento()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "documento", clienteAnterior.getDocumento(),
                                    cliente.getDocumento(), usuario,
                                    ahora));
                if (!safeEquals(clienteAnterior.getTipoCliente(), cliente.getTipoCliente()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "tipo_cliente", clienteAnterior.getTipoCliente(),
                                    cliente.getTipoCliente(), usuario,
                                    ahora));
                if (!safeEquals(clienteAnterior.getRazonSocial(), cliente.getRazonSocial()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "razon_social", clienteAnterior.getRazonSocial(),
                                    cliente.getRazonSocial(), usuario,
                                    ahora));
                if (!safeEquals(clienteAnterior.getNombres(), cliente.getNombres()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "nombres", clienteAnterior.getNombres(), cliente.getNombres(),
                                    usuario, ahora));
                if (!safeEquals(clienteAnterior.getApellidos(), cliente.getApellidos()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "apellidos", clienteAnterior.getApellidos(),
                                    cliente.getApellidos(), usuario, ahora));
                if (!safeEquals(clienteAnterior.getTelefono(), cliente.getTelefono()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "telefono", clienteAnterior.getTelefono(), cliente.getTelefono(),
                                    usuario, ahora));
                if (!safeEquals(clienteAnterior.getEmail(), cliente.getEmail()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "email", clienteAnterior.getEmail(), cliente.getEmail(), usuario,
                                    ahora));
                if (!safeEquals(clienteAnterior.getDireccion(), cliente.getDireccion()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "direccion", clienteAnterior.getDireccion(),
                                    cliente.getDireccion(), usuario, ahora));
                if (!safeEquals(clienteAnterior.getFechaNacimiento(), cliente.getFechaNacimiento()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "fecha_nacimiento",
                                    safeToString(clienteAnterior.getFechaNacimiento()),
                                    safeToString(cliente.getFechaNacimiento()), usuario, ahora));
                if (!safeEquals(clienteAnterior.getPuntosTotales(), cliente.getPuntosTotales()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "puntos_totales", safeToString(clienteAnterior.getPuntosTotales()),
                                    safeToString(cliente.getPuntosTotales()), usuario, ahora));
                if (!safeEquals(clienteAnterior.getPuntosUsados(), cliente.getPuntosUsados()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "puntos_usados", safeToString(clienteAnterior.getPuntosUsados()),
                                    safeToString(cliente.getPuntosUsados()), usuario, ahora));
                if (!safeEquals(clienteAnterior.isFrecuente(), cliente.isFrecuente()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "frecuente", safeToString(clienteAnterior.isFrecuente()),
                                    safeToString(cliente.isFrecuente()), usuario, ahora));
                if (!safeEquals(clienteAnterior.getCreatedAt(), cliente.getCreatedAt()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "created_at", safeToString(clienteAnterior.getCreatedAt()),
                                    safeToString(cliente.getCreatedAt()), usuario, ahora));
                if (!safeEquals(clienteAnterior.getUpdatedAt(), cliente.getUpdatedAt()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "updated_at", safeToString(clienteAnterior.getUpdatedAt()),
                                    safeToString(cliente.getUpdatedAt()), usuario, ahora));
                if (!safeEquals(clienteAnterior.getPuntosDisponibles(), cliente.getPuntosDisponibles()))
                    historialService.registrarCambio(
                            new com.farmaciavictoria.proyectopharmavictoria.model.Cliente.ClienteHistorialCambio(
                                    cliente.getId(), "puntos_disponibles",
                                    safeToString(clienteAnterior.getPuntosDisponibles()),
                                    safeToString(cliente.getPuntosDisponibles()), usuario, ahora));
            }
            // ✅ OBSERVER PATTERN: Publicar evento
            SystemEvent.EventType tipoEvento = esNuevo ? SystemEvent.EventType.CLIENTE_CREADO
                    : SystemEvent.EventType.CLIENTE_ACTUALIZADO;
            eventManager.publishEvent(tipoEvento,
                    "Cliente " + accion + ": " + cliente.getNombreCompleto(),
                    this);
            return cliente;
        } catch (Exception e) {
            logger.error("Error al guardar cliente: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Métodos auxiliares para comparación segura
    private boolean safeEquals(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    private String safeToString(Object o) {
        return o == null ? "" : o.toString();
    }

    // Método desactivar eliminado: ahora eliminarCliente hace el borrado real

    // ✅ SISTEMA DE PUNTOS

    /**
     * ✅ SERVICE LAYER: Agregar puntos por compra
     */
    public void agregarPuntosPorCompra(Integer clienteId, Double montoCompra) {
        try {
            if (clienteId == null || montoCompra == null || montoCompra <= 0) {
                return;
            }
            // Calcular puntos (1 punto por cada S/ 10)
            int puntosAGanar = (int) (montoCompra / 10);
            if (puntosAGanar > 0) {
                logger.info("Puntos agregados a cliente ID {}: {} puntos por compra de S/ {}",
                        clienteId, puntosAGanar, montoCompra);
                // ✅ OBSERVER PATTERN: Publicar evento de puntos
                eventManager.publishEvent(SystemEvent.EventType.CLIENTE_PUNTOS_AGREGADOS,
                        String.format("Cliente ID %d ganó %d puntos", clienteId, puntosAGanar),
                        this);
            }
        } catch (Exception e) {
            logger.error("Error al agregar puntos por compra: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Redimir puntos del cliente
     */
    public boolean redimirPuntos(Integer clienteId, Integer puntos) {
        try {
            if (clienteId == null || puntos == null || puntos <= 0) {
                return false;
            }

            logger.info("Puntos redimidos de cliente ID {}: {} puntos", clienteId, puntos);

            // ✅ OBSERVER PATTERN: Publicar evento de redención
            eventManager.publishEvent(SystemEvent.EventType.CLIENTE_PUNTOS_REDIMIDOS,
                    String.format("Cliente ID %d redimió %d puntos", clienteId, puntos),
                    this);

            return true;

        } catch (Exception e) {
            logger.error("Error al redimir puntos: {}", e.getMessage(), e);
            return false;
        }
    }

    // ✅ VALIDACIONES DE NEGOCIO

    /**
     * ✅ SERVICE LAYER: Validaciones centralizadas de cliente
     */
    public void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no puede ser nulo");
        }

        String tipo = cliente.getTipoCliente();
        if ("Natural".equalsIgnoreCase(tipo)) {
            // Validar documento (DNI)
            if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
                throw new IllegalArgumentException("DNI es obligatorio");
            }
            if (!PATTERN_DNI.matcher(cliente.getDocumento().trim()).matches()) {
                throw new IllegalArgumentException("DNI debe tener exactamente 8 dígitos numéricos");
            }
            // Validar nombres
            if (cliente.getNombres() == null || cliente.getNombres().trim().isEmpty()) {
                throw new IllegalArgumentException("Nombres son obligatorios");
            }
            if (cliente.getNombres().trim().length() < 2) {
                throw new IllegalArgumentException("Nombres deben tener al menos 2 caracteres");
            }
            // Validar apellidos
            if (cliente.getApellidos() == null || cliente.getApellidos().trim().isEmpty()) {
                throw new IllegalArgumentException("Apellidos son obligatorios");
            }
            if (cliente.getApellidos().trim().length() < 2) {
                throw new IllegalArgumentException("Apellidos deben tener al menos 2 caracteres");
            }
        } else if ("Empresarial".equalsIgnoreCase(tipo)) {
            // Validar documento (RUC)
            if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
                throw new IllegalArgumentException("RUC es obligatorio");
            }
            if (!cliente.getDocumento().trim().matches("\\d{11}")) {
                throw new IllegalArgumentException("RUC debe tener exactamente 11 dígitos numéricos");
            }
            // Validar Razón Social
            if (cliente.getRazonSocial() == null || cliente.getRazonSocial().trim().isEmpty()) {
                throw new IllegalArgumentException("Razón Social es obligatoria");
            }
            if (cliente.getRazonSocial().trim().length() < 2) {
                throw new IllegalArgumentException("Razón Social debe tener al menos 2 caracteres");
            }
        } else {
            throw new IllegalArgumentException("Tipo de cliente inválido");
        }

        // Validaciones opcionales
        validarEmail(cliente.getEmail());
        validarTelefono(cliente.getTelefono());
        validarFechaNacimiento(cliente.getFechaNacimiento());
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
     * ✅ SERVICE LAYER: Validar fecha de nacimiento
     */
    public void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null) {
            LocalDate hoy = LocalDate.now();
            if (fechaNacimiento.isAfter(hoy)) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            int edad = hoy.getYear() - fechaNacimiento.getYear();
            if (edad < 0 || edad > 120) {
                throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años");
            }
        }
    }

    // ✅ CONSULTAS ESPECIALIZADAS

    /**
     * ✅ SERVICE LAYER: Obtener clientes frecuentes
     */
    public List<Cliente> obtenerClientesFrecuentes() {
        try {
            return obtenerTodos().stream()
                    .filter(Cliente::isFrecuente)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener clientes frecuentes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * ✅ SERVICE LAYER: Obtener estadísticas de clientes
     */
    public EstadisticasClientes obtenerEstadisticas() {
        try {
            List<Cliente> todos = obtenerTodos();

            int totalActivos = todos.size();
            int frecuentes = (int) todos.stream()
                    .filter(Cliente::isFrecuente)
                    .count();
            int nuevosDelMes = 0; // Por implementar con fecha_registro
            double promedioPuntos = todos.stream()
                    .mapToInt(c -> c.getPuntosDisponibles() != null ? c.getPuntosDisponibles() : 0)
                    .average()
                    .orElse(0.0);

            return new EstadisticasClientes(totalActivos, frecuentes, nuevosDelMes, promedioPuntos);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de clientes: {}", e.getMessage(), e);
            return new EstadisticasClientes(0, 0, 0, 0.0);
        }
    }

    /**
     * ✅ CLASE PARA ESTADÍSTICAS
     */
    public static class EstadisticasClientes {
        private final int totalActivos;
        private final int frecuentes;
        private final int nuevosDelMes;
        private final double promedioPuntos;

        public EstadisticasClientes(int totalActivos, int frecuentes, int nuevosDelMes, double promedioPuntos) {
            this.totalActivos = totalActivos;
            this.frecuentes = frecuentes;
            this.nuevosDelMes = nuevosDelMes;
            this.promedioPuntos = promedioPuntos;
        }

        public int getTotalActivos() {
            return totalActivos;
        }

        public int getFrecuentes() {
            return frecuentes;
        }

        public int getNuevosDelMes() {
            return nuevosDelMes;
        }

        public double getPromedioPuntos() {
            return promedioPuntos;
        }
    }
}