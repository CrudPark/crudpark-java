package views;

import controllers.TicketController;
import exceptios.BusinessException;
import models.Ticket;
import models.Operador;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Vista para la gestión de tickets.
 * Muestra ingreso, salida y pago manual de vehículos.
 * Implementa manejo de excepciones de negocio (BusinessException) del Controller.
 */
public class TicketView extends JPanel {

    private final TicketController ticketController;
    private JTextField txtPlaca;
    private JComboBox<String> comboMetodoPago;
    private JTextField txtMontoPago;
    private JLabel lblOperador;
    private Operador operadorActual;

    public TicketView(TicketController ticketController) {
        this.ticketController = ticketController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior: operador
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblOperador = new JLabel("Operador: -");
        lblOperador.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelTop.add(lblOperador);
        add(panelTop, BorderLayout.NORTH);

        // Panel central: formulario
        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Placa
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCenter.add(new JLabel("Placa:"), gbc);

        gbc.gridx = 1;
        txtPlaca = new JTextField(12);
        panelCenter.add(txtPlaca, gbc);

        // Método de pago
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCenter.add(new JLabel("Método de pago:"), gbc);

        gbc.gridx = 1;
        // Se cambió "QR" por "Transferencia" para ser consistente con el Service
        comboMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        panelCenter.add(comboMetodoPago, gbc);

        // Monto para pago manual
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCenter.add(new JLabel("Monto pago manual:"), gbc);

        gbc.gridx = 1;
        txtMontoPago = new JTextField(12);
        panelCenter.add(txtMontoPago, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnIngreso = new JButton("Registrar Ingreso");
        btnIngreso.setBackground(new Color(52, 152, 219));
        btnIngreso.setForeground(Color.WHITE);
        btnIngreso.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngreso.setFocusPainted(false);
        btnIngreso.addActionListener(e -> registrarIngreso());

        JButton btnSalida = new JButton("Registrar Salida");
        btnSalida.setBackground(new Color(46, 204, 113));
        btnSalida.setForeground(Color.WHITE);
        btnSalida.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalida.setFocusPainted(false);
        btnSalida.addActionListener(e -> registrarSalida());

        JButton btnPago = new JButton("Registrar Pago Manual");
        btnPago.setBackground(new Color(231, 76, 60));
        btnPago.setForeground(Color.WHITE);
        btnPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPago.setFocusPainted(false);
        btnPago.addActionListener(e -> registrarPagoManual());

        panelBotones.add(btnIngreso);
        panelBotones.add(btnSalida);
        panelBotones.add(btnPago);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelCenter.add(panelBotones, gbc);

        add(panelCenter, BorderLayout.CENTER);
    }

    public void setOperador(Operador operador) {
        this.operadorActual = operador;
        lblOperador.setText("Operador: " + operador.getNombre() + " (ID: " + operador.getId() + ")");
    }

    private void registrarIngreso() {
        if (operadorActual == null) {
            mostrarError("No hay operador logueado.");
            return;
        }
        String placa = txtPlaca.getText().trim();
        if (placa.isEmpty()) {
            mostrarError("Ingrese la placa del vehículo.");
            return;
        }

        try {
            Ticket ticket = ticketController.registrarIngreso(placa, operadorActual.getId());
            // Si el Controller tiene éxito, retorna el ticket
            mostrarExito("Ingreso registrado correctamente.\nFolio: " + ticket.getNumeroFolio() + "\nPlaca: " + ticket.getPlaca());
            limpiarCampos();
        } catch (BusinessException e) {
            // Captura el error de negocio del Controller y muestra el mensaje específico
            mostrarError(e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error no esperado
            mostrarError("Error crítico inesperado al registrar el ingreso.");
        }
    }

    private void registrarSalida() {
        if (operadorActual == null) {
            mostrarError("No hay operador logueado.");
            return;
        }
        String placa = txtPlaca.getText().trim();
        // Usamos "Transferencia" para ser consistente con el service
        String metodo = comboMetodoPago.getSelectedItem().toString();
        if (placa.isEmpty()) {
            mostrarError("Ingrese la placa del vehículo.");
            return;
        }

        try {
            // CAMBIO CLAVE: Esperamos un BigDecimal como resultado del Controller
            BigDecimal montoCobrado = ticketController.registrarSalida(placa, operadorActual.getId(), metodo);

            String mensaje = "Salida registrada correctamente.";

            if (montoCobrado.compareTo(BigDecimal.ZERO) > 0) {
                // Monto mayor a cero: cobro
                mensaje += "\n\n💵 MONTO A COBRAR: $" + montoCobrado.toPlainString();
                mensaje += "\nMétodo de pago registrado: " + metodo;
            } else {
                // Monto cero: mensualidad o tiempo de gracia
                mensaje += "\n\nVehículo con mensualidad o dentro del tiempo de gracia.";
                mensaje += "\nMONTO COBRADO: $0.00";
            }

            mostrarExito(mensaje);
            limpiarCampos();

        } catch (BusinessException e) {
            // Captura el error de negocio del Controller y muestra el mensaje específico
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error crítico inesperado al registrar la salida.");
        }
    }

    private void registrarPagoManual() {
        if (operadorActual == null) {
            mostrarError("No hay operador logueado.");
            return;
        }
        String placa = txtPlaca.getText().trim();
        String metodo = comboMetodoPago.getSelectedItem().toString();
        String montoStr = txtMontoPago.getText().trim();

        if (placa.isEmpty() || montoStr.isEmpty()) {
            mostrarError("Ingrese placa y monto para el pago manual.");
            return;
        }

        try {
            BigDecimal monto = new BigDecimal(montoStr);
            boolean ok = ticketController.registrarPagoManual(placa, monto, metodo, operadorActual.getId());
            if (ok) {
                mostrarExito("Pago manual registrado correctamente.\nMonto: $" + monto);
                limpiarCampos();
            } else {
                mostrarError("Error al registrar pago manual.");
            }
        } catch (NumberFormatException e) {
            mostrarError("Monto inválido. Ingrese un número válido.");
        } catch (BusinessException e) {
            // Captura el error de negocio del Controller y muestra el mensaje específico
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error crítico inesperado al registrar el pago manual.");
        }
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void limpiarCampos() {
        txtPlaca.setText("");
        txtMontoPago.setText("");
        comboMetodoPago.setSelectedIndex(0);
    }
}