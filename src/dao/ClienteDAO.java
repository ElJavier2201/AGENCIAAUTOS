package dao;

import modelo.Cliente;
import org.mindrot.jbcrypt.BCrypt;
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
     * Autentica a un cliente ---
     * Compara el passPlano con el HASH almacenado.
     */
    public Cliente autenticar(String usuario, String passPlano) {
        // 1. Buscar solo por usuario
        String sql = "SELECT * FROM clientes WHERE usuario = ?";
        logger.debug("Autenticando cliente: {}", usuario);

        try (Connection conn = getConnection()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuario);

                try (ResultSet rs = ps.executeQuery()) {
                    // 2. Verificar si el usuario existe
                    if (rs.next()) {
                        // 3. Obtener el HASH guardado en la BD
                        String passHash = rs.getString("contraseña");

                        // 4. Comparar (asegurarse que el hash no sea nulo)
                        if (passHash != null && BCrypt.checkpw(passPlano, passHash)) {
                            logger.info("Login exitoso para cliente: {}", usuario);
                            return mapearCliente(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logError("autenticar cliente", e);
        } catch (IllegalArgumentException e) {
            logError("Error de HASH (probable contraseña en texto plano)", new SQLException(e.getMessage()));
        }
        logger.warn("Login fallido para cliente: {}", usuario);
        return null;
    }


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
     */
    public boolean agregar(Cliente c) {
        if (existeEmail(c.getEmail())) {
            logger.warn(" Intento de agregar cliente con email duplicado: {}", c.getEmail());
            return false;
        }

        // --- SQL MODIFICADO ---
        String sql = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion, rfc, usuario, contraseña) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        logger.debug("Agregando cliente: {} {}", c.getNombre(), c.getApellido());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getRfc());

            // --- NUEVA LÓGICA DE LOGIN ---
            // El DAO recibe la contraseña en plano (c.getContraseña()) y la hashea.
            if (c.getUsuario() != null && c.getContraseña() != null) {
                ps.setString(7, c.getUsuario());
                String passHash = BCrypt.hashpw(c.getContraseña(), BCrypt.gensalt());
                ps.setString(8, passHash);
            } else {
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            int rows = ps.executeUpdate();
            logSuccess("Agregar cliente", rows);
            return rows > 0;

        } catch (SQLException e) {
            logError("agregar cliente", e);
            if (e.getErrorCode() == 1062) {
                logger.warn(" El email o usuario '{}'/'{}' ya está registrado", c.getEmail(), c.getUsuario());
            }
            return false;
        }
    }

    /**
     * Verifica si un email esta registrado
     */
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
        String plainPassword = c.getContraseña();
        boolean updatePassword = (plainPassword != null && !plainPassword.isEmpty());

        // Construir SQL dinámicamente
        StringBuilder sql = new StringBuilder(
                "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ?, rfc = ?, usuario = ? ");

        if (updatePassword) {
            sql.append(", contraseña = ? ");
        }
        sql.append("WHERE id_cliente = ?");

        logger.debug("Actualizando cliente ID: {}", c.getIdCliente());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1; // Contador de parámetros
            ps.setString(i++, c.getNombre());
            ps.setString(i++, c.getApellido());
            ps.setString(i++, c.getTelefono());
            ps.setString(i++, c.getEmail());
            ps.setString(i++, c.getDireccion());
            ps.setString(i++, c.getRfc());

            // Actualizar usuario (puede ser nulo si lo borran)
            if (c.getUsuario() != null && !c.getUsuario().isEmpty()) {
                ps.setString(i++, c.getUsuario());
            } else {
                ps.setNull(i++, java.sql.Types.VARCHAR);
            }

            // Actualizar contraseña SOLO si se proporcionó una nueva
            if (updatePassword) {
                String passHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
                ps.setString(i++, passHash);
            }

            // ID del cliente al final
            ps.setInt(i++, c.getIdCliente());

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
     * (Asegúrate de que los campos de Persona se están seteando)
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre")); // Heredado de Persona
        c.setApellido(rs.getString("apellido")); // Heredado de Persona
        c.setTelefono(rs.getString("telefono")); // Heredado de Persona
        c.setEmail(rs.getString("email")); // Heredado de Persona
        c.setDireccion(rs.getString("direccion"));
        c.setRfc(rs.getString("rfc"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        c.setUsuario(rs.getString("usuario"));
        return c;
    }
}