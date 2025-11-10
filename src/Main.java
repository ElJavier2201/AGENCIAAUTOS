import vista.LoginPanel;
import vista.PortalCliente;
import vista.ClienteLoginDialog;
import modelo.Cliente;
import util.ConexionDB;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la aplicación Agencia de Autos.
 * CORREGIDO: Ahora incluye login para clientes y cierre del pool
 */
public class Main {
    public static void main(String[] args) {

        // Registrar shutdown hook para cerrar el pool al salir
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Cerrando aplicación...");
            ConexionDB.cerrarPool();
            System.out.println("Aplicación cerrada correctamente");
        }));

        // Asegura que la UI se ejecute en el hilo de Swing
        SwingUtilities.invokeLater(() -> {

            // 1. Preguntar al usuario su rol
            Object[] opciones = {"Soy Empleado (Iniciar Sesión)", "Soy Cliente (Ver Catálogo)"};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido a la Agencia de Autos",
                    "Seleccionar Acceso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            // 2. Abrir la ventana correspondiente
            if (seleccion == 0) {
                // 0 = Empleado
                new LoginPanel().setVisible(true);

            } else if (seleccion == 1) {
                // 1 = Cliente
                // CORRECCIÓN: Primero hacer login del cliente
                ClienteLoginDialog loginDialog = new ClienteLoginDialog(null);
                loginDialog.setVisible(true);

                // Si el login fue exitoso, abrir el portal
                Cliente clienteAutenticado = loginDialog.getClienteAutenticado();
                if (clienteAutenticado != null) {
                    new PortalCliente(clienteAutenticado).setVisible(true);
                } else {
                    // Si canceló o falló el login, volver a mostrar opciones
                    JOptionPane.showMessageDialog(null,
                            "No se pudo autenticar. La aplicación se cerrará.",
                            "Acceso Cancelado",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }
}