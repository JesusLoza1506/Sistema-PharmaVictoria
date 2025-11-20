package com.farmaciavictoria.proyectopharmavictoria.service;

import java.io.File;
import java.io.IOException;

public class RestoreService {
    private final String mysqlUser;
    private final String mysqlPassword;
    private final String databaseName;
    private final String mysqlHost;
    private final int mysqlPort;

    public RestoreService(String user, String password, String dbName, String host, int port) {
        this.mysqlUser = user;
        this.mysqlPassword = password;
        this.databaseName = dbName;
        this.mysqlHost = host;
        this.mysqlPort = port;
    }

    public boolean restaurarDesdeArchivo(String archivoRespaldo) {
        String command = String.format(
                "mysql -h%s -P%d -u%s -p%s %s < \"%s\"",
                mysqlHost, mysqlPort, mysqlUser, mysqlPassword, databaseName, archivoRespaldo);
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
