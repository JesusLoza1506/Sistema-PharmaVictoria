package com.farmaciavictoria.proyectopharmavictoria.reportes.export;

import com.farmaciavictoria.proyectopharmavictoria.reportes.dto.VentaReporteDTO;
import java.util.List;
import java.io.File;

public class ExportadorExcelReporte implements ExportadorReporte {
    @Override
    public void exportar(List<VentaReporteDTO> datos, File destino) {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Ventas");
            int rowIdx = 0;
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("N° Boleta");
            header.createCell(1).setCellValue("Serie");
            header.createCell(2).setCellValue("Fecha");
            header.createCell(3).setCellValue("Cliente");
            header.createCell(4).setCellValue("Vendedor");
            header.createCell(5).setCellValue("Total");
            header.createCell(6).setCellValue("Estado");
            header.createCell(7).setCellValue("Producto");
            header.createCell(8).setCellValue("Cantidad");
            header.createCell(9).setCellValue("Precio Unitario");
            header.createCell(10).setCellValue("Descuento");
            header.createCell(11).setCellValue("Subtotal");
            for (VentaReporteDTO venta : datos) {
                if (venta.getProductos() == null || venta.getProductos().isEmpty()) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(venta.getNumeroBoleta());
                    row.createCell(1).setCellValue(venta.getSerie());
                    row.createCell(2)
                            .setCellValue(venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : "");
                    row.createCell(3).setCellValue(venta.getCliente());
                    row.createCell(4).setCellValue(venta.getVendedor());
                    row.createCell(5).setCellValue(venta.getTotal() != null ? venta.getTotal().doubleValue() : 0);
                    row.createCell(6).setCellValue(venta.getEstado());
                } else {
                    for (com.farmaciavictoria.proyectopharmavictoria.reportes.dto.ProductoDetalleDTO prod : venta
                            .getProductos()) {
                        org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(venta.getNumeroBoleta());
                        row.createCell(1).setCellValue(venta.getSerie());
                        row.createCell(2)
                                .setCellValue(venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : "");
                        row.createCell(3).setCellValue(venta.getCliente());
                        row.createCell(4).setCellValue(venta.getVendedor());
                        row.createCell(5).setCellValue(venta.getTotal() != null ? venta.getTotal().doubleValue() : 0);
                        row.createCell(6).setCellValue(venta.getEstado());
                        row.createCell(7).setCellValue(prod.getNombre());
                        row.createCell(8).setCellValue(prod.getCantidad());
                        row.createCell(9).setCellValue(
                                prod.getPrecioUnitario() != null ? prod.getPrecioUnitario().doubleValue() : 0);
                        row.createCell(10)
                                .setCellValue(prod.getDescuento() != null ? prod.getDescuento().doubleValue() : 0);
                        row.createCell(11)
                                .setCellValue(prod.getSubtotal() != null ? prod.getSubtotal().doubleValue() : 0);
                    }
                }
            }
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(destino)) {
                workbook.write(fos);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
