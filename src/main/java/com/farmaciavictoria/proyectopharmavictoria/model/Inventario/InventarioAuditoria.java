package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

import java.util.ArrayList;
import java.util.List;

public class InventarioAuditoria {
    private static final List<InventarioCommand> historial = new ArrayList<>();

    public static void registrarMovimiento(InventarioCommand command) {
        historial.add(command);
    }

    public static List<InventarioCommand> obtenerHistorialPorProducto(Producto producto) {
        List<InventarioCommand> resultado = new ArrayList<>();
        for (InventarioCommand cmd : historial) {
            if (cmd.getProducto().getId().equals(producto.getId())) {
                resultado.add(cmd);
            }
        }
        return resultado;
    }

    public static List<InventarioCommand> obtenerHistorialCompleto() {
        return new ArrayList<>(historial);
    }
}
