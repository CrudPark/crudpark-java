package services;

import dao.OperadorDAO;
import models.Operador;

import java.sql.SQLException;
import java.util.List;

/**
 * Service encargado de la lógica de negocio relacionada con los operadores.
 *
 * Se centra únicamente en:
 *  - Validar inicio de sesión del operador activo.
 *  - Consultar datos de operadores activos (solo lectura).
 */
public class OperadorService {

    private final OperadorDAO operadorDAO;

    public OperadorService() {
        this.operadorDAO = new OperadorDAO();
    }

    /**
     * Valida las credenciales de un operador para el inicio de sesión.
     *
     * @param nombre Nombre del operador ingresado.
     * @return Operador si existe y está activo, de lo contrario null.
     */
    public Operador validarInicioSesion(String nombre) {
        try {
            return operadorDAO.validarOperadorActivo(nombre);
        } catch (SQLException e) {
            System.err.println("Error al validar inicio de sesión: " + e.getMessage());
            return null;
        }
    }

}
