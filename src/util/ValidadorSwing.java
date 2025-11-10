package util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Function;

/**
 *  NUEVA CLASE: JTextField con validación visual en tiempo real
 * Muestra borde rojo si el valor es inválido
 */
public class ValidadorSwing extends JTextField {

    private Function<String, String> validador;
    private final Border borderNormal;
    private final Border borderError;
    private JLabel labelError;
    private boolean obligatorio = true;

    /**
     * Constructor básico
     */
    public ValidadorSwing() {
        super();
        borderNormal = BorderFactory.createLineBorder(Color.GRAY, 1);
        borderError = BorderFactory.createLineBorder(Color.RED, 2);
        setBorder(borderNormal);
        inicializarListeners();
    }

    /**
     * Constructor con columnas
     */
    public ValidadorSwing(int columns) {
        super(columns);
        borderNormal = BorderFactory.createLineBorder(Color.GRAY, 1);
        borderError = BorderFactory.createLineBorder(Color.RED, 2);
        setBorder(borderNormal);
        inicializarListeners();
    }

    /**
     * Establece la función de validación
     * @param validador Función que recibe el texto y devuelve null si es válido o mensaje de error
     */
    public void setValidador(Function<String, String> validador) {
        this.validador = validador;
    }

    /**
     * Establece si el campo es obligatorio
     */
    public void setObligatorio(boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    /**
     * Asocia un JLabel para mostrar mensajes de error
     */
    public void setLabelError(JLabel labelError) {
        this.labelError = labelError;
        if (labelError != null) {
            labelError.setForeground(Color.RED);
            labelError.setText("");
        }
    }

    /**
     * Inicializa los listeners para validación en tiempo real
     */
    private void inicializarListeners() {
        // Validar al perder el foco
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validar();
            }
        });
    }

    /**
     * Valida el campo actual
     * @return true si es válido, false si no
     */
    public boolean validar() {
        if (validador == null) {
            return true; // Sin validador = siempre válido
        }

        String texto = getText();

        // Si no es obligatorio y está vacío, es válido
        if (!obligatorio && (texto == null || texto.trim().isEmpty())) {
            setBorder(borderNormal);
            if (labelError != null) {
                labelError.setText("");
            }
            return true;
        }

        String error = validador.apply(texto);

        if (error == null) {
            // Válido
            setBorder(borderNormal);
            setBackground(Color.WHITE);
            if (labelError != null) {
                labelError.setText("");
            }
            return true;
        } else {
            // Inválido
            setBorder(borderError);
            setBackground(new Color(255, 240, 240)); // Rojo muy claro
            if (labelError != null) {
                labelError.setText(error);
            }
            return false;
        }
    }

    /**
     * Obtiene el texto normalizado (sin espacios al inicio/final)
     */
    public String getTextoNormalizado() {
        String texto = getText();
        return texto != null ? texto.trim() : "";
    }

    /**
     * Resetea el campo (limpia y quita errores)
     */
    public void resetear() {
        setText("");
        setBorder(borderNormal);
        setBackground(Color.WHITE);
        if (labelError != null) {
            labelError.setText("");
        }
    }
}
