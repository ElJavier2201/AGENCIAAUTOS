
package modelo;
import java.sql.Date;
import java.sql.Timestamp;

public class Pago {
    private int idPago;
    private int idVenta;
    private int idFactura; // Puede ser nulo
    private int numeroPago;
    private Date fechaPago;
    private double monto;
    private String concepto;
    private String referencia;
    private String estado; // ENUM
    private Date fechaVencimiento; // Puede ser nulo
    private Timestamp fechaCreacion;

    public Pago() {}

    // Getters y Setters (aquí solo algunos como ejemplo)
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
