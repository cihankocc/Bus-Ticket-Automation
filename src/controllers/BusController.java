package controllers;

import models.Bus;
import utils.ConnectionManager;
import utils.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusController {

    public String addBus(String plate, String captain) throws DatabaseException {
        String sql = "INSERT INTO Bus (plate, captain) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            stmt.setString(2, captain);
            stmt.executeUpdate();
            return "Otobüs başarıyla eklendi.";
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                throw new DatabaseException("Bu plaka zaten kayıtlı: " + plate);
            throw new DatabaseException("Otobüs ekleme sırasında hata oluştu.", e);
        }
    }

    public String updateBus(String plate, String captain) throws DatabaseException {
        String sql = "UPDATE Bus SET captain = ? WHERE plate = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, captain);
            stmt.setString(2, plate);
            stmt.executeUpdate();
            return "Otobüs başarıyla güncellendi.";
        } catch (SQLException e) {
            throw new DatabaseException("Otobüs güncelleme sırasında hata oluştu.", e);
        }
    }

    public String deleteBus(String plate) throws DatabaseException {
        String sql = "DELETE FROM Bus WHERE plate = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            stmt.executeUpdate();
            return "Otobüs başarıyla silindi.";
        } catch (SQLException e) {
            throw new DatabaseException("Otobüs silme sırasında hata oluştu.", e);
        }
    }

    public List<Bus> getAllBuses() throws DatabaseException {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT * FROM Bus";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setPlate(rs.getString("plate"));
                bus.setCaptain(rs.getString("captain"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Otobüsleri listeleme sırasında hata oluştu.", e);
        }
        return buses;
    }
}