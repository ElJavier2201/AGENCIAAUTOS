package modelo;
import java.sql.Timestamp;

/**
 * MODIFICADO: Ahora hereda de Persona y tiene campos de login ---
 */
public class Cliente extends Persona {
    private int idCliente;
    private String direccion;
    private String rfc;
    private Timestamp fechaRegistro;
    private String usuario;
    private String contraseña; // Se usará para crear/actualizar, no para almacenar en el objeto

    public Cliente() {}

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}