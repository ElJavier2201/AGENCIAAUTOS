package controlador;

import dao.ReporteDAO;
import modelo.Venta;
import java.util.List;

/**
 * Controlador para la lógica de Reportes.
 */
public class ReporteControlador {

    private final ReporteDAO dao;

    public ReporteControlador() {
        this.dao = new ReporteDAO();
    }

    /**
     * Obtiene la lista detallada de ventas.
     */
    public List<Venta> listarReporteVentas() {
        return dao.listarVentasDetallado();
    }
}