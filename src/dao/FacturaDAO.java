
package dao;

import modelo.Factura;
import util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO para la tabla 'facturas'.
 */
public class FacturaDAO {

    /**
     * Inserta una nueva factura en la base de datos.
     * Devuelve el ID de la factura generada.
     */
    public int agregarFactura(Factura factura) {
        // (Asumimos que tu tabla se llama 'facturas')
        String sql = "INSERT INTO facturas (id_venta, numero_factura, fecha_factura, subtotal, iva, total, " +
                "tipo_comprobante, uso_cfdi, forma_pago_sat, metodo_pago_sat, lugar_expedicion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int idGenerado = 0;

        try (Connection conn = ConexionDB.getConexion()) {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, factura.getIdVenta());
                ps.setString(2, factura.getNumeroFactura());
                ps.setDate(3, factura.getFechaFactura());
                ps.setDouble(4, factura.getSubtotal());
                ps.setDouble(5, factura.getIva());
                ps.setDouble(6, factura.getTotal());
                ps.setString(7, factura.getTipoComprobante());
                ps.setString(8, factura.getUsoCfdi());
                ps.setString(9, factura.getFormaPagoSat());
                ps.setString(10, factura.getMetodoPagoSat());
                ps.setString(11, factura.getLugarExpedicion());

                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            idGenerado = rs.getInt(1);
                        }
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println("Error al agregar la factura: " + e.getMessage());
            e.printStackTrace();
        }

        return idGenerado;
    }
}