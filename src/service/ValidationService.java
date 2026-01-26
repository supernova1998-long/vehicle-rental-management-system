package service;

import model.Car;
import model.Reservation;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationService {

    // Validate ID (non-empty, alphanumeric)
    public boolean validateId(String id) {
        boolean valid = id != null && !id.trim().isEmpty() && id.matches("[A-Za-z0-9_-]+");
        System.out.println("ValidationService: ID validation -> " + id + " = " + valid);
        // #toconnect: Used by CarService, ReservationService, RentalService
        return valid;
    }

    // Validate email format
    public boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        boolean valid = email != null && Pattern.matches(regex, email);
        System.out.println("ValidationService: Email validation -> " + email + " = " + valid);
        // #toconnect: Used by AuthService during login/registration
        return valid;
    }

    // Validate phone number (basic digits check, length 7–15)
    public boolean validatePhone(String phone) {
        boolean valid = phone != null && phone.matches("\\d{7,15}");
        System.out.println("ValidationService: Phone validation -> " + phone + " = " + valid);
        // #toconnect: Used by CustomerService during registration
        return valid;
    }

    // Validate password (minimum 6 characters)
    public boolean validatePassword(String password) {
        boolean valid = password != null && password.length() >= 6;
        System.out.println("ValidationService: Password validation -> " + (valid ? "valid" : "invalid"));
        // #toconnect: Used by AuthService during registration/login
        return valid;
    }

    // Validate reservation dates (start before end, not in the past)
    public boolean validateReservationDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        boolean valid = startDate != null && endDate != null &&
                !startDate.isBefore(today) &&
                !endDate.isBefore(startDate);
        System.out.println("ValidationService: Reservation dates validation -> " + valid);
        // #toconnect: Used by ReservationService when creating reservations
        return valid;
    }

    // Check car availability
    public boolean validateCarAvailability(Car car) {
        boolean valid = car != null && car.isAvailable();
        System.out.println("ValidationService: Car availability validation -> " + (valid ? "available" : "not available"));
        // #toconnect: Used by ReservationService and RentalService
        return valid;
    }

    // Validate reservation status (must be APPROVED before rental)
    public boolean validateReservationForRental(Reservation reservation) {
        boolean valid = reservation != null && reservation.getStatus() == model.ReservationStatus.APPROVED;
        System.out.println("ValidationService: Reservation rental validation -> " + (valid ? "valid" : "invalid"));
        // #toconnect: Used by RentalService before starting rental
        return valid;
    }
}