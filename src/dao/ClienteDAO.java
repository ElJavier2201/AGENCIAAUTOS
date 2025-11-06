package dao;
import modelo.Cliente;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para el CRUD de la tabla 'clientes'.
 */
public class ClienteDAO {

    /**
     * Obtiene una lista de todos los clientes.
     */
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY apellido, nombre";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Agrega un nuevo cliente a la base de datos.
     */
    public boolean agregar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion, rfc) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getRfc());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un cliente existente.
     */
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ?, rfc = ? " +
                "WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getRfc());
            ps.setInt(7, c.getIdCliente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
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
