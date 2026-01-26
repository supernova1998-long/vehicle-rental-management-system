package io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Reservation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationFileManager {
    private static final String FILE_PATH = "data/reservations.json";

    // Define the Gson instance with custom LocalDate handling
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-MM-dd"
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            })
            .create();

    // Load reservations from JSON file
    public static List<Reservation> loadReservations() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Reservation>>() {}.getType();
            List<Reservation> reservations = gson.fromJson(reader, listType);
            return reservations != null ? reservations : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save reservations to JSON file
    public static void saveReservations(List<Reservation> reservations) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(reservations, writer);
            System.out.println("Reservations saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    // Add a new reservation
    public static void addReservation(Reservation reservation) {
        List<Reservation> reservations = loadReservations();
        reservations.add(reservation);
        saveReservations(reservations);
    }

    // Remove a reservation by ID
    public static void removeReservation(String reservationId) {
        List<Reservation> reservations = loadReservations();
        reservations.removeIf(r -> r.getReservationId().equals(reservationId));
        saveReservations(reservations);
    }

    // Find reservation by ID
    public static Reservation findReservationById(String reservationId) {
        return loadReservations().stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }
}