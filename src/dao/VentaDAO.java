package dao;

import modelo.Venta;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * DAO para la tabla 'ventas'.
 */
public class VentaDAO {

    /**
     * Registra una nueva venta y actualiza el estado del vehículo
     * (Usando una transacción para asegurar que ambas operaciones ocurran).
     */
    public boolean registrarVenta(Venta venta) {
        Connection conn = null;
        String sqlVenta = "INSERT INTO ventas (id_cliente, id_vehiculo, id_vendedor, id_metodo_pago, fecha_venta, precio_final, " +
                "descuento, enganche, plazo_meses, tasa_interes, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlVehiculo = "UPDATE vehiculos SET estado = 'vendido' WHERE id_vehiculo = ?";

        try {
            conn = ConexionDB.getConexion();
            if (conn == null) return false;

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
                psVenta.setDouble(7, 0); // Asumiendo 0 por ahora
                psVenta.setDouble(8, 0); // Asumiendo 0 por ahora
                psVenta.setInt(9, 0);    // Asumiendo 0 por ahora
                psVenta.setDouble(10, 0); // Asumiendo 0 por ahora
                psVenta.setString(11, ""); // Asumiendo "" por ahora

                int filasInsertadas = psVenta.executeUpdate();
                if (filasInsertadas == 0) {
                    throw new SQLException("Falló la inserción de la venta, no se insertaron filas.");
                }

                // (Opcional: Obtener el ID de la venta generada para facturas/pagos)
                // try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                //    if (generatedKeys.next()) {
                //        venta.setIdVenta(generatedKeys.getInt(1));
                //    }
                // }
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
            return true;

        } catch (SQLException e) {
            System.out.println("Error al registrar la venta (transacción): " + e.getMessage());
            // 4. Revertir Transacción en caso de error
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Regresar al modo normal
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
