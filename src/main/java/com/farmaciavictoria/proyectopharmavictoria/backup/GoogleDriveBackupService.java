package com.farmaciavictoria.proyectopharmavictoria.backup;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveBackupService {
    private static final String APPLICATION_NAME = "PharmaVictoria Backup";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Path TOKENS_DIRECTORY = resolveTokensDirectory();
    private static final java.util.List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    // Ruta corregida dentro de resources/google
    private static final String CREDENTIALS_FILE_PATH = "/google/client_secret.json";

    private final Drive driveService;

    public GoogleDriveBackupService() throws GeneralSecurityException, IOException {
        this.driveService = buildDriveService();
    }

    private Drive buildDriveService() throws GeneralSecurityException, IOException {
        Credential credential = getCredentials();
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials() throws IOException, GeneralSecurityException {
        try (InputStream in = GoogleDriveBackupService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new IOException("Credenciales no encontradas: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    SCOPES)
                    .setDataStoreFactory(
                            new com.google.api.client.util.store.FileDataStoreFactory(TOKENS_DIRECTORY.toFile()))
                    .setAccessType("offline")
                    .build();

            // Reutiliza tokens si existen (no abre navegador).
            Credential stored = flow.loadCredential("user");
            if (stored != null && stored.getAccessToken() != null) {
                return stored;
            }

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
    }

    public String uploadBackup(Path backupFilePath) throws IOException {
        File meta = new File();
        meta.setName(backupFilePath.getFileName().toString());
        java.io.File localFile = backupFilePath.toFile();
        com.google.api.client.http.FileContent media = new com.google.api.client.http.FileContent(
                "application/octet-stream", localFile);
        File uploaded = driveService.files()
                .create(meta, media)
                .setFields("id")
                .execute();
        return uploaded.getId();
    }

    private static Path resolveTokensDirectory() {
        String appData = System.getenv("APPDATA");
        Path base = (appData != null && !appData.isBlank())
                ? Paths.get(appData, "PharmaVictoria", "tokens")
                : Paths.get(System.getProperty("user.home"), ".pharmavictoria", "tokens");
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            System.err.println("[GoogleDriveBackupService] No se pudo crear tokens: " + base + " - " + e.getMessage());
        }
        return base;
    }
}