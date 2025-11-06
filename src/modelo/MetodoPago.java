package modelo;

public class MetodoPago {
    private int idMetodoPago;
    private String nombreMetodo;
    private boolean requiereFinanciamiento;

    public MetodoPago() {}

    // Getters y Setters
    public int getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(int idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }

    public boolean isRequiereFinanciamiento() { return requiereFinanciamiento; }
    public void setRequiereFinanciamiento(boolean requiereFinanciamiento) { this.requiereFinanciamiento = requiereFinanciamiento; }

    // Importante para JComboBox
    @Override
    public String toString() {
        return nombreMetodo;
    }
}
