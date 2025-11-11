// Clase que representa la configuración de correo electrónico para notificaciones
package com.farmaciavictoria.proyectopharmavictoria.controller.configuracion;

import java.io.Serializable;

public class EmailConfig implements Serializable {
    // Servidor SMTP para el envío de correos
    private String smtpServer;
    // Puerto SMTP
    private int smtpPort;
    // Usuario remitente
    private String smtpUser;
    // Contraseña del remitente
    private String smtpPassword;
    // Indica si se usa SSL
    private boolean useSSL;
    // Indica si se usa TLS
    private boolean useTLS;
    // Destinatario de las notificaciones
    private String destinatario;
    // Indica si las alertas están activas
    private boolean alertasActivas;

    // Constructor por defecto
    public EmailConfig() {
    }

    // Métodos getter y setter para cada campo
    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public boolean isAlertasActivas() {
        return alertasActivas;
    }

    public void setAlertasActivas(boolean alertasActivas) {
        this.alertasActivas = alertasActivas;
    }
}
