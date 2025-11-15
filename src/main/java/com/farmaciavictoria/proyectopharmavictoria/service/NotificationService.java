package com.farmaciavictoria.proyectopharmavictoria.service;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;

import java.util.List;

/**
 * ✅ SERVICE LAYER PATTERN: Servicio centralizado para gestionar notificaciones
 * del sistema
 * ✅ DEPENDENCY INJECTION: Integrado con ServiceContainer
 * ✅ OBSERVER PATTERN: Responde a eventos del sistema
 * 
 * OPTIMIZACIÓN: Solo notificaciones discretas arriba derecha - SIN POPUPS
 * INVASIVOS
 * 
 * @author PHARMAVICTORIA
 * @version 2.0 - Sistema Optimizado
 */
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // ✅ DEPENDENCY INJECTION: Inyección de dependencias
    private final ProductoService productoService;

    /**
     * ✅ DEPENDENCY INJECTION: Constructor con inyección de dependencias
     */
    public NotificationService(ProductoService productoService) {
        this.productoService = productoService;
        logger.info("NotificationService inicializado con enterprise patterns");
    }

    /**
     * ✅ SERVICE LAYER: Mostrar notificaciones DISCRETAS de inventario
     * Solo se ejecuta cuando se accede al módulo de inventario
     * OPTIMIZACIÓN: Solo arriba derecha - NO popups modales invasivos
     */
    public void mostrarNotificacionesInventario() {
        try {
            Platform.runLater(() -> {
                logger.debug("Generando notificaciones discretas de inventario...");

                // ✅ SOLO notificación discreta arriba derecha
                mostrarNotificacionStockBajo();

                logger.info("Notificaciones discretas de inventario generadas exitosamente");
            });

        } catch (Exception e) {
            logger.error("Error al mostrar notificaciones de inventario: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Notificación DISCRETA para stock bajo (SOLO arriba derecha)
     * OPTIMIZACIÓN: Eliminado popup modal invasivo + MEJOR DISEÑO VISUAL
     */
    private void mostrarNotificacionStockBajo() {
        try {
            List<Producto> productosStockBajo = productoService.obtenerProductosStockBajo();

            if (!productosStockBajo.isEmpty()) {
                String detalles = String.format("Se encontraron %d producto(s) con stock crítico",
                        productosStockBajo.size());

                if (productosStockBajo.size() <= 3) {
                    StringBuilder productos = new StringBuilder("\n\nProductos afectados:\n");
                    for (Producto p : productosStockBajo) {
                        productos.append(String.format("• %s (Stock: %d)\n",
                                p.getNombre(), p.getStockActual()));
                    }
                    detalles += productos.toString();
                }

                Notifications.create()
                        .title("ALERTA: Stock Bajo")
                        .text(detalles)
                        .position(Pos.TOP_RIGHT)
                        .hideAfter(Duration.seconds(12))
                        .darkStyle() // ✅ MEJOR DISEÑO: Estilo oscuro más visible
                        .showWarning();

                logger.info("Notificación mejorada de stock bajo mostrada: {} productos", productosStockBajo.size());
            }

        } catch (Exception e) {
            logger.error("Error al mostrar notificación de stock bajo: {}", e.getMessage(), e);
        }
    }

    /**
     * ✅ SERVICE LAYER: Notificación DISCRETA de éxito - MEJORADA
     */
    public void mostrarExito(String mensaje) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title("✅ Operación Exitosa")
                    .text(mensaje)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .darkStyle()
                    .showConfirm();
        });
    }

    /**
     * ✅ SERVICE LAYER: Notificación DISCRETA de advertencia - MEJORADA
     */
    public void mostrarAdvertencia(String mensaje) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title("⚠️ Atención")
                    .text(mensaje)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(7))
                    .darkStyle()
                    .showWarning();
        });
    }

    /**
     * ✅ SERVICE LAYER: Notificación DISCRETA de error - MEJORADA
     */
    public void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title("❌ Error")
                    .text(mensaje)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(10))
                    .darkStyle()
                    .showError();
        });
    }

    /**
     * ✅ SERVICE LAYER: Notificación DISCRETA de información - MEJORADA
     */
    public void mostrarInfo(String mensaje) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title("ℹ️ Información")
                    .text(mensaje)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(6))
                    .darkStyle()
                    .showInformation();
        });
    }
}