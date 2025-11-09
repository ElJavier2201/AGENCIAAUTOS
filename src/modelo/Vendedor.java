package modelo;
import java.sql.Date;

/**
 * --- MODIFICADO: Ahora hereda de Persona ---
 */
public class Vendedor extends Persona {
    private int idVendedor;
    private Date fechaContratacion;
    private double comisionPorcentaje;
    private boolean activo;
    private String usuario;
    private String contraseña; // Almacenará el hash
    private String rol; // 'Gerente', 'Vendedor'

    public Vendedor() {}

    // Getters y Setters
    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }

    public Date getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public double getComisionPorcentaje() { return comisionPorcentaje; }
    public void setComisionPorcentaje(double comisionPorcentaje) { this.comisionPorcentaje = comisionPorcentaje; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}