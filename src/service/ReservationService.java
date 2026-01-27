package service;

import io.ReservationFileManager;
import io.CarFileManager;
import model.Reservation;
import model.ReservationStatus;
import model.Car;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {

    private CarService carService = new CarService();

    public List<Reservation> getAllReservations() {
        return ReservationFileManager.loadReservations();
    }

    public void createReservation(String reservationId, String customerId, String carId,
                                  LocalDate startDate, LocalDate endDate) {
        Car car = CarFileManager.findCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.out.println("ReservationService: Car not available.");
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
    }

    public String generateNextId() {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        if (reservations.isEmpty()) {
            return "RES001";
        }
        String lastId = reservations.get(reservations.size() - 1).getReservationId();
        try {
            // Assuming ID format is "RESxxx"
            int idNum = Integer.parseInt(lastId.replace("RES", ""));
            return String.format("RES%03d", idNum + 1);
        } catch (NumberFormatException e) {
            return "RES" + (reservations.size() + 1);
        }
    }

    public void approveReservation(String reservationId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId) && r.getStatus() == ReservationStatus.PENDING) {
                r.setStatus(ReservationStatus.APPROVED);
                carService.updateCarAvailability(r.getVehicleId(), false);
                ReservationFileManager.saveReservations(reservations);
                System.out.println("ReservationService: Approved -> " + reservationId);
                return;
            }
        }
    }

    public void cancelReservation(String reservationId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId) && r.getStatus() == ReservationStatus.PENDING) {
                r.setStatus(ReservationStatus.CANCELLED);
                // Car is already available if it was PENDING, but ensuring it is true doesn't hurt
                carService.updateCarAvailability(r.getVehicleId(), true);
                ReservationFileManager.saveReservations(reservations);
                System.out.println("ReservationService: Cancelled -> " + reservationId);
                return;
            }
        }
    }

    public void revertApproval(String reservationId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId) && r.getStatus() == ReservationStatus.APPROVED) {
                r.setStatus(ReservationStatus.PENDING);
                carService.updateCarAvailability(r.getVehicleId(), true);
                ReservationFileManager.saveReservations(reservations);
                System.out.println("ReservationService: Reverted to PENDING -> " + reservationId);
                return;
            }
        }
    }

    public void updateReservationStatus(String reservationId, ReservationStatus newStatus) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                r.setStatus(newStatus);
                ReservationFileManager.saveReservations(reservations);
                return;
            }
        }
    }

    public Reservation findReservationById(String reservationId) {
        return ReservationFileManager.findReservationById(reservationId);
    }

    public List<Reservation> getReservationsByCustomer(String customerId) {
        return ReservationFileManager.loadReservations().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
}