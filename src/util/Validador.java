package util;

import java.util.regex.Pattern;

/**
 * NUEVA CLASE: Utilidades de validación para campos del sistema
 * Centraliza todas las reglas de validación de formato
 */
public class Validador {

    // ========== PATRONES REGEX ==========

    // Email: formato estándar RFC 5322 simplificado
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // RFC (Persona Física): 13 caracteres - AAAA######XXX
    private static final Pattern RFC_FISICA_PATTERN = Pattern.compile(
            "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$"
    );

    // RFC (Persona Moral): 12 caracteres - AAA######XXX
    private static final Pattern RFC_MORAL_PATTERN = Pattern.compile(
            "^[A-ZÑ&]{3}\\d{6}[A-Z0-9]{3}$"
    );

    // Teléfono México: 10 dígitos (puede tener guiones, paréntesis, espacios)
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
            "^[\\d\\s()+-]{10,15}$"
    );

    // Teléfono solo números: exactamente 10 dígitos
    private static final Pattern TELEFONO_SIMPLE_PATTERN = Pattern.compile(
            "^\\d{10}$"
    );

    // VIN (Vehicle Identification Number): 17 caracteres alfanuméricos
    private static final Pattern VIN_PATTERN = Pattern.compile(
            "^[A-HJ-NPR-Z0-9]{17}$"
    );

    // Código Postal México: 5 dígitos
    private static final Pattern CP_PATTERN = Pattern.compile(
            "^\\d{5}$"
    );

    // Nombre/Apellido: solo letras, espacios, acentos, ñ
    private static final Pattern NOMBRE_PATTERN = Pattern.compile(
            "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{2,50}$"
    );

    // Usuario: alfanumérico, guiones bajos, 4-20 caracteres
    private static final Pattern USUARIO_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_]{4,20}$"
    );

    // Contraseña: mínimo 8 caracteres, al menos 1 mayúscula, 1 minúscula, 1 número
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

    // Constructor privado para evitar instanciación
    private Validador() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    // ========== VALIDACIÓN DE EMAIL ==========

    /**
     * Valida formato de email
     * @param email Email a validar
     * @return true si es válido, false si no
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida email y devuelve mensaje de error si es inválido
     * @param email Email a validar
     * @return null si es válido, mensaje de error si no
     */
    public static String validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El email es obligatorio";
        }

        if (!esEmailValido(email)) {
            return "Formato de email inválido. Ejemplo: usuario@dominio.com";
        }

        return null; // Válido
    }

    // ========== VALIDACIÓN DE RFC ==========

    /**
     * Valida RFC (Persona Física o Moral)
     * @param rfc RFC a validar
     * @return true si es válido, false si no
     */
    public static boolean esRfcValido(String rfc) {
        if (rfc == null || rfc.trim().isEmpty()) {
            return false;
        }

        String rfcLimpio = rfc.trim().toUpperCase();

        // Puede ser persona física (13) o moral (12)
        return RFC_FISICA_PATTERN.matcher(rfcLimpio).matches() ||
                RFC_MORAL_PATTERN.matcher(rfcLimpio).matches();
    }

    /**
     * Valida RFC y devuelve mensaje de error
     * @param rfc RFC a validar
     * @param obligatorio Si es campo obligatorio
     * @return null si es válido, mensaje de error si no
     */
    public static String validarRfc(String rfc, boolean obligatorio) {
        if (rfc == null || rfc.trim().isEmpty()) {
            return obligatorio ? "El RFC es obligatorio" : null;
        }

        if (!esRfcValido(rfc)) {
            return "RFC inválido. Debe tener 12 caracteres (Persona Moral) o 13 (Persona Física). Ejemplo: XAXX010101000";
        }

        return null;
    }

    /**
     * Normaliza RFC (convierte a mayúsculas y elimina espacios)
     * @param rfc RFC a normalizar
     * @return RFC normalizado
     */
    public static String normalizarRfc(String rfc) {
        if (rfc == null) return null;
        return rfc.trim().toUpperCase().replaceAll("\\s", "");
    }

    // ========== VALIDACIÓN DE TELÉFONO ==========

    /**
     * Valida teléfono (acepta diferentes formatos)
     * @param telefono Teléfono a validar
     * @return true si es válido, false si no
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }

        return TELEFONO_PATTERN.matcher(telefono.trim()).matches();
    }

    /**
     * Valida que el teléfono tenga exactamente 10 dígitos
     * @param telefono Teléfono a validar
     * @return true si tiene 10 dígitos, false si no
     */
    public static boolean esTelefonoSimpleValido(String telefono) {
        if (telefono == null) return false;
        String soloNumeros = telefono.replaceAll("\\D", "");
        return TELEFONO_SIMPLE_PATTERN.matcher(soloNumeros).matches();
    }

    /**
     * Valida teléfono y devuelve mensaje de error
     * @param telefono Teléfono a validar
     * @param obligatorio Si es campo obligatorio
     * @return null si es válido, mensaje de error si no
     */
    public static String validarTelefono(String telefono, boolean obligatorio) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return obligatorio ? "El teléfono es obligatorio" : null;
        }

        if (!esTelefonoValido(telefono)) {
            return "Formato de teléfono inválido. Debe tener 10 dígitos";
        }

        // Validación adicional: debe tener 10 dígitos numéricos
        if (!esTelefonoSimpleValido(telefono)) {
            return "El teléfono debe tener exactamente 10 dígitos";
        }

        return null;
    }

    /**
     * Normaliza teléfono (elimina todo excepto dígitos)
     * @param telefono Teléfono a normalizar
     * @return Solo dígitos del teléfono
     */
    public static String normalizarTelefono(String telefono) {
        if (telefono == null) return null;
        return telefono.replaceAll("\\D", "");
    }

    /**
     * Formatea teléfono al estilo: (555) 123-4567
     * @param telefono Teléfono a formatear
     * @return Teléfono formateado o original si es inválido
     */
    public static String formatearTelefono(String telefono) {
        String limpio = normalizarTelefono(telefono);

        if (limpio == null || limpio.length() != 10) {
            return telefono;
        }

        return String.format("(%s) %s-%s",
                limpio.substring(0, 3),
                limpio.substring(3, 6),
                limpio.substring(6, 10)
        );
    }

    // ========== VALIDACIÓN DE VIN ==========

    /**
     * Valida VIN (Vehicle Identification Number)
     * @param vin VIN a validar
     * @return true si es válido, false si no
     */
    public static boolean esVinValido(String vin) {
        if (vin == null || vin.trim().isEmpty()) {
            return false;
        }

        String vinLimpio = vin.trim().toUpperCase();

        // No debe contener I, O, Q (confusión con 1, 0)
        if (vinLimpio.contains("I") || vinLimpio.contains("O") || vinLimpio.contains("Q")) {
            return false;
        }

        return VIN_PATTERN.matcher(vinLimpio).matches();
    }

    /**
     * Valida VIN y devuelve mensaje de error
     * @param vin VIN a validar
     * @return null si es válido, mensaje de error si no
     */
    public static String validarVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) {
            return "El VIN (Número de Serie) es obligatorio";
        }

        if (!esVinValido(vin)) {
            return "VIN inválido. Debe tener exactamente 17 caracteres alfanuméricos (sin I, O, Q)";
        }

        return null;
    }

    /**
     * Normaliza VIN (mayúsculas, sin espacios)
     * @param vin VIN a normalizar
     * @return VIN normalizado
     */
    public static String normalizarVin(String vin) {
        if (vin == null) return null;
        return vin.trim().toUpperCase().replaceAll("\\s", "");
    }

    // ========== VALIDACIÓN DE CÓDIGO POSTAL ==========

    /**
     * Valida código postal de México
     * @param cp Código postal a validar
     * @return true si es válido, false si no
     */
    public static boolean esCodigoPostalValido(String cp) {
        if (cp == null || cp.trim().isEmpty()) {
            return false;
        }
        return CP_PATTERN.matcher(cp.trim()).matches();
    }

    /**
     * Valida código postal y devuelve mensaje de error
     * @param cp Código postal a validar
     * @param obligatorio Si es campo obligatorio
     * @return null si es válido, mensaje de error si no
     */
    public static String validarCodigoPostal(String cp, boolean obligatorio) {
        if (cp == null || cp.trim().isEmpty()) {
            return obligatorio ? "El código postal es obligatorio" : null;
        }

        if (!esCodigoPostalValido(cp)) {
            return "Código postal inválido. Debe tener 5 dígitos";
        }

        return null;
    }

    // ========== VALIDACIÓN DE NOMBRES ==========

    /**
     * Valida nombre o apellido
     * @param nombre Nombre a validar
     * @return true si es válido, false si no
     */
    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return NOMBRE_PATTERN.matcher(nombre.trim()).matches();
    }

    /**
     * Valida nombre/apellido y devuelve mensaje de error
     * @param nombre Nombre a validar
     * @param campo Nombre del campo (para el mensaje)
     * @return null si es válido, mensaje de error si no
     */
    public static String validarNombre(String nombre, String campo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El " + campo + " es obligatorio";
        }

        if (nombre.trim().length() < 2) {
            return "El " + campo + " debe tener al menos 2 caracteres";
        }

        if (nombre.trim().length() > 50) {
            return "El " + campo + " no puede exceder 50 caracteres";
        }

        if (!esNombreValido(nombre)) {
            return "El " + campo + " solo puede contener letras, espacios y acentos";
        }

        return null;
    }

    /**
     * Normaliza nombre (capitaliza primera letra de cada palabra)
     * @param nombre Nombre a normalizar
     * @return Nombre capitalizado
     */
    public static String normalizarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return nombre;
        }

        String[] palabras = nombre.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    // ========== VALIDACIÓN DE USUARIO ==========

    /**
     * Valida nombre de usuario
     * @param usuario Usuario a validar
     * @return true si es válido, false si no
     */
    public static boolean esUsuarioValido(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return false;
        }
        return USUARIO_PATTERN.matcher(usuario.trim()).matches();
    }

    /**
     * Valida usuario y devuelve mensaje de error
     * @param usuario Usuario a validar
     * @return null si es válido, mensaje de error si no
     */
    public static String validarUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return "El usuario es obligatorio";
        }

        if (!esUsuarioValido(usuario)) {
            return "Usuario inválido. Debe tener 4-20 caracteres (letras, números, guión bajo)";
        }

        return null;
    }

    // ========== VALIDACIÓN DE CONTRASEÑA ==========

    /**
     * Valida fortaleza de contraseña
     * @param password Contraseña a validar
     * @return true si es válida, false si no
     */
    public static boolean esPasswordValida(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Valida contraseña y devuelve mensaje de error
     * @param password Contraseña a validar
     * @return null si es válida, mensaje de error si no
     */
    public static String validarPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña es obligatoria";
        }

        if (password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }

        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una minúscula";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una mayúscula";
        }

        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número";
        }

        return null;
    }

    // ========== VALIDACIONES NUMÉRICAS ==========

    /**
     * Valida que un número esté en un rango
     * @param valor Valor a validar
     * @param min Valor mínimo
     * @param max Valor máximo
     * @param campo Nombre del campo (para el mensaje)
     * @return null si es válido, mensaje de error si no
     */
    public static String validarRango(double valor, double min, double max, String campo) {
        if (valor < min || valor > max) {
            return String.format("%s debe estar entre %.2f y %.2f", campo, min, max);
        }
        return null;
    }

    /**
     * Valida que un número sea positivo
     * @param valor Valor a validar
     * @param campo Nombre del campo (para el mensaje)
     * @return null si es válido, mensaje de error si no
     */
    public static String validarPositivo(double valor, String campo) {
        if (valor <= 0) {
            return campo + " debe ser mayor a cero";
        }
        return null;
    }

    /**
     * Valida que un string no esté vacío
     * @param valor Valor a validar
     * @param campo Nombre del campo (para el mensaje)
     * @return null si es válido, mensaje de error si no
     */
    public static String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            return "El campo " + campo + " es obligatorio";
        }
        return null;
    }
}
