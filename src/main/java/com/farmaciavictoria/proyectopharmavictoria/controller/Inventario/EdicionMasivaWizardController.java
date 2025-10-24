package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class EdicionMasivaWizardController {
    private com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService proveedorService;

    public void setProveedorService(
            com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    public void setProductoService(ProductoService productoService) {
        this.productoService = productoService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @FXML
    private StackPane wizardStack;
    @FXML
    private VBox vistaSeleccion;
    @FXML
    private VBox vistaEdicion;
    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private TableView<Producto> tableSeleccionProductos;
    @FXML
    private TableColumn<Producto, Boolean> colSeleccionar;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private FlowPane camposFlow;
    @FXML
    private Button btnSiguiente;
    @FXML
    private TableView<Producto> tableEdicionMasiva;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnAplicarCambios;

    private ObservableList<Producto> productos = FXCollections.observableArrayList();
    private ObservableList<Producto> seleccionados = FXCollections.observableArrayList();
    private Map<Producto, SimpleBooleanProperty> seleccionMap = new HashMap<>();
    private ProductoService productoService;
    private NotificationService notificationService;

    public void mostrarVistaSeleccion() {
        vistaEdicion.setVisible(false);
        vistaSeleccion.setVisible(true);
    }

    public void setProductos(List<Producto> productos) {
        this.productos.setAll(productos);
        seleccionMap.clear();
        seleccionados.clear();
        for (Producto p : productos) {
            SimpleBooleanProperty prop = new SimpleBooleanProperty(false);
            prop.addListener((obs, oldVal, newVal) -> {
                System.out.println("[DEBUG] Cambio checkbox producto: " + p.getCodigo() + " seleccionado=" + newVal);
                if (newVal) {
                    if (!seleccionados.contains(p))
                        seleccionados.add(p);
                } else {
                    seleccionados.remove(p);
                }
            });
            seleccionMap.put(p, prop);
        }
        tableSeleccionProductos.setItems(this.productos);
    }

    @FXML
    public void initialize() {
        mostrarVistaSeleccion();
        colCodigo.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCodigo()));
        colNombre.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colSeleccionar.setCellValueFactory(cell -> {
            Producto p = cell.getValue();
            return seleccionMap.getOrDefault(p, new SimpleBooleanProperty(false));
        });
        tableSeleccionProductos.setEditable(true);
        colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(index -> {
            Producto p = tableSeleccionProductos.getItems().get(index);
            ObservableValue<Boolean> prop = seleccionMap.get(p);
            System.out.println("[DEBUG] CheckBoxTableCell para producto: " + (p != null ? p.getCodigo() : "null")
                    + " - Propiedad: " + prop);
            return prop != null ? prop : new SimpleBooleanProperty(false);
        }));
        tableSeleccionProductos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableSeleccionProductos.setItems(productos);
        txtBuscarProducto.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos(newVal));
        camposFlow.getChildren().clear();
        for (String campo : camposEditables()) {
            CheckBox chk = new CheckBox(campo);
            chk.setUserData(campo);
            camposFlow.getChildren().add(chk);
        }
        btnSiguiente.setOnAction(e -> {
            System.out.println("[DEBUG] Seleccionados: " + seleccionados.size());
            for (Producto p : seleccionados) {
                System.out.println("[DEBUG] Producto seleccionado: " + p.getCodigo());
            }
            boolean campoSeleccionado = false;
            for (javafx.scene.Node node : camposFlow.getChildren()) {
                if (node instanceof CheckBox chk && chk.isSelected()) {
                    campoSeleccionado = true;
                    System.out.println("[DEBUG] Campo seleccionado: " + chk.getText());
                    break;
                }
            }
            if (seleccionados.isEmpty()) {
                notificationService.mostrarAdvertencia("Selecciona al menos un producto para editar.");
                return;
            }
            if (!campoSeleccionado) {
                notificationService.mostrarAdvertencia("Selecciona al menos un campo para editar.");
                return;
            }
            mostrarVistaEdicion();
        });
        btnVolver.setOnAction(e -> mostrarVistaSeleccion());
        btnAplicarCambios.setOnAction(e -> aplicarCambios());
        vistaSeleccion.getStylesheets()
                .add(getClass().getResource("/css/Inventario/wizard-edicion-masiva.css").toExternalForm());
        vistaEdicion.getStylesheets()
                .add(getClass().getResource("/css/Inventario/wizard-edicion-masiva.css").toExternalForm());
    }

    private void filtrarProductos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tableSeleccionProductos.setItems(productos);
        } else {
            ObservableList<Producto> filtrados = FXCollections.observableArrayList();
            String f = filtro.trim().toLowerCase();
            for (Producto p : productos) {
                if ((p.getNombre() != null && p.getNombre().toLowerCase().contains(f)) ||
                        (p.getCodigo() != null && p.getCodigo().toLowerCase().contains(f))) {
                    filtrados.add(p);
                }
            }
            tableSeleccionProductos.setItems(filtrados);
        }
    }

    private List<String> camposEditables() {
        return List.of("Código", "Nombre", "Descripción", "Principio Activo", "Concentración", "Forma Farmacéutica",
                "Precio Compra", "Precio Venta", "Margen Ganancia", "Stock Actual", "Stock Mínimo", "Stock Máximo",
                "Proveedor", "Ubicación", "Lote", "Fecha Vencimiento", "Requiere Receta", "Es Controlado", "Activo",
                "Laboratorio", "Fecha Fabricación");
    }

    private void mostrarVistaEdicion() {
        vistaSeleccion.setVisible(false);
        vistaEdicion.setVisible(true);
        ObservableList<Producto> seleccionadosActuales = FXCollections.observableArrayList(seleccionados);
        tableEdicionMasiva.getColumns().clear();
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setEditable(false);
        tableEdicionMasiva.getColumns().add(colNombre);
        for (javafx.scene.Node node : camposFlow.getChildren()) {
            if (node instanceof CheckBox chk && chk.isSelected()) {
                String campo = (String) chk.getUserData();
                TableColumn<Producto, ?> col = generarColumnaEditable(campo);
                if (col != null)
                    tableEdicionMasiva.getColumns().add(col);
            }
        }
        tableEdicionMasiva.setItems(seleccionadosActuales);
        tableEdicionMasiva.setEditable(true);
        tableEdicionMasiva.getSelectionModel().setCellSelectionEnabled(true);
        tableEdicionMasiva.scrollTo(0);
    }

    private TableColumn<Producto, ?> generarColumnaEditable(String campo) {
        switch (campo) {
            case "Código": {
                TableColumn<Producto, String> col = new TableColumn<>("Código");
                col.setCellValueFactory(new PropertyValueFactory<>("codigo"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setCodigo(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Nombre": {
                TableColumn<Producto, String> col = new TableColumn<>("Nombre");
                col.setCellValueFactory(new PropertyValueFactory<>("nombre"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setNombre(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Descripción": {
                TableColumn<Producto, String> col = new TableColumn<>("Descripción");
                col.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setDescripcion(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Principio Activo": {
                TableColumn<Producto, String> col = new TableColumn<>("Principio Activo");
                col.setCellValueFactory(new PropertyValueFactory<>("principioActivo"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setPrincipioActivo(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Concentración": {
                TableColumn<Producto, String> col = new TableColumn<>("Concentración");
                col.setCellValueFactory(new PropertyValueFactory<>("concentracion"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setConcentracion(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Forma Farmacéutica": {
                TableColumn<Producto, FormaFarmaceutica> col = new TableColumn<>("Forma Farmacéutica");
                col.setCellValueFactory(new PropertyValueFactory<>("formaFarmaceutica"));
                col.setCellFactory(ComboBoxTableCell
                        .forTableColumn(FXCollections.observableArrayList(FormaFarmaceutica.values())));
                col.setOnEditCommit(t -> t.getRowValue().setFormaFarmaceutica(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Precio Compra": {
                TableColumn<Producto, BigDecimal> col = new TableColumn<>("Precio Compra");
                col.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setPrecioCompra(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Precio Venta": {
                TableColumn<Producto, BigDecimal> col = new TableColumn<>("Precio Venta");
                col.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setPrecioVenta(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Margen Ganancia": {
                TableColumn<Producto, BigDecimal> col = new TableColumn<>("Margen Ganancia");
                col.setCellValueFactory(new PropertyValueFactory<>("margenGanancia"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setMargenGanancia(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Stock Actual": {
                TableColumn<Producto, Integer> col = new TableColumn<>("Stock Actual");
                col.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setStockActual(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Stock Mínimo": {
                TableColumn<Producto, Integer> col = new TableColumn<>("Stock Mínimo");
                col.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setStockMinimo(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Stock Máximo": {
                TableColumn<Producto, Integer> col = new TableColumn<>("Stock Máximo");
                col.setCellValueFactory(new PropertyValueFactory<>("stockMaximo"));
                col.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                col.setOnEditCommit(t -> t.getRowValue().setStockMaximo(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Proveedor": {
                java.util.List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedores = proveedorService != null
                        ? proveedorService.obtenerTodos()
                        : java.util.Collections.emptyList();
                javafx.collections.ObservableList<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedoresObs = javafx.collections.FXCollections
                        .observableArrayList(proveedores);
                TableColumn<Producto, com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> col = new TableColumn<>(
                        "Proveedor");
                col.setCellValueFactory(cell -> {
                    Producto p = cell.getValue();
                    com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor proveedor = proveedores
                            .stream()
                            .filter(pr -> pr.getId() != null && pr.getId().equals(p.getProveedorId()))
                            .findFirst().orElse(null);
                    return new javafx.beans.property.SimpleObjectProperty<>(proveedor);
                });
                col.setCellFactory(ComboBoxTableCell.forTableColumn(proveedoresObs));
                col.setOnEditCommit(t -> {
                    com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor nuevoProveedor = t
                            .getNewValue();
                    t.getRowValue().setProveedorId(nuevoProveedor != null ? nuevoProveedor.getId() : null);
                    t.getRowValue().setProveedorNombre(nuevoProveedor != null ? nuevoProveedor.getRazonSocial() : "");
                });
                col.setEditable(true);
                return col;
            }
            case "Categoría": {
                TableColumn<Producto, CategoriaProducto> col = new TableColumn<>("Categoría");
                col.setCellValueFactory(new PropertyValueFactory<>("categoria"));
                col.setCellFactory(ComboBoxTableCell
                        .forTableColumn(FXCollections.observableArrayList(CategoriaProducto.values())));
                col.setOnEditCommit(t -> t.getRowValue().setCategoria(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Ubicación": {
                TableColumn<Producto, String> col = new TableColumn<>("Ubicación");
                col.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setUbicacion(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Lote": {
                TableColumn<Producto, String> col = new TableColumn<>("Lote");
                col.setCellValueFactory(new PropertyValueFactory<>("lote"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setLote(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Fecha Vencimiento": {
                TableColumn<Producto, LocalDate> col = new TableColumn<>("Fecha Vencimiento");
                col.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
                col.setCellFactory(param -> new TableCell<Producto, LocalDate>() {
                    private final DatePicker datePicker = new DatePicker();
                    {
                        datePicker.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            producto.setFechaVencimiento(datePicker.getValue());
                        });
                    }

                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            datePicker.setValue(item);
                            setGraphic(datePicker);
                        }
                    }
                });
                col.setEditable(true);
                return col;
            }
            case "Requiere Receta": {
                TableColumn<Producto, Boolean> col = new TableColumn<>("Requiere Receta");
                col.setCellValueFactory(cell -> cell.getValue().requiereRecetaProperty());
                col.setCellFactory(CheckBoxTableCell.forTableColumn(index -> {
                    Producto p = tableEdicionMasiva.getItems().get(index);
                    return p.requiereRecetaProperty();
                }));
                col.setEditable(true);
                return col;
            }
            case "Es Controlado": {
                TableColumn<Producto, Boolean> col = new TableColumn<>("Es Controlado");
                col.setCellValueFactory(cell -> cell.getValue().esControladoProperty());
                col.setCellFactory(CheckBoxTableCell.forTableColumn(index -> {
                    Producto p = tableEdicionMasiva.getItems().get(index);
                    return p.esControladoProperty();
                }));
                col.setEditable(true);
                return col;
            }
            case "Activo": {
                TableColumn<Producto, Boolean> col = new TableColumn<>("Activo");
                col.setCellValueFactory(new PropertyValueFactory<>("activo"));
                col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
                col.setOnEditCommit(t -> t.getRowValue().setActivo(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Laboratorio": {
                TableColumn<Producto, String> col = new TableColumn<>("Laboratorio");
                col.setCellValueFactory(new PropertyValueFactory<>("laboratorio"));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setOnEditCommit(t -> t.getRowValue().setLaboratorio(t.getNewValue()));
                col.setEditable(true);
                return col;
            }
            case "Fecha Fabricación": {
                TableColumn<Producto, LocalDate> col = new TableColumn<>("Fecha Fabricación");
                col.setCellValueFactory(new PropertyValueFactory<>("fechaFabricacion"));
                col.setCellFactory(param -> new TableCell<Producto, LocalDate>() {
                    private final DatePicker datePicker = new DatePicker();
                    {
                        datePicker.setOnAction(e -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            producto.setFechaFabricacion(datePicker.getValue());
                        });
                    }

                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            datePicker.setValue(item);
                            setGraphic(datePicker);
                        }
                    }
                });
                col.setEditable(true);
                return col;
            }
        }
        return null;
    }

    private void aplicarCambios() {
        for (Producto producto : tableEdicionMasiva.getItems()) {
            productoService.actualizar(producto);
        }
        notificationService.mostrarInfo("Productos actualizados correctamente.");
    }

    @FXML
    private void limpiarCampos() {
        tableSeleccionProductos.getSelectionModel().clearSelection();
        for (javafx.scene.Node node : camposFlow.getChildren()) {
            if (node instanceof CheckBox chk)
                chk.setSelected(false);
        }
        tableEdicionMasiva.getColumns().clear();
        tableEdicionMasiva.setItems(FXCollections.observableArrayList());
        mostrarVistaSeleccion();
    }
}
