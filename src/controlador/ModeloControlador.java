package controlador;

import dao.ModeloDAO;
import modelo.Modelo;
import java.util.List;

public class ModeloControlador {

    private final ModeloDAO dao;

    public ModeloControlador() {
        this.dao = new ModeloDAO();
    }

    /**
     * Devuelve la lista de modelos filtrada por una marca.
     */
    public List<Modelo> listarModelosPorMarca(int idMarca) {
        return dao.listarModelosPorMarca(idMarca);
    }
}