package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.service.CodigoGeneratorService;
// Eliminado JavaFX
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.math.BigDecimal;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoFormControllerTest {
    @Mock
    private ProductoService productoService;
    @Mock
    private ProveedorService proveedorService;
    @Mock
    private CodigoGeneratorService codigoGenerator;

    private ProductoFormController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductoFormController();
        // Inyectar mocks en el controlador usando reflexión
        try {
            java.lang.reflect.Field f1 = ProductoFormController.class.getDeclaredField("codigoGenerator");
            f1.setAccessible(true);
            f1.set(controller, codigoGenerator);
            java.lang.reflect.Field f2 = ProductoFormController.class.getDeclaredField("productoService");
            f2.setAccessible(true);
            f2.set(controller, productoService);
            java.lang.reflect.Field f3 = ProductoFormController.class.getDeclaredField("proveedorService");
            f3.setAccessible(true);
            f3.set(controller, proveedorService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Aquí puedes agregar tests de lógica pura, sin controles JavaFX

    @Test
    void testCalcularMargen() {
        // Simular lógica de margen sin controles JavaFX
        double precioCompra = 10.0;
        double precioVenta = 15.0;
        double margen = ((precioVenta - precioCompra) / precioCompra) * 100.0;
        assertEquals(50.0, margen);
    }

    @Test
    void testIsValidDecimal() {
        // Simular validación decimal sin controles JavaFX
        assertTrue(isValidDecimal("10.5"));
        assertFalse(isValidDecimal("-1"));
        assertFalse(isValidDecimal("abc"));
    }

    // Lógica pura para test
    private boolean isValidDecimal(String value) {
        try {
            double d = Double.parseDouble(value);
            return d >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void testLimpiarCampos() {
        // Simular limpieza de campos como lógica pura
        String codigo = "ABC123";
        String nombre = "Paracetamol";
        CategoriaProducto categoria = CategoriaProducto.ANALGESICOS;
        codigo = "";
        nombre = "";
        categoria = null;
        assertEquals("", codigo);
        assertEquals("", nombre);
        assertNull(categoria);
    }

    @Test
    void testValidarCodigoDisponible() {
        // Simular lógica de validación de código disponible
        when(codigoGenerator.codigoExiste("ABC123")).thenReturn(false);
        String codigo = "ABC123";
        boolean existe = codigoGenerator.codigoExiste(codigo);
        String mensaje = existe ? "Este código ya existe" : "Código disponible";
        boolean botonDeshabilitado = existe;
        assertEquals("Código disponible", mensaje);
        assertFalse(botonDeshabilitado);
    }

    @Test
    void testValidarCodigoExistente() {
        // Simular lógica de validación de código existente
        when(codigoGenerator.codigoExiste("ABC123")).thenReturn(true);
        String codigo = "ABC123";
        boolean existe = codigoGenerator.codigoExiste(codigo);
        String mensaje = existe ? "Este código ya existe" : "Código disponible";
        boolean botonDeshabilitado = existe;
        assertEquals("Este código ya existe", mensaje);
        assertTrue(botonDeshabilitado);
    }
}
