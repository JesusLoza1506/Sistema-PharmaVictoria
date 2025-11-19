package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchByNameStrategyTest {
    @Test
    void buscarPorNombreCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByNameStrategy strategy = new SearchByNameStrategy();
        List<Producto> resultado = strategy.buscar(productos, "para");
        assertEquals(1, resultado.size());
        assertEquals("Paracetamol", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorNombreNoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        List<Producto> productos = Collections.singletonList(p1);
        SearchByNameStrategy strategy = new SearchByNameStrategy();
        List<Producto> resultado = strategy.buscar(productos, "aspirina");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarFiltroVacioONuloDevuelveTodos() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByNameStrategy strategy = new SearchByNameStrategy();
        assertEquals(productos, strategy.buscar(productos, ""));
        assertEquals(productos, strategy.buscar(productos, null));
    }

    @Test
    void buscarProductosSinNombre() {
        Producto p1 = new Producto();
        p1.setNombre(null);
        List<Producto> productos = Collections.singletonList(p1);
        SearchByNameStrategy strategy = new SearchByNameStrategy();
        List<Producto> resultado = strategy.buscar(productos, "para");
        assertTrue(resultado.isEmpty());
    }
}
