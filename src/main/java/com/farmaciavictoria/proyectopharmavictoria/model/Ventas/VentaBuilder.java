package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.model.Sucursal;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;

public class VentaBuilder {
    private final Venta venta;

    public VentaBuilder() {
        this.venta = new Venta();
    }

    public VentaBuilder conId(int id) {
        venta.setId(id);
        return this;
    }
    public VentaBuilder conCliente(Cliente cliente) {
        venta.setCliente(cliente);
        return this;
    }
    public VentaBuilder conUsuario(Usuario usuario) {
        venta.setUsuario(usuario);
        return this;
    }
    public VentaBuilder conSucursal(Sucursal sucursal) {
        venta.setSucursal(sucursal);
        return this;
    }
    public VentaBuilder conDetalles(List<DetalleVenta> detalles) {
        venta.setDetalles(detalles);
        return this;
    }
    public VentaBuilder conComprobante(Comprobante comprobante) {
        venta.setComprobante(comprobante);
        return this;
    }
    public VentaBuilder conSubtotal(BigDecimal subtotal) {
        venta.setSubtotal(subtotal);
        return this;
    }
    public VentaBuilder conDescuentoMonto(BigDecimal descuento) {
        venta.setDescuentoMonto(descuento);
        return this;
    }
    public VentaBuilder conIgvMonto(BigDecimal igv) {
        venta.setIgvMonto(igv);
        return this;
    }
    public VentaBuilder conTotal(BigDecimal total) {
        venta.setTotal(total);
        return this;
    }
    public VentaBuilder conTipoPago(String tipoPago) {
        venta.setTipoPago(tipoPago);
        return this;
    }
    public VentaBuilder conTipoComprobante(String tipoComprobante) {
        venta.setTipoComprobante(tipoComprobante);
        return this;
    }
    public VentaBuilder conNumeroBoleta(String numeroBoleta) {
        venta.setNumeroBoleta(numeroBoleta);
        return this;
    }
    public VentaBuilder conSerie(String serie) {
        venta.setSerie(serie);
        return this;
    }
    public VentaBuilder conFechaVenta(LocalDateTime fechaVenta) {
        venta.setFechaVenta(fechaVenta);
        return this;
    }
    public VentaBuilder conEstado(String estado) {
        venta.setEstado(estado);
        return this;
    }
    public VentaBuilder conObservaciones(String observaciones) {
        venta.setObservaciones(observaciones);
        return this;
    }
    public VentaBuilder conCreatedAt(LocalDateTime createdAt) {
        venta.setCreatedAt(createdAt);
        return this;
    }
    public VentaBuilder conUpdatedAt(LocalDateTime updatedAt) {
        venta.setUpdatedAt(updatedAt);
        return this;
    }
    public Venta build() {
        return venta;
    }
}