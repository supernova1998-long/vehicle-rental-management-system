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

    public void startRental(String rentalId, Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            System.err.println("RentalService: Only APPROVED reservations can be rented.");
            return;
        }

        // Record actual start date as NOW, end date as NULL
        Rental rental = new Rental(
                rentalId,
                reservation.getReservationId(),
                LocalDate.now(),
                null,
                0.0, // Price calculated on return
                false,
                false // Paid is initially false
        );

        // State Sync: 1. Start Rental, 2. Update Reservation to RENTED
        RentalFileManager.addRental(rental);
        reservationService.updateReservationStatus(reservation.getReservationId(), ReservationStatus.RENTED);

        System.out.println("RentalService: Rental started -> " + rentalId);
    }

    public void completeRental(String rentalId) {
        List<Rental> rentals = RentalFileManager.loadRentals();
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId) && !r.isReturned()) {
                // Find reservation to get car details for pricing
                Reservation res = reservationService.findReservationById(r.getReservationId());
                if (res != null) {
                    Car car = carService.findCarById(res.getVehicleId());
                    if (car != null) {
                        LocalDate returnDate = LocalDate.now();
                        LocalDate expectedEndDate = res.getEndDate();
                        
                        long totalDays = ChronoUnit.DAYS.between(r.getActualStartDate(), returnDate);
                        if (totalDays <= 0) totalDays = 1; // Minimum 1 day charge
                        
                        double finalPrice = 0.0;
                        
                        if (returnDate.isAfter(expectedEndDate)) {
                            long overdueDays = ChronoUnit.DAYS.between(expectedEndDate, returnDate);
                            long normalDays = totalDays - overdueDays;
                            
                            // Penalty: Double the rate for overdue days
                            finalPrice = (normalDays * car.getPricePerDay()) + (overdueDays * car.getPricePerDay() * 2);
                            System.out.println("RentalService: Penalty applied for " + overdueDays + " overdue days.");
                        } else {
                            finalPrice = totalDays * car.getPricePerDay();
                        }
                        
                        // 1. Update Rental Entry
                        r.setReturned(true);
                        r.setActualEndDate(returnDate);
                        r.setTotalCharge(finalPrice);
                        // Paid status remains unchanged (false) until Admin updates it
                        RentalFileManager.saveRentals(rentals);

                        // 2. Update Reservation to COMPLETED
                        reservationService.updateReservationStatus(r.getReservationId(), ReservationStatus.COMPLETED);

                        // 3. Update Car to AVAILABLE
                        carService.updateCarAvailability(res.getVehicleId(), true);

                        System.out.println("RentalService: Rental completed -> " + rentalId + " Price: " + finalPrice);
                        return;
                    }
                }
            }
        }
    }

    public void updateRentalPaidStatus(String rentalId, boolean paid) {
        List<Rental> rentals = RentalFileManager.loadRentals();
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId)) {
                r.setPaid(paid);
                RentalFileManager.saveRentals(rentals);
                System.out.println("RentalService: Rental " + rentalId + " paid status set to " + paid);
                return;
            }
        }
    }
    
    public Rental findActiveRentalByReservationId(String reservationId) {
        List<Rental> rentals = RentalFileManager.loadRentals();
        for (Rental r : rentals) {
            if (r.getReservationId().equals(reservationId) && !r.isReturned()) {
                return r;
            }
        }
        return null;
    }

    public Rental findRentalById(String rentalId) {
        return RentalFileManager.findRentalById(rentalId);
    }
}