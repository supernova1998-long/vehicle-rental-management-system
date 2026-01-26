package service;

import io.RentalFileManager;
import io.CarFileManager;
import model.Rental;
import model.Reservation;
import model.ReservationStatus;
import model.Car;

import java.time.LocalDate;
import java.util.List;

public class RentalService {

    // Get all rentals
    public List<Rental> getAllRentals() {
        return RentalFileManager.loadRentals();
    }

    // Start a rental from a reservation
    public void startRental(String rentalId, Reservation reservation, double dailyRate) {
        Car car = CarFileManager.findCarById(reservation.getCarId());
        if (car == null || !car.isAvailable()) {
            System.out.println("RentalService: Car not available for rental.");
            return;
        }

        // Calculate total charge based on reservation dates
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                reservation.getStartDate(), reservation.getEndDate());
        double totalCharge = days * dailyRate;

        Rental rental = new Rental(
                rentalId,
                reservation.getReservationId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                totalCharge,
                false
        );

        // Update reservation status
        reservation.setStatus(ReservationStatus.RENTED);

        // Update car availability
        car.setAvailable(false);
        List<Car> cars = CarFileManager.loadCars();
        for (Car c : cars) {
            if (c.getId().equals(car.getId())) {
                c.setAvailable(false);
                break;
            }
        }
        CarFileManager.saveCars(cars);

        // Save rental
        RentalFileManager.addRental(rental);
        System.out.println("RentalService: Rental started -> " + rentalId);
        // #toconnect: AdminDashboardController will call this when converting reservation to rental
    }

    // Complete a rental
    public void completeRental(String rentalId) {
        List<Rental> rentals = RentalFileManager.loadRentals();
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId)) {
                r.setReturned(true);
                r.setActualEndDate(LocalDate.now());

                // Update car availability
                Car car = CarFileManager.findCarById(r.getReservationId());
                if (car != null) {
                    car.setAvailable(true);
                    List<Car> cars = CarFileManager.loadCars();
                    for (Car c : cars) {
                        if (c.getId().equals(car.getId())) {
                            c.setAvailable(true);
                            break;
                        }
                    }
                    CarFileManager.saveCars(cars);
                }

                break;
            }
        }
        RentalFileManager.saveRentals(rentals);
        System.out.println("RentalService: Rental completed -> " + rentalId);
        // #toconnect: CustomerDashboardController will call this when customer returns rental
    }

    // Find rental by ID
    public Rental findRentalById(String rentalId) {
        Rental rental = RentalFileManager.findRentalById(rentalId);
        if (rental != null) {
            System.out.println("RentalService: Rental found -> " + rentalId);
        } else {
            System.out.println("RentalService: Rental not found -> " + rentalId);
        }
        // #toconnect: Controllers will use this for lookups
        return rental;
    }
}