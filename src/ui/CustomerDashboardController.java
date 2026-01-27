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
import service.ValidationService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDashboardController {

    // --- Available Cars Tab ---
    @FXML private TableView<Car> availableCarsTable;
    @FXML private TableColumn<Car, String> carIdColumn;
    @FXML private TableColumn<Car, String> carModelColumn;
    @FXML private TableColumn<Car, String> carTypeColumn;
    @FXML private TableColumn<Car, String> carFuelColumn;
    @FXML private TableColumn<Car, Integer> carSeatsColumn;
    @FXML private TableColumn<Car, Double> carPriceColumn;
    @FXML private TableColumn<Car, Boolean> carAvailabilityColumn;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    // --- Reservations Tab ---
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> reservationCarColumn;
    @FXML private TableColumn<Reservation, LocalDate> reservationStartDateColumn;
    @FXML private TableColumn<Reservation, LocalDate> reservationEndDateColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

    // --- Rental History & Prices Tab ---
    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, String> rentalIdColumn;
    @FXML private TableColumn<Rental, String> rentalReservationColumn;
    @FXML private TableColumn<Rental, LocalDate> rentalStartDateColumn;
    @FXML private TableColumn<Rental, LocalDate> rentalEndDateColumn;
    @FXML private TableColumn<Rental, Double> rentalPriceColumn;
    @FXML private TableColumn<Rental, Boolean> rentalReturnedColumn;
    @FXML private TableColumn<Rental, Boolean> rentalPaidColumn;

    @FXML private Label messageLabel;

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    @FXML
    private void initialize() {
        // Available Cars Table
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        carFuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        carSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        carPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        carAvailabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(available ? "Available" : "Reserved");
                    setTextFill(available ? Color.GREEN : Color.RED);
                }
            }
        });

        // Reservation Table
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        reservationEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Rental Table
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualStartDate"));
        rentalEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualEndDate"));
        rentalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalCharge"));
        rentalReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));
        rentalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));

        rentalReturnedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean returned, boolean empty) {
                super.updateItem(returned, empty);
                if (empty || returned == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(returned ? "Yes" : "No");
                    setTextFill(returned ? Color.GREEN : Color.RED);
                }
            }
        });

        rentalPaidColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean paid, boolean empty) {
                super.updateItem(paid, empty);
                if (empty || paid == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(paid ? "Yes" : "No");
                    setTextFill(paid ? Color.GREEN : Color.RED);
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

            // Filter for rentals belonging to the user AND are returned (completed)
            List<Rental> userRentals = allRentals.stream()
                    .filter(r -> userResIds.contains(r.getReservationId()))
                    .filter(Rental::isReturned) // Only show returned rentals
                    .collect(Collectors.toList());

            rentalTable.getItems().setAll(userRentals);
        }
    }

    @FXML
    private void handleMakeReservation(ActionEvent event) {
        Car selected = availableCarsTable.getSelectionModel().getSelectedItem();
        User currentUser = AuthService.getLoggedInUser();
        
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (selected == null) {
            messageLabel.setText("Please select a car.");
            messageLabel.setTextFill(Color.RED);
            return;
        }
        
        String dateValidation = ValidationService.validateReservationDates(start, end);
        if (dateValidation != null) {
            messageLabel.setText(dateValidation);
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (currentUser != null) {
            String resId = reservationService.generateNextId();
            reservationService.createReservation(resId, currentUser.getId(), selected.getId(), start, end);
            refreshAll();
            messageLabel.setText("Reservation " + resId + " submitted (Pending).");
            messageLabel.setTextFill(Color.GREEN);
            
            // Clear pickers
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        }
    }

    @FXML
    private void handleRentAction(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus() == ReservationStatus.APPROVED) {
                String rentalId = "RNT-" + System.currentTimeMillis(); 
                rentalService.startRental(rentalId, selected);
                refreshAll();
                messageLabel.setText("Rental started! Car collected.");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("You can only rent APPROVED reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        } else {
            messageLabel.setText("Please select a reservation.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleReturnRental(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus() == ReservationStatus.RENTED) {
                Rental activeRental = rentalService.findActiveRentalByReservationId(selected.getReservationId());
                if (activeRental != null) {
                    rentalService.completeRental(activeRental.getRentalId());
                    refreshAll();
                    messageLabel.setText("Car returned successfully.");
                    messageLabel.setTextFill(Color.BLUE);
                } else {
                    messageLabel.setText("Error: Could not find active rental for this reservation.");
                    messageLabel.setTextFill(Color.RED);
                }
            } else {
                messageLabel.setText("You can only return cars for RENTED reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        } else {
            messageLabel.setText("Please select a reservation to return.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus() == ReservationStatus.PENDING || selected.getStatus() == ReservationStatus.APPROVED) {
                reservationService.cancelReservation(selected.getReservationId());
                refreshAll();
                messageLabel.setText("Reservation Cancelled.");
                messageLabel.setTextFill(Color.ORANGE);
            } else {
                messageLabel.setText("Can only cancel PENDING or APPROVED reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        } else {
            messageLabel.setText("Please select a reservation to cancel.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void refreshAll() {
        loadAvailableCars();
        loadReservations();
        loadRentals();
    }
}