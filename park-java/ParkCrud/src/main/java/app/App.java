package app;

import controllers.TicketController;
import models.Operador;
import views.OperadorView;
import views.TicketView;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal que maneja la aplicación CrudPark.
 * Controla el flujo entre login y panel de tickets.
 */
public class App {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private OperadorView operadorView;
    private TicketView ticketView;
    private TicketController ticketController;

    public App() {
        ticketController = new TicketController();
        initUI();
    }

    private void initUI() {
        frame = new JFrame("CrudPark - Crudzaso");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        operadorView = new OperadorView();
        ticketView = new TicketView(ticketController);


        operadorView.setLoginListener(operador -> loginExitoso(operador));

        mainPanel.add(operadorView, "LOGIN");
        mainPanel.add(ticketView, "TICKETS");

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        showPanel("LOGIN");
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }


    private void loginExitoso(Operador operador) {

        ticketView.setOperador(operador);
        showPanel("TICKETS");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
