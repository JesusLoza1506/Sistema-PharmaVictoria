package com.farmaciavictoria.proyectopharmavictoria.events;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ✅ OBSERVER PATTERN - Administrador de eventos del sistema
 * Implementa el patrón Observer para notificaciones automáticas
 * Gestiona suscripciones y notificaciones de forma thread-safe
 */
public class SystemEventManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemEventManager.class);
    
    private static SystemEventManager instance;
    private final List<SystemEventObserver> observers = new CopyOnWriteArrayList<>();
    private final ExecutorService eventExecutor = Executors.newFixedThreadPool(3);
    
    private SystemEventManager() {
        logger.info("SystemEventManager inicializado");
    }
    
    /**
     * ✅ SINGLETON PATTERN: Obtener instancia única
     */
    public static SystemEventManager getInstance() {
        if (instance == null) {
            synchronized (SystemEventManager.class) {
                if (instance == null) {
                    instance = new SystemEventManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * ✅ OBSERVER PATTERN: Suscribir observador
     */
    public void subscribe(SystemEventObserver observer) {
        if (observer == null) {
            logger.warn("Intento de suscribir observador nulo");
            return;
        }
        
        // Evitar duplicados
        if (observers.stream().anyMatch(o -> o.getObserverId().equals(observer.getObserverId()))) {
            logger.debug("Observador ya existe: {}", observer.getObserverId());
            return;
        }
        
        observers.add(observer);
        logger.info("Observador suscrito: {} (Total: {})", 
                   observer.getObserverId(), observers.size());
    }
    
    /**
     * ✅ OBSERVER PATTERN: Desuscribir observador
     */
    public void unsubscribe(SystemEventObserver observer) {
        if (observer == null) return;
        
        boolean removed = observers.removeIf(o -> o.getObserverId().equals(observer.getObserverId()));
        if (removed) {
            logger.info("Observador desuscrito: {} (Total: {})", 
                       observer.getObserverId(), observers.size());
        }
    }
    
    /**
     * ✅ OBSERVER PATTERN: Desuscribir por ID
     */
    public void unsubscribe(String observerId) {
        boolean removed = observers.removeIf(o -> o.getObserverId().equals(observerId));
        if (removed) {
            logger.info("Observador desuscrito por ID: {} (Total: {})", 
                       observerId, observers.size());
        }
    }
    
    /**
     * ✅ OBSERVER PATTERN: Publicar evento
     * Notifica a todos los observadores de forma asíncrona
     */
    public void publishEvent(SystemEvent event) {
        if (event == null) {
            logger.warn("Intento de publicar evento nulo");
            return;
        }
        
        logger.debug("Publicando evento: {} a {} observadores", 
                    event.getTipo(), observers.size());
        
        // Notificar observadores de forma asíncrona
        eventExecutor.submit(() -> notifyObservers(event));
    }
    
    /**
     * ✅ OBSERVER PATTERN: Publicar evento simple
     */
    public void publishEvent(SystemEvent.EventType tipo, String mensaje, Object source) {
        publishEvent(new SystemEvent(tipo, mensaje, source));
    }
    
    /**
     * ✅ OBSERVER PATTERN: Notificar observadores
     * Ejecuta notificaciones en el hilo de JavaFX UI para actualizaciones seguras
     */
    private void notifyObservers(SystemEvent event) {
        for (SystemEventObserver observer : observers) {
            try {
                // Ejecutar en hilo UI de JavaFX para actualizaciones de interfaz
                Platform.runLater(() -> {
                    try {
                        observer.onEvent(event);
                    } catch (Exception e) {
                        logger.error("Error en observador {}: {}", 
                                   observer.getObserverId(), e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                logger.error("Error al notificar observador {}: {}", 
                           observer.getObserverId(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * ✅ OBSERVER PATTERN: Obtener número de observadores
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * ✅ OBSERVER PATTERN: Limpiar todos los observadores
     */
    public void clearAllObservers() {
        int count = observers.size();
        observers.clear();
        logger.info("Todos los observadores eliminados: {}", count);
    }
    
    /**
     * ✅ OBSERVER PATTERN: Cerrar administrador de eventos
     */
    public void shutdown() {
        logger.info("Cerrando SystemEventManager...");
        
        clearAllObservers();
        eventExecutor.shutdown();
        
        logger.info("SystemEventManager cerrado");
    }
    
    /**
     * ✅ OBSERVER PATTERN: Información del administrador
     */
    public String getManagerInfo() {
        return String.format("SystemEventManager - Observadores: %d, Executor activo: %s", 
                           observers.size(), !eventExecutor.isShutdown());
    }
}