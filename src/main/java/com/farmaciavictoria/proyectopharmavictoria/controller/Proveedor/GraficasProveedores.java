package com.farmaciavictoria.proyectopharmavictoria.controller.Proveedor;

import com.farmaciavictoria.proyectopharmavictoria.config.ServiceContainer;
import com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.List;

final class GraficasProveedores {
    private GraficasProveedores() {
    }

    static void cargarTop(BarChart<String, Number> barChartTopProveedores) {
        if (barChartTopProveedores == null)
            return;
        barChartTopProveedores.getData().clear();
        barChartTopProveedores.setLegendVisible(false);
        barChartTopProveedores.setAnimated(false);
        barChartTopProveedores.getStylesheets()
                .add(GraficasProveedores.class.getResource("/css/Proveedor/barChart-custom.css").toExternalForm());

        // Configurar el eje Y para mostrar solo enteros
        if (barChartTopProveedores.getYAxis() instanceof javafx.scene.chart.NumberAxis yAxis) {
            yAxis.setTickUnit(1);
            yAxis.setMinorTickCount(0);
            yAxis.setAutoRanging(true);
            yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.valueOf(object.intValue());
                }

                @Override
                public Number fromString(String string) {
                    try {
                        return Integer.parseInt(string);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
        }

        List<com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto> productos = ServiceContainer
                .getInstance().getProductoService().obtenerTodos();
        List<com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor> proveedoresActivos = ServiceContainer
                .getInstance().getProveedorService().obtenerTodos();
        java.util.Set<Integer> proveedoresValidos = new java.util.HashSet<>();
        java.util.Map<Integer, String> proveedorNombreMap = new java.util.HashMap<>();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Proveedor.Proveedor proveedor : proveedoresActivos) {
            proveedoresValidos.add(proveedor.getId());
            proveedorNombreMap.put(proveedor.getId(), proveedor.getRazonSocial());
        }
        java.util.Map<Integer, Integer> proveedorProductoCount = new java.util.HashMap<>();
        for (com.farmaciavictoria.proyectopharmavictoria.model.Inventario.Producto prod : productos) {
            Integer proveedorId = prod.getProveedorId();
            if (proveedorId != null && proveedoresValidos.contains(proveedorId)) {
                proveedorProductoCount.put(proveedorId, proveedorProductoCount.getOrDefault(proveedorId, 0) + 1);
            }
        }
        // Obtener top 3 proveedores
        java.util.List<java.util.Map.Entry<Integer, Integer>> top = proveedorProductoCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Productos asociados");
        // Añadir los proveedores reales
        for (java.util.Map.Entry<Integer, Integer> entry : top) {
            String nombre = proveedorNombreMap.get(entry.getKey());
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>(
                    nombre != null ? nombre : "Proveedor " + entry.getKey(), entry.getValue());
            series.getData().add(data);
        }
        // Si hay menos de 3, rellenar con barras vacías
        int faltantes = 3 - top.size();
        for (int i = 0; i < faltantes; i++) {
            javafx.scene.chart.XYChart.Data<String, Number> data = new javafx.scene.chart.XYChart.Data<>("Sin datos",
                    0);
            series.getData().add(data);
        }
        barChartTopProveedores.getData().add(series);
        // Etiquetas de valor sobre cada barra
        Platform.runLater(() -> {
            for (javafx.scene.chart.XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    final Label label = new Label(
                            data.getYValue().intValue() > 0 ? String.valueOf(data.getYValue()) : "");
                    label.getStyleClass().add("bar-value-label");
                    label.setStyle(
                            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-background-color: transparent;");
                    data.getNode().parentProperty().addListener((obs, oldParent, newParent) -> {
                        if (newParent != null) {
                            ((javafx.scene.layout.StackPane) data.getNode()).getChildren().add(label);
                        }
                    });
                }
            }
        });
    }

    static void cargarEstado(PieChart pieChartEstadoProveedores, List<Proveedor> proveedoresOriginales) {
        if (pieChartEstadoProveedores == null)
            return;
        pieChartEstadoProveedores.getData().clear();
        long activos = proveedoresOriginales.stream().filter(Proveedor::getActivo).count();
        long inactivos = proveedoresOriginales.size() - activos;
        javafx.scene.chart.PieChart.Data activosData = new javafx.scene.chart.PieChart.Data("Activos", activos);
        javafx.scene.chart.PieChart.Data inactivosData = new javafx.scene.chart.PieChart.Data("Inactivos", inactivos);
        pieChartEstadoProveedores.getData().addAll(activosData, inactivosData);
    }
}
