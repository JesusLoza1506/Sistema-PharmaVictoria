package com.farmaciavictoria.proyectopharmavictoria.configuracion;

import java.io.*;

public class EmailConfigService {
    private static String getProperty(String key) {
        try (InputStream input = EmailConfigService.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            java.util.Properties prop = new java.util.Properties();
            if (input != null) {
                prop.load(input);
                return prop.getProperty(key);
            }
        } catch (Exception e) {
            // Ignorar, usar valor por defecto
        }
        return null;
    }

    private static final String CONFIG_FILE = "email_config.ser";
    private static EmailConfig configCache;

    public static void guardarConfig(EmailConfig config) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(config);
            configCache = config;
        }
    }

    public static EmailConfig cargarConfig() throws IOException, ClassNotFoundException {
        if (configCache != null)
            return configCache;
        File file = new File(CONFIG_FILE);
        EmailConfig config;
        if (!file.exists()) {
            config = new EmailConfig();
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                config = (EmailConfig) ois.readObject();
            }
        }
        // Forzar remitente y contraseña desde application.properties
        String smtpUser = getProperty("mail.smtp.user");
        String smtpPassword = getProperty("mail.smtp.password");
        if (smtpUser != null && !smtpUser.isEmpty())
            config.setSmtpUser(smtpUser);
        if (smtpPassword != null && !smtpPassword.isEmpty())
            config.setSmtpPassword(smtpPassword);
        configCache = config;
        return config;
    }
}
