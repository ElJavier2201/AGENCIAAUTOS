package modelo;

import java.sql.Date;

/**
 * Modelo para la tabla 'vendedores'.
 * Esta clase también manejará la autenticación (login).
 */
public class Vendedor {
    private int idVendedor;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private Date fechaContratacion;
    private double comisionPorcentaje;
    private boolean activo;

    // Campos de Login (de la Opción A)
    private String usuario;
    private String contraseña; // Almacenará el hash
    private String rol; // 'Gerente', 'Vendedor'

    public Vendedor() {}

    // Getters y Setters
    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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