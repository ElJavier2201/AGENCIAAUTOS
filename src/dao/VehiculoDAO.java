package dao;

import modelo.Vehiculo;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'vehiculos'.
 */
public class VehiculoDAO {

    /**
     * Lista todos los vehículos que NO están vendidos.
     * Usa JOINs para obtener los nombres de la marca y el modelo.
     */
    public List<Vehiculo> listarVehiculosDisponibles() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        // Consulta SQL con JOINs
        String sql = "SELECT v.*, m.nombre_modelo, ma.nombre_marca " +
                "FROM vehiculos v " +
                "JOIN modelos m ON v.id_modelo = m.id_modelo " +
                "JOIN marcas ma ON m.id_marca = ma.id_marca " +
                "WHERE v.estado != 'vendido' " +
                "ORDER BY ma.nombre_marca, m.nombre_modelo, v.año";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehiculos.add(mapearVehiculo(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar vehículos: " + e.getMessage());
        }
        return vehiculos;
    }

    /**
     * Agrega un nuevo vehículo a la base de datos.
     */
    public boolean agregarVehiculo(Vehiculo v) {
        String sql = "INSERT INTO vehiculos (id_modelo, numero_serie, año, color, kilometraje, precio, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, v.getIdModelo());
            ps.setString(2, v.getNumeroSerie());
            ps.setInt(3, v.getAnio());
            ps.setString(4, v.getColor());
            ps.setInt(5, v.getKilometraje());
            ps.setDouble(6, v.getPrecio());
            ps.setString(7, v.getEstado()); // 'nuevo' o 'usado'

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar vehículo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza la información de un vehículo existente.
     */
    public boolean actualizarVehiculo(Vehiculo v) {
        String sql = "UPDATE vehiculos SET id_modelo = ?, año = ?, color = ?, kilometraje = ?, precio = ?, estado = ? " +
                "WHERE id_vehiculo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, v.getIdModelo());
            ps.setInt(2, v.getAnio());
            ps.setString(3, v.getColor());
            ps.setInt(4, v.getKilometraje());
            ps.setDouble(5, v.getPrecio());
            ps.setString(6, v.getEstado());
            ps.setInt(7, v.getIdVehiculo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar vehículo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca un vehículo como 'vendido'. (Usado por el VendedorPanel).
     */
    public boolean marcarComoVendido(int idVehiculo) {
        String sql = "UPDATE vehiculos SET estado = 'vendido' WHERE id_vehiculo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar como vendido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper para convertir un ResultSet en un objeto Vehiculo (incluyendo JOINs).
     */
    private Vehiculo mapearVehiculo(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getInt("id_vehiculo"));
        v.setIdModelo(rs.getInt("id_modelo"));
        v.setNumeroSerie(rs.getString("numero_serie"));
        v.setAnio(rs.getInt("año"));
        v.setColor(rs.getString("color"));
        v.setKilometraje(rs.getInt("kilometraje"));
        v.setPrecio(rs.getDouble("precio"));
        v.setEstado(rs.getString("estado"));
        v.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));

        // Datos de los JOINs
        v.setNombreModelo(rs.getString("nombre_modelo"));
        v.setNombreMarca(rs.getString("nombre_marca"));

        return v;
    }
}