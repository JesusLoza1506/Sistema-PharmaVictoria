package com.farmaciavictoria.proyectopharmavictoria.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

// Configuración y gestión de la base de datos (Singleton)
public class DatabaseConfig {

    // Logger para registrar eventos y errores
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    // Instancia única
    private static DatabaseConfig instance;
    // Pool de conexiones
    private static HikariDataSource dataSource;
    // Propiedades de configuración
    private static Properties properties;

    // Constructor privado: carga configuración y pool
    private DatabaseConfig() {
        loadProperties();
        initializeDataSource();
    }

    // Devuelve la instancia única
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    // Carga las propiedades desde el archivo application.properties
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar application.properties");
            }
            properties.load(input);
            logger.info("Propiedades de configuración cargadas correctamente");
        } catch (IOException e) {
            logger.error("Error al cargar propiedades de configuración", e);
            throw new RuntimeException("Error al cargar configuración", e);
        }
    }

    // Inicializa el pool de conexiones HikariCP
    private void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maximum-pool-size", "3")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minimum-idle", "1")));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connection-timeout", "30000")));
            config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idle-timeout", "600000")));
            config.setMaxLifetime(Long.parseLong(properties.getProperty("db.pool.max-lifetime", "1800000")));
            config.setConnectionTestQuery(properties.getProperty("db.pool.connection-test-query", "SELECT 1"));
            config.setPoolName("PharmavictoriaPool");
            config.setRegisterMbeans(true);
            config.setLeakDetectionThreshold(60000);
            dataSource = new HikariDataSource(config);
            logger.info("Conexión a base de datos inicializada correctamente");
            logger.info("Pool configurado - Max: {}, Min: {}",
                    config.getMaximumPoolSize(), config.getMinimumIdle());
        } catch (Exception e) {
            logger.error("Error al inicializar conexión a base de datos", e);
            throw new RuntimeException("Error de conexión a BD", e);
        }
    }

    // Obtiene una conexión del pool
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource no inicializado");
        }
        return dataSource.getConnection();
    }

    // Devuelve el DataSource para uso externo
    public DataSource getDataSource() {
        return dataSource;
    }

    // Prueba la conexión a la base de datos
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection.isValid(5);
            logger.info("Test de conexión: {}", isValid ? "EXITOSO" : "FALLIDO");
            return isValid;
        } catch (SQLException e) {
            logger.error("Error en test de conexión", e);
            return false;
        }
    }

    // Devuelve información del estado del pool
    public String getPoolStatus() {
        if (dataSource != null) {
            return String.format(
                    "Pool Status - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections(),
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
        return "Pool no inicializado";
    }

    // Obtiene una propiedad de configuración
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Obtiene una propiedad con valor por defecto
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Cierra el pool de conexiones
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado");
        }
    }
}
