package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Rental;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RentalFileManager {
    private static final String FILE_PATH = "data/rentals.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Load rentals from JSON file
    public static List<Rental> loadRentals() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Rental>>() {}.getType();
            List<Rental> rentals = gson.fromJson(reader, listType);
            return rentals != null ? rentals : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading rentals: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save rentals to JSON file
    public static void saveRentals(List<Rental> rentals) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(rentals, writer);
            System.out.println("Rentals saved successfully.");
            // #toconnect: RentalService will call this after add/update/remove operations
        } catch (IOException e) {
            System.out.println("Error saving rentals: " + e.getMessage());
        }
    }

    // Add a new rental
    public static void addRental(Rental rental) {
        List<Rental> rentals = loadRentals();
        rentals.add(rental);
        saveRentals(rentals);
        System.out.println("Rental added: " + rental.getRentalId());
        // #toconnect: RentalService will wrap this for business logic
    }

    // Remove a rental by ID
    public static void removeRental(String rentalId) {
        List<Rental> rentals = loadRentals();
        rentals.removeIf(r -> r.getRentalId().equals(rentalId));
        saveRentals(rentals);
        System.out.println("Rental removed: " + rentalId);
        // #toconnect: RentalService will wrap this for business logic
    }

    // Find rental by ID
    public static Rental findRentalById(String rentalId) {
        List<Rental> rentals = loadRentals();
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId)) {
                return r;
            }
        }
        return null;
        // #toconnect: RentalService will use this for lookups
    }
}