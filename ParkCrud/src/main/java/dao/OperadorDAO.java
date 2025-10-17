package dao;

import config.DatabaseConfig;
import models.Operador;

import java.sql.*;

/**
 * DAO para manejar la interacción con la tabla 'operadores'.
 *
 * Solo realiza operaciones de consulta y validación,
 * ya que los operadores deben estar previamente registrados.
 */
public class OperadorDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Buscar operador por ID.
     */
    public Operador findById(int id) throws SQLException {
        String sql = "SELECT * FROM operadores WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperador(rs);
            }
        }
        return null;
    }

    /**
     * Validar operador por nombre y estado activo (para login).
     */
    public Operador validarOperadorActivo(String nombre) throws SQLException {
        String sql = "SELECT * FROM operadores WHERE nombre = ? AND activo = true";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperador(rs);
            }
        }
        return null; // No encontrado o inactivo
    }


    /**
     * Mapea un registro de ResultSet al objeto Operador.
     */
    private Operador mapResultSetToOperador(ResultSet rs) throws SQLException {
        Operador operador = new Operador();
        operador.setId(rs.getInt("id"));
        operador.setNombre(rs.getString("nombre"));
        operador.setEmail(rs.getString("email"));
        operador.setActivo(rs.getBoolean("activo"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            operador.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            operador.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return operador;
    }
}
