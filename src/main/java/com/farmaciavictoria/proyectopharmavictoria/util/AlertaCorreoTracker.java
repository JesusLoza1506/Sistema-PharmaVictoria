package com.farmaciavictoria.proyectopharmavictoria.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controla los productos ya notificados por correo en la sesión actual.
 */
public class AlertaCorreoTracker {
    private static final Set<String> productosNotificadosStock = ConcurrentHashMap.newKeySet();
    private static final Set<String> productosNotificadosVencimiento = ConcurrentHashMap.newKeySet();

    public static boolean yaNotificadoStock(String productoNombre) {
        return productosNotificadosStock.contains(productoNombre);
    }

    public static void marcarNotificadoStock(String productoNombre) {
        productosNotificadosStock.add(productoNombre);
    }

    public static boolean yaNotificadoVencimiento(String productoNombre) {
        return productosNotificadosVencimiento.contains(productoNombre);
    }

    public static void marcarNotificadoVencimiento(String productoNombre) {
        productosNotificadosVencimiento.add(productoNombre);
    }

    public static void limpiar() {
        productosNotificadosStock.clear();
        productosNotificadosVencimiento.clear();
    }
}
