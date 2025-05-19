package org.example.Utilities;

/**
 * Utility class for validating user input fields such as email, phone, and empty strings.
 */

public class ValidationUtils {

    /**
     * Validates whether a string is a properly formatted email address.
     * @param email the email string to validate
     * @return true if the email is valid
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Validates whether a string is a valid phone number (digits only, 10+ characters).
     * @param phone the phone number string
     * @return true if the phone number is valid
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{7,15}$");
    }

    /**
     * Checks if a string is not null and not blank.
     * @param str the string to check
     * @return true if the string is not null or blank
     */
    public static boolean isNonEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Checks if a number is positive.
     * @param str the string to check.
     * @return true if the string is a positive number
     */
    public static boolean isPositiveNumber(String str) {
        try {
            return Double.parseDouble(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
