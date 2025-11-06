package controlador;

import dao.VendedorDAO;
import modelo.Vendedor;
import java.util.List;

/**
 * Controlador para Vendedores (Login y CRUD).
 */
public class VendedorControlador {
    private final VendedorDAO dao;

    public VendedorControlador() {
        dao = new VendedorDAO();
    }

    public Vendedor login(String usuario, String contraseña) {
        return dao.autenticar(usuario, contraseña);
    }

    public List<Vendedor> listarVendedores() {
        return dao.listar();
    }

    public boolean agregarVendedor(Vendedor v) {
        // (Aquí irían validaciones, ej. que el email sea válido)
        return dao.agregar(v);
    }

    public boolean actualizarVendedor(Vendedor v) {
        return dao.actualizar(v);
    }
}