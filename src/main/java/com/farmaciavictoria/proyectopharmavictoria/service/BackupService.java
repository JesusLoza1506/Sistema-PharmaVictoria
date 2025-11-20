package com.farmaciavictoria.proyectopharmavictoria.service;

import java.io.File;
import java.io.IOException;

public class BackupService {
    private final String mysqlUser;
    private final String mysqlPassword;
    private final String databaseName;
    private final String mysqlHost;
    private final int mysqlPort;

    public BackupService(String user, String password, String dbName, String host, int port) {
        this.mysqlUser = user;
        this.mysqlPassword = password;
        this.databaseName = dbName;
        this.mysqlHost = host;
        this.mysqlPort = port;
    }

    public BackupResult realizarRespaldo(String rutaDestino, String hora) {
        com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig dbConfig = com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig
                .getInstance();
        String mysqldumpPath = dbConfig.getProperty("mysqldump.path", "mysqldump");
        String fileName = rutaDestino; // ruta completa del archivo
        File mysqldumpFile = new File(mysqldumpPath);
        if (!mysqldumpFile.exists()) {
            return new BackupResult(false, "No se encontró el ejecutable mysqldump en la ruta: " + mysqldumpPath);
        }
        if (mysqlUser == null || mysqlUser.isEmpty()) {
            return new BackupResult(false, "Usuario de MySQL no configurado.");
        }
        // Solo validar carpeta si el archivo tiene una carpeta (Local/Ambos)
        File carpeta = new File(fileName).getParentFile();
        if (carpeta != null && !carpeta.exists() && fileName.contains(File.separator)) {
            return new BackupResult(false, "La carpeta de destino no existe o no es válida: "
                    + carpeta.getAbsolutePath());
        }
        String comando;
        ProcessBuilder pb;
        if (mysqlPassword == null || mysqlPassword.isEmpty()) {
            comando = String.format("\"%s\" -h%s -P%d -u%s %s > \"%s\"", mysqldumpPath, mysqlHost, mysqlPort, mysqlUser,
                    databaseName, fileName);
            pb = new ProcessBuilder(mysqldumpPath, "-h" + mysqlHost, "-P" + mysqlPort, "-u" + mysqlUser, databaseName);
        } else {
            comando = String.format("\"%s\" -h%s -P%d -u%s -p%s %s > \"%s\"", mysqldumpPath, mysqlHost, mysqlPort,
                    mysqlUser, mysqlPassword, databaseName, fileName);
            pb = new ProcessBuilder(mysqldumpPath, "-h" + mysqlHost, "-P" + mysqlPort, "-u" + mysqlUser,
                    "-p" + mysqlPassword, databaseName);
        }
        System.out.println("[BackupService] Ejecutando comando: " + comando);
        pb.redirectOutput(new File(fileName));
        StringBuilder stdErr = new StringBuilder();
        StringBuilder stdOut = new StringBuilder();
        try {
            Process process = pb.start();
            java.io.InputStream is = process.getInputStream();
            try (java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A")) {
                while (s.hasNext()) {
                    stdOut.append(s.next());
                }
            }
            java.io.InputStream es = process.getErrorStream();
            try (java.util.Scanner se = new java.util.Scanner(es).useDelimiter("\\A")) {
                while (se.hasNext()) {
                    stdErr.append(se.next());
                }
            }
            int exitCode = process.waitFor();
            File backupFile = new File(fileName);
            boolean hasContent = backupFile.exists() && backupFile.length() > 0;
            if (exitCode == 0 && hasContent) {
                // Subir respaldo a Google Drive
                try {
                    com.farmaciavictoria.proyectopharmavictoria.backup.GoogleDriveBackupService driveService = new com.farmaciavictoria.proyectopharmavictoria.backup.GoogleDriveBackupService();
                    String fileId = driveService.uploadBackup(backupFile.toPath());
                    return new BackupResult(true, "Respaldo realizado correctamente. Subido a Google Drive (ID: "
                            + fileId + ").\nSTDOUT: " + stdOut.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new BackupResult(false,
                            "Respaldo local OK, pero error al subir a Google Drive: " + ex.getMessage());
                }
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("Error en mysqldump. Código: ").append(exitCode);
                if (!hasContent)
                    msg.append("\nEl archivo de respaldo está vacío.");
                if (stdErr.length() > 0)
                    msg.append("\nSTDERR: ").append(stdErr.toString());
                if (stdOut.length() > 0)
                    msg.append("\nSTDOUT: ").append(stdOut.toString());
                return new BackupResult(false, msg.toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new BackupResult(false, "Excepción: " + e.getMessage());
        }
    }

    public static class BackupResult {
        public final boolean success;
        public final String message;

        public BackupResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
