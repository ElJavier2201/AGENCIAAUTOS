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
     * --- MODIFICADO: Devuelve el ID de la venta ---
     */
    public int registrarVenta(Venta venta) {
        // (Validaciones podrían ir aquí)
        return dao.registrarVenta(venta);
    }

    /**
     * --- NUEVO MÉTODO ---
     * Obtiene la lista de ventas que tienen un financiamiento activo.
     */
    public List<Venta> listarVentasConFinanciamiento() {
        return dao.listarVentasConFinanciamiento();
    }
}