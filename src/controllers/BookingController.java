package controllers;

import models.Booking;
import models.BookingDetail;
import utils.ConnectionManager;
import utils.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    public String createBooking(int userId, int departureId, int seatNumber) throws DatabaseException {
        String sql = "INSERT INTO Booking (user_id, dpt_id, seat_number) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, departureId);
            stmt.setInt(3, seatNumber);
            stmt.executeUpdate();
            return "Rezervasyon başarıyla oluşturuldu.";
        } catch (SQLException e) {
            throw new DatabaseException("Rezervasyon oluşturulurken hata oluştu.", e);
        }
    }

    public String deleteBooking(int bookingId) throws DatabaseException {
        String sql = "DELETE FROM Booking WHERE booking_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
            return "Rezervasyon başarıyla silindi.";
        } catch (SQLException e) {
            throw new DatabaseException("Rezervasyon silinirken hata oluştu.", e);
        }
    }

    /** Kullanıcının tüm rezervasyonlarını sefer bilgileriyle döndürür */
    public List<BookingDetail> getUserBookings(int userId) throws DatabaseException {
        List<BookingDetail> list = new ArrayList<>();
        String sql = """
                SELECT b.booking_id, b.seat_number, b.dpt_id, b.created_at,
                       d.departure_city, d.arrival_city, d.date, d.time, d.ticket_price
                FROM Booking b
                JOIN Dpt d ON b.dpt_id = d.departure_id
                WHERE b.user_id = ?
                ORDER BY d.date DESC, d.time DESC
                """;
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookingDetail(
                            rs.getInt("booking_id"),
                            rs.getInt("seat_number"),
                            rs.getString("departure_city"),
                            rs.getString("arrival_city"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getDouble("ticket_price"),
                            rs.getString("created_at")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Rezervasyonlar getirilirken hata oluştu.", e);
        }
        return list;
    }

    /**
     * Rezervasyonu iptal eder: Booking siler + Seat'i 'available' yapar
     * (transaction)
     */
    public String cancelBooking(int bookingId, int seatNumber, int departureId) throws DatabaseException {
        String deleteBookingSql = "DELETE FROM Booking WHERE booking_id = ?";
        String resetSeatSql = "UPDATE Seat SET status='available' WHERE seat_number=? AND dpt_id=?";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement s1 = conn.prepareStatement(deleteBookingSql)) {
                    s1.setInt(1, bookingId);
                    s1.executeUpdate();
                }
                try (PreparedStatement s2 = conn.prepareStatement(resetSeatSql)) {
                    s2.setInt(1, seatNumber);
                    s2.setInt(2, departureId);
                    s2.executeUpdate();
                }
                conn.commit();
                return "Rezervasyon başarıyla iptal edildi.";
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Rezervasyon iptal edilirken hata oluştu.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Bağlantı hatası.", e);
        }
    }

    public Booking getBookingById(int bookingId) throws DatabaseException {
        String sql = "SELECT * FROM Booking WHERE booking_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Booking(rs.getInt("booking_id"), rs.getInt("user_id"),
                            rs.getInt("dpt_id"), rs.getInt("seat_number"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Rezervasyon bilgileri getirilirken hata oluştu.", e);
        }
        return null;
    }
}