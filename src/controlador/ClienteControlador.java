
package controlador;

import dao.ClienteDAO;
import modelo.Cliente;
import java.util.List;

/**
 * Controlador para la lógica de Clientes (CRUD).
 */
public class ClienteControlador {

    private final ClienteDAO dao;

    public ClienteControlador() {
        this.dao = new ClienteDAO();
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
        // (Validaciones podrían ir aquí, ej. verificar que el email no sea nulo)
        return dao.agregar(c);
    }

    /**
     * Llama al DAO para actualizar un cliente.
     */
    public boolean actualizarCliente(Cliente c) {
        return dao.actualizar(c);
    }
}
