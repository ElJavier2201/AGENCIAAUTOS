package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos MySQL.
 * (Actualizado para la base de datos 'agencia_autos')
 */
public class ConexionDB {
    // CAMBIO: Apuntar a la nueva base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/agencia_autos";
    private static final String USER = "root";
    private static final String PASS = "220105";
    /**
     * Retorna una conexión válida a la base de datos.
     * @return Objeto Connection o null si hay error.
     */
    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la BD 'agencia_autos': " + e.getMessage());
            return null;
        }
    }
}