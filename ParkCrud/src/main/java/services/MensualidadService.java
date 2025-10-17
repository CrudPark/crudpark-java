package services;

import dao.MensualidadDAO;
import models.Mensualidad;

import java.sql.SQLException;

/**
 * Servicio que maneja la lógica de negocio relacionada con las mensualidades.
 * Se utiliza principalmente durante el ingreso de vehículos.
 */
public class MensualidadService {

    private final MensualidadDAO mensualidadDAO;

    public MensualidadService() {
        this.mensualidadDAO = new MensualidadDAO();
    }

    /**
     * Busca una mensualidad vigente por placa.
     * @param placa Placa del vehículo ingresada por el operador.
     * @return La mensualidad si está vigente, o null si no existe o está vencida.
     */
    public Mensualidad obtenerMensualidadVigente(String placa) {
        try {
            return mensualidadDAO.findVigenteByPlaca(placa);
        } catch (SQLException e) {
            System.err.println("Error al buscar mensualidad vigente: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca una mensualidad por placa, sin importar si está vigente o no.
     * @param placa Placa del vehículo ingresada por el operador.
     * @return Mensualidad si existe (vigente o no), o null si no existe.
     */
    public Mensualidad obtenerMensualidadPorPlaca(String placa) {
        try {
            return mensualidadDAO.findByPlaca(placa);
        } catch (SQLException e) {
            System.err.println("Error al buscar mensualidad por placa: " + e.getMessage());
            return null;
        }
    }

    /**
     * Determina el tipo de cliente según la placa.
     * Si tiene mensualidad vigente → "Mensualidad".
     * Si no tiene o está vencida → "Invitado".
     */
    public String determinarTipoVehiculo(String placa) {
        Mensualidad mensualidad = obtenerMensualidadVigente(placa);
        return (mensualidad != null) ? "Mensualidad" : "Invitado";
    }

}
