package dao;

import modelo.Venta;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'ventas'.
 */
public class VentaDAO {

    /**
     * Registra una nueva venta y actualiza el estado del vehículo
     * (Usando una transacción para asegurar que ambas operaciones ocurran).
     *
     * --- DEVUELVE EL ID DE LA VENTA (int) ---
     */
    public int registrarVenta(Venta venta) {
        Connection conn = null;
        String sqlVenta = "INSERT INTO ventas (id_cliente, id_vehiculo, id_vendedor, id_metodo_pago, fecha_venta, precio_final, " +
                "descuento, enganche, plazo_meses, tasa_interes, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlVehiculo = "UPDATE vehiculos SET estado = 'vendido' WHERE id_vehiculo = ?";

        int idVentaGenerada = 0; // El ID que devolveremos

        try {
            conn = ConexionDB.getConexion();
            if (conn == null) return 0; // Devuelve 0 si falla la conexión

            // Iniciar Transacción
            conn.setAutoCommit(false);

            // 1. Insertar la Venta
            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, venta.getIdCliente());
                psVenta.setInt(2, venta.getIdVehiculo());
                psVenta.setInt(3, venta.getIdVendedor());
                psVenta.setInt(4, venta.getIdMetodoPago());
                psVenta.setDate(5, venta.getFechaVenta());
                psVenta.setDouble(6, venta.getPrecioFinal());
                psVenta.setDouble(7, 0);
                psVenta.setDouble(8, 0);
                psVenta.setInt(9, 0);
                psVenta.setDouble(10, 0);
                psVenta.setString(11, "");

                int filasInsertadas = psVenta.executeUpdate();
                if (filasInsertadas == 0) {
                    throw new SQLException("Falló la inserción de la venta, no se insertaron filas.");
                }

                // Obtener el ID de la venta generada
                try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idVentaGenerada = generatedKeys.getInt(1);
                        venta.setIdVenta(idVentaGenerada); // Actualizamos el objeto original
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la venta.");
                    }
                }
            }

            // 2. Actualizar el Vehículo
            try (PreparedStatement psVehiculo = conn.prepareStatement(sqlVehiculo)) {
                psVehiculo.setInt(1, venta.getIdVehiculo());
                int filasActualizadas = psVehiculo.executeUpdate();
                if (filasActualizadas == 0) {
                    throw new SQLException("Falló la actualización del vehículo, no se encontró el ID.");
                }
            }

            // 3. Confirmar Transacción
            conn.commit();
            return idVentaGenerada; // Devolver el ID

        } catch (SQLException e) {
            System.out.println("Error al registrar la venta (transacción): " + e.getMessage());
            // 4. Revertir Transacción en caso de error
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            return 0; // Devolver 0 en caso de error
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Regresar al modo normal
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * --- NUEVO MÉTODO (Sobrecarga) ---
     * Lista todas las ventas con financiamiento (plazos > 0)
     * PARA UN CLIENTE ESPECÍFICO.
     */
    /**
     * --- NUEVO MÉTODO (Sobrecarga para el Gerente) ---
     * Lista TODAS las ventas que tienen financiamiento (plazos > 0).
     * Incluye el nombre del cliente para mostrar en un ComboBox.
     */
    public List<Venta> listarVentasConFinanciamiento() {
        List<Venta> lista = new ArrayList<>();
        // Esta es la consulta original sin el filtro de id_cliente
        String sql = "SELECT v.*, CONCAT(c.nombre, ' ', c.apellido) AS cliente_nombre " +
                "FROM ventas v " +
                "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE v.plazo_meses > 0 " +
                "ORDER BY v.fecha_venta DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setIdCliente(rs.getInt("id_cliente"));
                v.setIdVehiculo(rs.getInt("id_vehiculo"));
                v.setFechaVenta(rs.getDate("fecha_venta"));
                v.setPrecioFinal(rs.getDouble("precio_final"));
                v.setEnganche(rs.getDouble("enganche"));
                v.setPlazoMeses(rs.getInt("plazo_meses"));
                v.setTasaInteres(rs.getDouble("tasa_interes"));
                v.setNombreCliente(rs.getString("cliente_nombre"));
                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}