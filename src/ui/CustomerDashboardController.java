package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import model.Rental;
import model.User;
import service.CarService;
import service.ReservationService;
import service.RentalService;
import service.AuthService;

import java.time.LocalDate;
import java.util.List;

public class CustomerDashboardController {

    @FXML private TableView<Car> availableCarsTable;
    @FXML private TableColumn<Car, String> carIdColumn;
    @FXML private TableColumn<Car, String> carModelColumn;
    @FXML private TableColumn<Car, Boolean> carAvailabilityColumn;

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> reservationCarColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, String> rentalIdColumn;
    @FXML private TableColumn<Rental, String> rentalReservationColumn;
    @FXML private TableColumn<Rental, Boolean> rentalReturnedColumn;

    @FXML private Label messageLabel;

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();
    private AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        // 1. Bind Available Cars Table
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Make the "Available" column look better
        carAvailabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                } else {
                    setText(available ? "Available" : "Booked");
                    setTextFill(available ? Color.GREEN : Color.RED);
                }
            }
        });

        // 2. Bind Reservations Table
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        // IMPORTANT: Must match the getter "getVehicleId" we fixed earlier
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 3. Bind Rental Table
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));

        // Format the "Returned" column
        rentalReturnedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean returned, boolean empty) {
                super.updateItem(returned, empty);
                if (empty || returned == null) {
                    setText(null);
                } else {
                    setText(returned ? "Completed" : "Active");
                    setTextFill(returned ? Color.BLUE : Color.ORANGE);
                }
            }
        });

        // 4. Load the data
        loadAvailableCars();
        loadReservations();
        loadRentals();
    }

    private void loadAvailableCars() {
        List<Car> cars = carService.getAvailableCars();
        availableCarsTable.getItems().setAll(cars);
    }

    private void loadReservations() {
        // Access the STATIC user from AuthService
        User currentUser = AuthService.getLoggedInUser();

        if (currentUser != null) {
            String customerId = currentUser.getId();
            // Filter reservations to only show those belonging to THIS customer
            List<Reservation> reservations = reservationService.getReservationsByCustomer(customerId);
            reservationTable.getItems().setAll(reservations);
            messageLabel.setText("Welcome " + currentUser.getName() + "! You have " + reservations.size() + " reservations.");
        } else {
            messageLabel.setText("Error: User session lost.");
        }
    }

    private void loadRentals() {
        // Ideally filter by the logged-in customer's reservations
        List<Rental> rentals = rentalService.getAllRentals();
        rentalTable.getItems().setAll(rentals);
    }

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
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3)
            );
            loadReservations();
            loadAvailableCars(); // Refresh car availability
            messageLabel.setText("Reservation created: " + reservationId);
        } else {
            messageLabel.setText("Please select a car.");
        }
    }

    @FXML
    private void handleReturnRental(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.completeRental(selected.getRentalId());
            loadRentals();
            loadAvailableCars(); // Car is now available again
            messageLabel.setText("Rental returned: " + selected.getRentalId());
        } else {
            messageLabel.setText("Please select a rental record.");
        }
    }
}