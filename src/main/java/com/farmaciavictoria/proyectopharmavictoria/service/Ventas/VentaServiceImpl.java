package com.farmaciavictoria.proyectopharmavictoria.service.Ventas;

import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Venta;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepository;
import java.util.List;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.Comprobante;
import com.farmaciavictoria.proyectopharmavictoria.model.Ventas.VentaHistorialCambio;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.DetalleVentaRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.ComprobanteRepository;
import com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaHistorialCambioRepository;

public class VentaServiceImpl implements VentaService {
    public List<Venta> listarVentasPorCliente(int clienteId) {
        if (ventaRepository instanceof com.farmaciavictoria.proyectopharmavictoria.repository.Ventas.VentaRepositoryJdbcImpl jdbcRepo) {
            return jdbcRepo.findByClienteId(clienteId);
        }
        throw new UnsupportedOperationException("El repositorio no soporta búsqueda por clienteId");
    }

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final VentaHistorialCambioRepository historialCambioRepository;
    private final com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository productoRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ComprobanteRepository comprobanteRepository,
            VentaHistorialCambioRepository historialCambioRepository,
            com.farmaciavictoria.proyectopharmavictoria.repository.Inventario.ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.historialCambioRepository = historialCambioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Venta obtenerVentaPorId(int id) {
        return ventaRepository.findById(id);
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public void registrarVenta(Venta venta) {
        try {
            // Registro completo de venta
            // 1. Guardar venta principal
            ventaRepository.save(venta);
            System.out.println("[VENTA] Venta registrada correctamente. ID: " + venta.getId());

            // 2. Guardar detalles de venta (si existen)
            List<DetalleVenta> detalles = venta.getDetalles();
            if (detalles != null) {
                for (DetalleVenta detalle : detalles) {
                    detalle.setVenta(venta);
                    detalleVentaRepository.save(detalle);
                    // Descontar stock del producto vendido
                    if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                        int stockActual = detalle.getProducto().getStockActual() != null
                                ? detalle.getProducto().getStockActual()
                                : 0;
                        int nuevoStock = stockActual - detalle.getCantidad();
                        if (nuevoStock < 0)
                            nuevoStock = 0;
                        productoRepository.updateStock(detalle.getProducto().getId(), nuevoStock);
                    }
                }
            }

            // 3. Guardar comprobante (si existe)
            Comprobante comprobante = venta.getComprobante();
            if (comprobante != null) {
                comprobante.setVenta(venta);
                comprobanteRepository.save(comprobante);
            }

            // 4. Registrar historial de creación
            VentaHistorialCambio historial = new VentaHistorialCambio();
            historial.setVenta(venta);
            historial.setTipoCambio("CREACION");
            historial.setMotivo("Registro de nueva venta");
            historial.setFecha(java.time.LocalDateTime.now());
            // Asignar usuario actual si existe en la venta
            if (venta.getUsuario() != null) {
                historial.setUsuario(venta.getUsuario());
            } else {
                com.farmaciavictoria.proyectopharmavictoria.model.Usuario.Usuario usuarioActual = com.farmaciavictoria.proyectopharmavictoria.service.AuthenticationService
                        .getUsuarioActual();
                historial.setUsuario(usuarioActual);
            }
            historialCambioRepository.save(historial);
            System.out.println("[HISTORIAL VENTA] Historial de venta registrado correctamente. VentaID: "
                    + venta.getId() + ", HistorialID: " + historial.getId());
        } catch (Exception ex) {
            System.err.println("[ERROR VENTA] Error al registrar venta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void actualizarVenta(Venta venta) {
        ventaRepository.update(venta);
    }

    @Override
    public void anularVenta(int id, String motivo, int usuarioId) {
        Venta venta = ventaRepository.findById(id);
        if (venta != null) {
            venta.setEstado("ANULADA");
            ventaRepository.update(venta);

            // Restablecer stock de cada producto vendido
            if (venta.getDetalles() != null) {
                for (com.farmaciavictoria.proyectopharmavictoria.model.Ventas.DetalleVenta detalle : venta
                        .getDetalles()) {
                    com.farmaciavictoria.proyectopharmavictoria.command.RestablecerStockCommand cmd = new com.farmaciavictoria.proyectopharmavictoria.command.RestablecerStockCommand(
                            productoRepository, detalle);
                    cmd.execute();
                }
            }

            // Registrar en historial de cambios
            VentaHistorialCambio historial = new VentaHistorialCambio();
            historial.setVenta(venta);
            historial.setTipoCambio("ANULACION");
            historial.setMotivo(motivo);
            historial.setFecha(java.time.LocalDateTime.now());
            historialCambioRepository.save(historial);
        }
    }
}
