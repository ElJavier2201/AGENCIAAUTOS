package controlador;

import dao.ClienteDAO;
import modelo.Cliente;
import java.util.List;

/**
 * Controlador para la lógica de Clientes (CRUD).
 * --- MODIFICADO: Incluye login ---
 */
public class ClienteControlador {

    private final ClienteDAO dao;

    public ClienteControlador() {
        this.dao = new ClienteDAO();
    }

    /**
     * Llama al DAO para autenticar un cliente.
     */
    public Cliente autenticar(String usuario, String contraseña) {
        if (usuario == null || usuario.trim().isEmpty() || contraseña == null || contraseña.isEmpty()) {
            return null;
        }
        return dao.autenticar(usuario.trim(), contraseña);
    }

    /**
     * Obtiene la lista de todos los clientes.
     */
    public List<Cliente> listarClientes() {
        return dao.listar();
    }

    /**
     * Llama al DAO para agregar un nuevo cliente.
     */
    public boolean agregarCliente(Cliente c) {
        // (Validaciones podrían ir aquí)
        return dao.agregar(c);
    }

    /**
     * Llama al DAO para actualizar un cliente.
     */
    public boolean actualizarCliente(Cliente c) {
        return dao.actualizar(c);
    }
}