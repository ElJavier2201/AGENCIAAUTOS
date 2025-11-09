package controlador;

import dao.FacturaDAO;
import modelo.Factura;

/**
 * Controlador para la lógica de Facturación.
 * (Simplificado: ahora solo pasa el objeto al DAO)
 */
public class FacturaControlador {

    private final FacturaDAO dao;

    public FacturaControlador() {
        this.dao = new FacturaDAO();
    }

    /**
     * Llama al DAO para guardar una factura ya construida.
     */
    public boolean registrarFactura(Factura factura) {
        if (factura == null) return false;
        return dao.agregarFactura(factura) > 0;
    }
}