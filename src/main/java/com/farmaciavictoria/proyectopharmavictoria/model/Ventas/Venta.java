package com.farmaciavictoria.proyectopharmavictoria.model.Ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.model.Cliente.Cliente;
import com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario;
import com.farmaciavictoria.proyectopharmavictoria.model.Sucursal;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;

public class Venta {
    private int id;
    private Cliente cliente;
    private Usuario usuario;
    private Sucursal sucursal;
    private BigDecimal subtotal;
    private BigDecimal descuentoMonto;
    private BigDecimal igvMonto;
    private BigDecimal total;
    private String tipoPago; // EFECTIVO, TARJETA, TRANSFERENCIA, MIXTO
    private String tipoComprobante; // BOLETA, FACTURA
    private String numeroBoleta;
    private String serie;
    private LocalDateTime fechaVenta;
    private String estado; // REALIZADA, ANULADA, PENDIENTE
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DetalleVenta> detalles;
    private Comprobante comprobante;

    // Getters y setters
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public Comprobante getComprobante() { return comprobante; }
    public void setComprobante(Comprobante comprobante) { this.comprobante = comprobante; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDescuentoMonto() { return descuentoMonto; }
    public void setDescuentoMonto(BigDecimal descuentoMonto) { this.descuentoMonto = descuentoMonto; }

    public BigDecimal getIgvMonto() { return igvMonto; }
    public void setIgvMonto(BigDecimal igvMonto) { this.igvMonto = igvMonto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getNumeroBoleta() { return numeroBoleta; }
    public void setNumeroBoleta(String numeroBoleta) { this.numeroBoleta = numeroBoleta; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
