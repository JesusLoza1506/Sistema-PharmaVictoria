package com.farmaciavictoria.proyectopharmavictoria.model.Inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Producto que corresponde al esquema de base de datos actual
 */
public class Producto {
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String principioActivo;
    private String concentracion;
    private FormaFarmaceutica formaFarmaceutica;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal margenGanancia; // Columna calculada
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer proveedorId;
    private String proveedorNombre; // Nombre del proveedor (solo lectura)
    private CategoriaProducto categoria;
    private String ubicacion;
    private String lote;
    private LocalDate fechaVencimiento;
    private String laboratorio; // Campo solo para interfaz
    private LocalDate fechaFabricacion; // Campo solo para interfaz
    private javafx.beans.property.BooleanProperty requiereReceta = new javafx.beans.property.SimpleBooleanProperty(
            false);
    private javafx.beans.property.BooleanProperty esControlado = new javafx.beans.property.SimpleBooleanProperty(false);
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Producto() {
        this.activo = true;
        this.requiereReceta.set(false);
        this.esControlado.set(false);
        this.stockActual = 0;
        this.stockMinimo = 5;
        this.stockMaximo = 100;
        this.formaFarmaceutica = FormaFarmaceutica.TABLETA;
        this.categoria = CategoriaProducto.OTROS;
    }

    // Constructor completo
    public Producto(String codigo, String nombre, String descripcion, String principioActivo,
            String concentracion, FormaFarmaceutica formaFarmaceutica, BigDecimal precioCompra,
            BigDecimal precioVenta, Integer stockActual, Integer stockMinimo, Integer stockMaximo,
            Integer proveedorId, CategoriaProducto categoria, String ubicacion, String lote,
            LocalDate fechaVencimiento, Boolean requiereReceta, Boolean esControlado) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.principioActivo = principioActivo;
        this.concentracion = concentracion;
        this.formaFarmaceutica = formaFarmaceutica;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.proveedorId = proveedorId;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.lote = lote;
        this.fechaVencimiento = fechaVencimiento;
        this.requiereReceta.set(requiereReceta != null ? requiereReceta : false);
        this.esControlado.set(esControlado != null ? esControlado : false);
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrincipioActivo() {
        return principioActivo;
    }

    public void setPrincipioActivo(String principioActivo) {
        this.principioActivo = principioActivo;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public FormaFarmaceutica getFormaFarmaceutica() {
        return formaFarmaceutica;
    }

    public void setFormaFarmaceutica(FormaFarmaceutica formaFarmaceutica) {
        this.formaFarmaceutica = formaFarmaceutica;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getMargenGanancia() {
        return margenGanancia;
    }

    public void setMargenGanancia(BigDecimal margenGanancia) {
        this.margenGanancia = margenGanancia;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public LocalDate getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(LocalDate fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }

    public Boolean getRequiereReceta() {
        return requiereReceta.get();
    }

    public void setRequiereReceta(Boolean requiereReceta) {
        this.requiereReceta.set(requiereReceta != null ? requiereReceta : false);
    }

    public javafx.beans.property.BooleanProperty requiereRecetaProperty() {
        return requiereReceta;
    }

    // Eliminar método duplicado y corregir asignación

    public Boolean getEsControlado() {
        return esControlado.get();
    }

    public void setEsControlado(Boolean esControlado) {
        this.esControlado.set(esControlado != null ? esControlado : false);
    }

    public javafx.beans.property.BooleanProperty esControladoProperty() {
        return esControlado;
    }

    // Eliminar método duplicado y corregir asignación

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos de utilidad
    public boolean isStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }

    public boolean isProximoVencer() {
        if (fechaVencimiento == null)
            return false;
        return fechaVencimiento.isBefore(LocalDate.now().plusDays(30));
    }

    public boolean isVencido() {
        if (fechaVencimiento == null)
            return false;
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    public String getCodigoNombre() {
        return codigo + " - " + nombre;
    }

    @Override
    public String toString() {
        return getCodigoNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Producto producto = (Producto) obj;
        // Compara código, nombre y stock para detectar cambios relevantes en la tabla
        if (codigo != null ? !codigo.equals(producto.codigo) : producto.codigo != null)
            return false;
        if (nombre != null ? !nombre.equals(producto.nombre) : producto.nombre != null)
            return false;
        if (stockActual != null ? !stockActual.equals(producto.stockActual) : producto.stockActual != null)
            return false;
        if (stockMaximo != null ? !stockMaximo.equals(producto.stockMaximo) : producto.stockMaximo != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
}