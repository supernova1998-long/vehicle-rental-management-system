package service;

import model.Car;
import model.Reservation;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationService {

    // Validate ID (non-empty, alphanumeric)
    public static boolean validateId(String id) {
        boolean valid = id != null && !id.trim().isEmpty() && id.matches("[A-Za-z0-9_-]+");
        System.out.println("ValidationService: ID validation -> " + id + " = " + valid);
        return valid;
    }

    // Validate email format
    public static boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        boolean valid = email != null && Pattern.matches(regex, email);
        System.out.println("ValidationService: Email validation -> " + email + " = " + valid);
        return valid;
    }

    // Validate phone number (basic digits check, length 7–15)
    public static boolean validatePhone(String phone) {
        boolean valid = phone != null && phone.matches("\\d{7,15}");
        System.out.println("ValidationService: Phone validation -> " + phone + " = " + valid);
        return valid;
    }

    // Validate password (minimum 6 characters)
    public static boolean validatePassword(String password) {
        boolean valid = password != null && password.length() >= 6;
        System.out.println("ValidationService: Password validation -> " + (valid ? "valid" : "invalid"));
        return valid;
    }

    // Validate reservation dates (start before end, not in the past)
    public static String validateReservationDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return "Start and end dates must be selected.";
        }
        if (endDate.isBefore(startDate)) {
            return "End date cannot be before start date.";
        }
        if (startDate.isBefore(LocalDate.now())) {
            return "Start date cannot be in the past.";
        }
        return null; // All good
    }

    // Check car availability
    public static boolean validateCarAvailability(Car car) {
        boolean valid = car != null && car.isAvailable();
        System.out.println("ValidationService: Car availability validation -> " + (valid ? "available" : "not available"));
        return valid;
    }

    // Validate reservation status (must be APPROVED before rental)
    public static boolean validateReservationForRental(Reservation reservation) {
        boolean valid = reservation != null && reservation.getStatus() == model.ReservationStatus.APPROVED;
        System.out.println("ValidationService: Reservation rental validation -> " + (valid ? "valid" : "invalid"));
        return valid;
    }

    // Validate car input fields
    public static String validateCarInput(String model, String type, String fuel, String seatsStr, String priceStr) {
        if (model == null || model.trim().isEmpty()) return "Model cannot be empty.";
        if (type == null || type.trim().isEmpty()) return "Type cannot be empty.";
        if (fuel == null || fuel.trim().isEmpty()) return "Fuel cannot be empty.";

        try {
            int seats = Integer.parseInt(seatsStr);
            if (seats <= 0) return "Seats must be a positive number.";
        } catch (NumberFormatException e) {
            return "Seats must be a valid number.";
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) return "Price per day must be a positive number.";
        } catch (NumberFormatException e) {
            return "Price per day must be a valid number.";
        }
        return null; // All good
    }

    // Validate customer input fields
    public static String validateCustomerInput(String name, String email, String phone, String password) {
        if (name == null || name.trim().isEmpty()) return "Name cannot be empty.";
        if (email == null || email.trim().isEmpty()) return "Email cannot be empty.";
        if (phone == null || phone.trim().isEmpty()) return "Phone cannot be empty.";
        if (password == null || password.trim().isEmpty()) return "Password cannot be empty.";

        if (!validateEmail(email)) return "Invalid email format.";
        if (!validatePhone(phone)) return "Invalid phone number format (7-15 digits).";
        if (!validatePassword(password)) return "Password must be at least 6 characters long.";

        return null; // All good
    }
}
