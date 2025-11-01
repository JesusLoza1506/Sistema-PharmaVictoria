package com.farmaciavictoria.proyectopharmavictoria.util;

/**
 * Utilidades y modelos para comprobantes electrónicos y comunicación con
 * NubeFacT.
 */
public class ComprobanteUtils {
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

    // Método para enviar el JSON de factura a la API de NubeFacT
    public static String enviarFacturaNubeFact(String jsonFactura, String apiUrl, String apiToken) {
        try {
            // Mostrar el JSON que se va a enviar
            System.out.println("[NubeFacT] JSON enviado:");
            System.out.println(jsonFactura);

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
                return response.body();
            } else {
                System.err.println("Error al enviar factura: " + response.statusCode());
                System.err.println(response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
