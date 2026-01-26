package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import service.CarService;
import service.ReservationService;
import service.RentalService;

import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<Car> carTable;

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
    private TableColumn<Reservation, String> reservationCustomerColumn;

    @FXML
    private TableColumn<Reservation, String> reservationCarColumn;

    @FXML
    private TableColumn<Reservation, String> reservationStatusColumn;

    @FXML
    private Button approveReservationButton;

    @FXML
    private Button convertToRentalButton;

    @FXML
    private Button removeCarButton;

    @FXML
    private Label messageLabel;

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    // Initialize dashboard data
    @FXML
    private void initialize() {
        loadCars();
        loadReservations();
        // #toconnect: Bind table columns to Car and Reservation properties
    }

    // Load cars into table
    private void loadCars() {
        List<Car> cars = carService.getAllCars();
        carTable.getItems().setAll(cars);
        messageLabel.setText("Cars loaded: " + cars.size());
    }

    // Load reservations into table
    private void loadReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        reservationTable.getItems().setAll(reservations);
        messageLabel.setText("Reservations loaded: " + reservations.size());
    }

    // Approve selected reservation
    @FXML
    private void handleApproveReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reservationService.approveReservation(selected.getReservationId());
            loadReservations();
            messageLabel.setText("Reservation approved: " + selected.getReservationId());
            // #toconnect: Refresh UI table after approval
        } else {
            messageLabel.setText("No reservation selected.");
        }
    }

    // Convert reservation to rental
    @FXML
    private void handleConvertToRental(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.startRental("RNT-" + selected.getReservationId(), selected, 50.0); // Example daily rate
            loadReservations();
            messageLabel.setText("Reservation converted to rental: " + selected.getReservationId());
            // #toconnect: Refresh UI table after conversion
        } else {
            messageLabel.setText("No reservation selected.");
        }
    }

    // Remove selected car
    @FXML
    private void handleRemoveCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            carService.removeCar(selected.getId());
            loadCars();
            messageLabel.setText("Car removed: " + selected.getId());
            // #toconnect: Refresh UI table after removal
        } else {
            messageLabel.setText("No car selected.");
        }
    }
}