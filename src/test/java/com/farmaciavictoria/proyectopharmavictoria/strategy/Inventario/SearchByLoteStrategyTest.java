package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchByLoteStrategyTest {
    @Test
    void buscarPorLoteCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLote("L123");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setLote("L456");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByLoteStrategy strategy = new SearchByLoteStrategy();
        List<Producto> resultado = strategy.buscar(productos, "123");
        assertEquals(1, resultado.size());
        assertEquals("Paracetamol", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorLoteNoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLote("L123");
        List<Producto> productos = Collections.singletonList(p1);
        SearchByLoteStrategy strategy = new SearchByLoteStrategy();
        List<Producto> resultado = strategy.buscar(productos, "999");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarFiltroVacioONuloDevuelveTodos() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLote("L123");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setLote("L456");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByLoteStrategy strategy = new SearchByLoteStrategy();
        assertEquals(productos, strategy.buscar(productos, ""));
        assertEquals(productos, strategy.buscar(productos, null));
    }

    @Test
    void buscarProductosSinLote() {
        Producto p1 = new Producto();
        p1.setNombre("Sin lote");
        p1.setLote(null);
        List<Producto> productos = Collections.singletonList(p1);
        SearchByLoteStrategy strategy = new SearchByLoteStrategy();
        List<Producto> resultado = strategy.buscar(productos, "L123");
        assertTrue(resultado.isEmpty());
    }
}
