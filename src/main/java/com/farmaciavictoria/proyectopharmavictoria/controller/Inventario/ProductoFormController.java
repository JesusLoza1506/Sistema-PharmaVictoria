package com.farmaciavictoria.proyectopharmavictoria.controller.Inventario;

import com.farmaciavictoria.proyectopharmavictoria.service.AuditoriaEdicionProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.CategoriaProducto;
import com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import com.farmaciavictoria.proyectopharmavictoria.service.ProductoService;
import com.farmaciavictoria.proyectopharmavictoria.service.ProveedorService;
import com.farmaciavictoria.proyectopharmavictoria.service.CodigoGeneratorService;
import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.controller.DashboardController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ProductoFormController implements Initializable {
    // Listener de filtrado de proveedores
    private javafx.beans.value.ChangeListener<String> proveedorFiltroListener;
    @FXML
    private javafx.scene.image.ImageView imgConfiguracion;
    private static final Logger logger = LoggerFactory.getLogger(ProductoFormController.class);
    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrincipioActivo;
    @FXML
    private TextField txtConcentracion;
    @FXML
    private ComboBox<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica> cmbFormaFarmaceutica;
    @FXML
    private ComboBox<CategoriaProducto> cmbCategoria;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtPrecioCompra;
    @FXML
    private TextField txtPrecioVenta;
    @FXML
    private TextField txtMargen;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;
    @FXML
    private TextField txtLaboratorio;
    @FXML
    private Spinner<Integer> spnStockActual;
    @FXML
    private Spinner<Integer> spnStockMinimo;
    @FXML
    private Spinner<Integer> spnStockMaximo;
    @FXML
    private TextField txtUbicacion;
    @FXML
    private TextField txtLote;
    @FXML
    private DatePicker dpFechaVencimiento;
    @FXML
    private DatePicker dpFechaFabricacion;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private CheckBox chkRequiereReceta;
    @FXML
    private CheckBox chkControlado;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    // Botón Cancelar eliminado
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnGenerarCodigo;
    @FXML
    private Label lblUltimoCodigo;
    @FXML
    private Label lblValidacionCodigo;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    private final CodigoGeneratorService codigoGenerator;
    private Producto productoActual;
    private boolean modoEdicion = false;
    private Consumer<Producto> onProductoGuardado;

    public ProductoFormController() {
        try {
            ServiceContainer container = ServiceContainer.getInstance();
            this.productoService = container.getProductoService();
            this.proveedorService = container.getProveedorService();
            this.codigoGenerator = container.getCodigoGeneratorService();
            logger.info("ProductoFormController inicializado con enterprise patterns");
        } catch (Exception e) {
            logger.error("Error al inicializar ProductoFormController: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo crítico en inicialización del formulario", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (imgConfiguracion != null) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icons/configuracion.png"));
                if (img.isError()) {
                    logger.error("No se pudo cargar la imagen de configuración: /icons/configuracion.png");
                } else {
                    imgConfiguracion.setImage(img);
                    logger.info("Imagen de configuración cargada correctamente");
                }
            } catch (Exception ex) {
                logger.error("Error al cargar imagen de configuración", ex);
            }
        }
        try {
            logger.debug("Iniciando inicialización de ProductoFormController");
            setupNumericFields();
            initializeComponents();
            setupEventHandlers();
            loadComboBoxData();
            logger.info("ProductoFormController inicializado exitosamente");
        } catch (Exception e) {
            logger.error("Error en inicialización de ProductoFormController: {}", e.getMessage(), e);
            showError("Error de inicialización", "No se pudo inicializar el formulario: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        // Configurar spinners
        spnStockActual.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        spnStockMinimo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 1));
        spnStockMaximo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 100));

        // Configurar combo de categorías
        cmbCategoria.setItems(FXCollections.observableArrayList(CategoriaProducto.values()));
        cmbCategoria.setConverter(new StringConverter<CategoriaProducto>() {
            @Override
            public String toString(CategoriaProducto categoria) {
                return categoria != null ? categoria.toString() : "";
            }

            @Override
            public CategoriaProducto fromString(String string) {
                return null;
            }
        });

        // Configurar combo de forma farmacéutica
        cmbFormaFarmaceutica.setItems(FXCollections.observableArrayList(
                com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica.values()));
        cmbFormaFarmaceutica.setConverter(
                new StringConverter<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica>() {
                    @Override
                    public String toString(
                            com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica forma) {
                        return forma != null ? forma.getDescripcion() : "";
                    }

                    @Override
                    public com.farmaciavictoria.proyectopharmavictoria.model.Inventario.FormaFarmaceutica fromString(
                            String string) {
                        return null;
                    }
                });

        // Configurar combo de proveedores editable y con búsqueda
        cmbProveedor.setEditable(true);
        cmbProveedor.setConverter(new StringConverter<Proveedor>() {
            @Override
            public String toString(Proveedor proveedor) {
                return proveedor != null ? proveedor.getRazonSocial() : "";
            }

            @Override
            public Proveedor fromString(String string) {
                // Permite que el usuario escriba libremente
                // Si coincide con algún proveedor, lo retorna; si no, retorna null
                if (string == null || string.trim().isEmpty())
                    return null;
                for (Proveedor p : cmbProveedor.getItems()) {
                    if (p.getRazonSocial().equalsIgnoreCase(string.trim())) {
                        return p;
                    }
                }
                return null;
            }
        });

        // Listener para filtrar proveedores en tiempo real (solo se agrega una vez)
        proveedorFiltroListener = (obs, oldText, newText) -> filtrarProveedoresComboBox(newText);
        cmbProveedor.getEditor().textProperty().addListener(proveedorFiltroListener);

        // Mantener la lista desplegada al escribir
        cmbProveedor.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused)
                cmbProveedor.show();
        });

        // Permitir selección libre (si el usuario escribe y presiona Enter)
        cmbProveedor.getEditor().setOnAction(e -> {
            String texto = cmbProveedor.getEditor().getText();
            Proveedor seleccionado = cmbProveedor.getConverter().fromString(texto);
            cmbProveedor.setValue(seleccionado);
        });
        // ...existing code...
    }

    // Filtra proveedores en el ComboBox según el texto ingresado (multi-palabra)
    private void filtrarProveedoresComboBox(String texto) {
        // Evitar ciclo infinito: en modo edición, solo filtrar si el ComboBox está
        // desplegado
        if (modoEdicion && !cmbProveedor.isShowing()) {
            return;
        }
        String textoActual = cmbProveedor.getEditor().getText();
        Proveedor seleccionado = cmbProveedor.getValue();
        java.util.List<Proveedor> proveedores;
        if (texto == null || texto.trim().isEmpty()) {
            proveedores = proveedorService.obtenerTodos();
        } else {
            String[] palabras = texto.toLowerCase().split("\\s+");
            proveedores = proveedorService.obtenerTodos().stream()
                    .filter(p -> {
                        String nombre = p.getRazonSocial().toLowerCase();
                        return java.util.Arrays.stream(palabras).allMatch(nombre::contains);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedores));
        cmbProveedor.getEditor().setText(textoActual);
        // Solo modificar la selección si el ComboBox está desplegado o no está en modo
        // edición
        if (cmbProveedor.isShowing() || !modoEdicion) {
            if (seleccionado != null && proveedores.contains(seleccionado)) {
                cmbProveedor.setValue(seleccionado);
            } else {
                cmbProveedor.setValue(null);
            }
        }
    }

    private void setupNumericFields() {
        // Validación para campos de precio
        txtPrecioCompra.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtPrecioCompra.setText(oldVal);
            } else {
                calcularMargen();
            }
        });

        txtPrecioVenta.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtPrecioVenta.setText(oldVal);
            } else {
                calcularMargen();
            }
        });

        // Validación para código (solo letras y números)
        txtCodigo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Za-z0-9]*")) {
                txtCodigo.setText(oldVal);
            }
        });
    }

    private void setupEventHandlers() {
        // Eventos de los spinners
        spnStockActual.valueProperty().addListener((obs, oldVal, newVal) -> validateStock());
        spnStockMinimo.valueProperty().addListener((obs, oldVal, newVal) -> validateStock());
        spnStockMaximo.valueProperty().addListener((obs, oldVal, newVal) -> validateStock());

        // 🚀 Eventos del generador de códigos
        if (btnGenerarCodigo != null) {
            btnGenerarCodigo.setOnAction(e -> generarCodigoAutomatico());
        }

        // Validación de código en tiempo real
        txtCodigo.textProperty().addListener((obs, oldText, newText) -> {
            validarCodigo(newText);
        });

        // Actualizar información y código cuando cambia la categoría
        cmbCategoria.setOnAction(e -> {
            actualizarInfoCategoria();
            // Si el código fue generado automáticamente y no fue modificado manualmente,
            // regenerar
            if (!modoEdicion && (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()
                    || esCodigoDeCategoriaAnterior())) {
                sugerirCodigo();
            }
        });

        // Sugerir código cuando cambia el nombre (solo en modo nuevo)
        txtNombre.textProperty().addListener((obs, oldText, newText) -> {
            if (!modoEdicion && (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty())) {
                sugerirCodigo();
            }
        });

        // Opcional: podrías agregar validación en txtConcentracion si lo deseas
    }

    // Devuelve true si el código actual corresponde a la categoría anterior (o a
    // "OTR")
    private boolean esCodigoDeCategoriaAnterior() {
        String codigo = txtCodigo.getText();
        CategoriaProducto categoria = cmbCategoria.getValue();
        if (codigo == null || categoria == null)
            return false;
        // Si el código empieza con OTR o con el prefijo de la categoría anterior, se
        // debe regenerar
        for (CategoriaProducto cat : CategoriaProducto.values()) {
            if (codigo.startsWith(cat.name().substring(0, 3))) {
                return !codigo.startsWith(categoria.name().substring(0, 3));
            }
        }
        return false;
    }

    private void loadComboBoxData() {
        try {
            // ✅ SERVICE LAYER: Cargar proveedores usando service layer
            List<Proveedor> proveedores = proveedorService.obtenerTodos();
            cmbProveedor.setItems(FXCollections.observableArrayList(proveedores));

            logger.debug("Proveedores cargados exitosamente: {}", proveedores.size());
        } catch (Exception e) {
            logger.error("Error al cargar proveedores: {}", e.getMessage(), e);
            showWarning("Advertencia", "No se pudieron cargar los proveedores: " + e.getMessage());
        }
    }

    private void calcularMargen() {
        try {
            String precioCompraStr = txtPrecioCompra.getText();
            String precioVentaStr = txtPrecioVenta.getText();

            if (!precioCompraStr.isEmpty() && !precioVentaStr.isEmpty()) {
                BigDecimal precioCompra = new BigDecimal(precioCompraStr);
                BigDecimal precioVenta = new BigDecimal(precioVentaStr);

                if (precioCompra.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal margen = precioVenta.subtract(precioCompra)
                            .divide(precioCompra, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));

                    txtMargen.setText(String.format("%.2f%%", margen.doubleValue()));
                } else {
                    txtMargen.setText("0.00%");
                }
            } else {
                txtMargen.setText("0.00%");
            }
        } catch (NumberFormatException e) {
            txtMargen.setText("0.00%");
        }
    }

    private void validateStock() {
        // Validar que stock mínimo no sea mayor que máximo
        // Solo ajustar el stock máximo si NO estamos editando un producto existente con
        // stock máximo definido
        if (spnStockMinimo.getValue() > spnStockMaximo.getValue()) {
            if (!modoEdicion || (productoActual == null) || productoActual.getStockMaximo() == null
                    || productoActual.getStockMaximo() <= 0) {
                spnStockMaximo.getValueFactory().setValue(spnStockMinimo.getValue());
            }
        }
    }

    public void setProducto(Producto producto) {
        this.productoActual = producto;
        this.modoEdicion = (producto != null);

        // Resetear spinner de stock antes de cargar datos
        if (spnStockActual != null && spnStockActual.getValueFactory() != null) {
            spnStockActual.getValueFactory().setValue(0);
        }

        if (modoEdicion) {
            lblTitulo.setText("EDITAR PRODUCTO");
            if (btnGuardar != null)
                btnGuardar.setText("Actualizar Producto");
            cargarDatosProducto();
            // El spinner de stock máximo se inicializa SOLO en cargarDatosProducto
            txtCodigo.setEditable(false); // No permitir cambiar código en edición
            if (btnGenerarCodigo != null) {
                btnGenerarCodigo.setDisable(true); // Deshabilitar generación en edición
            }
        } else {
            lblTitulo.setText("NUEVO PRODUCTO");
            if (btnGuardar != null)
                btnGuardar.setText("Guardar Producto");
            limpiarCampos();
            txtCodigo.setEditable(true); // Permitir editar código en nuevo producto
            if (btnGenerarCodigo != null) {
                btnGenerarCodigo.setDisable(false); // Habilitar generación en nuevo producto
            }
        }

        // Actualizar información inicial de categoría
        actualizarInfoCategoria();
    }

    private void cargarDatosProducto() {
        if (productoActual == null)
            return;

        // Información básica
        txtCodigo.setText(productoActual.getCodigo());
        txtNombre.setText(productoActual.getNombre());
        txtPrincipioActivo.setText(productoActual.getPrincipioActivo());
        txtConcentracion.setText(productoActual.getConcentracion());
        cmbFormaFarmaceutica.setValue(productoActual.getFormaFarmaceutica());
        cmbCategoria.setValue(productoActual.getCategoria());
        txtDescripcion.setText(productoActual.getDescripcion());

        // Información comercial
        txtPrecioCompra.setText(productoActual.getPrecioCompra().toString());
        txtPrecioVenta.setText(productoActual.getPrecioVenta().toString());
        txtLaboratorio.setText(productoActual.getLaboratorio() != null ? productoActual.getLaboratorio() : "");

        // --- Solución robusta: cargar lista completa y limpiar filtro antes de
        // seleccionar proveedor ---
        // --- Desactivar temporalmente el listener de filtrado para evitar ciclo de
        // eventos ---
        cmbProveedor.getEditor().textProperty().removeListener(proveedorFiltroListener);
        List<Proveedor> proveedoresFull = proveedorService.obtenerTodos();
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedoresFull));
        cmbProveedor.getEditor().setText("");
        if (productoActual.getProveedorId() != null) {
            proveedoresFull.stream()
                    .filter(p -> p.getId().equals(productoActual.getProveedorId()))
                    .findFirst()
                    .ifPresent(cmbProveedor::setValue);
        }
        // Agregar listener solo después de cargar y seleccionar
        cmbProveedor.getEditor().textProperty().addListener(proveedorFiltroListener);

        // Control de inventario
        spnStockActual.getValueFactory().setValue(productoActual.getStockActual());
        spnStockMinimo.getValueFactory().setValue(productoActual.getStockMinimo());
        // SOLO aquí se inicializa el spinner de stock máximo
        Integer stockMaximoReal = productoActual.getStockMaximo();
        if (stockMaximoReal != null && stockMaximoReal > 0) {
            spnStockMaximo.getValueFactory().setValue(stockMaximoReal);
        } else {
            spnStockMaximo.getValueFactory().setValue(100); // Solo si no hay valor real
        }
        txtUbicacion.setText(productoActual.getUbicacion());
        txtLote.setText(productoActual.getLote());

        // Fechas
        dpFechaVencimiento.setValue(productoActual.getFechaVencimiento());
        dpFechaFabricacion.setValue(productoActual.getFechaFabricacion()); // Puede ser null y está bien

        // Configuración
        chkActivo.setSelected(productoActual.getActivo());
        chkRequiereReceta.setSelected(productoActual.getRequiereReceta());
        chkControlado.setSelected(productoActual.getEsControlado());

        calcularMargen();
        // Mostrar margen en campo txtMargen si existe
        if (productoActual.getMargenGanancia() != null) {
            txtMargen.setText(String.format("%.2f%%", productoActual.getMargenGanancia().doubleValue()));
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        if (validarCampos()) {
            try {
                Producto producto = new Producto();
                // Establecer datos básicos
                producto.setId(modoEdicion && productoActual != null ? productoActual.getId() : null);
                producto.setCodigo(txtCodigo.getText().trim().toUpperCase());
                producto.setNombre(txtNombre.getText().trim());
                producto.setPrincipioActivo(txtPrincipioActivo.getText().trim());
                producto.setConcentracion(txtConcentracion.getText().trim());
                producto.setFormaFarmaceutica(cmbFormaFarmaceutica.getValue());
                producto.setCategoria(cmbCategoria.getValue());
                producto.setDescripcion(txtDescripcion.getText().trim());

                // Datos comerciales
                producto.setPrecioCompra(new BigDecimal(txtPrecioCompra.getText()));
                producto.setPrecioVenta(new BigDecimal(txtPrecioVenta.getText()));
                producto.setLaboratorio(txtLaboratorio.getText().trim());
                if (cmbProveedor.getValue() != null) {
                    producto.setProveedorId(cmbProveedor.getValue().getId());
                }

                // Control de inventario
                int stockAnterior = modoEdicion && productoActual != null ? productoActual.getStockActual() : -1;
                int stockNuevo = spnStockActual.getValue();
                producto.setStockActual(stockNuevo);
                producto.setStockMinimo(spnStockMinimo.getValue());
                producto.setStockMaximo(spnStockMaximo.getValue());
                producto.setUbicacion(txtUbicacion.getText().trim());
                producto.setLote(txtLote.getText().trim());

                // Fechas
                producto.setFechaVencimiento(dpFechaVencimiento.getValue());
                producto.setFechaFabricacion(dpFechaFabricacion.getValue());

                // Configuración
                producto.setActivo(chkActivo.isSelected());
                producto.setRequiereReceta(chkRequiereReceta.isSelected());
                producto.setEsControlado(chkControlado.isSelected());

                // Calcular y guardar margen de ganancia
                try {
                    BigDecimal precioCompra = new BigDecimal(txtPrecioCompra.getText());
                    BigDecimal precioVenta = new BigDecimal(txtPrecioVenta.getText());
                    BigDecimal margen = BigDecimal.ZERO;
                    if (precioCompra.compareTo(BigDecimal.ZERO) > 0) {
                        margen = precioVenta.subtract(precioCompra)
                                .divide(precioCompra, 4, java.math.RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"));
                    }
                    producto.setMargenGanancia(margen);
                } catch (Exception e) {
                    producto.setMargenGanancia(BigDecimal.ZERO);
                }

                // Registrar cambios en historial para todos los campos relevantes ANTES de
                // guardar
                boolean esEdicion = modoEdicion && productoActual != null && productoActual.getId() != null;
                if (esEdicion) {
                    AuditoriaEdicionProducto.registrar(productoActual, producto, stockAnterior, stockNuevo);
                }

                // Guardar producto
                Producto productoGuardado;
                if (esEdicion) {
                    productoGuardado = productoService.actualizar(producto);
                } else {
                    productoGuardado = productoService.guardar(producto);
                }

                if (productoGuardado != null) {
                    // Refrescar desde la base de datos para asegurar datos actualizados
                    Producto productoRefrescado = productoService.obtenerPorId(productoGuardado.getId());
                    // ...existing code...

                    // Notificar éxito
                    String mensaje = esEdicion ? "Producto actualizado correctamente" : "Producto creado correctamente";
                    showInfo("Éxito", mensaje);

                    // Notificar al controlador padre si existe callback
                    if (onProductoGuardado != null) {
                        onProductoGuardado.accept(productoRefrescado != null ? productoRefrescado : productoGuardado);
                    }

                    // Lanzar evento de actualización para refresco inmediato
                    com.farmaciavictoria.proyectopharmavictoria.events.SystemEventManager.getInstance().publishEvent(
                            esEdicion
                                    ? com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent.EventType.PRODUCTO_ACTUALIZADO
                                    : com.farmaciavictoria.proyectopharmavictoria.events.SystemEvent.EventType.PRODUCTO_CREADO,
                            "Producto guardado/actualizado",
                            productoGuardado.getId());

                    // Cerrar el formulario automáticamente
                    Stage stage = (Stage) btnGuardar.getScene().getWindow();
                    if (stage != null) {
                        stage.close();
                    }
                } else {
                    showError("Error", "No se pudo guardar el producto en la base de datos.");
                }

            } catch (Exception e) {
                showError("Error", "No se pudo guardar el producto: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Limpiar Campos");
        alert.setHeaderText("¿Limpiar todos los campos?");
        alert.setContentText("Esta acción borrará toda la información ingresada.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            limpiarCampos();
        }
    }

    // Método onCancelar eliminado
    // Método onCerrar eliminado si no se usa

    private void limpiarCampos() {
        // Información básica
        txtCodigo.clear();
        txtNombre.clear();
        txtPrincipioActivo.clear();
        txtConcentracion.clear();
        cmbFormaFarmaceutica.setValue(null);
        cmbCategoria.setValue(null);
        txtDescripcion.clear();

        // Información comercial
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtMargen.setText("0.00%");
        cmbProveedor.setValue(null);
        txtLaboratorio.clear();

        // Control de inventario
        spnStockActual.getValueFactory().setValue(0);
        spnStockMinimo.getValueFactory().setValue(1);
        spnStockMaximo.getValueFactory().setValue(100);
        txtUbicacion.clear();
        txtLote.clear();
        dpFechaVencimiento.setValue(null);
        dpFechaFabricacion.setValue(null);

        // Configuración
        chkActivo.setSelected(true);
        chkRequiereReceta.setSelected(false);
        chkControlado.setSelected(false);

        // Remover estilos de error
        limpiarEstilosValidacion();
    }

    private boolean validarCampos() {
        boolean valido = true;
        limpiarEstilosValidacion();

        // Código de producto obligatorio
        if (txtCodigo.getText().trim().isEmpty()) {
            marcarError(txtCodigo);
            valido = false;
        }
        // Nombre de producto obligatorio
        if (txtNombre.getText().trim().isEmpty()) {
            marcarError(txtNombre);
            valido = false;
        }
        // Forma Farmacéutica NO obligatoria
        // Categoría obligatoria
        if (cmbCategoria.getValue() == null) {
            marcarError(cmbCategoria);
            valido = false;
        }
        // Precio de compra obligatorio
        if (txtPrecioCompra.getText().trim().isEmpty() || !isValidDecimal(txtPrecioCompra.getText())) {
            marcarError(txtPrecioCompra);
            valido = false;
        }
        // Precio de venta obligatorio
        if (txtPrecioVenta.getText().trim().isEmpty() || !isValidDecimal(txtPrecioVenta.getText())) {
            marcarError(txtPrecioVenta);
            valido = false;
        }
        // Proveedor obligatorio
        if (cmbProveedor.getValue() == null) {
            marcarError(cmbProveedor);
            valido = false;
        }
        // Stock actual obligatorio
        if (spnStockActual.getValue() == null || spnStockActual.getValue() <= 0) {
            marcarError(spnStockActual);
            valido = false;
        }
        // Stock mínimo es opcional, no marcar error visual ni validar como obligatorio
        // Fecha de vencimiento obligatoria
        if (dpFechaVencimiento.getValue() == null) {
            marcarError(dpFechaVencimiento);
            valido = false;
        }

        // Validar que precio de venta sea mayor a precio de compra
        try {
            BigDecimal precioCompra = new BigDecimal(txtPrecioCompra.getText());
            BigDecimal precioVenta = new BigDecimal(txtPrecioVenta.getText());

            if (precioVenta.compareTo(precioCompra) <= 0) {
                marcarError(txtPrecioVenta);
                showWarning("Validación", "El precio de venta debe ser mayor al precio de compra.");
                valido = false;
            }
        } catch (NumberFormatException e) {
            // Ya se validó antes
        }

        // Validar fechas lógicas
        if (dpFechaFabricacion.getValue() != null && dpFechaVencimiento.getValue() != null) {
            if (dpFechaFabricacion.getValue().isAfter(dpFechaVencimiento.getValue())) {
                marcarError(dpFechaVencimiento);
                showWarning("Validación", "La fecha de vencimiento debe ser posterior a la fecha de fabricación.");
                valido = false;
            }
        }

        if (!valido) {
            showWarning("Campos requeridos", "Por favor completa todos los campos obligatorios marcados con *");
        }

        return valido;
    }

    private boolean isValidDecimal(String text) {
        try {
            BigDecimal value = new BigDecimal(text);
            return value.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void marcarError(Control control) {
        control.getStyleClass().add("field-error");
    }

    private void limpiarEstilosValidacion() {
        txtCodigo.getStyleClass().removeAll("field-error", "field-success");
        txtNombre.getStyleClass().removeAll("field-error", "field-success");
        cmbCategoria.getStyleClass().removeAll("field-error", "field-success");
        txtPrecioVenta.getStyleClass().removeAll("field-error", "field-success");
        cmbProveedor.getStyleClass().removeAll("field-error", "field-success");
        spnStockActual.getStyleClass().removeAll("field-error", "field-success");
        // spnStockMinimo no se limpia porque es opcional
        dpFechaVencimiento.getStyleClass().removeAll("field-error", "field-success");
        // Los campos opcionales NO se limpian porque nunca deben marcar error
    }

    // Método cerrarVentana eliminado completamente

    // Setter para el callback
    public void setOnProductoGuardado(Consumer<Producto> callback) {
        this.onProductoGuardado = callback;
    }

    // Métodos de utilidad para mostrar alertas
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void generarCodigoAutomatico() {
        try {
            CategoriaProducto categoria = cmbCategoria.getValue();
            if (categoria == null) {
                showWarning("⚠️ Selecciona una categoría",
                        "Debes seleccionar una categoría antes de generar el código.");
                return;
            }

            String nuevoCodigo = codigoGenerator.generarSiguienteCodigo(categoria.name());
            txtCodigo.setText(nuevoCodigo);

            showInfo("Código generado", "Código generado automáticamente: " + nuevoCodigo);

        } catch (Exception e) {
            showError("Error", "No se pudo generar el código automáticamente: " + e.getMessage());
        }
    }

    // Sugiere un código basado en el nombre y categoría
    private void sugerirCodigo() {
        try {
            String nombre = txtNombre.getText();
            CategoriaProducto categoria = cmbCategoria.getValue();

            if (nombre != null && !nombre.trim().isEmpty()) {
                String categoriaStr = categoria != null ? categoria.name() : null;
                String codigoSugerido = codigoGenerator.sugerirCodigoPorNombre(nombre, categoriaStr);

                // Solo sugerir si el campo está vacío
                if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
                    txtCodigo.setText(codigoSugerido);
                }
            }
        } catch (Exception e) {
            // Error silencioso para sugerencias
        }
    }

    // Valida el código ingresado
    private void validarCodigo(String codigo) {
        if (lblValidacionCodigo == null)
            return; // Si no existe el label, no validar

        if (codigo == null || codigo.trim().isEmpty()) {
            lblValidacionCodigo.setText("");
            lblValidacionCodigo.setStyle("");
            return;
        }

        try {
            boolean existe = codigoGenerator.codigoExiste(codigo.trim());

            if (existe && !modoEdicion) {
                lblValidacionCodigo.setText("Este código ya existe");
                lblValidacionCodigo.setStyle("-fx-text-fill: red;");
                btnGuardar.setDisable(true);
            } else if (!existe) {
                lblValidacionCodigo.setText("Código disponible");
                lblValidacionCodigo.setStyle("-fx-text-fill: green;");
                btnGuardar.setDisable(false);
            } else {
                // Editando producto existente
                lblValidacionCodigo.setText("ℹ️ Código actual");
                lblValidacionCodigo.setStyle("-fx-text-fill: blue;");
                btnGuardar.setDisable(false);
            }

        } catch (Exception e) {
            lblValidacionCodigo.setText("⚠️ Error al validar");
            lblValidacionCodigo.setStyle("-fx-text-fill: orange;");
        }
    }

    // 📋 Actualiza la información de la categoría seleccionada
    private void actualizarInfoCategoria() {
        if (lblUltimoCodigo == null)
            return; // Si no existe el label, no actualizar

        CategoriaProducto categoria = cmbCategoria.getValue();
        if (categoria != null) {
            try {
                String ultimoCodigo = codigoGenerator.obtenerUltimoCodigo(categoria.name());
                if (ultimoCodigo != null) {
                    lblUltimoCodigo.setText("Último código: " + ultimoCodigo);
                } else {
                    lblUltimoCodigo.setText("Categoría nueva (sin códigos previos)");
                }
            } catch (Exception e) {
                lblUltimoCodigo.setText("Error al obtener información");
            }
        } else {
            lblUltimoCodigo.setText("");
        }
    }
}