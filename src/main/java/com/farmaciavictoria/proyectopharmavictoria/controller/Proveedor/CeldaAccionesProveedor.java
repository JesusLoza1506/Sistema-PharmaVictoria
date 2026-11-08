package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

final class CeldaAccionesProveedor extends TableCell<Proveedor, Void> {
    private final Button btnEditar = new Button();
    private final Button btnVer = new Button();
    private final Button btnToggleEstado = new Button();
    private final Button btnEliminar = new Button();

    private final BooleanSupplier permisoEditar;
    private final BooleanSupplier permisoToggleEstado;
    private final BooleanSupplier permisoEliminar;

    CeldaAccionesProveedor(Consumer<Proveedor> editar, Consumer<Proveedor> ver, Consumer<Proveedor> cambiarEstado,
            Consumer<Proveedor> eliminar, BooleanSupplier permisoEditar, BooleanSupplier permisoToggleEstado,
            BooleanSupplier permisoEliminar) {
        this.permisoEditar = permisoEditar;
        this.permisoToggleEstado = permisoToggleEstado;
        this.permisoEliminar = permisoEliminar;

        // Íconos: los mismos recursos que usa Inventario
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

        // Clases CSS coherentes con Inventario
        btnEditar.getStyleClass().setAll("btn-action", "btn-add");
        btnVer.getStyleClass().setAll("btn-action", "btn-eye");
        btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle");
        btnEliminar.getStyleClass().setAll("btn-action", "btn-trash");

        btnEditar.setPrefSize(35, 35);
        btnVer.setPrefSize(35, 35);
        btnToggleEstado.setPrefSize(35, 35);
        btnEliminar.setPrefSize(35, 35);

        // Tooltips
        btnEditar.setTooltip(new Tooltip("Editar proveedor"));
        btnVer.setTooltip(new Tooltip("Ver detalles"));
        btnEliminar.setTooltip(new Tooltip("Eliminar"));

        btnEditar.setOnAction(e -> {
            Proveedor proveedor = getTableView().getItems().get(getIndex());
            editar.accept(proveedor);
        });
        btnVer.setOnAction(e -> {
            Proveedor proveedor = getTableView().getItems().get(getIndex());
            ver.accept(proveedor);
        });
        btnToggleEstado.setOnAction(e -> {
            Proveedor proveedor = getTableView().getItems().get(getIndex());
            cambiarEstado.accept(proveedor);
        });
        btnEliminar.setOnAction(e -> {
            Proveedor proveedor = getTableView().getItems().get(getIndex());
            eliminar.accept(proveedor);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuario = com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer
                    .getInstance().getAuthenticationService().getUsuarioActual();
            boolean esAdmin = usuario != null && usuario.isAdmin();
            boolean esVendedor = usuario != null && usuario.isVendedor();

            boolean mostrarEditar = esAdmin || (esVendedor && permisoEditar.getAsBoolean());
            boolean mostrarToggle = esAdmin || (esVendedor && permisoToggleEstado.getAsBoolean());
            boolean mostrarEliminar = esAdmin || (esVendedor && permisoEliminar.getAsBoolean());
            // El botón Ver SIEMPRE debe mostrarse para vendedores
            boolean mostrarVer = esAdmin || esVendedor;

            btnEditar.setVisible(mostrarEditar);
            btnEditar.setManaged(mostrarEditar);
            btnToggleEstado.setVisible(mostrarToggle);
            btnToggleEstado.setManaged(mostrarToggle);
            btnEliminar.setVisible(mostrarEliminar);
            btnEliminar.setManaged(mostrarEliminar);
            btnVer.setVisible(mostrarVer);
            btnVer.setManaged(mostrarVer);

            // Estado visual del botón toggle
            if (mostrarToggle) {
                Proveedor proveedor = getTableView().getItems().get(getIndex());
                if (proveedor != null && proveedor.getActivo()) {
                    btnToggleEstado.setText("⏸");
                    btnToggleEstado.setTooltip(new Tooltip("Inactivar proveedor"));
                    btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-active");
                } else {
                    btnToggleEstado.setText("▶");
                    btnToggleEstado.setTooltip(new Tooltip("Activar proveedor"));
                    btnToggleEstado.getStyleClass().setAll("btn-action", "btn-toggle", "btn-toggle-inactive");
                }
            }

            HBox hbox = new HBox(7);
            hbox.setAlignment(javafx.geometry.Pos.CENTER);
            if (mostrarEditar)
                hbox.getChildren().add(btnEditar);
            if (mostrarVer)
                hbox.getChildren().add(btnVer);
            if (mostrarToggle)
                hbox.getChildren().add(btnToggleEstado);
            if (mostrarEliminar)
                hbox.getChildren().add(btnEliminar);
            setGraphic(hbox);
        }
    }
}
