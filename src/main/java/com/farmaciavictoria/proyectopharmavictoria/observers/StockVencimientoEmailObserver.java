package com.farmaciavictoria.proyectopharmavictoria.observers;

import com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent;
import com.farmaciavictoria.proyectopharmavictoria.events.SystemEventObserver;
import com.farmaciavictoria.proyectopharmavictoria.service.EmailService;
import com.farmaciavictoria.proyectopharmavictoria.util.AlertaCorreoPersistente;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockVencimientoEmailObserver implements SystemEventObserver {
    private static final Logger logger = LoggerFactory.getLogger(StockVencimientoEmailObserver.class);
    private final EmailService emailService;
    private final String destinatario;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public StockVencimientoEmailObserver(EmailService emailService, String destinatario) {
        this.emailService = emailService;
        this.destinatario = destinatario;
    }

    @Override
    public void onEvent(SystemEvent event) {
        String mensaje = event.getMensaje();
        String producto = extraerNombreProducto(mensaje);
        switch (event.getTipo()) {
            case STOCK_BAJO:
                if (!AlertaCorreoPersistente.yaNotificadoStock(producto)) {
                    AlertaCorreoPersistente.marcarNotificadoStock(producto);
                    executor.submit(() -> {
                        try {
                            emailService.sendEmail(destinatario, "Alerta: Stock Bajo", mensaje);
                            logger.info("Correo de alerta de stock bajo enviado a {}", destinatario);
                        } catch (MessagingException e) {
                            logger.error("Error enviando correo de alerta: {}", e.getMessage(), e);
                        }
                    });
                }
                break;
            case PRODUCTO_VENCIMIENTO_PROXIMO:
                if (!AlertaCorreoPersistente.yaNotificadoVencimiento(producto)) {
                    AlertaCorreoPersistente.marcarNotificadoVencimiento(producto);
                    executor.submit(() -> {
                        try {
                            emailService.sendEmail(destinatario, "Alerta: Producto Próximo a Vencer", mensaje);
                            logger.info("Correo de alerta de vencimiento enviado a {}", destinatario);
                        } catch (MessagingException e) {
                            logger.error("Error enviando correo de alerta: {}", e.getMessage(), e);
                        }
                    });
                }
                break;
            default:
                // No enviar correo para otros eventos
        }
    }

    /**
     * Extrae el nombre del producto del mensaje del evento.
     */
    private String extraerNombreProducto(String mensaje) {
        // Ejemplo: "Stock bajo en producto: Floratil2" o "Producto próximo a vencer:
        // Floratil2"
        if (mensaje == null)
            return "";
        int idx = mensaje.indexOf(":");
        if (idx >= 0 && idx + 1 < mensaje.length()) {
            return mensaje.substring(idx + 1).trim();
        }
        return mensaje;
    }

    @Override
    public String getObserverId() {
        return "StockVencimientoEmailObserver";
    }
}
