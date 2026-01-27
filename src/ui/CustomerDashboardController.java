package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import model.Car;
import model.Reservation;
import model.ReservationStatus;
import model.Rental;
import model.User;
import service.CarService;
import service.ReservationService;
import service.RentalService;
import service.AuthService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
                } else {
                    setText(available ? "Available" : "Reserved");
                    setTextFill(available ? Color.GREEN : Color.RED);
                }
            }
        });

        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));

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

        refreshAll();
    }

    private void loadAvailableCars() {
        availableCarsTable.getItems().setAll(carService.getAvailableCars());
    }

    private void loadReservations() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser != null) {
            List<Reservation> reservations = reservationService.getReservationsByCustomer(currentUser.getId());
            reservationTable.getItems().setAll(reservations);
        }
    }

    private void loadRentals() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser != null) {
            List<Rental> allRentals = rentalService.getAllRentals();
            List<Reservation> userRes = reservationService.getReservationsByCustomer(currentUser.getId());
            List<String> userResIds = userRes.stream().map(Reservation::getReservationId).collect(Collectors.toList());

            List<Rental> userRentals = allRentals.stream()
                    .filter(r -> userResIds.contains(r.getReservationId()))
                    .collect(Collectors.toList());

            rentalTable.getItems().setAll(userRentals);
        }
    }

    @FXML
    private void handleMakeReservation(ActionEvent event) {
        Car selected = availableCarsTable.getSelectionModel().getSelectedItem();
        User currentUser = AuthService.getLoggedInUser();
        if (selected != null && currentUser != null) {
            String resId = "RES-" + System.currentTimeMillis();
            reservationService.createReservation(resId, currentUser.getId(), selected.getId(),
                    LocalDate.now(), LocalDate.now().plusDays(3));
            refreshAll();
            messageLabel.setText("Reservation " + resId + " submitted (Pending).");
        }
    }

    @FXML
    private void handleRentAction(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == ReservationStatus.APPROVED) {
            rentalService.startRental("RNT-" + System.currentTimeMillis(), selected, 50.0);
            refreshAll();
            messageLabel.setText("Rental started!");
        } else {
            messageLabel.setText("You can only rent APPROVED reservations.");
        }
    }

    @FXML
    private void handleReturnRental(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isReturned()) {
            rentalService.completeRental(selected.getRentalId());
            refreshAll();
            messageLabel.setText("Car returned successfully.");
        }
    }

    private void refreshAll() {
        loadAvailableCars();
        loadReservations();
        loadRentals();
    }
}