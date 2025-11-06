package controlador;

import dao.VehiculoDAO;
import modelo.Vehiculo;
import java.util.List;

public class VehiculoControlador {

    private final VehiculoDAO dao;

    public VehiculoControlador() {
        this.dao = new VehiculoDAO();
    }

    /**
     * Obtiene la lista de vehículos disponibles (no vendidos).
     */
    public List<Vehiculo> listarVehiculosDisponibles() {
        return dao.listarVehiculosDisponibles();
    }

    /**
     * Llama al DAO para agregar un nuevo vehículo.
     */
    public boolean agregarVehiculo(Vehiculo v) {
        // (Aquí podrías añadir validaciones, ej. que el VIN no esté vacío)
        return dao.agregarVehiculo(v);
    }

    /**
     * Llama al DAO para actualizar un vehículo.
     */
    public boolean actualizarVehiculo(Vehiculo v) {
        return dao.actualizarVehiculo(v);
    }

    /**
     * Llama al DAO para marcar un vehículo como 'vendido'.
     */
    public boolean marcarComoVendido(int idVehiculo) {
        return dao.marcarComoVendido(idVehiculo);
    }
}