package views;

import controllers.OperadorController;
import models.Operador;

import javax.swing.*;
import java.awt.*;

/**
 * Vista para el inicio de sesión de operadores.
 * Llama al controlador para validar login y notifica a App cuando es exitoso.
 */
public class OperadorView extends JPanel {

    private final OperadorController operadorController;
    private JTextField txtNombre = null;
    private JButton btnLogin = null;
    private LoginListener loginListener;

    public OperadorView() {
        this.operadorController = new OperadorController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Login de Operador", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(45, 52, 54));
        add(lblTitulo, gbc);

        // Campo nombre
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Nombre del operador:"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        add(txtNombre, gbc);

        // Botón login
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(btnLogin, gbc);

        btnLogin.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa el nombre del operador.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Operador operador = operadorController.iniciarSesion(nombre);

        if (operador != null) {
            JOptionPane.showMessageDialog(this,
                    "Bienvenido, " + operador.getNombre() + "!",
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            if (loginListener != null) {
                loginListener.onLoginSuccess(operador);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se encontró un operador activo con ese nombre.",
                    "Error de inicio de sesión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Permite que App reciba el operador logueado
    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    // Interfaz para notificar login exitoso
    public interface LoginListener {
        void onLoginSuccess(Operador operador);
    }
}
