package controllers;

import models.Passenger;
import utils.ConnectionManager;
import utils.DatabaseException;
import utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerController {

    public String addPassenger(String firstName, String lastName, String email, String password)
            throws DatabaseException {
        String sql = "INSERT INTO User (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, 'passenger')";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, PasswordUtils.hash(password));
            stmt.executeUpdate();
            return "Yolcu başarıyla eklendi.";
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                throw new DatabaseException("Bu e-posta adresi zaten kayıtlı.");
            throw new DatabaseException("Yolcu ekleme sırasında hata oluştu.", e);
        }
    }

    public String updatePassenger(int userId, String firstName, String lastName, String email, String password)
            throws DatabaseException {
        String sql = "UPDATE User SET first_name=?, last_name=?, email=?, password=? WHERE user_id=? AND role='passenger'";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, PasswordUtils.hash(password));
            stmt.setInt(5, userId);
            stmt.executeUpdate();
            return "Yolcu başarıyla güncellendi.";
        } catch (SQLException e) {
            throw new DatabaseException("Yolcu güncelleme sırasında hata oluştu.", e);
        }
    }

    public String deletePassenger(int userId) throws DatabaseException {
        String sql = "DELETE FROM User WHERE user_id=? AND role='passenger'";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return "Yolcu başarıyla silindi.";
        } catch (SQLException e) {
            throw new DatabaseException("Yolcu silme sırasında hata oluştu.", e);
        }
    }

    public List<Passenger> getAllPassengers() throws DatabaseException {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE role='passenger'";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Passenger p = new Passenger();
                p.setUserId(rs.getInt("user_id"));
                p.setFirstName(rs.getString("first_name"));
                p.setLastName(rs.getString("last_name"));
                p.setEmail(rs.getString("email"));
                passengers.add(p);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Yolcuları listeleme sırasında hata oluştu.", e);
        }
        return passengers;
    }
}