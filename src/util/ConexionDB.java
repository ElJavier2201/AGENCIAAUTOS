package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *  CLASE REFACTORIZADA: Gestión de conexiones a la base de datos con HikariCP
 *
 */
public class ConexionDB {

    private static final Logger logger = LoggerFactory.getLogger(ConexionDB.class);
    private static HikariDataSource dataSource;
    private static boolean initialized = false;

    // Constructor privado para evitar instanciación
    private ConexionDB() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    /**
     * Inicializa el DataSource con HikariCP
     * Se ejecuta una sola vez al inicio de la aplicación
     */
    private static synchronized void inicializar() {
        if (initialized) {
            return;
        }

        try {
            logger.info("Inicializando Connection Pool (HikariCP)...");

            // Validar configuración
            Configuracion.validateConfiguration();

            // Configurar HikariCP
            HikariConfig config = new HikariConfig();

            // Propiedades básicas de conexión
            config.setJdbcUrl(Configuracion.getProperty(Configuracion.DB_URL));
            config.setUsername(Configuracion.getProperty(Configuracion.DB_USERNAME));
            config.setPassword(Configuracion.getProperty(Configuracion.DB_PASSWORD));
            config.setDriverClassName(Configuracion.getProperty(Configuracion.DB_DRIVER,
                    "com.mysql.cj.jdbc.Driver"));

            // Configuración del Pool
            config.setMaximumPoolSize(Configuracion.getIntProperty(
                    Configuracion.DB_POOL_MAX_SIZE, 10));
            config.setMinimumIdle(Configuracion.getIntProperty(
                    Configuracion.DB_POOL_MIN_IDLE, 2));
            config.setConnectionTimeout(Configuracion.getLongProperty(
                    Configuracion.DB_POOL_CONNECTION_TIMEOUT, 30000)); // 30 segundos
            config.setIdleTimeout(Configuracion.getLongProperty(
                    Configuracion.DB_POOL_IDLE_TIMEOUT, 600000)); // 10 minutos
            config.setMaxLifetime(Configuracion.getLongProperty(
                    Configuracion.DB_POOL_MAX_LIFETIME, 1800000)); // 30 minutos

            // Nombre del pool para logging
            config.setPoolName("AgenciaAutosPool");

            // Propiedades de MySQL para mejor rendimiento
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            // Query de validación de conexión
            config.setConnectionTestQuery("SELECT 1");

            // Inicializar el DataSource
            dataSource = new HikariDataSource(config);
            initialized = true;

            logger.info("Connection Pool inicializado exitosamente");
            logger.info("Pool Stats: Max={}, Min={}",
                    config.getMaximumPoolSize(),
                    config.getMinimumIdle());

            // Probar conexión inicial
            probarConexionInicial();

        } catch (Exception e) {
            logger.error("ERROR CRÍTICO: No se pudo inicializar el Connection Pool", e);
            throw new RuntimeException("Fallo al inicializar el sistema de base de datos", e);
        }
    }

    /**
     * Prueba que la conexión a la BD funcione correctamente
     */
    private static void probarConexionInicial() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                logger.info("Conexión a base de datos verificada exitosamente");
            }
        } catch (SQLException e) {
            logger.error("Falló la prueba de conexión inicial", e);
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }

    /**
     * Obtiene una conexión del pool
     * @return Connection lista para usar
     * @throws SQLException si no se puede obtener la conexión
     */
    public static Connection getConexion() throws SQLException {
        if (!initialized) {
            inicializar();
        }

        if (dataSource == null) {
            throw new SQLException("DataSource no inicializado");
        }

        try {
            Connection conn = dataSource.getConnection();

            if (conn == null || conn.isClosed()) {
                logger.error("Se obtuvo una conexión inválida del pool");
                throw new SQLException("Conexión inválida del pool");
            }

            return conn;

        } catch (SQLException e) {
            logger.error(" Error al obtener conexión del pool", e);
            throw e;
        }
    }

    /**
     * Obtiene estadísticas del pool de conexiones
     * Útil para monitoreo y debugging
     */
    public static String getPoolStats() {
        if (!initialized || dataSource == null) {
            return "Pool no inicializado";
        }

        return String.format(
                "Pool Stats: Activas=%d, Idle=%d, Esperando=%d, Total=%d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                dataSource.getHikariPoolMXBean().getTotalConnections()
        );
    }

    /**
     * Imprime estadísticas del pool en la consola
     */
    public static void logPoolStats() {
        if (initialized && dataSource != null) {
            logger.info(getPoolStats());
        }
    }

    /**
     * Cierra el pool de conexiones de forma segura
     * Debe llamarse al cerrar la aplicación
     */
    public static synchronized void cerrarPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Cerrando Connection Pool...");

            try {
                dataSource.close();
                logger.info("Connection Pool cerrado exitosamente");
            } catch (Exception e) {
                logger.error("Error al cerrar el Connection Pool", e);
            } finally {
                dataSource = null;
                initialized = false;
            }
        }
    }

    /**
     * Verifica si el pool está inicializado y funcionando
     */
    public static boolean isPoolActivo() {
        return initialized && dataSource != null && !dataSource.isClosed();
    }

    /**
     * Reinicia el pool de conexiones
     * Útil para aplicar cambios de configuración
     */
    public static synchronized void reiniciarPool() {
        logger.warn(" Reiniciando Connection Pool...");
        cerrarPool();
        inicializar();
    }
}