package com.farmaciavictoria.proyectopharmavictoria.config;

import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;
import com.farmaciavictoria.proyectopharmavictoria.repository.ProveedorRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Cliente.ClienteRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Usuario.UsuarioRepository;
import com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService;
import com.farmaciavictoria.proyectopharmavictoria.service.CodigoGeneratorService;
import com.farmaciavictoria.proyectopharmavictoria.service.NotificationService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.ClienteService;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ✅ DEPENDENCY INJECTION PATTERN - Contenedor de Dependencias
 * Implementa inyección de dependencias simple para desacoplar componentes
 * Centraliza la creación y gestión del ciclo de vida de objetos
 */
public class ServiceContainer {
    public com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService getUsuarioService() {
        return getService(com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(ServiceContainer.class);

    private static ServiceContainer instance;
    private final Map<Class<?>, Object> services = new HashMap<>();
    private final Map<Class<?>, Object> repositories = new HashMap<>();

    private ServiceContainer() {
        initializeContainer();
    }

    /**
     * ✅ SINGLETON PATTERN: Obtener instancia única del contenedor
     */
    public static ServiceContainer getInstance() {
        if (instance == null) {
            synchronized (ServiceContainer.class) {
                if (instance == null) {
                    instance = new ServiceContainer();
                }
            }
        }
        return instance;
    }

    /**
     * ✅ DEPENDENCY INJECTION: Inicializar todas las dependencias
     */
    private void initializeContainer() {
        try {
            logger.info("Inicializando contenedor de dependencias...");

            // ✅ OBSERVER PATTERN: Inicializar sistema de eventos
            SystemEventManager.getInstance();

            // Registrar Repositories
            registerRepositories();

            // Registrar Services
            registerServices();

            logger.info("Contenedor de dependencias inicializado exitosamente");

        } catch (Exception e) {
            logger.error("Error al inicializar contenedor de dependencias: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo crítico en inicialización del contenedor DI", e);
        }
    }

    /**
     * ✅ DEPENDENCY INJECTION: Registrar todos los repositories
     */
    private void registerRepositories() {
        logger.debug("Registrando repositories...");

        // Repository Pattern - Capa de acceso a datos
        repositories.put(UsuarioRepository.class, new UsuarioRepository());
        repositories.put(ProveedorRepository.class, new ProveedorRepository());
        repositories.put(ProductoRepository.class, new ProductoRepository());
        repositories.put(ClienteRepository.class, new ClienteRepository());

        // Eliminado registro de SucursalRepository

        logger.debug("Repositories registrados: {}", repositories.size());
    }

    /**
     * ✅ DEPENDENCY INJECTION: Registrar todos los services
     */
    private void registerServices() {
        logger.debug("Registrando services...");

        // Service Layer Pattern - Lógica de negocio
        UsuarioRepository usuarioRepo = getRepository(UsuarioRepository.class);
        ProveedorRepository proveedorRepo = getRepository(ProveedorRepository.class);
        ProductoRepository productoRepo = getRepository(ProductoRepository.class);
        ClienteRepository clienteRepo = getRepository(ClienteRepository.class);
        CodigoGeneratorService codigoService = new CodigoGeneratorService();

        // Servicios principales
        ProductoService productoService = new ProductoService(productoRepo, codigoService);
        ClienteService clienteService = new ClienteService(clienteRepo);

        // Registrar UsuarioService
        UsuarioService usuarioService = UsuarioService.getInstance();
        services.put(UsuarioService.class, usuarioService);

        services.put(AuthenticationService.class, new AuthenticationService(usuarioRepo));
        services.put(ProveedorService.class, new ProveedorService(proveedorRepo));
        services.put(ProductoService.class, productoService);
        services.put(CodigoGeneratorService.class, codigoService);
        services.put(ClienteService.class, clienteService);

        // ✅ NUEVO: NotificationService con enterprise patterns
        services.put(NotificationService.class, new NotificationService(productoService));

        logger.debug("Services registrados: {}", services.size());
    }

    /**
     * ✅ DEPENDENCY INJECTION: Obtener service por tipo
     * 
     * @param serviceClass Clase del service a obtener
     * @return Instancia del service
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        try {
            T service = (T) services.get(serviceClass);

            if (service == null) {
                logger.error("Service no encontrado: {}", serviceClass.getSimpleName());
                throw new IllegalArgumentException("Service no registrado: " + serviceClass.getSimpleName());
            }

            logger.debug("Service obtenido: {}", serviceClass.getSimpleName());
            return service;

        } catch (Exception e) {
            logger.error("Error al obtener service {}: {}", serviceClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Error en inyección de dependencias", e);
        }
    }

    /**
     * ✅ DEPENDENCY INJECTION: Obtener repository por tipo
     * 
     * @param repositoryClass Clase del repository a obtener
     * @return Instancia del repository
     */
    @SuppressWarnings("unchecked")
    public <T> T getRepository(Class<T> repositoryClass) {
        try {
            T repository = (T) repositories.get(repositoryClass);

            if (repository == null) {
                logger.error("Repository no encontrado: {}", repositoryClass.getSimpleName());
                throw new IllegalArgumentException("Repository no registrado: " + repositoryClass.getSimpleName());
            }

            logger.debug("Repository obtenido: {}", repositoryClass.getSimpleName());
            return repository;

        } catch (Exception e) {
            logger.error("Error al obtener repository {}: {}", repositoryClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Error en inyección de dependencias", e);
        }
    }

    /**
     * ✅ DEPENDENCY INJECTION: Métodos de conveniencia para obtener services comunes
     */
    public AuthenticationService getAuthenticationService() {
        return getService(AuthenticationService.class);
    }

    public ProveedorService getProveedorService() {
        return getService(ProveedorService.class);
    }

    public ProductoService getProductoService() {
        return getService(ProductoService.class);
    }

    public NotificationService getNotificationService() {
        return getService(NotificationService.class);
    }

    public CodigoGeneratorService getCodigoGeneratorService() {
        return getService(CodigoGeneratorService.class);
    }

    /**
     * ✅ DEPENDENCY INJECTION: Métodos de conveniencia para obtener repositories
     * comunes
     */
    public UsuarioRepository getUsuarioRepository() {
        return getRepository(UsuarioRepository.class);
    }

    public ProveedorRepository getProveedorRepository() {
        return getRepository(ProveedorRepository.class);
    }

    public ProductoRepository getProductoRepository() {
        return getRepository(ProductoRepository.class);
    }

    public ClienteService getClienteService() {
        return getService(ClienteService.class);
    }

    /**
     * ✅ DEPENDENCY INJECTION: Log de servicios registrados
     */
    public void logRegisteredServices() {
        logger.info("=== SERVICIOS REGISTRADOS ===");

        logger.info("Services ({}):", services.size());
        services.keySet().forEach(clazz -> logger.info("  - {}", clazz.getSimpleName()));

        logger.info("Repositories ({}):", repositories.size());
        repositories.keySet().forEach(clazz -> logger.info("  - {}", clazz.getSimpleName()));

        logger.info("==============================");
    }

    /**
     * ✅ DEPENDENCY INJECTION: Reinicializar contenedor (para testing o
     * reconfiguración)
     */
    public void reinitialize() {
        logger.info("Reinicializando contenedor de dependencias...");

        services.clear();
        repositories.clear();

        initializeContainer();

        logger.info("Contenedor reinicializado exitosamente");
    }

    /**
     * ✅ DEPENDENCY INJECTION: Obtener información del contenedor
     */
    public String getContainerInfo() {
        return String.format("ServiceContainer - Services: %d, Repositories: %d",
                services.size(), repositories.size());
    }
}