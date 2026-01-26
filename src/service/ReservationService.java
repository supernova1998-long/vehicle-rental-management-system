package service;

import io.ReservationFileManager;
import io.CarFileManager;
import model.Reservation;
import model.ReservationStatus;
import model.Car;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    // Get all reservations
    public List<Reservation> getAllReservations() {
        return ReservationFileManager.loadReservations();
    }

    // Create a new reservation
    public void createReservation(String reservationId, String customerId, String carId,
                                  LocalDate startDate, LocalDate endDate) {
        Car car = CarFileManager.findCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.out.println("ReservationService: Car not available for reservation.");
            return;
        }

        Reservation reservation = new Reservation(
                reservationId,
                customerId,
                carId,
                startDate,
                endDate,
                ReservationStatus.PENDING
        );

        ReservationFileManager.addReservation(reservation);
        System.out.println("ReservationService: Reservation created -> " + reservationId);
        // #toconnect: CustomerDashboardController will call this when customer makes a reservation
    }

    // Approve a reservation
    public void approveReservation(String reservationId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                r.setStatus(ReservationStatus.APPROVED);
                break;
            }
        }
        ReservationFileManager.saveReservations(reservations);
        System.out.println("ReservationService: Reservation approved -> " + reservationId);
        // #toconnect: AdminDashboardController will call this when admin approves reservation
    }

    // Cancel a reservation
    public void cancelReservation(String reservationId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                r.setStatus(ReservationStatus.CANCELLED);
                break;
            }
        }
        ReservationFileManager.saveReservations(reservations);
        System.out.println("ReservationService: Reservation cancelled -> " + reservationId);
        // #toconnect: AdminDashboardController or CustomerDashboardController will call this
    }

    // Find reservation by ID
    public Reservation findReservationById(String reservationId) {
        Reservation reservation = ReservationFileManager.findReservationById(reservationId);
        if (reservation != null) {
            System.out.println("ReservationService: Reservation found -> " + reservationId);
        } else {
            System.out.println("ReservationService: Reservation not found -> " + reservationId);
        }
        // #toconnect: RentalService will use this to convert reservation into rental
        return reservation;
    }

    // Get reservations by customer ID
    public List<Reservation> getReservationsByCustomer(String customerId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        reservations.removeIf(r -> !r.getCustomerId().equals(customerId));
        System.out.println("ReservationService: Returning reservations for customer -> " + customerId);
        // #toconnect: CustomerDashboardController will use this to show reservations
        return reservations;
    }
}