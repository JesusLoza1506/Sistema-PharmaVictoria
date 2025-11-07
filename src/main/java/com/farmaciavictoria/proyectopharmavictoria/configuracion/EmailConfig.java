package com.farmaciavictoria.proyectopharmavictoria.configuracion;

import java.io.Serializable;

public class EmailConfig implements Serializable {

    public void setAlertasActivas(boolean alertasActivas) {
        this.alertasActivas = alertasActivas;
    }

    private String smtpServer;
    private int smtpPort;
    private String smtpUser;
    private String smtpPassword;
    private boolean useSSL;
    private boolean useTLS;
    private String destinatario;
    private boolean alertasActivas;

    public EmailConfig() {
    }

    // Getters y setters
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

    // El remitente solo se puede modificar por el administrador o desde el archivo
    // de propiedades
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

    public boolean isAlertasActivas() {
        return alertasActivas;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
