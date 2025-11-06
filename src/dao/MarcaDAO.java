package dao;

import modelo.Marca;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    /**
     * Obtiene una lista de todas las marcas de la base de datos.
     */
    public List<Marca> listarMarcas() {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT * FROM marcas ORDER BY nombre_marca";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Marca marca = new Marca();
                marca.setIdMarca(rs.getInt("id_marca"));
                marca.setNombreMarca(rs.getString("nombre_marca"));
                marca.setPaisOrigen(rs.getString("pais_origen"));
                marcas.add(marca);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar marcas: " + e.getMessage());
        }
        return marcas;
    }
}