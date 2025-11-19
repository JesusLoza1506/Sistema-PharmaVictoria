package com.farmaciavictoria.proyectopharmavictoria.events;

import java.time.LocalDateTime;
import java.util.Map;

public class SystemEvent {

    public enum EventType {
        // Eventos de proveedores
        PROVEEDOR_CREADO,
        PROVEEDOR_ACTUALIZADO,
        PROVEEDOR_ELIMINADO,

        // Eventos de productos
        PRODUCTO_CREADO,
        PRODUCTO_ACTUALIZADO,
        PRODUCTO_ELIMINADO,
        STOCK_BAJO,
        PRODUCTO_VENCIMIENTO_PROXIMO,

        // Eventos de clientes
        CLIENTE_CREADO,
        CLIENTE_ACTUALIZADO,
        CLIENTE_ELIMINADO,
        CLIENTE_PUNTOS_AGREGADOS,
        CLIENTE_PUNTOS_REDIMIDOS,

        // EVENTOS DE VENTA AGREGADOS
        VENTA_REGISTRADA,
        VENTA_ANULADA,

        // Eventos de sistema
        DASHBOARD_REFRESH_NEEDED,
        DATABASE_CONNECTION_CHANGED,

        // Eventos de usuario
        USUARIO_LOGIN,
        USUARIO_LOGOUT
    }

    private final EventType tipo;
    private final String mensaje;
    private final Object source;
    private final Map<String, Object> data;
    private final LocalDateTime timestamp;

    public SystemEvent(EventType tipo, String mensaje, Object source) {
        this(tipo, mensaje, source, null);
    }

    public SystemEvent(EventType tipo, String mensaje, Object source, Map<String, Object> data) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.source = source;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public EventType getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Object getSource() {
        return source;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("SystemEvent{tipo=%s, mensaje='%s', timestamp=%s}",
                tipo, mensaje, timestamp);
    }
}