package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

final class CeldaAccionesProducto extends TableCell<Producto, Void> {
    private final Button btnEditar = new Button();
    private final Button btnVer = new Button();
    private final Button btnToggleEstado = new Button();
    private final Button btnEliminar = new Button();

    CeldaAccionesProducto(Consumer<Producto> editarProducto, Consumer<Producto> verDetallesProducto,
            Consumer<Producto> toggleEstadoProducto, Consumer<Producto> eliminarProductoDefinitivo,
            Runnable enlazarPaginacion) {
        javafx.scene.image.ImageView iconEditar = new javafx.scene.image.ImageView(
                getClass().getResource("/icons/editar.png").toExternalForm());
        javafx.scene.image.ImageView iconVer = new javafx.scene.image.ImageView(
                getClass().getResource("/icons/ver.png").toExternalForm());
        javafx.scene.image.ImageView iconEliminar = new javafx.scene.image.ImageView(
                getClass().getResource("/icons/eliminar.png").toExternalForm());

        iconEditar.setFitWidth(24);
        iconEditar.setFitHeight(24);
        iconVer.setFitWidth(24);
        iconVer.setFitHeight(24);
        iconEliminar.setFitWidth(24);
        iconEliminar.setFitHeight(24);

        btnEditar.setGraphic(iconEditar);
        btnVer.setGraphic(iconVer);
        btnEliminar.setGraphic(iconEliminar);
        btnEditar.getStyleClass().setAll("btn-action", "btn-add");
        btnVer.getStyleClass().setAll("btn-action", "btn-eye");
        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle");
        enlazarPaginacion.run();
        btnEliminar.getStyleClass().setAll("btn-action", "btn-trash");
        btnEditar.setPrefWidth(35);
        btnEditar.setPrefHeight(35);
        btnVer.setPrefWidth(35);
        btnVer.setPrefHeight(35);
        btnToggleEstado.setPrefWidth(35);
        btnToggleEstado.setPrefHeight(35);
        btnEliminar.setPrefWidth(35);
        btnEliminar.setPrefHeight(35);

        btnEditar.setOnAction(e -> {
            Producto producto = getTableView().getItems().get(getIndex());
            editarProducto.accept(producto);
        });
        btnVer.setOnAction(e -> {
            Producto producto = getTableView().getItems().get(getIndex());
            verDetallesProducto.accept(producto);
        });
        btnToggleEstado.setOnAction(e -> {
            Producto producto = getTableView().getItems().get(getIndex());
            toggleEstadoProducto.accept(producto);
        });
        btnEliminar.setOnAction(e -> {
            Producto producto = getTableView().getItems().get(getIndex());
            eliminarProductoDefinitivo.accept(producto);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            Producto producto = getTableView().getItems().get(getIndex());
            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                    .getInstance()
                    .getAuthenticationService().getUsuarioActual();
            boolean esAdmin = usuario != null && usuario.isAdmin();
            // Acciones por producto según rol
            if (esAdmin) {
                btnEditar.setVisible(true);
                btnEditar.setManaged(true);
                btnToggleEstado.setVisible(true);
                btnToggleEstado.setManaged(true);
                btnEliminar.setVisible(true);
                btnEliminar.setManaged(true);
                btnVer.setVisible(true);
                btnVer.setManaged(true);
            } else if (usuario != null && usuario.isVendedor()) {
                // Obtener permisos granulares del primer vendedor (global)
                com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService usuarioService = com.farmaciavictoria.proyectopharmavictoria.service.UsuarioService
                        .getInstance();
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario> vendedores = usuarioService
                        .obtenerUsuarios().stream()
                        .filter(u -> u
                                .getRol() == com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario.Rol.VENDEDOR)
                        .collect(java.util.stream.Collectors.toList());
                final java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Usuario.UsuarioPermiso> permisos;
                if (!vendedores.isEmpty()) {
                    permisos = usuarioService.obtenerPermisos(vendedores.get(0).getId());
                } else {
                    permisos = java.util.Collections.emptyList();
                }
                java.util.function.Function<String, Boolean> tienePermiso = clave -> permisos.stream()
                        .anyMatch(p -> clave.equals(p.getPermiso()) && p.isValor());

                btnEditar.setVisible(tienePermiso.apply("inventario.editar"));
                btnEditar.setManaged(tienePermiso.apply("inventario.editar"));
                btnToggleEstado.setVisible(tienePermiso.apply("inventario.activar_inactivar"));
                btnToggleEstado.setManaged(tienePermiso.apply("inventario.activar_inactivar"));
                btnEliminar.setVisible(tienePermiso.apply("inventario.eliminar"));
                btnEliminar.setManaged(tienePermiso.apply("inventario.eliminar"));
                btnVer.setVisible(true); // Ver detalles siempre visible
                btnVer.setManaged(true);
            } else {
                btnEditar.setVisible(false);
                btnEditar.setManaged(false);
                btnToggleEstado.setVisible(false);
                btnToggleEstado.setManaged(false);
                btnEliminar.setVisible(false);
                btnEliminar.setManaged(false);
                btnVer.setVisible(true);
                btnVer.setManaged(true);
            }
            if (producto.getActivo()) {
                btnToggleEstado.setText("⏸");
                btnToggleEstado.setTooltip(new Tooltip("Inactivar producto"));
                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
            } else {
                btnToggleEstado.setText("▶");
                btnToggleEstado.setTooltip(new Tooltip("Activar producto"));
                btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle",
                        "btn-toggle-inactive");
            }
            HBox buttons = new HBox(5, btnEditar, btnVer, btnToggleEstado, btnEliminar);
            buttons.setAlignment(javafx.geometry.Pos.CENTER);
            setGraphic(buttons);
        }
    }
}
