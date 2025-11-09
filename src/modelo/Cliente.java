package modelo;
import java.sql.Timestamp;

/**
 * --- MODIFICADO: Ahora hereda de Persona ---
 */
public class Cliente extends Persona {
    private int idCliente;
    private String direccion;
    private String rfc;
    private Timestamp fechaRegistro;

    public Cliente() {}

    // Getters y Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    // Importante para JComboBox (Usa los campos heredados)
    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
