package dao;

import config.DatabaseConfig;
import models.Pago;

import java.sql.*;
import java.time.LocalDateTime;

public class PagoDAO {

    private DatabaseConfig DatabaseConfig;

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }


    // Crear nuevo pago
    public Pago create(Pago pago, Connection conn) throws SQLException {
        String sql = "INSERT INTO pagos (ticket_id, monto, metodo_pago, operador_id, fecha_pago, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pago.getTicketId());
            stmt.setBigDecimal(2, pago.getMonto());
            stmt.setString(3, pago.getMetodoPago());
            stmt.setInt(4, pago.getOperadorId());
            stmt.setTimestamp(5, Timestamp.valueOf(pago.getFechaPago() != null ?
                    pago.getFechaPago() : LocalDateTime.now()));
            stmt.setString(6, pago.getObservaciones());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pago.setId(rs.getInt("id"));
                return pago;
            }
        }
        return null;
    }

    public Pago create(Pago pago) throws SQLException {
        String sql = "INSERT INTO pagos (ticket_id, monto, metodo_pago, operador_id, fecha_pago, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try ( Connection conn = getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pago.getTicketId());
            stmt.setBigDecimal(2, pago.getMonto());
            stmt.setString(3, pago.getMetodoPago());
            stmt.setInt(4, pago.getOperadorId());
            stmt.setTimestamp(5, Timestamp.valueOf(pago.getFechaPago() != null ?
                    pago.getFechaPago() : LocalDateTime.now()));
            stmt.setString(6, pago.getObservaciones());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pago.setId(rs.getInt("id"));
                return pago;
            }
        }
        return null;
    }

}
