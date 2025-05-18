package org.example.Utilities;

/**
 * Utility class for validating client/productField.
 */
public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{7,15}$");
    }

    public static boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isPositiveNumber(String str) {
        try {
            return Double.parseDouble(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
