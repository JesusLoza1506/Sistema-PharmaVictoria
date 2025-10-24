package com.farmaciavictoria.proyectopharmavictoria.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class IconLoader {
    
    private static final String ICONS_PATH = "/icons/";
    
    public static ImageView loadIcon(String iconName, double size) {
        try {
            // Intentar cargar el archivo PNG primero
            String pngPath = ICONS_PATH + iconName + ".png";
            InputStream pngStream = IconLoader.class.getResourceAsStream(pngPath);
            
            if (pngStream != null) {
                Image image = new Image(pngStream, size, size, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                return imageView;
            }
            
            // Si no hay PNG, intentar SVG (aunque JavaFX no soporta SVG nativamente)
            String svgPath = ICONS_PATH + iconName + ".svg";
            InputStream svgStream = IconLoader.class.getResourceAsStream(svgPath);
            
            if (svgStream != null) {
                // Para SVG, podríamos usar una librería externa, pero por ahora usamos fallback
                return createFallbackIcon(iconName, size);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + iconName + " - " + e.getMessage());
        }
        
        return createFallbackIcon(iconName, size);
    }
    
    private static ImageView createFallbackIcon(String iconName, double size) {
        // Crear un icono de texto como fallback
        ImageView fallback = new ImageView();
        fallback.setFitWidth(size);
        fallback.setFitHeight(size);
        return fallback;
    }
    
    public static ImageView getEditIcon() {
        return loadIcon("edit", 20);
    }

    public static ImageView getViewIcon() {
        return loadIcon("view", 20);
    }

    public static ImageView getDeleteIcon() {
        return loadIcon("delete", 20);
    }

    public static ImageView getToggleIcon() {
        return loadIcon("toggle", 20);
    }
}