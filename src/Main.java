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

            Object[] opciones = {
                    "Soy Empleado (Gerente/Vendedor)",
                    "Soy Cliente (Iniciar Sesión)",
                    "Ver Catálogo (Invitado)"
            };
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido a la Agencia de Autos",
                    "Seleccionar Acceso",
                    JOptionPane.YES_NO_CANCEL_OPTION, // Cambiado
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            // 2. Abrir la ventana correspondiente
            if (seleccion == 0) {
                // 0 = Empleado
                new LoginPanel().setVisible(true);

            }else if (seleccion == 1) {
                // 1 = Cliente (Iniciar Sesión)
                ClienteLoginDialog loginDialog = new ClienteLoginDialog(null);
                loginDialog.setVisible(true);

                Cliente clienteAutenticado = loginDialog.getClienteAutenticado();
                if (clienteAutenticado != null) {
                    new PortalCliente(clienteAutenticado).setVisible(true);
                }

            } else if (seleccion == 2) {
                // 2 = Invitado
                new PortalCliente(null).setVisible(true); // Abre el portal con cliente NULL

            } else {
                System.exit(0);
            }
        });
    }
}