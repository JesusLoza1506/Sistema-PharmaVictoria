package com.farmaciavictoria.proyectopharmavictoria.strategy.Cliente;

import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FiltroPorEmailTest {
    @Test
    void filtraPorEmailCoincidente() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setEmail("juan@mail.com");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setEmail("ana@mail.com");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorEmail filtro = new FiltroPorEmail();
        List<Cliente> resultado = filtro.filtrar(clientes, "juan");
        assertEquals(1, resultado.size());
        assertEquals(c1, resultado.get(0));
    }

    @Test
    void criterioVacioRetornaTodos() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setEmail("juan@mail.com");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setEmail("ana@mail.com");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorEmail filtro = new FiltroPorEmail();
        List<Cliente> resultado = filtro.filtrar(clientes, "");
        assertEquals(2, resultado.size());
    }

    @Test
    void sinCoincidenciaRetornaVacio() {
        Cliente c1 = new Cliente("12345678", "Juan", "Perez");
        c1.setEmail("juan@mail.com");
        Cliente c2 = new Cliente("87654321", "Ana", "Lopez");
        c2.setEmail("ana@mail.com");
        List<Cliente> clientes = Arrays.asList(c1, c2);
        FiltroPorEmail filtro = new FiltroPorEmail();
        List<Cliente> resultado = filtro.filtrar(clientes, "zzz");
        assertTrue(resultado.isEmpty());
    }
}
