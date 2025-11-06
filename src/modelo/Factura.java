
package modelo;
import java.sql.Date;
import java.sql.Timestamp;

public class Factura {
    private int idFactura;
    private int idVenta;
    private String numeroFactura;
    private Date fechaFactura;
    private double subtotal;
    private double iva;
    private double total;
    private String tipoComprobante;
    private String usoCfdi;
    private String formaPagoSat;
    private String metodoPagoSat;
    private String lugarExpedicion;
    private Timestamp fechaCreacion;

    public Factura() {}

    // Getters y Setters (aquí solo algunos como ejemplo, deberías añadir todos)
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}