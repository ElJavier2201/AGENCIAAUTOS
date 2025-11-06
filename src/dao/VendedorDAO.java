package dao;
import modelo.Vendedor;
import util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'vendedores'.
 * (Versión con contraseñas en TEXTO PLANO - Sin encriptar)
 */
public class VendedorDAO {

    /**
     * Autentica a un vendedor usando su 'usuario' y 'contraseña' en texto plano.
     */
    public Vendedor autenticar(String usuario, String passPlano) {
        // --- LÓGICA DE LOGIN CAMBIADA ---
        // Compara el usuario y la contraseña directamente en la consulta
        String sql = "SELECT * FROM vendedores WHERE usuario = ? AND contraseña = ? AND activo = 1";
        try (Connection conn = ConexionDB.getConexion()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuario);
                ps.setString(2, passPlano); // Envía la contraseña en texto plano

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Si hay un resultado, la contraseña era correcta
                        return mapearVendedor(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al autenticar vendedor: " + e.getMessage());
        }
        return null; // Falla el login
    }

    /**
     * Lista todos los vendedores (activos e inactivos).
     */
    public List<Vendedor> listar() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendedores ORDER BY apellido, nombre";
        try (Connection conn = ConexionDB.getConexion()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearVendedor(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar vendedores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Agrega un nuevo vendedor (contraseña en texto plano).
     */
    public boolean agregar(Vendedor v) {
        String sql = "INSERT INTO vendedores (nombre, apellido, telefono, email, fecha_contratacion, comision_porcentaje, activo, usuario, contraseña, rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, v.getNombre());
                ps.setString(2, v.getApellido());
                ps.setString(3, v.getTelefono());
                ps.setString(4, v.getEmail());
                ps.setDate(5, v.getFechaContratacion());
                ps.setDouble(6, v.getComisionPorcentaje());
                ps.setBoolean(7, v.isActivo());
                ps.setString(8, v.getUsuario());
                ps.setString(9, v.getContraseña()); // --- CAMBIO: Guardar texto plano
                ps.setString(10, v.getRol());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al agregar vendedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un vendedor. Si la contraseña viene vacía, no la actualiza.
     */
    public boolean actualizar(Vendedor v) {
        String plainPassword = v.getContraseña();
        boolean updatePassword = (plainPassword != null && !plainPassword.isEmpty());
        String sql;

        if (updatePassword) {
            sql = "UPDATE vendedores SET nombre = ?, apellido = ?, telefono = ?, email = ?, fecha_contratacion = ?, " +
                    "comision_porcentaje = ?, activo = ?, usuario = ?, contraseña = ?, rol = ? " +
                    "WHERE id_vendedor = ?";
        } else {
            sql = "UPDATE vendedores SET nombre = ?, apellido = ?, telefono = ?, email = ?, fecha_contratacion = ?, " +
                    "comision_porcentaje = ?, activo = ?, usuario = ?, rol = ? " +
                    "WHERE id_vendedor = ?";
        }

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1; // Contador de parámetros
            ps.setString(i++, v.getNombre());
            ps.setString(i++, v.getApellido());
            ps.setString(i++, v.getTelefono());
            ps.setString(i++, v.getEmail());
            ps.setDate(i++, v.getFechaContratacion());
            ps.setDouble(i++, v.getComisionPorcentaje());
            ps.setBoolean(i++, v.isActivo());
            ps.setString(i++, v.getUsuario());

            if (updatePassword) {
                ps.setString(i++, plainPassword); // --- CAMBIO: Guardar texto plano
            }

            ps.setString(i++, v.getRol());
            ps.setInt(i++, v.getIdVendedor());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar vendedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper para convertir un ResultSet en un objeto Vendedor.
     */
    private Vendedor mapearVendedor(ResultSet rs) throws SQLException {
        Vendedor v = new Vendedor();
        v.setIdVendedor(rs.getInt("id_vendedor"));
        v.setNombre(rs.getString("nombre"));
        v.setApellido(rs.getString("apellido"));
        v.setTelefono(rs.getString("telefono"));
        v.setEmail(rs.getString("email"));
        v.setFechaContratacion(rs.getDate("fecha_contratacion"));
        v.setComisionPorcentaje(rs.getDouble("comision_porcentaje"));
        v.setActivo(rs.getBoolean("activo"));
        v.setUsuario(rs.getString("usuario"));
        v.setContraseña(rs.getString("contraseña")); // Guarda el texto plano
        v.setRol(rs.getString("rol"));
        return v;
    }
}