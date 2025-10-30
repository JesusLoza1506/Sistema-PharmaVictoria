package com.farmaciavictoria.proyectopharmavictoria;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.util.DatabaseUpdater;
import javafx.application.Application;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;

public class PharmavictoriaApplication extends Application {

    private static final String APP_TITLE = "PharmaVictoria";
    private static final String APP_VERSION = "v1.0";
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            configurePrimaryStage(primaryStage);
            if (!verifyDatabaseConnection()) {
                showDatabaseErrorAndExit();
                return;
            }
            DatabaseUpdater.updateProductsTable();
            DatabaseUpdater.updateProveedoresTable();
            DatabaseUpdater.ensureUsuarioHistorialTables();
            Parent root = loadLoginScreenWithFade(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo iniciar la aplicación:");
            e.printStackTrace();
            showErrorAndExit("Error Fatal", "No se pudo iniciar la aplicación:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void configurePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        try {
            primaryStage.getIcons().add(new Image(
                    PharmavictoriaApplication.class.getResourceAsStream("/icons/logofarmacia.png")));
        } catch (Exception e) {
            // Silenciar error de icono
        }
        primaryStage.setOnCloseRequest(event -> {
            closeApplication();
        });
    }

    private boolean verifyDatabaseConnection() {
        try {
            DatabaseConfig.getInstance().getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Parent loadLoginScreenWithFade(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 700);
        String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(750), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        return root;
    }

    private void showDatabaseErrorAndExit() {
        Platform.runLater(() -> {
            showErrorAndExit(
                    "Error de Conexión",
                    "No se pudo conectar a la base de datos.\n\n" +
                            "Verifique que:\n" +
                            "• El servidor MySQL esté ejecutándose\n" +
                            "• La base de datos 'pharmavictoria' exista\n" +
                            "• Las credenciales sean correctas\n" +
                            "• El puerto 3306 esté disponible");
        });
    }

    private void showErrorAndExit(String title, String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("PHARMAVICTORIA - Error Fatal");
            alert.setContentText(message);
            alert.showAndWait();
        } finally {
            closeApplication();
        }
    }

    private void closeApplication() {
        try {
            DatabaseConfig.getInstance().close();
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void stop() throws Exception {
        closeApplication();
        super.stop();
    }
}