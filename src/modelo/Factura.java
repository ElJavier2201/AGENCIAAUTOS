
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

    public double getSubtotal() {return subtotal;}

    public void setSubtotal(double subtotal) {this.subtotal = subtotal;}

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

    public Date getFechaFactura() {return fechaFactura;}
    public void setFechaFactura(Date fechaFactura) {this.fechaFactura = fechaFactura;}

    public double getIva() {return iva;}

    public void setIva(double iva) {this.iva = iva;}


    public String getTipoComprobante() {return tipoComprobante;}

    public void setTipoComprobante(String tipoComprobante) {this.tipoComprobante = tipoComprobante;}

    public String getUsoCfdi() {return usoCfdi;}

    public void setUsoCfdi(String usoCfdi) {this.usoCfdi = usoCfdi;}

    public String getFormaPagoSat() {return formaPagoSat;}

    public void setFormaPagoSat(String formaPagoSat) {this.formaPagoSat = formaPagoSat;}

    public String getMetodoPagoSat() {return metodoPagoSat;}

    public void setMetodoPagoSat(String metodoPagoSat) {this.metodoPagoSat = metodoPagoSat;}

    public String getLugarExpedicion() {return lugarExpedicion;}

    public void setLugarExpedicion(String lugarExpedicion) {this.lugarExpedicion = lugarExpedicion;}

    public Timestamp getFechaCreacion() {return fechaCreacion;}

    public void setFechaCreacion(Timestamp fechaCreacion) {this.fechaCreacion = fechaCreacion;}
}