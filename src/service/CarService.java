package service;

import io.CarFileManager;
import io.ReservationFileManager;
import model.Car;
import model.Reservation;
import model.ReservationStatus;

import java.util.List;
import java.util.stream.Collectors;

public class CarService {

    public List<Car> getAllCars() {
        return CarFileManager.loadCars();
    }

    public void addCar(Car car) {
        CarFileManager.addCar(car);
        System.out.println("CarService: Car added -> " + car.getModel());
    }

    public void updateCar(Car updatedCar) {
        List<Car> cars = CarFileManager.loadCars();
        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId().equals(updatedCar.getId())) {
                cars.set(i, updatedCar);
                break;
            }
        }
        CarFileManager.saveCars(cars);
        System.out.println("CarService: Car updated -> " + updatedCar.getId());
    }

    public String generateNextId() {
        List<Car> cars = CarFileManager.loadCars();
        if (cars.isEmpty()) {
            return "C001";
        }
        String lastId = cars.get(cars.size() - 1).getId();
        try {
            int idNum = Integer.parseInt(lastId.substring(1));
            return String.format("C%03d", idNum + 1);
        } catch (NumberFormatException e) {
            // Fallback: If last ID was malformed, count the list size + 1
            return String.format("C%03d", cars.size() + 1);
        }
    }

    public void removeCar(String carId) {
        if (canRemoveOrEdit(carId)) {
            CarFileManager.removeCar(carId);
            System.out.println("CarService: Car removed -> " + carId);
        } else {
            System.err.println("CarService: Cannot remove car " + carId + " because it has active/pending reservations.");
        }
    }

    public boolean canRemoveOrEdit(String carId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        // Restriction: Only allow if no PENDING, APPROVED, or RENTED reservations exist for this car
        return reservations.stream()
                .filter(r -> r.getVehicleId().equals(carId))
                .noneMatch(r -> r.getStatus() == ReservationStatus.PENDING ||
                        r.getStatus() == ReservationStatus.APPROVED ||
                        r.getStatus() == ReservationStatus.RENTED);
    }

    public Car findCarById(String carId) {
        return CarFileManager.findCarById(carId);
    }

    public List<Car> getAvailableCars() {
        return CarFileManager.loadCars().stream()
                .filter(Car::isAvailable)
                .collect(Collectors.toList());
    }

    public void updateCarAvailability(String carId, boolean available) {
        List<Car> cars = CarFileManager.loadCars();
        for (Car c : cars) {
            if (c.getId().equals(carId)) {
                c.setAvailable(available);
                break;
            }
        }
        CarFileManager.saveCars(cars);
        System.out.println("CarService: Car availability updated -> " + carId + " = " + available);
    }
}