package modelo;

public class Modelo {
    private int idModelo;
    private String nombreModelo;
    private int idMarca;
    private String tipoVehiculo;
    private int anioLanzamiento;

    // Campo extra para JOIN
    private String nombreMarca;

    public Modelo() {}

    // Getters y Setters
    public int getIdModelo() { return idModelo; }
    public void setIdModelo(int idModelo) { this.idModelo = idModelo; }

    public String getNombreModelo() { return nombreModelo; }
    public void setNombreModelo(String nombreModelo) { this.nombreModelo = nombreModelo; }

    public int getIdMarca() { return idMarca; }
    public void setIdMarca(int idMarca) { this.idMarca = idMarca; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public int getAnioLanzamiento() { return anioLanzamiento; }
    public void setAnioLanzamiento(int anioLanzamiento) { this.anioLanzamiento = anioLanzamiento; }

    public String getNombreMarca() { return nombreMarca; }
    public void setNombreMarca(String nombreMarca) { this.nombreMarca = nombreMarca; }

    // Importante para JComboBox
    @Override
    public String toString() {
        return nombreModelo;
    }
}

