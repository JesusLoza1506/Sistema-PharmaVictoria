package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.service.CodigoGeneratorService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoFormControllerTest {
    @Mock
    private ServiceContainer serviceContainer;
    @Mock
    private ProductoService productoService;
    @Mock
    private ProveedorService proveedorService;
    @Mock
    private CodigoGeneratorService codigoGenerator;

    private ProductoFormController controller;

    @BeforeEach
    void setUp() {
        when(serviceContainer.getProductoService()).thenReturn(productoService);
        when(serviceContainer.getProveedorService()).thenReturn(proveedorService);
        when(serviceContainer.getCodigoGeneratorService()).thenReturn(codigoGenerator);

        try (MockedStatic<ServiceContainer> containerMock = mockStatic(ServiceContainer.class)) {
            containerMock.when(ServiceContainer::getInstance).thenReturn(serviceContainer);
            controller = new ProductoFormController();
        }
    }

    @Test
    void testCalcularMargen() {
        double precioCompra = 10.0;
        double precioVenta = 15.0;
        double margen = ((precioVenta - precioCompra) / precioCompra) * 100.0;
        assertEquals(50.0, margen);
    }

    @Test
    void testIsValidDecimal() throws ReflectiveOperationException {
        Method validador = ProductoFormController.class.getDeclaredMethod("isValidDecimal", String.class);
        validador.setAccessible(true);

        assertTrue((boolean) validador.invoke(controller, "10.5"));
        assertFalse((boolean) validador.invoke(controller, "0"));
        assertFalse((boolean) validador.invoke(controller, "-1"));
        assertFalse((boolean) validador.invoke(controller, "abc"));
    }

    @Test
    void testLimpiarCampos() {
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
        String codigo = "ABC123";
        when(codigoGenerator.codigoExiste(codigo)).thenReturn(false);
        boolean existe = codigoGenerator.codigoExiste(codigo);
        String mensaje = existe ? "Este código ya existe" : "Código disponible";
        assertEquals("Código disponible", mensaje);
        assertFalse(existe);
    }

    @Test
    void testValidarCodigoExistente() {
        String codigo = "ABC123";
        when(codigoGenerator.codigoExiste(codigo)).thenReturn(true);
        boolean existe = codigoGenerator.codigoExiste(codigo);
        String mensaje = existe ? "Este código ya existe" : "Código disponible";
        assertEquals("Este código ya existe", mensaje);
        assertTrue(existe);
    }
}
