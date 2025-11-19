package com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchByProveedorStrategyTest {
    @Test
    void buscarPorProveedorCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setProveedorNombre("Distribuidora Norte");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setProveedorNombre("Farmacia Central");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByProveedorStrategy strategy = new SearchByProveedorStrategy();
        List<Producto> resultado = strategy.buscar(productos, "norte");
        assertEquals(1, resultado.size());
        assertEquals("Paracetamol", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorProveedorNoCoincidente() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setProveedorNombre("Distribuidora Norte");
        List<Producto> productos = Collections.singletonList(p1);
        SearchByProveedorStrategy strategy = new SearchByProveedorStrategy();
        List<Producto> resultado = strategy.buscar(productos, "XYZ");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarFiltroVacioONuloDevuelveTodos() {
        Producto p1 = new Producto();
        p1.setNombre("Paracetamol");
        p1.setProveedorNombre("Distribuidora Norte");
        Producto p2 = new Producto();
        p2.setNombre("Ibuprofeno");
        p2.setProveedorNombre("Farmacia Central");
        List<Producto> productos = Arrays.asList(p1, p2);
        SearchByProveedorStrategy strategy = new SearchByProveedorStrategy();
        assertEquals(productos, strategy.buscar(productos, ""));
        assertEquals(productos, strategy.buscar(productos, null));
    }

    @Test
    void buscarProductosSinProveedor() {
        Producto p1 = new Producto();
        p1.setNombre("Sin proveedor");
        p1.setProveedorNombre(null);
        List<Producto> productos = Collections.singletonList(p1);
        SearchByProveedorStrategy strategy = new SearchByProveedorStrategy();
        List<Producto> resultado = strategy.buscar(productos, "norte");
        assertTrue(resultado.isEmpty());
    }
}
