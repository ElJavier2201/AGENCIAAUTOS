

package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * NUEVA CLASE: Base abstracta para todos los DAOs
 * Centraliza logging y manejo de errores
 */
public abstract class BaseDAO {

    protected final Logger logger;

    /**
     * Constructor que inicializa el logger con el nombre de la clase hija
     */
    protected BaseDAO() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Obtiene una conexión y registra el evento
     */
    protected Connection getConnection() throws SQLException {
        try {
            Connection conn = ConexionDB.getConexion();
            logger.debug("Conexión obtenida: {}", "OK");
            return conn;
        } catch (SQLException e) {
            logger.error("Error al obtener conexión", e);
            throw e;
        }
    }

    /**
     * Cierra recursos de forma segura con logging
     */
    protected void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
                logger.trace("ResultSet cerrado");
            } catch (SQLException e) {
                logger.warn("Error al cerrar ResultSet", e);
            }
        }

        if (ps != null) {
            try {
                ps.close();
                logger.trace("PreparedStatement cerrado");
            } catch (SQLException e) {
                logger.warn("Error al cerrar PreparedStatement", e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
                logger.trace("Connection cerrada");
            } catch (SQLException e) {
                logger.warn("Error al cerrar Connection", e);
            }
        }
    }

    /**
     * Registra error en operación de BD
     */
    protected void logError(String operacion, SQLException e) {
        logger.error("Error en {}: {} (SQLState: {}, ErrorCode: {})",
                operacion,
                e.getMessage(),
                e.getSQLState(),
                e.getErrorCode());
    }

    /**
     * Registra éxito en operación de BD
     */
    protected void logSuccess(String operacion, int rowsAffected) {
        logger.info("{} exitoso - Filas afectadas: {}", operacion, rowsAffected);
    }

    /**
     * Registra inicio de operación de lectura
     */
    protected void logQuery(String listarClientes) {
        logger.debug("Ejecutando consulta: {}", "Listar clientes");
    }
}