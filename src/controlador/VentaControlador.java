package controlador;

import dao.VentaDAO;
import modelo.Venta;

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
     */
    public boolean registrarVenta(Venta venta) {
        // (Aquí irían validaciones, ej. que el precio final no sea cero)
        return dao.registrarVenta(venta);
    }
}