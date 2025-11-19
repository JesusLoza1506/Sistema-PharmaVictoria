package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchByVencimientoStrategyTest {
    @Test
    void buscarPorFechaVencimientoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setFechaVencimiento(LocalDate.of(2025, 11, 16));
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByVencimientoStrategy strategy = new SearchByVencimientoStrategy();
        List<Producto> resultado = strategy.buscar(productos, "16/11/2025");
        assertEquals(1, resultado.size());
        assertEquals("Ibuprofeno", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorFechaVencimientoNoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        List<Producto> productos = Collections.singletonList(p1);
        SearchByVencimientoStrategy strategy = new SearchByVencimientoStrategy();
        List<Producto> resultado = strategy.buscar(productos, "01/01/2026");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarFiltroVacioONuloDevuelveTodos() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setFechaVencimiento(LocalDate.of(2025, 11, 16));
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByVencimientoStrategy strategy = new SearchByVencimientoStrategy();
        assertEquals(productos, strategy.buscar(productos, ""));
        assertEquals(productos, strategy.buscar(productos, null));
    }

    @Test
    void buscarProductosSinFechaVencimiento() {
        Producto p1 = new Producto();
        p1.setNombre("Sin fecha");
        p1.setFechaVencimiento(null);
        List<Producto> productos = Collections.singletonList(p1);
        SearchByVencimientoStrategy strategy = new SearchByVencimientoStrategy();
        List<Producto> resultado = strategy.buscar(productos, "16/11/2025");
        assertTrue(resultado.isEmpty());
    }
}
