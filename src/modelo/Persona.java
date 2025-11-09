
package modelo;

/**
 * Clase abstracta que representa a una Persona.
 * Contiene los campos comunes que Vendedor y Cliente heredarán.
 */
public abstract class Persona {

    // 1. Usamos 'protected' para que las clases hijas (Cliente, Vendedor)
    // puedan acceder a ellos, pero sigan encapsulados del exterior.
    protected String nombre;
    protected String apellido;
    protected String telefono;
    protected String email;

    // 2. Definimos los getters y setters comunes en un solo lugar.
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}