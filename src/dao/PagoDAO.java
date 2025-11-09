package dao;

import modelo.Pago;
import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla 'pagos'.
 */
public class PagoDAO {

    /**
     * Inserta un nuevo registro de pago en la base de datos.
     */
    public boolean agregarPago(Pago pago) {
        // (Asumimos que tu tabla se llama 'pagos')
        String sql = "INSERT INTO pagos (id_venta, id_factura, numero_pago, fecha_pago, monto, " +
                "concepto, referencia, estado, fecha_vencimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pago.getIdVenta());

            // id_factura puede ser nulo
            if (pago.getIdFactura() > 0) {
                ps.setInt(2, pago.getIdFactura());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setInt(3, pago.getNumeroPago());
            ps.setDate(4, pago.getFechaPago());
            ps.setDouble(5, pago.getMonto());
            ps.setString(6, pago.getConcepto());
            ps.setString(7, pago.getReferencia());
            ps.setString(8, pago.getEstado()); // ej. 'pagado'

            // fecha_vencimiento puede ser nulo
            if (pago.getFechaVencimiento() != null) {
                ps.setDate(9, pago.getFechaVencimiento());
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar el pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista todos los pagos (enganche y mensualidades) asociados a una venta.
     */
    public List<Pago> listarPagosPorVenta(int idVenta) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id_venta = ? ORDER BY numero_pago ASC";

        try (Connection conn = ConexionDB.getConexion()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idVenta);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Pago p = new Pago();
                        p.setIdPago(rs.getInt("id_pago"));
                        p.setIdVenta(rs.getInt("id_venta"));
                        p.setNumeroPago(rs.getInt("numero_pago"));
                        p.setFechaPago(rs.getDate("fecha_pago"));
                        p.setMonto(rs.getDouble("monto"));
                        p.setConcepto(rs.getString("concepto"));
                        p.setEstado(rs.getString("estado"));
                        p.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                        lista.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     *
     * Marca un pago 'pendiente' como 'pagado' y establece la fecha de pago.
     */
    public boolean pagarMensualidad(int idPago, java.sql.Date fechaDePago) {
        String sql = "UPDATE pagos SET estado = 'pagado', fecha_pago = ? " +
                "WHERE id_pago = ? AND estado = 'pendiente'";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, fechaDePago);
            ps.setInt(2, idPago);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
