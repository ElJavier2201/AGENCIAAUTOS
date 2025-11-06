package dao;

import modelo.MetodoPago; // (Necesitarás crear este modelo, es simple)
import util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetodoPagoDAO {
    public List<MetodoPago> listarActivos() {
        List<MetodoPago> lista = new ArrayList<>();
        String sql = "SELECT * FROM metodos_pago WHERE activo = 1 ORDER BY nombre_metodo";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MetodoPago mp = new MetodoPago();
                mp.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                mp.setNombreMetodo(rs.getString("nombre_metodo"));
                mp.setRequiereFinanciamiento(rs.getBoolean("requiere_financiamiento"));
                lista.add(mp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
