package dao;

import config.DatabaseConfig;
import models.Ticket;

import java.sql.*;

public class TicketDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Buscar ticket abierto por placa (CRÍTICO - verifica si hay ticket sin salida)
     */
    public Ticket findTicketAbiertoByPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE placa = ? AND fecha_salida IS NULL " +
                "AND activo = true ORDER BY fecha_ingreso DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa.toUpperCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        }
        return null;
    }



    /**
     * Crear nuevo ticket (ingreso de vehículo)
     */
    public Ticket create(Ticket ticket, Connection conn) throws SQLException {
        String sql = "INSERT INTO tickets (numero_folio, placa, tipo_ingreso, fecha_ingreso, " +
                "operador_ingreso_id, qr_code, monto_cobrado, pagado, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getNumeroFolio());
            stmt.setString(2, ticket.getPlaca().toUpperCase());
            stmt.setString(3, ticket.getTipoIngreso());
            stmt.setTimestamp(4, Timestamp.valueOf(ticket.getFechaIngreso()));
            stmt.setInt(5, ticket.getOperadorIngresoId());
            stmt.setString(6, ticket.getQrCode());
            stmt.setBigDecimal(7, ticket.getMontoCobrado());
            stmt.setBoolean(8, ticket.isPagado());
            stmt.setBoolean(9, ticket.isActivo());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ticket.setId(rs.getInt("id"));
                return ticket;
            }
        }
        return null;
    }

    /**
     * Registrar salida de vehículo
     */
    public boolean registrarSalida(int ticketId, int operadorSalidaId, int tiempoEstadiaMinutos, Connection conn) throws SQLException {
        String sql = "UPDATE tickets SET fecha_salida = CURRENT_TIMESTAMP, " +
                "operador_salida_id = ?, tiempo_estadia_minutos = ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ? AND fecha_salida IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, operadorSalidaId);
            stmt.setInt(2, tiempoEstadiaMinutos);
            stmt.setInt(3, ticketId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualizar monto cobrado y marcar como pagado
     */
    public boolean registrarPago(int ticketId, java.math.BigDecimal monto, Connection conn) throws SQLException {
        String sql = "UPDATE tickets SET monto_cobrado = ?, pagado = true, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, monto);
            stmt.setInt(2, ticketId);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean registrarPago(int ticketId, java.math.BigDecimal monto) throws SQLException {
        String sql = "UPDATE tickets SET monto_cobrado = ?, pagado = true, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";

        try ( Connection conn = getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, monto);
            stmt.setInt(2, ticketId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Generar número de folio único
     */
    public String generarNumeroFolio() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero_folio FROM 4) AS INTEGER)), 0) + 1 " +
                "FROM tickets WHERE numero_folio LIKE 'TKT%'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int numero = rs.getInt(1);
                return String.format("TKT%06d", numero);
            }
        }
        return "TKT000001";
    }

    /**
     * Contar tickets abiertos (vehículos en el parqueadero)
     */
    public int contarTicketsAbiertos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE fecha_salida IS NULL AND activo = true";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Verificar si existe ticket abierto para una placa
     */
    public boolean existeTicketAbierto(String placa) throws SQLException {
        return findTicketAbiertoByPlaca(placa) != null;
    }

    /**
     * Mapear ResultSet a objeto Ticket
     * ✅ CORREGIDO: Manejo apropiado de campos NULL
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setNumeroFolio(rs.getString("numero_folio"));
        ticket.setPlaca(rs.getString("placa"));
        ticket.setTipoIngreso(rs.getString("tipo_ingreso"));
        ticket.setQrCode(rs.getString("qr_code"));
        ticket.setMontoCobrado(rs.getBigDecimal("monto_cobrado"));
        ticket.setPagado(rs.getBoolean("pagado"));
        ticket.setActivo(rs.getBoolean("activo"));

        // ✅ Campos Integer que pueden ser NULL
        int operadorIngresoId = rs.getInt("operador_ingreso_id");
        if (!rs.wasNull()) {
            ticket.setOperadorIngresoId(operadorIngresoId);
        }

        int operadorSalidaId = rs.getInt("operador_salida_id");
        if (!rs.wasNull()) {
            ticket.setOperadorSalidaId(operadorSalidaId);
        }

        int tiempoEstadia = rs.getInt("tiempo_estadia_minutos");
        if (!rs.wasNull()) {
            ticket.setTiempoEstadiaMinutos(tiempoEstadia);
        }

        // ✅ Timestamps
        Timestamp fechaIngreso = rs.getTimestamp("fecha_ingreso");
        if (fechaIngreso != null) {
            ticket.setFechaIngreso(fechaIngreso.toLocalDateTime());
        }

        Timestamp fechaSalida = rs.getTimestamp("fecha_salida");
        if (fechaSalida != null) {
            ticket.setFechaSalida(fechaSalida.toLocalDateTime());
        }

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            ticket.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            ticket.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        return ticket;
    }
}
