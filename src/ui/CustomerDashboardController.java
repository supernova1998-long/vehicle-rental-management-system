package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import model.Rental;
import service.CarService;
import service.ReservationService;
import service.RentalService;
import service.AuthService;

import java.time.LocalDate;
import java.util.List;

public class CustomerDashboardController {

    @FXML
    private TableView<Car> availableCarsTable;

    @FXML
    private TableColumn<Car, String> carIdColumn;

    @FXML
    private TableColumn<Car, String> carModelColumn;

    @FXML
    private TableColumn<Car, Boolean> carAvailabilityColumn;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> reservationIdColumn;

    @FXML
    private TableColumn<Reservation, String> reservationCarColumn;

    @FXML
    private TableColumn<Reservation, String> reservationStatusColumn;

    @FXML
    private TableView<Rental> rentalTable;

    @FXML
    private TableColumn<Rental, String> rentalIdColumn;

    @FXML
    private TableColumn<Rental, String> rentalReservationColumn;

    @FXML
    private TableColumn<Rental, Boolean> rentalReturnedColumn;

    @FXML
    private Button makeReservationButton;

    @FXML
    private Button returnRentalButton;

    @FXML
    private Label messageLabel;

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();
    private AuthService authService = new AuthService();

    // Initialize dashboard data
    @FXML
    private void initialize() {
        loadAvailableCars();
        loadReservations();
        loadRentals();
        // #toconnect: Bind table columns to Car, Reservation, and Rental properties
    }

    // Load available cars
    private void loadAvailableCars() {
        List<Car> cars = carService.getAvailableCars();
        availableCarsTable.getItems().setAll(cars);
        messageLabel.setText("Available cars loaded: " + cars.size());
    }

    // Load reservations for logged-in customer
    private void loadReservations() {
        if (authService.getLoggedInUser() != null) {
            String customerId = authService.getLoggedInUser().getId();
            List<Reservation> reservations = reservationService.getReservationsByCustomer(customerId);
            reservationTable.getItems().setAll(reservations);
            messageLabel.setText("Reservations loaded: " + reservations.size());
        }
    }

    // Load rentals (for demonstration, could filter by customer later)
    private void loadRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        rentalTable.getItems().setAll(rentals);
        messageLabel.setText("Rentals loaded: " + rentals.size());
    }

    // Make reservation for selected car
    @FXML
    private void handleMakeReservation(ActionEvent event) {
        Car selected = availableCarsTable.getSelectionModel().getSelectedItem();
        if (selected != null && authService.getLoggedInUser() != null) {
            String reservationId = "RES-" + System.currentTimeMillis();
            String customerId = authService.getLoggedInUser().getId();
            reservationService.createReservation(
                    reservationId,
                    customerId,
                    selected.getId(),
                    LocalDate.now().plusDays(1), // Example start date
                    LocalDate.now().plusDays(3)  // Example end date
            );
            loadReservations();
            messageLabel.setText("Reservation created: " + reservationId);
            // #toconnect: Refresh UI table after reservation
        } else {
            messageLabel.setText("No car selected or user not logged in.");
        }
    }

    // Return selected rental
    @FXML
    private void handleReturnRental(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.completeRental(selected.getRentalId());
            loadRentals();
            messageLabel.setText("Rental returned: " + selected.getRentalId());
            // #toconnect: Refresh UI table after return
        } else {
            messageLabel.setText("No rental selected.");
        }
    }
}