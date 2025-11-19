package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchByLaboratorioStrategyTest {
    @Test
    void buscarPorLaboratorioCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLaboratorio("ACME Labs");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setLaboratorio("Farmacia Central");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByLaboratorioStrategy strategy = new SearchByLaboratorioStrategy();
        List<Producto> resultado = strategy.buscar(productos, "acme");
        assertEquals(1, resultado.size());
        assertEquals("Paracetamol", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorLaboratorioNoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLaboratorio("ACME Labs");
        List<Producto> productos = Collections.singletonList(p1);
        SearchByLaboratorioStrategy strategy = new SearchByLaboratorioStrategy();
        List<Producto> resultado = strategy.buscar(productos, "XYZ");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarFiltroVacioONuloDevuelveTodos() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setLaboratorio("ACME Labs");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setLaboratorio("Farmacia Central");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByLaboratorioStrategy strategy = new SearchByLaboratorioStrategy();
        assertEquals(productos, strategy.buscar(productos, ""));
        assertEquals(productos, strategy.buscar(productos, null));
    }

    @Test
    void buscarProductosSinLaboratorio() {
        Producto p1 = new Producto();
        p1.setNombre("Sin laboratorio");
        p1.setLaboratorio(null);
        List<Producto> productos = Collections.singletonList(p1);
        SearchByLaboratorioStrategy strategy = new SearchByLaboratorioStrategy();
        List<Producto> resultado = strategy.buscar(productos, "acme");
        assertTrue(resultado.isEmpty());
    }
}
