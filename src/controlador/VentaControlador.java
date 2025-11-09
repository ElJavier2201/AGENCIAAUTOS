package controlador;

import dao.VentaDAO;
import modelo.Venta;

import java.util.List;

/**
 * Controlador para la lógica de registrar Ventas.
 */
public class VentaControlador {

    private final VentaDAO dao;

    public VentaControlador() {
        this.dao = new VentaDAO();
    }

    /**
     * Llama al DAO para registrar la venta (la cual incluye la transacción).
     * Devuelve el ID de la venta generada.
     */
    public int registrarVenta(Venta venta) {
        // Validaciones de negocio
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser null");
        }

        if (venta.getIdCliente() <= 0) {
            throw new IllegalArgumentException("La venta debe tener un cliente válido");
        }

        if (venta.getIdVehiculo() <= 0) {
            throw new IllegalArgumentException("La venta debe tener un vehículo válido");
        }

        if (venta.getPrecioFinal() <= 0) {
            throw new IllegalArgumentException("El precio final debe ser mayor a cero");
        }

        // ✅ NUEVA VALIDACIÓN: Si hay financiamiento, validar datos
        if (venta.getPlazoMeses() > 0) {
            if (venta.getEnganche() < 0) {
                throw new IllegalArgumentException("El enganche no puede ser negativo");
            }

            if (venta.getEnganche() >= venta.getPrecioFinal()) {
                throw new IllegalArgumentException("El enganche debe ser menor al precio final");
            }

            if (venta.getTasaInteres() < 0 || venta.getTasaInteres() > 100) {
                throw new IllegalArgumentException("La tasa de interés debe estar entre 0 y 100");
            }
        }

        return dao.registrarVenta(venta);
    }

    /**
     * Obtiene la lista de ventas que tienen un financiamiento activo.
     * (Para el panel del GERENTE)
     */
    public List<Venta> listarVentasConFinanciamiento() {
        return dao.listarVentasConFinanciamiento();
    }

    /**
     * Obtiene la lista de ventas con financiamiento de UN CLIENTE ESPECÍFICO.
     * (Para el portal del CLIENTE)
     */
    public List<Venta> listarVentasConFinanciamiento(int idCliente) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        return dao.listarVentasConFinanciamiento(idCliente);
    }
}
