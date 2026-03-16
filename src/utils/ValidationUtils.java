package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Kullanıcı girişlerini doğrulayan yardımcı sınıf.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidDate(String date) {
        return formatDateForDB(date) != null;
    }

    /** 
     * Kullanıcının girdiği tarihi DB standartlarına ("yyyy-MM-dd") çevirir.
     * Hem "yyyy-MM-dd" (2026-04-16) hem de "dd-MM-yyyy" (16-04-2026) kabul edilir.
     */
    public static String formatDateForDB(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        dateStr = dateStr.trim();
        
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    public static boolean isValidTime(String time) {
        if (time == null || time.isBlank())
            return false;
        try {
            LocalTime.parse(time.trim(), DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String s) {
        try {
            return Double.parseDouble(s.trim()) > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isPositiveInt(String s) {
        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isNotBlank(String... fields) {
        for (String f : fields)
            if (f == null || f.isBlank())
                return false;
        return true;
    }

    /**
     * Şifre gücü: minimum 6 karakter
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
