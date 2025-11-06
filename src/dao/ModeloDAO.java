package dao;

import modelo.Modelo;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloDAO {

    /**
     * Obtiene una lista de todos los modelos de una marca específica.
     */
    public List<Modelo> listarModelosPorMarca(int idMarca) {
        List<Modelo> modelos = new ArrayList<>();
        String sql = "SELECT * FROM modelos WHERE id_marca = ? ORDER BY nombre_modelo";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Modelo modelo = new Modelo();
                    modelo.setIdModelo(rs.getInt("id_modelo"));
                    modelo.setNombreModelo(rs.getString("nombre_modelo"));
                    modelo.setIdMarca(rs.getInt("id_marca"));
                    modelo.setTipoVehiculo(rs.getString("tipo_vehiculo"));
                    modelo.setAnioLanzamiento(rs.getInt("año_lanzamiento"));
                    modelos.add(modelo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar modelos por marca: " + e.getMessage());
        }
        return modelos;
    }
}