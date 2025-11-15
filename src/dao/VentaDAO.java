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
     * DEVUELVE EL ID DE LA VENTA (int)
     */
    public int registrarVenta(Venta venta) {
        Connection conn = null;
        String sqlVenta = "INSERT INTO ventas (id_cliente, id_vehiculo, id_vendedor, id_metodo_pago, fecha_venta, precio_final, " +
                "descuento, enganche, plazo_meses, tasa_interes, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlVehiculo = "UPDATE vehiculos SET estado = 'vendido' WHERE id_vehiculo = ?";

        int idVentaGenerada = 0;

        try {
            conn = ConexionDB.getConexion();

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

                // CORRECCIÓN: Ahora se guardan los valores reales
                psVenta.setDouble(7, venta.getDescuento());
                psVenta.setDouble(8, venta.getEnganche());
                psVenta.setInt(9, venta.getPlazoMeses());
                psVenta.setDouble(10, venta.getTasaInteres());

                // Observaciones (puede ser null)
                String obs = venta.getObservaciones();
                psVenta.setString(11, (obs != null && !obs.isEmpty()) ? obs : "");

                int filasInsertadas = psVenta.executeUpdate();
                if (filasInsertadas == 0) {
                    throw new SQLException("Falló la inserción de la venta, no se insertaron filas.");
                }

                // Obtener el ID de la venta generada
                try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idVentaGenerada = generatedKeys.getInt(1);
                        venta.setIdVenta(idVentaGenerada);
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
            return idVentaGenerada;

        } catch (SQLException e) {
            System.out.println("Error al registrar la venta (transacción): " + e.getMessage());
            e.printStackTrace();

            // 4. Revertir Transacción en caso de error
            try {
                assert conn != null;
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            return 0;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  NUEVO MÉTODO: Lista TODAS las ventas que tienen financiamiento (plazos > 0).
     * Incluye el nombre del cliente para mostrar en un ComboBox.
     */
    public List<Venta> listarVentasConFinanciamiento() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, CONCAT(c.nombre, ' ', c.apellido) AS cliente_nombre " +
                "FROM ventas v " +
                "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE v.plazo_meses > 0 " +
                "ORDER BY v.fecha_venta DESC";

        try (Connection conn = ConexionDB.getConexion()) {
            try (PreparedStatement ps = conn.prepareStatement(sql);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * ✅ NUEVO MÉTODO (SOBRECARGA): Lista las ventas con financiamiento de UN CLIENTE ESPECÍFICO.
     * Incluye el nombre del vehículo para mostrar en el portal del cliente.
     */
    public List<Venta> listarVentasConFinanciamiento(int idCliente) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, " +
                "CONCAT(c.nombre, ' ', c.apellido) AS cliente_nombre, " +
                "CONCAT(ma.nombre_marca, ' ', m.nombre_modelo, ' (', vh.año, ')') AS vehiculo_nombre " +
                "FROM ventas v " +
                "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "JOIN vehiculos vh ON v.id_vehiculo = vh.id_vehiculo " +
                "JOIN modelos m ON vh.id_modelo = m.id_modelo " +
                "JOIN marcas ma ON m.id_marca = ma.id_marca " +
                "WHERE v.plazo_meses > 0 AND v.id_cliente = ? " +
                "ORDER BY v.fecha_venta DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
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
                    v.setNombreVehiculo(rs.getString("vehiculo_nombre"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas con financiamiento por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}