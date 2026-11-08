// 1) Valores por defecto embebidos en /default-email.properties
// 2) Archivo de usuario en %APPDATA%/PharmaVictoria/config.properties
// 3) Migración desde formato antiguo email_config.ser si existe
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailConfigService {

    private static final String DEFAULT_RESOURCE = "/default-email.properties";
    private static final Path APP_BASE = Paths.get(System.getenv("APPDATA"), "PharmaVictoria");
    private static final Path USER_CONFIG = APP_BASE.resolve("config.properties");
    // Archivo antiguo de serialización (migración)
    private static final Path OLD_SERIALIZED = Paths.get("email_config.ser");

    private static EmailConfig configCache;

    // Carga configuración fusionando defaults + usuario + migración antigua
    public static synchronized EmailConfig cargarConfig() throws IOException {
        if (configCache != null) {
            return configCache;
        }
        ensureAppBase();
        Properties defaults = loadDefaults();
        Properties userProps = loadUserProperties();

        // Migración desde email_config.ser si existe y usuario aún no tiene algunas
        // claves
        migrateFromSerializedIfNeeded(userProps);

        EmailConfig cfg = new EmailConfig();
        // smtp.server
        cfg.setSmtpServer(firstNonEmpty(userProps.getProperty("smtp.server"), defaults.getProperty("smtp.server")));
        // smtp.port
        cfg.setSmtpPort(
                parsePort(firstNonEmpty(userProps.getProperty("smtp.port"), defaults.getProperty("smtp.port"))));
        // smtp.user / password
        cfg.setSmtpUser(firstNonEmpty(userProps.getProperty("smtp.user"), defaults.getProperty("smtp.user")));
        cfg.setSmtpPassword(
                firstNonEmpty(userProps.getProperty("smtp.password"), defaults.getProperty("smtp.password")));
        // ssl / tls
        cfg.setUseSSL(parseBoolean(firstNonEmpty(userProps.getProperty("smtp.ssl"), defaults.getProperty("smtp.ssl"))));
        cfg.setUseTLS(parseBoolean(firstNonEmpty(userProps.getProperty("smtp.tls"), defaults.getProperty("smtp.tls"))));
        // destinatario
        cfg.setDestinatario(
                firstNonEmpty(userProps.getProperty("smtp.destinatario"), defaults.getProperty("smtp.destinatario")));
        // alertas activas (si no existe, false por defecto)
        cfg.setAlertasActivas(parseBoolean(firstNonEmpty(userProps.getProperty("alertas.activas"), "false")));

        configCache = cfg;
        return cfg;
    }

    // Guarda sólo en el archivo de usuario (no se serializa)
    public static synchronized void guardarConfig(EmailConfig cfg) throws IOException {
        ensureAppBase();
        Properties p = new Properties();
        putIfNotNull(p, "smtp.server", cfg.getSmtpServer());
        if (cfg.getSmtpPort() > 0) {
            p.setProperty("smtp.port", String.valueOf(cfg.getSmtpPort()));
        }
        putIfNotNull(p, "smtp.user", cfg.getSmtpUser());
        // Guardar contraseña sólo si existe (puede quedar vacía para seguridad)
        if (cfg.getSmtpPassword() != null && !cfg.getSmtpPassword().isEmpty()) {
            p.setProperty("smtp.password", cfg.getSmtpPassword());
        }
        p.setProperty("smtp.ssl", String.valueOf(cfg.isUseSSL()));
        p.setProperty("smtp.tls", String.valueOf(cfg.isUseTLS()));
        putIfNotNull(p, "smtp.destinatario", cfg.getDestinatario());
        p.setProperty("alertas.activas", String.valueOf(cfg.isAlertasActivas()));

        try (OutputStream out = Files.newOutputStream(USER_CONFIG)) {
            p.store(out, "Configuración de correo PharmaVictoria");
        }
        configCache = cfg;
    }

    private static void ensureAppBase() throws IOException {
        if (!Files.exists(APP_BASE)) {
            Files.createDirectories(APP_BASE);
        }
    }

    private static Properties loadDefaults() throws IOException {
        Properties p = new Properties();
        try (InputStream in = EmailConfigService.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in != null) {
                p.load(in);
            }
        }
        return p;
    }

    private static Properties loadUserProperties() throws IOException {
        Properties p = new Properties();
        if (Files.exists(USER_CONFIG)) {
            try (InputStream in = Files.newInputStream(USER_CONFIG)) {
                p.load(in);
            }
        }
        return p;
    }

    private static void migrateFromSerializedIfNeeded(Properties userProps) {
        if (!Files.exists(OLD_SERIALIZED)) {
            return;
        }
        // Sólo migrar si faltan claves críticas
        boolean needsMigration = isEmpty(userProps.getProperty("smtp.user"))
                || isEmpty(userProps.getProperty("smtp.server"));
        if (!needsMigration) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OLD_SERIALIZED.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof EmailConfig legacy) {
                if (!isEmpty(legacy.getSmtpUser()))
                    userProps.setProperty("smtp.user", legacy.getSmtpUser());
                if (!isEmpty(legacy.getSmtpPassword()))
                    userProps.setProperty("smtp.password", legacy.getSmtpPassword());
                if (!isEmpty(legacy.getSmtpServer()))
                    userProps.setProperty("smtp.server", legacy.getSmtpServer());
                if (legacy.getSmtpPort() > 0)
                    userProps.setProperty("smtp.port", String.valueOf(legacy.getSmtpPort()));
                userProps.setProperty("smtp.ssl", String.valueOf(legacy.isUseSSL()));
                userProps.setProperty("smtp.tls", String.valueOf(legacy.isUseTLS()));
                if (!isEmpty(legacy.getDestinatario()))
                    userProps.setProperty("smtp.destinatario", legacy.getDestinatario());
                userProps.setProperty("alertas.activas", String.valueOf(legacy.isAlertasActivas()));
            }
        } catch (Exception ignored) {
            // Silenciar cualquier problema de migración
        }
        // Opcionalmente eliminar el archivo antiguo para no volver a migrar
        try {
            Files.deleteIfExists(OLD_SERIALIZED);
        } catch (Exception ignored) {
        }
    }

    private static String firstNonEmpty(String a, String b) {
        if (a != null && !a.isEmpty())
            return a;
        if (b != null && !b.isEmpty())
            return b;
        return "";
    }

    private static boolean parseBoolean(String v) {
        return v != null && (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes") || v.equals("1"));
    }

    private static int parsePort(String v) {
        if (v == null || v.isEmpty())
            return 0;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private static void putIfNotNull(Properties p, String key, String value) {
        if (value != null && !value.isEmpty()) {
            p.setProperty(key, value);
        }
    }
}
