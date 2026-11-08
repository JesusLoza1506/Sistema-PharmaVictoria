package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;

import java.time.LocalDate;
import java.util.List;

final class FiltroProductos {
    private FiltroProductos() {
    }

    static List<Producto> filtrar(List<Producto> productos, String textoBusqueda,
            CategoriaProducto categoriaSeleccionada, String estadoSeleccionado, boolean mostrarInactivos,
            String estrategia, int diasVencimiento) {
        com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.ProductSearchStrategy strategy;
        switch (estrategia) {
            case "Laboratorio":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByLaboratorioStrategy();
                break;
            case "Lote":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByLoteStrategy();
                break;
            case "Proveedor":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByProveedorStrategy();
                break;
            case "Vencimiento":
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByVencimientoStrategy();
                break;
            case "Nombre":
            default:
                strategy = new com.farmaciavictoria.proyectopharmavictoria.strategy.Inventario.SearchByNameStrategy();
                break;
        }

        return strategy.buscar(productos, textoBusqueda)
                .stream()
                .filter(producto -> mostrarInactivos || producto.getActivo())
                .filter(producto -> categoriaSeleccionada == null
                        || producto.getCategoria().equals(categoriaSeleccionada))
                .filter(producto -> {
                    if ("Todos".equals(estadoSeleccionado))
                        return true;
                    if ("Activo".equals(estadoSeleccionado))
                        return producto.getActivo();
                    if ("Inactivo".equals(estadoSeleccionado))
                        return !producto.getActivo();
                    if ("Stock Bajo".equals(estadoSeleccionado))
                        return producto.getStockActual() <= producto.getStockMinimo();
                    if ("Próximos a Vencer".equals(estadoSeleccionado))
                        return producto.getFechaVencimiento() != null
                                && producto.getFechaVencimiento().isAfter(LocalDate.now())
                                && (producto.getFechaVencimiento().isEqual(LocalDate.now().plusDays(diasVencimiento))
                                        || producto.getFechaVencimiento()
                                                .isBefore(LocalDate.now().plusDays(diasVencimiento)));
                    if ("Sin Stock".equals(estadoSeleccionado))
                        return producto.getStockActual() == 0;
                    if ("Productos Vencidos".equals(estadoSeleccionado))
                        return producto.getFechaVencimiento() != null
                                && producto.getFechaVencimiento().isBefore(LocalDate.now());
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
