package com.farmaciavictoria.proyectopharmavictoria;

import com.farmaciavictoria.proyectopharmavictoria.config.DatabaseConfig;
import com.farmaciavictoria.proyectopharmavictoria.util.DatabaseUpdater;
import javafx.application.Application;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;

public class PharmavictoriaApplication extends Application {
    // Ejemplo de método para mapear datos desde la base de datos a los modelos
    // Este método es solo ilustrativo, debes adaptarlo a tu estructura y consultas
    // reales
    public static Venta obtenerVentaDesdeDB(int idVenta) {
        Venta venta = new Venta();
        // Aquí deberías hacer la consulta a la tabla ventas y asignar los valores
        // Ejemplo:
        venta.tipo_comprobante = 1; // 1=Factura venta.serie = "F001";
        venta.numero = idVenta;
        venta.fecha_emision = "2025-10-25"; // Debe venir de la DB
        venta.total_venta = 100.00; // Debe venir de la DB
        venta.total_igv = 18.00; // Debe venir de la DB
        venta.total_gravada = 82.00; // Debe venir de la DB
        return venta;
    }

    public static Cliente obtenerClienteDesdeDB(int idCliente) {
        Cliente cliente = new Cliente();
        // Consulta a la tabla clientes y asignar valores
        cliente.tipo_documento = "6"; // 6=RUC, 1=DNI
        cliente.numero_documento = "20100070970"; // Debe venir de la DB
        cliente.razon_social = "Banco de la Nación"; // Debe venir de la DB
        cliente.direccion = "Av. Principal 123"; // Debe venir de la DB
        return cliente;
    }

    public static List<DetalleVenta> obtenerDetallesDesdeDB(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        // Consulta a la tabla detalle_ventas y productos
        // Ejemplo de un solo producto:
        DetalleVenta item = new DetalleVenta();
        item.unidad_de_medida = "NIU";
        item.codigo_producto = "P001";
        item.descripcion = "Paracetamol 500mg";
        item.cantidad = 2;
        item.valor_unitario = 41.00;
        item.precio_unitario = 48.38;
        item.subtotal = 82.00;
        item.tipo_de_igv = 1;
        item.igv = 18.00;
        item.total = 100.00;
        detalles.add(item);
        // Repite para cada producto en la venta
        return detalles;
    }

