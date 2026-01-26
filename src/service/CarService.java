package service;

import io.CarFileManager;
import model.Car;

import java.util.List;

public class CarService {

    // Get all cars
    public List<Car> getAllCars() {
        return CarFileManager.loadCars();
    }

    // Add a new car
    public void addCar(Car car) {
        CarFileManager.addCar(car);
        System.out.println("CarService: Car added -> " + car.getModel());
        // #toconnect: AdminDashboardController will call this when admin adds a car
    }

    // Remove a car by ID
    public void removeCar(String carId) {
        CarFileManager.removeCar(carId);
        System.out.println("CarService: Car removed -> " + carId);
        // #toconnect: AdminDashboardController will call this when admin removes a car
    }

    // Find car by ID
    public Car findCarById(String carId) {
        Car car = CarFileManager.findCarById(carId);
        if (car != null) {
            System.out.println("CarService: Car found -> " + car.getModel());
        } else {
            System.out.println("CarService: Car not found -> " + carId);
        }
        // #toconnect: ReservationService will use this to check availability
        return car;
    }

    // List available cars
    public List<Car> getAvailableCars() {
        List<Car> cars = CarFileManager.loadCars();
        cars.removeIf(c -> !c.isAvailable());
        System.out.println("CarService: Returning available cars list.");
        // #toconnect: CustomerDashboardController will use this to show cars to customers
        return cars;
    }

    // Update car availability
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
        // #toconnect: RentalService will call this when a rental starts or ends
    }
}