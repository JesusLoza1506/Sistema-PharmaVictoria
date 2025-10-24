package com.farmaciavictoria.proyectopharmavictoria.tools;

import com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService;

public class TestHistorial {
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando prueba de historial...");
            // Asegurar que las tablas existen
            com.farmaciavictoria.proyectopharmavictoria.util.DatabaseUpdater.ensureUsuarioHistorialTables();
            UsuarioService us = UsuarioService.getInstance();
            Long testUserId = 1L; // Ajusta según tu BD
            us.registrarAcceso(testUserId, true, "127.0.0.1", "test-agent", null);
            System.out.println("Acceso registrado. Leyendo historiales...");
            var accesos = us.obtenerHistorialAccesos(testUserId);
            System.out.println("Accesos recuperados: " + (accesos != null ? accesos.size() : 0));
            accesos.forEach(a -> System.out.println(a.getFechaAcceso() + " - " + a.getIpAddress() + " - " + a.getUserAgent()));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fallo en prueba de historial: " + e.getMessage());
        }
    }
}
