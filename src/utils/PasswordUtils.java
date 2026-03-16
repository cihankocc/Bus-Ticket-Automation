package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Şifre hashleme yardımcı sınıfı.
 * SHA-256 algoritması kullanılır (Java SE yerleşik — ek kütüphane gerektirmez).
 */
public class PasswordUtils {

    private PasswordUtils() {
    }

    /** Verilen plain-text şifreyi SHA-256 ile hashler */
    public static String hash(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı.", e);
        }
    }

    /** Düz metin şifreyi hashlenmiş şifreyle karşılaştırır */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return hash(plainPassword).equals(hashedPassword);
    }
}
