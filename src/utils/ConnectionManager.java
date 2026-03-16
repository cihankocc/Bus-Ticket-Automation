package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Veritabanı bağlantısını yöneten sınıf.
 * Bağlantı bilgileri config.properties dosyasından okunur.
 */
public class ConnectionManager {

    private static String url;
    private static String user;
    private static String password;

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(fis);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            // Fallback: config dosyası yoksa varsayılan değerler
            url = "jdbc:mariadb://localhost:3306/otomasyon";
            user = "root";
            password = "1234";
            System.err.println("[Uyarı] config.properties bulunamadı, varsayılan ayarlar kullanılıyor.");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB driver bulunamadı.", e);
        }
    }
}