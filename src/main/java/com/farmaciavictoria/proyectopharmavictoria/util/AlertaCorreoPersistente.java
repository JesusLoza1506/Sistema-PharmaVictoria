package com.farmaciavictoria.proyectopharmavictoria.util;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Persistencia de productos notificados por correo entre sesiones.
 */
public class AlertaCorreoPersistente {
    /**
     * Elimina la marca de notificado de vencimiento para un producto específico
     */
    public static void eliminarNotificadoVencimiento(String producto) {
        productosVencimiento.remove(producto);
        guardar(VENCIMIENTO_FILE, productosVencimiento);
    }

    private static final String STOCK_FILE = "notificados_stock.ser";
    private static final String VENCIMIENTO_FILE = "notificados_vencimiento.ser";

    private static Set<String> productosStock = cargar(STOCK_FILE);
    private static Set<String> productosVencimiento = cargar(VENCIMIENTO_FILE);

    public static boolean yaNotificadoStock(String producto) {
        return productosStock.contains(producto);
    }

    public static void marcarNotificadoStock(String producto) {
        productosStock.add(producto);
        guardar(STOCK_FILE, productosStock);
    }

    public static boolean yaNotificadoVencimiento(String producto) {
        return productosVencimiento.contains(producto);
    }

    public static void marcarNotificadoVencimiento(String producto) {
        productosVencimiento.add(producto);
        guardar(VENCIMIENTO_FILE, productosVencimiento);
    }

    public static void limpiar() {
        productosStock.clear();
        productosVencimiento.clear();
        guardar(STOCK_FILE, productosStock);
        guardar(VENCIMIENTO_FILE, productosVencimiento);
    }

    private static Set<String> cargar(String file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Set<String>) ois.readObject();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    private static void guardar(String file, Set<String> set) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(set);
        } catch (Exception e) {
            // Silenciar error de guardado
        }
    }
}
