package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FiltroProductosTest {
    @Test
    void combinaLaBusquedaConElEstadoYLaVisibilidadDeInactivos() {
        Producto activo = producto("Paracetamol", true, 2, 2);
        Producto inactivo = producto("Paracetamol 500", false, 1, 2);
        Producto otro = producto("Ibuprofeno", true, 1, 2);
        List<Producto> productos = List.of(activo, inactivo, otro);

        assertEquals(List.of(activo),
                FiltroProductos.filtrar(productos, "para", null, "Stock Bajo", false, "Nombre", 30));
        assertEquals(List.of(inactivo),
                FiltroProductos.filtrar(productos, "para", null, "Inactivo", true, "Nombre", 30));
    }

    @Test
    void incluyeElLimiteDeAlertaYExcluyeLosProductosQueVencenHoy() {
        LocalDate hoy = LocalDate.now();
        Producto venceHoy = producto("Hoy", true, 4, 2);
        venceHoy.setFechaVencimiento(hoy);
        Producto limite = producto("Límite", true, 4, 2);
        limite.setFechaVencimiento(hoy.plusDays(30));
        Producto fuera = producto("Fuera de alerta", true, 4, 2);
        fuera.setFechaVencimiento(hoy.plusDays(31));

        assertEquals(List.of(limite), FiltroProductos.filtrar(List.of(venceHoy, limite, fuera), "",
                null, "Próximos a Vencer", false, "Nombre", 30));
    }

    private Producto producto(String nombre, boolean activo, int stock, int minimo) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setActivo(activo);
        producto.setStockActual(stock);
        producto.setStockMinimo(minimo);
        return producto;
    }
}
