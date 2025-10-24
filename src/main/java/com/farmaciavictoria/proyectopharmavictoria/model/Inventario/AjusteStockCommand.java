package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

public class AjusteStockCommand extends InventarioCommand {
    public AjusteStockCommand(Producto producto, int cantidadAnterior, int cantidadNueva, String usuario, String observacion) {
        super(producto, cantidadAnterior, cantidadNueva, usuario, "AJUSTE_STOCK", observacion);
    }

    @Override
    public void ejecutar() {
        producto.setStockActual(cantidadNueva);
        // Aquí podrías agregar lógica para registrar en la BD o en el servicio de auditoría
    }

    @Override
    public void deshacer() {
        producto.setStockActual(cantidadAnterior);
        // Registrar reversión en auditoría si es necesario
    }
}
