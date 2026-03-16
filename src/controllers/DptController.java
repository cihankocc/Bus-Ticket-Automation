package controllers;

import models.Dpt;
import utils.ConnectionManager;
import utils.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DptController {

    private static final int SEAT_COUNT = 40;

    public String addDpt(String departureCity, String arrivalCity, String date, String time,
            double ticketPrice, int busId, int availableSeats, String routeInfo) throws DatabaseException {
        String dptSql = "INSERT INTO Dpt (departure_city, arrival_city, date, time, ticket_price, bus_id, available_seats, route_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String seatSql = "INSERT INTO Seat (seat_number, status, dpt_id) VALUES (?, 'available', ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int newDptId;
                try (PreparedStatement stmt = conn.prepareStatement(dptSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, departureCity);
                    stmt.setString(2, arrivalCity);
                    stmt.setString(3, date);
                    stmt.setString(4, time);
                    stmt.setDouble(5, ticketPrice);
                    stmt.setInt(6, busId);
                    stmt.setInt(7, availableSeats);
                    stmt.setString(8, routeInfo);
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next())
                            throw new DatabaseException("Sefer ID alınamadı.");
                        newDptId = keys.getInt(1);
                    }
                }
                try (PreparedStatement seatStmt = conn.prepareStatement(seatSql)) {
                    for (int i = 1; i <= SEAT_COUNT; i++) {
                        seatStmt.setInt(1, i);
                        seatStmt.setInt(2, newDptId);
                        seatStmt.addBatch();
                    }
                    seatStmt.executeBatch();
                }
                conn.commit();
                return "Sefer ve " + SEAT_COUNT + " koltuk başarıyla oluşturuldu.";
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Sefer ekleme sırasında hata oluştu.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Bağlantı hatası.", e);
        }
    }

    public String updateDpt(int departureId, String departureCity, String arrivalCity, String date, String time,
            double ticketPrice, int busId, int availableSeats, String routeInfo) throws DatabaseException {
        String sql = "UPDATE Dpt SET departure_city=?, arrival_city=?, date=?, time=?, ticket_price=?, bus_id=?, available_seats=?, route_info=? WHERE departure_id=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departureCity);
            stmt.setString(2, arrivalCity);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setDouble(5, ticketPrice);
            stmt.setInt(6, busId);
            stmt.setInt(7, availableSeats);
            stmt.setString(8, routeInfo);
            stmt.setInt(9, departureId);
            stmt.executeUpdate();
            return "Sefer başarıyla güncellendi.";
        } catch (SQLException e) {
            throw new DatabaseException("Sefer güncelleme sırasında hata oluştu.", e);
        }
    }

    public String deleteDpt(int departureId) throws DatabaseException {
        String sql = "DELETE FROM Dpt WHERE departure_id=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departureId);
            stmt.executeUpdate();
            return "Sefer başarıyla silindi.";
        } catch (SQLException e) {
            throw new DatabaseException("Sefer silme sırasında hata oluştu.", e);
        }
    }

    public List<Dpt> getAllDpts() throws DatabaseException {
        List<Dpt> dptList = new ArrayList<>();
        String sql = "SELECT * FROM Dpt";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                dptList.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Seferleri listeleme sırasında hata oluştu.", e);
        }
        return dptList;
    }

    public Dpt getDptById(int departureId) throws DatabaseException {
        String sql = "SELECT * FROM Dpt WHERE departure_id=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Sefer bilgisi getirilirken hata oluştu.", e);
        }
        return null;
    }

    private Dpt mapResultSet(ResultSet rs) throws SQLException {
        Dpt dpt = new Dpt();
        dpt.setDepartureId(rs.getInt("departure_id"));
        dpt.setDepartureCity(rs.getString("departure_city"));
        dpt.setArrivalCity(rs.getString("arrival_city"));
        dpt.setDate(rs.getString("date"));
        dpt.setTime(rs.getString("time"));
        dpt.setPrice(rs.getDouble("ticket_price"));
        dpt.setBusId(rs.getInt("bus_id"));
        dpt.setAvailableSeats(rs.getInt("available_seats"));
        dpt.setRouteInfo(rs.getString("route_info"));
        return dpt;
    }
}