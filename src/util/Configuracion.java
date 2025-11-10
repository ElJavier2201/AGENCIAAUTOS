

package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**

 * Lee propiedades desde archivo y variables de entorno
 */
public class Configuracion {

    private static final Properties properties = new Properties();
    private static boolean initialized = false;

    // Constantes para las propiedades
    public static final String DB_URL = "db.url";
    public static final String DB_DRIVER = "db.driver";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_POOL_MAX_SIZE = "db.pool.maximumPoolSize";
    public static final String DB_POOL_MIN_IDLE = "db.pool.minimumIdle";
    public static final String DB_POOL_CONNECTION_TIMEOUT = "db.pool.connectionTimeout";
    public static final String DB_POOL_IDLE_TIMEOUT = "db.pool.idleTimeout";
    public static final String DB_POOL_MAX_LIFETIME = "db.pool.maxLifetime";

    /**
     * Inicializa el ConfigManager cargando las propiedades
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }

        try {
            // 1. Intentar cargar desde el classpath
            InputStream inputStream = Configuracion.class
                    .getClassLoader()
                    .getResourceAsStream("recursos/db.properties");

            if (inputStream == null) {
                throw new IOException("No se encontró el archivo db.properties en recursos/");
            }

            properties.load(inputStream);
            inputStream.close();

            // 2. Sobrescribir con variables de entorno si existen (mayor prioridad)
            overrideWithEnvironmentVariables();

            initialized = true;
            System.out.println("Configuración cargada exitosamente");

        } catch (IOException e) {
            System.err.println(" ERROR CRÍTICO: No se pudo cargar la configuración");
            e.printStackTrace();
            throw new RuntimeException("Fallo al cargar configuración de la aplicación", e);
        }
    }

    /**
     * Sobrescribe propiedades con variables de entorno si están definidas
     * Esto permite configuración diferente en producción sin cambiar archivos
     */
    private static void overrideWithEnvironmentVariables() {
        // Mapeo de variables de entorno a propiedades
        String[][] envMappings = {
                {"DB_URL", DB_URL},
                {"DB_USERNAME", DB_USERNAME},
                {"DB_PASSWORD", DB_PASSWORD},
                {"DB_DRIVER", DB_DRIVER}
        };

        for (String[] mapping : envMappings) {
            String envVar = System.getenv(mapping[0]);
            if (envVar != null && !envVar.isEmpty()) {
                properties.setProperty(mapping[1], envVar);
                System.out.println("Usando variable de entorno: " + mapping[0]);
            }
        }
    }

    /**
     * Obtiene una propiedad como String
     * @param key Clave de la propiedad
     * @return Valor de la propiedad o null si no existe
     */
    public static String getProperty(String key) {
        if (!initialized) {
            init();
        }
        return properties.getProperty(key);
    }

    /**
     * Obtiene una propiedad como String con valor por defecto
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe
     * @return Valor de la propiedad o defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        if (!initialized) {
            init();
        }
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Obtiene una propiedad como entero
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe o no es válido
     * @return Valor de la propiedad como int
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Valor inválido para " + key + ": " + value +
                    ". Usando valor por defecto: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Obtiene una propiedad como long
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe o no es válido
     * @return Valor de la propiedad como long
     */
    public static long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println(" Valor inválido para " + key + ": " + value +
                    ". Usando valor por defecto: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Verifica si una propiedad está definida
     * @param key Clave de la propiedad
     * @return true si existe, false si no
     */
    public static boolean hasProperty(String key) {
        if (!initialized) {
            init();
        }
        return properties.containsKey(key);
    }

    /**
     * Obtiene todas las propiedades (útil para debugging)
     *  NUNCA usar en producción para logging (expone contraseñas)
     */
    public static Properties getAllProperties() {
        if (!initialized) {
            init();
        }
        return (Properties) properties.clone();
    }

    /**
     * Valida que las propiedades críticas estén configuradas
     * @throws IllegalStateException si falta alguna propiedad crítica
     */
    public static void validateConfiguration() {
        if (!initialized) {
            init();
        }

        String[] requiredProperties = {
                DB_URL, DB_USERNAME, DB_PASSWORD, DB_DRIVER
        };

        StringBuilder missing = new StringBuilder();

        for (String prop : requiredProperties) {
            if (getProperty(prop) == null || getProperty(prop).isEmpty()) {
                missing.append("\n  - ").append(prop);
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    " Configuración incompleta. Propiedades faltantes:" + missing
            );
        }

        System.out.println("✅ Configuración validada correctamente");
    }
}