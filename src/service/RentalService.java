package service;

import io.RentalFileManager;
import io.CarFileManager;
import model.Rental;
import model.Reservation;
import model.ReservationStatus;
import model.Car;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalService {

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();

    public List<Rental> getAllRentals() {
        return RentalFileManager.loadRentals();
    }

    public void startRental(String rentalId, Reservation reservation, double dailyRate) {
        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            System.err.println("RentalService: Only APPROVED reservations can be rented.");
            return;
        }

        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        if (days <= 0) days = 1;
        double totalCharge = days * dailyRate;

        Rental rental = new Rental(
                rentalId,
                reservation.getReservationId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                totalCharge,
                false
        );

        // State Sync: 1. Start Rental, 2. Update Reservation to RENTED, 3. Car is already false from Approval
        RentalFileManager.addRental(rental);
        reservationService.updateReservationStatus(reservation.getReservationId(), ReservationStatus.RENTED);

        System.out.println("RentalService: Rental started -> " + rentalId);
    }

    public void completeRental(String rentalId) {
        List<Rental> rentals = RentalFileManager.loadRentals();
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId) && !r.isReturned()) {
                // 1. Update Rental Entry
                r.setReturned(true);
                r.setActualEndDate(LocalDate.now());
                RentalFileManager.saveRentals(rentals);

                // 2. Update Reservation to COMPLETED
                reservationService.updateReservationStatus(r.getReservationId(), ReservationStatus.COMPLETED);

                // 3. Update Car to AVAILABLE
                Reservation res = reservationService.findReservationById(r.getReservationId());
                if (res != null) {
                    carService.updateCarAvailability(res.getVehicleId(), true);
                }

                System.out.println("RentalService: Rental completed -> " + rentalId);
                return;
            }
        }
    }

    public Rental findRentalById(String rentalId) {
        return RentalFileManager.findRentalById(rentalId);
    }
}