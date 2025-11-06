package modelo;

public class Marca {
    private int idMarca;
    private String nombreMarca;
    private String paisOrigen;

    public Marca() {}

    // Getters y Setters
    public int getIdMarca() { return idMarca; }
    public void setIdMarca(int idMarca) { this.idMarca = idMarca; }

    public String getNombreMarca() { return nombreMarca; }
    public void setNombreMarca(String nombreMarca) { this.nombreMarca = nombreMarca; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    // Importante para JComboBox
    @Override
    public String toString() {
        return nombreMarca;
    }
}