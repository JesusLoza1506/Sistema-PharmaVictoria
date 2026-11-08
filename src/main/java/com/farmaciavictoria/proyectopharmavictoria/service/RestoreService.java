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
        // Construir el comando para Windows usando cmd.exe /c
        String command = String.format(
                "mysql -h%s -P%d -u%s -p%s %s < \"%s\"",
                mysqlHost, mysqlPort, mysqlUser, mysqlPassword, databaseName, archivoRespaldo);
        String[] fullCommand = { "cmd.exe", "/c", command };
        try {
            Process process = Runtime.getRuntime().exec(fullCommand);
            // Capturar salida y errores
            java.io.BufferedReader stdOut = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            java.io.BufferedReader stdErr = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = stdOut.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            while ((line = stdErr.readLine()) != null) {
                error.append(line).append(System.lineSeparator());
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Restauración completada con éxito.");
                if (output.length() > 0)
                    System.out.println(output);
                return true;
            } else {
                System.err.println("Error al restaurar la base de datos:");
                System.err.println(error);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
