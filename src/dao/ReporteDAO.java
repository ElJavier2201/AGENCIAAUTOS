package dao;

import modelo.Venta; // Reutilizaremos el modelo Venta, ya que tiene campos para los JOINs
import util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para generar reportes de ventas complejos.
 */
public class ReporteDAO {

    /**
     * Obtiene una lista detallada de todas las ventas realizadas, uniendo
     * información de clientes, vendedores y vehículos.
     */
    public List<Venta> listarVentasDetallado() {
        List<Venta> reporte = new ArrayList<>();

        // Esta consulta SQL usa múltiples JOINs para construir el reporte
        String sql = "SELECT v.id_venta, v.fecha_venta, v.precio_final, " +
                "CONCAT(c.nombre, ' ', c.apellido) AS cliente_nombre, " +
                "CONCAT(ve.nombre, ' ', ve.apellido) AS vendedor_nombre, " +
                "CONCAT(ma.nombre_marca, ' ', m.nombre_modelo, ' (', vh.año, ')') AS vehiculo_nombre " +
                "FROM ventas v " +
                "JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "JOIN vendedores ve ON v.id_vendedor = ve.id_vendedor " +
                "JOIN vehiculos vh ON v.id_vehiculo = vh.id_vehiculo " +
                "JOIN modelos m ON vh.id_modelo = m.id_modelo " +
                "JOIN marcas ma ON m.id_marca = ma.id_marca " +
                "ORDER BY v.fecha_venta DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFechaVenta(rs.getDate("fecha_venta"));
                v.setPrecioFinal(rs.getDouble("precio_final"));
                v.setNombreCliente(rs.getString("cliente_nombre"));
                v.setNombreVendedor(rs.getString("vendedor_nombre"));
                v.setNombreVehiculo(rs.getString("vehiculo_nombre"));
                reporte.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar reporte de ventas: " + e.getMessage());
        }
        return reporte;
    }
}