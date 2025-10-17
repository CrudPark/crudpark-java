package controllers;

import models.Operador;
import services.OperadorService;

import javax.swing.JOptionPane;

/**
 * Controlador encargado de gestionar las acciones relacionadas con los operadores.
 * Actúa como intermediario entre la vista (interfaz) y el servicio OperadorService.
 */
public class OperadorController {

    private final OperadorService operadorService;

    public OperadorController() {
        this.operadorService = new OperadorService();
    }

    /**
     * Inicia sesión de un operador activo.
     *
     * @param nombre Nombre del operador que intenta iniciar sesión.
     * @return El objeto Operador si las credenciales son válidas, o null si no.
     */
    public Operador iniciarSesion(String nombre) {
        Operador operador = operadorService.validarInicioSesion(nombre);

        if (operador != null) {
            JOptionPane.showMessageDialog(null,
                    " Inicio de sesión exitoso.\nBienvenido, " + operador.getNombre() + "!",
                    "Inicio de sesión",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    " No se encontró un operador activo con ese nombre.",
                    "Error de inicio de sesión",
                    JOptionPane.ERROR_MESSAGE);
        }

        return operador;
    }

}

