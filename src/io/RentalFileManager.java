package io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Rental;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RentalFileManager {
    private static final String FILE_PATH = "data/rentals.json";

    // UPDATED: Added TypeAdapters for LocalDate to prevent reflection errors
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .create();

    // Load rentals from JSON file
    public static List<Rental> loadRentals() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Rental>>() {}.getType();
            List<Rental> rentals = gson.fromJson(reader, listType);
            return rentals != null ? rentals : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading rentals: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save rentals to JSON file
    public static void saveRentals(List<Rental> rentals) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(rentals, writer);
            System.out.println("Rentals saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving rentals: " + e.getMessage());
        }
    }

    // Add a new rental
    public static void addRental(Rental rental) {
        List<Rental> rentals = loadRentals();
        rentals.add(rental);
        saveRentals(rentals);
        System.out.println("Rental added: " + rental.getRentalId());
    }

    // Remove a rental by ID
    public static void removeRental(String rentalId) {
        List<Rental> rentals = loadRentals();
        rentals.removeIf(r -> r.getRentalId().equals(rentalId));
        saveRentals(rentals);
        System.out.println("Rental removed: " + rentalId);
    }

    // Find rental by ID
    public static Rental findRentalById(String rentalId) {
        return loadRentals().stream()
                .filter(r -> r.getRentalId().equals(rentalId))
                .findFirst()
                .orElse(null);
    }
}