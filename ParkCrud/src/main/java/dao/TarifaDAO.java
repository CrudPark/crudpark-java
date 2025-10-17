package dao;

import config.DatabaseConfig;
import models.Tarifa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // Buscar tarifa por ID
    public Tarifa findById(int id) throws SQLException {
        String sql = "SELECT * FROM tarifas WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTarifa(rs);
            }
        }
        return null;
    }

    // Obtener la tarifa activa (CRÍTICO para cálculo de cobros)
    public Tarifa findTarifaActiva() throws SQLException {
        String sql = "SELECT * FROM tarifas WHERE activa = true ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return mapResultSetToTarifa(rs);
            }
        }
        return null;
    }


    // Mapear ResultSet a objeto Tarifa
    private Tarifa mapResultSetToTarifa(ResultSet rs) throws SQLException {
        Tarifa tarifa = new Tarifa();
        tarifa.setId(rs.getInt("id"));
        tarifa.setNombre(rs.getString("nombre"));
        tarifa.setValorBaseHora(rs.getBigDecimal("valor_base_hora"));
        tarifa.setValorFraccion(rs.getBigDecimal("valor_fraccion"));
        tarifa.setTopeDiario(rs.getBigDecimal("tope_diario"));
        tarifa.setTiempoGraciaMinutos(rs.getInt("tiempo_gracia_minutos"));
        tarifa.setActiva(rs.getBoolean("activa"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            tarifa.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            tarifa.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return tarifa;
    }
}