package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import model.ReservationStatus;
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
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        carAvailabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(available ? "Available" : "Booked/Rented");
                    setTextFill(available ? Color.GREEN : Color.RED);
                }
            }
        });

        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadCars();
        loadReservations();
    }

    private void loadCars() {
        carTable.getItems().setAll(carService.getAllCars());
    }

    private void loadReservations() {
        reservationTable.getItems().setAll(reservationService.getAllReservations());
    }

    @FXML
    private void handleApproveReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == ReservationStatus.PENDING) {
            reservationService.approveReservation(selected.getReservationId());
            refreshAll();
            messageLabel.setText("Reservation " + selected.getReservationId() + " Approved");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Selection must be PENDING.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == ReservationStatus.PENDING) {
            reservationService.cancelReservation(selected.getReservationId());
            refreshAll();
            messageLabel.setText("Reservation Cancelled.");
            messageLabel.setTextFill(Color.ORANGE);
        }
    }

    @FXML
    private void handleConvertToRental(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == ReservationStatus.APPROVED) {
            rentalService.startRental("RNT-" + System.currentTimeMillis(), selected, 50.0);
            refreshAll();
            messageLabel.setText("Rental Active for: " + selected.getReservationId());
            messageLabel.setTextFill(Color.BLUE);
        } else {
            messageLabel.setText("Only APPROVED reservations can be rented.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleRemoveCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (carService.canRemoveOrEdit(selected.getId())) {
                carService.removeCar(selected.getId());
                loadCars();
                messageLabel.setText("Car removed.");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("Cannot remove: Car has active reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    private void refreshAll() {
        loadCars();
        loadReservations();
    }
}