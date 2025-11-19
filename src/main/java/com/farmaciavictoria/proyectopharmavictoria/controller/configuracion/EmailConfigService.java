// Servicio para gestionar la configuración de correo electrónico
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import java.io.*;

public class EmailConfigService {
    // Obtiene una propiedad del archivo config.properties en la raíz del proyecto
    private static String getProperty(String key) {
        try (InputStream input = new FileInputStream("config.properties")) {
            java.util.Properties prop = new java.util.Properties();
            prop.load(input);
            return prop.getProperty(key);
        } catch (Exception e) {
            // Si no existe el archivo, retorna null
        }
        return null;
    }

    // Nombre del archivo donde se guarda la configuración serializada
    private static final String CONFIG_FILE = "email_config.ser";
    // Caché de la configuración cargada
    private static EmailConfig configCache;

    // Guarda la configuración de correo en disco y en caché
    public static void guardarConfig(EmailConfig config) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(config);
            configCache = config;
        }
    }

    // Carga la configuración de correo desde disco o crea una nueva si no existe
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
        // Carga usuario y contraseña SMTP desde config.properties si existen
        String smtpUser = getProperty("smtp.user");
        String smtpPassword = getProperty("smtp.password");
        if (smtpUser != null && !smtpUser.isEmpty())
            config.setSmtpUser(smtpUser);
        if (smtpPassword != null && !smtpPassword.isEmpty())
            config.setSmtpPassword(smtpPassword);
        configCache = config;
        return config;
    }
}
