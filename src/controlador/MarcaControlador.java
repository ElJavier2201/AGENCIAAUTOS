package controlador;

import dao.MarcaDAO;
import modelo.Marca;
import java.util.List;
public class MarcaControlador {

    private final MarcaDAO dao;

    public MarcaControlador() {
        this.dao = new MarcaDAO();
    }

    /**
     * Devuelve la lista de todas las marcas.
     */
    public List<Marca> listarMarcas() {
        return dao.listarMarcas();
    }
}
