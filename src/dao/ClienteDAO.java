package dao;

import modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para el CRUD de la tabla 'clientes'.
 */
public class ClienteDAO extends BaseDAO {

    /**
     * Obtiene una lista de todos los clientes.
     */
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY apellido, nombre";

        logQuery("Listar clientes");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

            logger.info("Se obtuvieron {} clientes", lista.size());

        } catch (SQLException e) {
            logError("listar clientes", e);
        }

        return lista;
    }

    /**
     * Agrega un nuevo cliente a la base de datos.
     *  INCLUYE: Validación adicional de unicidad de email
     */
    public boolean agregar(Cliente c) {
        if (existeEmail(c.getEmail())) {
            logger.warn(" Intento de agregar cliente con email duplicado: {}", c.getEmail());
            return false;
        }

        String sql = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion, rfc) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        logger.debug("Agregando cliente: {} {}", c.getNombre(), c.getApellido());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getRfc());

            int rows = ps.executeUpdate();
            logSuccess("Agregar cliente", rows);
            return rows > 0;

        } catch (SQLException e) {
            logError("agregar cliente", e);

            // Mensaje específico para duplicados
            if (e.getErrorCode() == 1062) { // MySQL duplicate key error
                logger.warn(" El email '{}' ya está registrado", c.getEmail());
            }

            return false;
        }
    }
   /**
    Verifica si un email esta registrado
    **/
    private boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar email", e);
        }

        return false;
    }

    /**
     * Actualiza un cliente existente.
     */
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ?, rfc = ? " +
                "WHERE id_cliente = ?";

        logger.debug("Actualizando cliente ID: {}", c.getIdCliente());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getRfc());
            ps.setInt(7, c.getIdCliente());

            int rows = ps.executeUpdate();
            logSuccess("Actualizar cliente", rows);
            return rows > 0;

        } catch (SQLException e) {
            logError("actualizar cliente", e);
            return false;
        }
    }

    /**
     * Helper para convertir un ResultSet en un objeto Cliente.
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setRfc(rs.getString("rfc"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return c;
    }
}