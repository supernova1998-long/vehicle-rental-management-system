package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Car;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CarFileManager {
    private static final String FILE_PATH = "data/cars.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Load cars from JSON file
    public static List<Car> loadCars() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Car>>() {}.getType();
            List<Car> cars = gson.fromJson(reader, listType);
            return cars != null ? cars : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading cars: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save cars to JSON file
    public static void saveCars(List<Car> cars) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cars, writer);
            System.out.println("Cars saved successfully.");
            // #toconnect: CarService will call this after add/update/remove operations
        } catch (IOException e) {
            System.out.println("Error saving cars: " + e.getMessage());
        }
    }

    // Add a new car
    public static synchronized void addCar(Car car) {
        List<Car> cars = loadCars();
        cars.add(car);
        saveCars(cars);
        System.out.println("Car added: " + car.getModel());
        // #toconnect: CarService will wrap this for business logic
    }

    // Remove a car by ID
    public static void removeCar(String carId) {
        List<Car> cars = loadCars();
        cars.removeIf(c -> c.getId().equals(carId));
        saveCars(cars);
        System.out.println("Car removed: " + carId);
        // #toconnect: CarService will wrap this for business logic
    }

    // Find car by ID
    public static Car findCarById(String carId) {
        List<Car> cars = loadCars();
        for (Car c : cars) {
            if (c.getId().equals(carId)) {
                return c;
            }
        }
        return null;
        // #toconnect: CarService will use this for lookups
    }
}
