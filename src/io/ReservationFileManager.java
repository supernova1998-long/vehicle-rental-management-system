package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Reservation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationFileManager {
    private static final String FILE_PATH = "data/reservations.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Load reservations from JSON file
    public static List<Reservation> loadReservations() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Reservation>>() {}.getType();
            List<Reservation> reservations = gson.fromJson(reader, listType);
            return reservations != null ? reservations : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save reservations to JSON file
    public static void saveReservations(List<Reservation> reservations) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(reservations, writer);
            System.out.println("Reservations saved successfully.");
            // #toconnect: ReservationService will call this after add/update/remove operations
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    // Add a new reservation
    public static void addReservation(Reservation reservation) {
        List<Reservation> reservations = loadReservations();
        reservations.add(reservation);
        saveReservations(reservations);
        System.out.println("Reservation added: " + reservation.getReservationId());
        // #toconnect: ReservationService will wrap this for business logic
    }

    // Remove a reservation by ID
    public static void removeReservation(String reservationId) {
        List<Reservation> reservations = loadReservations();
        reservations.removeIf(r -> r.getReservationId().equals(reservationId));
        saveReservations(reservations);
        System.out.println("Reservation removed: " + reservationId);
        // #toconnect: ReservationService will wrap this for business logic
    }

    // Find reservation by ID
    public static Reservation findReservationById(String reservationId) {
        List<Reservation> reservations = loadReservations();
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                return r;
            }
        }
        return null;
        // #toconnect: ReservationService will use this for lookups
    }
}