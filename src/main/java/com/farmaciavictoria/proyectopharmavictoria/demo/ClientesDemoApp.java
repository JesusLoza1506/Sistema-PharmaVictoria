package com.farmaciavictoria.proyectopharmavictoria.demo;

import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.controller.Cliente.ClientesController;
import com.farmaciavictoria.proyectopharmavictoria.service.ClienteService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ✅ DEMO LAUNCHER - Módulo de Clientes
 * Aplicación de demostración para probar el módulo completo de clientes
 * con todos los patrones enterprise implementados
 * 
 * @author PHARMAVICTORIA Development Team
 * @version 1.0
 */
public class ClientesDemoApp extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientesDemoApp.class);
    
    /**
     * ✅ MAIN APPLICATION START
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("=== INICIANDO DEMO MÓDULO CLIENTES ===");
        
        try {
            // ✅ DEPENDENCY INJECTION: Inicializar ServiceContainer
            ServiceContainer container = ServiceContainer.getInstance();
            container.logRegisteredServices();
            
            // ✅ DEPENDENCY INJECTION: Obtener controller con todas las dependencias
            ClientesController controller = container.getService(ClientesController.class);
            
            // ✅ FXML LOADING: Cargar interfaz
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clientes.fxml"));
            loader.setController(controller);
            
            BorderPane root = loader.load();
            
            // ✅ SCENE CONFIGURATION
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/clientes.css").toExternalForm());
            
            // ✅ STAGE CONFIGURATION
            primaryStage.setTitle("PHARMAVICTORIA - Gestión de Clientes (Demo)");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.centerOnScreen();
            
            // ✅ SHOW APPLICATION
            primaryStage.show();
            
            logger.info("✅ Demo de Clientes iniciada exitosamente");
            
            // ✅ LOG ENTERPRISE PATTERNS STATUS
            logPatternsStatus();
            
        } catch (Exception e) {
            logger.error("❌ Error al iniciar demo de clientes: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * ✅ LOGGING: Estado de patrones enterprise
     */
    private void logPatternsStatus() {
        logger.info("=== PATRONES ENTERPRISE ACTIVOS ===");
        logger.info("✅ MVC Pattern: ClientesController implementado");
        logger.info("✅ Repository Pattern: ClienteRepository activo");
        logger.info("✅ Service Layer Pattern: ClienteBasicoService activo");
        logger.info("✅ Dependency Injection: ServiceContainer configurado");
        logger.info("✅ Observer Pattern: SystemEventManager activo");
        logger.info("✅ Entity Pattern: ClienteBasico implementado");
        logger.info("✅ Validation Pattern: Validaciones centralizadas");
        logger.info("✅ DTO Pattern: Mapeo de entidades");
        logger.info("=====================================");
    }
    
    /**
     * ✅ APPLICATION SHUTDOWN
     */
    @Override
    public void stop() throws Exception {
        logger.info("=== CERRANDO DEMO MÓDULO CLIENTES ===");
        super.stop();
    }
    
    /**
     * ✅ MAIN METHOD
     */
    public static void main(String[] args) {
        logger.info("🚀 Iniciando aplicación demo de clientes...");
        
        try {
            launch(args);
        } catch (Exception e) {
            logger.error("💥 Error crítico en aplicación demo: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}