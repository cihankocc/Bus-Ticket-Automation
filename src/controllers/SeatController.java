package controllers;

import utils.ConnectionManager;
import utils.DatabaseException;

import java.sql.*;

public class SeatController {

    /** Belirli bir koltuğun rezerve olup olmadığını kontrol eder */
    public boolean isSeatBooked(int seatNumber, int departureId) throws DatabaseException {
        String sql = "SELECT status FROM Seat WHERE seat_number=? AND dpt_id=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatNumber);
            stmt.setInt(2, departureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return "booked".equalsIgnoreCase(rs.getString("status"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Koltuk durumu kontrol edilirken hata oluştu.", e);
        }
        return false;
    }

    /** Koltuk durumunu günceller ('available', 'booked', 'reserved') */
    public String updateSeatStatus(int seatNumber, int departureId, String newStatus) throws DatabaseException {
        String sql = "UPDATE Seat SET status=? WHERE seat_number=? AND dpt_id=?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, seatNumber);
            stmt.setInt(3, departureId);
            stmt.executeUpdate();
            return "Koltuk durumu başarıyla güncellendi.";
        } catch (SQLException e) {
            throw new DatabaseException("Koltuk durumu güncellenirken hata oluştu.", e);
        }
    }

    /**
     * Atomik rezervasyon: koltuk müsaitken güncelle ve Booking kaydı oluştur.
     * Aynı anda iki kişi aynı koltuğu alamaz.
     * 
     * @return true başarılı, false koltuk zaten doluydu
     */
    public synchronized boolean bookSeat(int userId, int departureId, int seatNumber) throws DatabaseException {
        String seatSql = "UPDATE Seat SET status='booked' WHERE seat_number=? AND dpt_id=? AND status='available'";
        String bookingSql = "INSERT INTO Booking (user_id, dpt_id, seat_number) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Koltuğu kapat (sadece 'available' ise günceller)
                int updated;
                try (PreparedStatement seatStmt = conn.prepareStatement(seatSql)) {
                    seatStmt.setInt(1, seatNumber);
                    seatStmt.setInt(2, departureId);
                    updated = seatStmt.executeUpdate();
                }

                if (updated == 0) {
                    conn.rollback();
                    return false; // Koltuk zaten doluydu
                }

                // 2. Booking kaydı ekle
                try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql)) {
                    bookingStmt.setInt(1, userId);
                    bookingStmt.setInt(2, departureId);
                    bookingStmt.setInt(3, seatNumber);
                    bookingStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Rezervasyon sırasında hata oluştu.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Bağlantı hatası.", e);
        }
    }
}