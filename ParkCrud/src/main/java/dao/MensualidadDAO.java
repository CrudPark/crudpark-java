package dao;

import config.DatabaseConfig;
import models.Mensualidad;

import java.sql.*;

public class MensualidadDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }


    // Buscar mensualidad vigente por placa (CRÍTICO para ingreso de vehículos)
    public Mensualidad findVigenteByPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM mensualidades " +
                "WHERE placa = ? AND activa = true " +
                "AND CURRENT_DATE BETWEEN fecha_inicio AND fecha_fin";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa.toUpperCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMensualidad(rs);
            }
        }
        return null;
    }



    // Mapear ResultSet a objeto Mensualidad
    private Mensualidad mapResultSetToMensualidad(ResultSet rs) throws SQLException {
        Mensualidad mensualidad = new Mensualidad();
        mensualidad.setId(rs.getInt("id"));
        mensualidad.setNombrePropietario(rs.getString("nombre_propietario"));
        mensualidad.setEmail(rs.getString("email"));
        mensualidad.setPlaca(rs.getString("placa"));

        Date fechaInicio = rs.getDate("fecha_inicio");
        if (fechaInicio != null) {
            mensualidad.setFechaInicio(fechaInicio.toLocalDate());
        }

        Date fechaFin = rs.getDate("fecha_fin");
        if (fechaFin != null) {
            mensualidad.setFechaFin(fechaFin.toLocalDate());
        }

        mensualidad.setActiva(rs.getBoolean("activa"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            mensualidad.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            mensualidad.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return mensualidad;
    }
}