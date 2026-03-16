package controllers;

import models.User;
import models.Owner;
import models.Passenger;
import utils.ConnectionManager;
import utils.DatabaseException;
import utils.PasswordUtils;
import utils.ValidationUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public String register(String firstName, String lastName, String email, String password) throws DatabaseException {
        validateInput(firstName, lastName, email, password);
        String sql = "INSERT INTO User (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, 'passenger')";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName.trim());
            stmt.setString(2, lastName.trim());
            stmt.setString(3, email.trim().toLowerCase());
            stmt.setString(4, PasswordUtils.hash(password));
            stmt.executeUpdate();
            return "Kullanıcı başarıyla kaydedildi.";
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                throw new DatabaseException("Bu e-posta adresi zaten kayıtlı.");
            throw new DatabaseException("Kullanıcı kaydı sırasında hata oluştu.", e);
        }
    }

    public String registerAdmin(String firstName, String lastName, String email, String password)
            throws DatabaseException {
        validateInput(firstName, lastName, email, password);
        String sql = "INSERT INTO User (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, 'owner')";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName.trim());
            stmt.setString(2, lastName.trim());
            stmt.setString(3, email.trim().toLowerCase());
            stmt.setString(4, PasswordUtils.hash(password));
            stmt.executeUpdate();
            return "Yönetici başarıyla kaydedildi.";
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                throw new DatabaseException("Bu e-posta adresi zaten kayıtlı.");
            throw new DatabaseException("Yönetici kaydı sırasında hata oluştu.", e);
        }
    }

    public User login(String email, String password) throws DatabaseException {
        if (!ValidationUtils.isNotBlank(email, password))
            throw new DatabaseException("E-posta ve şifre boş olamaz.");
        String sql = "SELECT * FROM User WHERE email = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (!PasswordUtils.verify(password, rs.getString("password")))
                        return null;
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kullanıcı girişi sırasında hata oluştu.", e);
        }
        return null;
    }

    public User getUserByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM User WHERE email = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kullanıcı bilgisi getirme sırasında hata oluştu.", e);
        }
        return null;
    }

    public User getUserById(int userId) throws DatabaseException {
        String sql = "SELECT * FROM User WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kullanıcı bilgisi getirme sırasında hata oluştu.", e);
        }
        return null;
    }

    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                if (u != null)
                    users.add(u);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kullanıcıları listeleme sırasında hata oluştu.", e);
        }
        return users;
    }

    private void validateInput(String firstName, String lastName, String email, String password)
            throws DatabaseException {
        if (!ValidationUtils.isNotBlank(firstName, lastName, email, password))
            throw new DatabaseException("Tüm alanlar zorunludur.");
        if (!ValidationUtils.isValidEmail(email))
            throw new DatabaseException("Geçersiz e-posta formatı. Örnek: ornek@mail.com");
        if (!ValidationUtils.isValidPassword(password))
            throw new DatabaseException("Şifre en az 6 karakter olmalıdır.");
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;
        if ("passenger".equals(role))
            user = new Passenger();
        else if ("owner".equals(role))
            user = new Owner();
        else
            return null;
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}