    // Ejemplo de método para enviar el JSON de factura a la API de NubeFacT
    public static String enviarFacturaNubeFact(String jsonFactura, String apiUrl, String apiToken) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", apiToken)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonFactura))
                    .build();
            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Procesar respuesta exitosa (XML, CDR, etc.)
                return response.body();
            } else {
                // Manejo de error
                System.err.println("Error al enviar factura: " + response.statusCode());
                System.err.println(response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String APP_TITLE = "PharmaVictoria";
    private static final String APP_VERSION = "v1.0";
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            configurePrimaryStage(primaryStage);
            if (!verifyDatabaseConnection()) {
                showDatabaseErrorAndExit();
                return;
            }
            DatabaseUpdater.updateProductsTable();
            DatabaseUpdater.updateProveedoresTable();
            DatabaseUpdater.ensureUsuarioHistorialTables();
            Parent root = loadLoginScreenWithFade(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo iniciar la aplicación:");
            e.printStackTrace();
            showErrorAndExit("Error Fatal", "No se pudo iniciar la aplicación:\n" + e.getMessage());
        }
    }

    // Modelo básico para Cliente
    public static class Cliente {
        public String tipo_documento; // "1"=DNI, "6"=RUC
        public String numero_documento;
        public String razon_social;
        public String direccion;
    }

    // Modelo básico para DetalleVenta
    public static class DetalleVenta {
        public String unidad_de_medida; // "NIU"=unidad
        public String codigo_producto;
        public String descripcion;
        public double cantidad;
        public double valor_unitario;
        public double precio_unitario;
        public double subtotal;
        public double tipo_de_igv; // "1"=gravado
        public double igv;
        public double total;
    }

    // Modelo básico para Venta
    public static class Venta {
        public int tipo_comprobante; // 1=Factura, 2=Boleta, 3=Nota de crédito, 4=Nota de débito
        public String serie;
        public int numero;
        public String fecha_emision; // "2025-10-25"
        public double total_venta;
        public double total_igv;
        public double total_gravada;
    }

    // Método para construir el JSON de la factura
    public static String construirJsonFactura(Venta venta, Cliente cliente, List<DetalleVenta> detalles) {
        Gson gson = new Gson();
        var factura = new java.util.LinkedHashMap<String, Object>();
        factura.put("operacion", "generar_comprobante");
        factura.put("tipo_de_comprobante", 1);
        factura.put("serie", "FFF1");
        factura.put("numero", 1);
        factura.put("sunat_transaction", 1);
        factura.put("cliente_tipo_de_documento", 6);
        factura.put("cliente_numero_de_documento", "20600695771");
        factura.put("cliente_denominacion", "NUBEFACT SA");
        factura.put("cliente_direccion", "CALLE LIBERTAD 116 MIRAFLORES - LIMA - PERU");
        factura.put("cliente_email", "tucliente@gmail.com");
        factura.put("cliente_email_1", "");
        factura.put("cliente_email_2", "");
        factura.put("fecha_de_emision", venta.fecha_emision);
        factura.put("fecha_de_vencimiento", "");
        factura.put("moneda", 1);
        factura.put("tipo_de_cambio", "");
        factura.put("porcentaje_de_igv", 18.00);
        factura.put("descuento_global", "");
        factura.put("total_descuento", "");
        factura.put("total_anticipo", "");
        factura.put("total_gravada", 600);
        factura.put("total_inafecta", "");
        factura.put("total_exonerada", "");
        factura.put("total_igv", 108);
        factura.put("total_gratuita", "");
        factura.put("total_otros_cargos", "");
        factura.put("total", 708);
        factura.put("percepcion_tipo", "");
        factura.put("percepcion_base_imponible", "");
        factura.put("total_percepcion", "");
        factura.put("total_incluido_percepcion", "");
        factura.put("retencion_tipo", "");
        factura.put("retencion_base_imponible", "");
        factura.put("total_retencion", "");
        factura.put("total_impuestos_bolsas", "");
        factura.put("detraccion", false);
        factura.put("observaciones", "");
        factura.put("documento_que_se_modifica_tipo", "");
        factura.put("documento_que_se_modifica_serie", "");
        factura.put("documento_que_se_modifica_numero", "");
        factura.put("tipo_de_nota_de_credito", "");
        factura.put("tipo_de_nota_de_debito", "");
        factura.put("enviar_automaticamente_a_la_sunat", true);
        factura.put("enviar_automaticamente_al_cliente", false);
        factura.put("condiciones_de_pago", "");
        factura.put("medio_de_pago", "");
        factura.put("placa_vehiculo", "");
        factura.put("orden_compra_servicio", "");
        factura.put("formato_de_pdf", "");
        factura.put("generado_por_contingencia", "");
        factura.put("bienes_region_selva", "");
        factura.put("servicios_region_selva", "");
        // Items
        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
        var item1 = new java.util.LinkedHashMap<String, Object>();
        item1.put("unidad_de_medida", "NIU");
        item1.put("codigo", "001");
        item1.put("codigo_producto_sunat", "10000000");
        item1.put("descripcion", "DETALLE DEL PRODUCTO");
        item1.put("cantidad", 1);
        item1.put("valor_unitario", 500);
        item1.put("precio_unitario", 590);
        item1.put("descuento", "");
        item1.put("subtotal", 500);
        item1.put("tipo_de_igv", 1);
        item1.put("igv", 90);
        item1.put("total", 590);
        item1.put("anticipo_regularizacion", false);
        item1.put("anticipo_documento_serie", "");
        item1.put("anticipo_documento_numero", "");
        items.add(item1);
        var item2 = new java.util.LinkedHashMap<String, Object>();
        item2.put("unidad_de_medida", "ZZ");
        item2.put("codigo", "001");
        item2.put("codigo_producto_sunat", "20000000");
        item2.put("descripcion", "DETALLE DEL SERVICIO");
        item2.put("cantidad", 5);
        item2.put("valor_unitario", 20);
        item2.put("precio_unitario", 23.60);
        item2.put("descuento", "");
        item2.put("subtotal", 100);
        item2.put("tipo_de_igv", 1);
        item2.put("igv", 18);
        item2.put("total", 118);
        item2.put("anticipo_regularizacion", false);
        item2.put("anticipo_documento_serie", "");
        item2.put("anticipo_documento_numero", "");
        items.add(item2);
        factura.put("items", items);
        // Guias
        java.util.List<java.util.Map<String, Object>> guias = new java.util.ArrayList<>();
        var guia = new java.util.LinkedHashMap<String, Object>();
        guia.put("guia_tipo", 1);
        guia.put("guia_serie_numero", "0001-23");
        guias.add(guia);
        factura.put("guias", guias);
        // Venta al crédito
        java.util.List<java.util.Map<String, Object>> ventaCredito = new java.util.ArrayList<>();
        var cuota1 = new java.util.LinkedHashMap<String, Object>();
        cuota1.put("cuota", 1);
        cuota1.put("fecha_de_pago", "11-03-2021");
        cuota1.put("importe", 600);
        ventaCredito.add(cuota1);
        var cuota2 = new java.util.LinkedHashMap<String, Object>();
        cuota2.put("cuota", 2);
        cuota2.put("fecha_de_pago", "11-04-2021");
        cuota2.put("importe", 100);
        ventaCredito.add(cuota2);
        var cuota3 = new java.util.LinkedHashMap<String, Object>();
        cuota3.put("cuota", 3);
        cuota3.put("fecha_de_pago", "11-05-2021");
        cuota3.put("importe", 8);
        ventaCredito.add(cuota3);
        factura.put("venta_al_credito", ventaCredito);
        return gson.toJson(factura);
    }

    private void configurePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        try {
            primaryStage.getIcons().add(new Image(
                    PharmavictoriaApplication.class.getResourceAsStream("/icons/logofarmacia.png")));
        } catch (Exception e) {
            // Silenciar error de icono
        }
        primaryStage.setOnCloseRequest(event -> {
            closeApplication();
        });
    }

    private boolean verifyDatabaseConnection() {
        try {
            DatabaseConfig.getInstance().getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Parent loadLoginScreenWithFade(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 700);
        String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
        root.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(750), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        return root;
    }

    private void showDatabaseErrorAndExit() {
        Platform.runLater(() -> {
            showErrorAndExit(
                    "Error de Conexión",
                    "No se pudo conectar a la base de datos.\n\n" +
                            "Verifique que:\n" +
                            "• El servidor MySQL esté ejecutándose\n" +
                            "• La base de datos 'pharmavictoria' exista\n" +
                            "• Las credenciales sean correctas\n" +
                            "• El puerto 3306 esté disponible");
        });
    }

    private void showErrorAndExit(String title, String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("PHARMAVICTORIA - Error Fatal");
            alert.setContentText(message);
            alert.showAndWait();
        } finally {
            closeApplication();
        }
    }

    private void closeApplication() {
        try {
            DatabaseConfig.getInstance().close();
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void stop() throws Exception {
        closeApplication();
        super.stop();
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        // Ejemplo de uso de integración NubeFacT (solo para pruebas, puedes moverlo a
        // otro lugar)
        if (args.length > 0 && args[0].equals("test-nubefact")) {

            Venta venta = new Venta();
            venta.tipo_comprobante = 1;
            venta.serie = "FFF1";
            venta.numero = 1;
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            venta.fecha_emision = hoy.format(formatter);
            venta.total_venta = 708.00;
            venta.total_igv = 108.00;
            venta.total_gravada = 600.00;

            Cliente cliente = new Cliente();
            cliente.tipo_documento = "6";
            cliente.numero_documento = "20600695771";
            cliente.razon_social = "NUBEFACT SA";
            cliente.direccion = "CALLE LIBERTAD 116 MIRAFLORES - LIMA - PERU";

            // No se usan los detalles, el JSON se arma directo en construirJsonFactura
            java.util.List<DetalleVenta> detalles = new java.util.ArrayList<>();

            String jsonFactura = construirJsonFactura(venta, cliente, detalles);
            System.out.println("JSON enviado a NubeFacT:\n" + jsonFactura);
            String apiUrl = "https://api.nubefact.com/api/v1/b1f7ac80-5d5e-4fd9-8c8d-7b00c2638da0"; // RUTA REAL
            String apiToken = "3e15b81cab1b46dc9881d4979e343a273d7abde7bb3e406686eb92f84f6bebd1"; // Reemplaza con tu
                                                                                                  // TOKEN

            String respuesta = enviarFacturaNubeFact(jsonFactura, apiUrl, apiToken);
            System.out.println("Respuesta NubeFacT:\n" + respuesta);
        } else {
            Application.launch(PharmavictoriaApplication.class, args);
        }
    }
}