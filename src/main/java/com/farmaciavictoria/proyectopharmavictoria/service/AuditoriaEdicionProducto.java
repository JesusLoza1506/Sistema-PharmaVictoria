package com.farmaciavictoria.proyectopharmavictoria.service;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoHistorialCambioRepository;

import java.util.Objects;

public final class AuditoriaEdicionProducto {
    private AuditoriaEdicionProducto() {
    }

    public static void registrar(Producto anterior, Producto nuevo, int stockAnterior, int stockNuevo) {
        String usuarioNombreCompleto = "Desconocido";
        try {
            Usuario usuarioActual = AuthenticationService
                    .getUsuarioActual();
            if (usuarioActual != null) {
                usuarioNombreCompleto = usuarioActual.getNombreCompleto();
            }
        } catch (Exception ex) {
        }

        registrarCambio(anterior, "nombre", anterior.getNombre(), nuevo.getNombre(), usuarioNombreCompleto);
        registrarCambio(anterior, "principio_activo", anterior.getPrincipioActivo(), nuevo.getPrincipioActivo(), usuarioNombreCompleto);
        registrarCambio(anterior, "concentracion", anterior.getConcentracion(), nuevo.getConcentracion(), usuarioNombreCompleto);
        registrarCambio(anterior, "laboratorio", anterior.getLaboratorio(), nuevo.getLaboratorio(), usuarioNombreCompleto);
        registrarCambio(anterior, "categoria", anterior.getCategoria(), nuevo.getCategoria(), usuarioNombreCompleto);
        registrarCambio(anterior, "proveedor_id", anterior.getProveedorId(), nuevo.getProveedorId(), usuarioNombreCompleto);
        registrarCambio(anterior, "descripcion", anterior.getDescripcion(), nuevo.getDescripcion(), usuarioNombreCompleto);
        registrarCambio(anterior, "precio_compra", anterior.getPrecioCompra(), nuevo.getPrecioCompra(), usuarioNombreCompleto);
        registrarCambio(anterior, "precio_venta", anterior.getPrecioVenta(), nuevo.getPrecioVenta(), usuarioNombreCompleto);
        registrarCambio(anterior, "fecha_fabricacion", anterior.getFechaFabricacion(), nuevo.getFechaFabricacion(), usuarioNombreCompleto);
        registrarCambio(anterior, "fecha_vencimiento", anterior.getFechaVencimiento(), nuevo.getFechaVencimiento(), usuarioNombreCompleto);
        registrarCambio(anterior, "ubicacion", anterior.getUbicacion(), nuevo.getUbicacion(), usuarioNombreCompleto);
        registrarCambio(anterior, "lote", anterior.getLote(), nuevo.getLote(), usuarioNombreCompleto);
        if (stockAnterior != -1 && stockAnterior != stockNuevo) {
            registrarCambio(anterior, "stock_actual", stockAnterior, stockNuevo, usuarioNombreCompleto);
        }
        registrarCambio(anterior, "requiere_receta", anterior.getRequiereReceta(), nuevo.getRequiereReceta(), usuarioNombreCompleto);
        registrarCambio(anterior, "es_controlado", anterior.getEsControlado(), nuevo.getEsControlado(), usuarioNombreCompleto);
        registrarCambio(anterior, "activo", anterior.getActivo(), nuevo.getActivo(), usuarioNombreCompleto);
    }

    private static void registrarCambio(Producto producto, String campo, Object anterior, Object nuevo, String usuario) {
        if (!Objects.equals(anterior, nuevo)) {
            ProductoHistorialCambioRepository.registrarCambio(producto.getId(), campo,
                    String.valueOf(anterior), String.valueOf(nuevo), usuario);
        }
    }
}
