package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import service.CarService;
import service.ReservationService;
import service.RentalService;

import java.util.List;

public class AdminDashboardController {

    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> carIdColumn;
    @FXML private TableColumn<Car, String> carModelColumn;
    @FXML private TableColumn<Car, Boolean> carAvailabilityColumn;

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> reservationCustomerColumn;
    @FXML private TableColumn<Reservation, String> reservationCarColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

    @FXML private Label messageLabel;

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    @FXML
    private void initialize() {
        // 1. Setup Car Table Columns
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        // Custom Cell Factory to make Availability look nice
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        carAvailabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(available ? "Available" : "Rented");
                    setTextFill(available ? Color.GREEN : Color.RED);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // 2. Setup Reservation Table Columns
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 3. Load Data
        loadCars();
        loadReservations();
    }

    private void loadCars() {
        List<Car> cars = carService.getAllCars();
        carTable.getItems().setAll(cars);
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        reservationTable.getItems().setAll(reservations);
    }

    @FXML
    private void handleApproveReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reservationService.approveReservation(selected.getReservationId());
            loadReservations();
            messageLabel.setText("Reservation " + selected.getReservationId() + " Approved");
        }
    }

    @FXML
    private void handleConvertToRental(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.startRental("RNT-" + selected.getReservationId(), selected, 50.0);
            loadReservations();
            loadCars(); // Refresh cars too since availability likely changed
            messageLabel.setText("Rental started for " + selected.getReservationId());
        }
    }

    @FXML
    private void handleRemoveCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            carService.removeCar(selected.getId());
            loadCars();
            messageLabel.setText("Car removed successfully.");
        }
    }
}