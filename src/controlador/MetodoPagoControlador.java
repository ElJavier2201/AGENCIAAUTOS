
package controlador;
import dao.MetodoPagoDAO;
import modelo.MetodoPago;
import java.util.List;

public class MetodoPagoControlador {
    private final MetodoPagoDAO dao;
    public MetodoPagoControlador() { this.dao = new MetodoPagoDAO(); }
    public List<MetodoPago> listarActivos() { return dao.listarActivos(); }
}
