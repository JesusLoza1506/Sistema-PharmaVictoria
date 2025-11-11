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

// Contenedor central de servicios y repositorios para la aplicación (Inyección de dependencias)
public class ServiceContainer {
    // Devuelve la instancia de UsuarioService
    public com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService getUsuarioService() {
        return getService(com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(ServiceContainer.class);

    // Instancia única (Singleton)
    private static ServiceContainer instance;
    // Almacena servicios registrados
    private final Map<Class<?>, Object> services = new HashMap<>();
    // Almacena repositorios registrados
    private final Map<Class<?>, Object> repositories = new HashMap<>();

    // Constructor privado: inicializa el contenedor
    private ServiceContainer() {
        initializeContainer();
    }

    // Devuelve la instancia única del contenedor
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

    // Inicializa todos los servicios y repositorios
    private void initializeContainer() {
        try {
            logger.info("Inicializando contenedor de dependencias...");
            SystemEventManager.getInstance();
            registerRepositories();
            registerServices();
            logger.info("Contenedor de dependencias inicializado exitosamente");
        } catch (Exception e) {
            logger.error("Error al inicializar contenedor de dependencias: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo crítico en inicialización del contenedor DI", e);
        }
    }

    // Registra los repositorios principales
    private void registerRepositories() {
        logger.debug("Registrando repositories...");
        repositories.put(UsuarioRepository.class, new UsuarioRepository());
        repositories.put(ProveedorRepository.class, new ProveedorRepository());
        repositories.put(ProductoRepository.class, new ProductoRepository());
        repositories.put(ClienteRepository.class, new ClienteRepository());
        logger.debug("Repositories registrados: {}", repositories.size());
    }

    // Registra los servicios principales
    private void registerServices() {
        logger.debug("Registrando services...");
        UsuarioRepository usuarioRepo = getRepository(UsuarioRepository.class);
        ProveedorRepository proveedorRepo = getRepository(ProveedorRepository.class);
        ProductoRepository productoRepo = getRepository(ProductoRepository.class);
        ClienteRepository clienteRepo = getRepository(ClienteRepository.class);
        CodigoGeneratorService codigoService = new CodigoGeneratorService();
        ProductoService productoService = new ProductoService(productoRepo, codigoService);
        ClienteService clienteService = new ClienteService(clienteRepo);
        UsuarioService usuarioService = UsuarioService.getInstance();
        services.put(UsuarioService.class, usuarioService);
        services.put(AuthenticationService.class, new AuthenticationService(usuarioRepo));
        services.put(ProveedorService.class, new ProveedorService(proveedorRepo));
        services.put(ProductoService.class, productoService);
        services.put(CodigoGeneratorService.class, codigoService);
        services.put(ClienteService.class, clienteService);
        services.put(NotificationService.class, new NotificationService(productoService));
        logger.debug("Services registrados: {}", services.size());
    }

    // Devuelve un servicio por tipo (clase)
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

    // Devuelve un repositorio por tipo (clase)
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

    // Métodos de conveniencia para obtener servicios comunes
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

    // Métodos de conveniencia para obtener repositorios comunes
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

    // Muestra en el log todos los servicios y repositorios registrados
    public void logRegisteredServices() {
        logger.info("=== SERVICIOS REGISTRADOS ===");
        logger.info("Services ({}):", services.size());
        services.keySet().forEach(clazz -> logger.info("  - {}", clazz.getSimpleName()));
        logger.info("Repositories ({}):", repositories.size());
        repositories.keySet().forEach(clazz -> logger.info("  - {}", clazz.getSimpleName()));
        logger.info("==============================");
    }

    // Reinicializa el contenedor (útil para pruebas o recarga)
    public void reinitialize() {
        logger.info("Reinicializando contenedor de dependencias...");
        services.clear();
        repositories.clear();
        initializeContainer();
        logger.info("Contenedor reinicializado exitosamente");
    }

    // Devuelve información resumida del contenedor
    public String getContainerInfo() {
        return String.format("ServiceContainer - Services: %d, Repositories: %d",
                services.size(), repositories.size());
    }
}