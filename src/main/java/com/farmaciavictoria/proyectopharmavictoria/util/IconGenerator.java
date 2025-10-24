package com.farmaciavictoria.proyectopharmavictoria.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IconGenerator {
    
    public static void generateIcons() {
        try {
            String iconsPath = "src/main/resources/icons/";
            
            // Generar icono de editar (lápiz)
            BufferedImage editIcon = createEditIcon();
            ImageIO.write(editIcon, "PNG", new File(iconsPath + "edit.png"));
            
            // Generar icono de ver (ojo)
            BufferedImage viewIcon = createViewIcon();
            ImageIO.write(viewIcon, "PNG", new File(iconsPath + "view.png"));
            
            // Generar icono de eliminar (papelera)
            BufferedImage deleteIcon = createDeleteIcon();
            ImageIO.write(deleteIcon, "PNG", new File(iconsPath + "delete.png"));
            
            System.out.println("Iconos generados exitosamente!");
            
        } catch (IOException e) {
            System.err.println("Error generando iconos: " + e.getMessage());
        }
    }
    
    private static BufferedImage createEditIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Color verde para editar
        g2d.setColor(new Color(76, 175, 80));
        
        // Dibujar lápiz
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Cuerpo del lápiz
        g2d.drawLine(6, 18, 18, 6);
        g2d.drawLine(4, 20, 16, 8);
        
        // Punta del lápiz
        g2d.fillPolygon(new int[]{17, 19, 20, 18}, new int[]{7, 5, 6, 8}, 4);
        
        // Líneas de edición
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(3, 21, 5, 19);
        g2d.drawLine(2, 22, 4, 20);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage createViewIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Color azul para ver
        g2d.setColor(new Color(33, 150, 243));
        
        // Dibujar ojo exterior
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawArc(4, 8, 16, 8, 0, 180);
        g2d.drawArc(4, 8, 16, 8, 180, 180);
        
        // Dibujar pupila
        g2d.fillOval(9, 9, 6, 6);
        
        // Dibujar reflejo
        g2d.setColor(Color.WHITE);
        g2d.fillOval(11, 11, 2, 2);
        
        g2d.dispose();
        return image;
    }
    
    private static BufferedImage createDeleteIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Color rojo para eliminar
        g2d.setColor(new Color(244, 67, 54));
        
        // Dibujar papelera
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Base de la papelera
        g2d.drawRect(7, 9, 10, 11);
        
        // Tapa de la papelera
        g2d.drawLine(5, 8, 19, 8);
        g2d.drawLine(9, 6, 15, 6);
        g2d.drawLine(9, 6, 9, 8);
        g2d.drawLine(15, 6, 15, 8);
        
        // Líneas verticales dentro
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(10, 11, 10, 17);
        g2d.drawLine(12, 11, 12, 17);
        g2d.drawLine(14, 11, 14, 17);
        
        g2d.dispose();
        return image;
    }
    
    public static void main(String[] args) {
        generateIcons();
    }
}