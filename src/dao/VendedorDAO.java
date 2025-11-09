package dao;
import modelo.Vendedor;
import util.ConexionDB;
// --- 1. IMPORTAR LA LIBRERÍA ---
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'vendedores'.
 * (Versión con contraseñas HASHEADAS usando BCrypt)
 */
public class VendedorDAO {

    /**
     * Autentica a un vendedor usando su 'usuario' y comparando el 'passPlano'
     * con el HASH almacenado en la base de datos.
     */
    public Vendedor autenticar(String usuario, String passPlano) {
        // --- 2. LÓGICA DE LOGIN ACTUALIZADA ---

        // 1. Buscar solo por usuario y que esté activo
        String sql = "SELECT * FROM vendedores WHERE usuario = ? AND activo = 1";

        try (Connection conn = ConexionDB.getConexion()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuario);

                try (ResultSet rs = ps.executeQuery()) {
                    // 2. Verificar si el usuario existe
                    if (rs.next()) {
                        // 3. Obtener el HASH guardado en la BD
                        String passHash = rs.getString("contraseña");

                        // 4. Comparar la contraseña plana con el HASH
                        if (BCrypt.checkpw(passPlano, passHash)) {
                            // ¡Contraseña correcta! Mapear y devolver al vendedor
                            return mapearVendedor(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al autenticar vendedor: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Esto puede pasar si una contraseña en la BD es texto plano y no un hash válido
            System.out.println("Error de HASH (probable contraseña en texto plano): " + e.getMessage());
        }

        // Falla el login (usuario no encontrado o contraseña incorrecta)
        return null;
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
     * Agrega un nuevo vendedor (hasheando la contraseña).
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

                // --- 3. HASHEAR LA CONTRASEÑA ANTES DE GUARDAR ---
                String passHash = BCrypt.hashpw(v.getContraseña(), BCrypt.gensalt());
                ps.setString(9, passHash); // Guardar el HASH

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
            // SQL para actualizar contraseña
            sql = "UPDATE vendedores SET nombre = ?, apellido = ?, telefono = ?, email = ?, fecha_contratacion = ?, " +
                    "comision_porcentaje = ?, activo = ?, usuario = ?, contraseña = ?, rol = ? " +
                    "WHERE id_vendedor = ?";
        } else {
            // SQL sin actualizar contraseña
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
                // --- 4. HASHEAR LA NUEVA CONTRASEÑA ---
                String passHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
                ps.setString(i++, passHash); // Guardar el HASH
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
        v.setContraseña(rs.getString("contraseña")); // Guarda el HASH (no el texto plano)
        v.setRol(rs.getString("rol"));
        return v;
    }
}