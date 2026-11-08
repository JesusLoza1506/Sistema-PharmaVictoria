package com.farmaciavictoria.proyectopharmavictoria.util;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PoolConcurrencyTest {
    public static void main(String[] args) {
        int numThreads = 5;

        Runnable task = () -> {
            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    System.out.println(Thread.currentThread().getName() + " obtuvo resultado: " + rs.getInt(1));
                }
                Thread.sleep(60000); // Simula trabajo manteniendo la conexión abierta
            } catch (Exception e) {
                System.err.println(Thread.currentThread().getName() + " error: " + e.getMessage());
            }
        };

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(task, "UsuarioSimulado-" + (i + 1));
            threads[i].start();
        }

        // Espera a que todos los hilos terminen
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("Simulación terminada.");
    }
}
