package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDashboardController {

    // --- Common ---
    @FXML private TabPane tabPane;
    @FXML private Label messageLabel;

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

    private CarService carService = new CarService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    @FXML
    private void initialize() {
        // --- Initialize Tables ---
        setupAvailableCarsTable();
        setupReservationTable();
        setupRentalTable();

        // --- Initial Data Load ---
        refreshAll();

        // --- Listeners ---
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                refreshAll();
                clearMessage();
            }
        });
    }

    private void setupAvailableCarsTable() {
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        carFuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        carSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        carPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        carAvailabilityColumn.setCellFactory(column -> createStatusCell(available -> available ? "Available" : "Reserved", available -> available ? Color.GREEN : Color.RED));
    }

    private void setupReservationTable() {
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        reservationEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupRentalTable() {
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualStartDate"));
        rentalEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualEndDate"));
        rentalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalCharge"));
        rentalReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));
        rentalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));

        rentalReturnedColumn.setCellFactory(column -> createStatusCell(returned -> returned ? "Yes" : "No", returned -> returned ? Color.GREEN : Color.RED));
        rentalPaidColumn.setCellFactory(column -> createStatusCell(paid -> paid ? "Yes" : "No", paid -> paid ? Color.GREEN : Color.RED));
    }

    private <T> TableCell<T, Boolean> createStatusCell(java.util.function.Function<Boolean, String> textMapper, java.util.function.Function<Boolean, Color> colorMapper) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(textMapper.apply(item));
                    setTextFill(colorMapper.apply(item));
                }
            }
        };
    }

    private void loadAvailableCars() { availableCarsTable.getItems().setAll(carService.getAvailableCars()); }
    private void loadReservations() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser != null) {
            reservationTable.getItems().setAll(reservationService.getReservationsByCustomer(currentUser.getId()));
        }
    }
    private void loadRentals() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser != null) {
            List<Rental> userRentals = rentalService.getAllRentals().stream()
                    .filter(r -> reservationService.getReservationsByCustomer(currentUser.getId()).stream().anyMatch(res -> res.getReservationId().equals(r.getReservationId())))
                    .filter(Rental::isReturned)
                    .collect(Collectors.toList());
            rentalTable.getItems().setAll(userRentals);
        }
    }

    @FXML private void handleMakeReservation(ActionEvent event) {
        Car selected = availableCarsTable.getSelectionModel().getSelectedItem();
        User currentUser = AuthService.getLoggedInUser();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (selected == null) { showMessage("Please select a car to reserve.", true); return; }
        String dateValidation = ValidationService.validateReservationDates(start, end);
        if (dateValidation != null) { showMessage(dateValidation, true); return; }
        if (currentUser == null) { showMessage("No user logged in.", true); return; }

        reservationService.createReservation(reservationService.generateNextId(), currentUser.getId(), selected.getId(), start, end);
        refreshAll();
        clearDatePickerFields();
        showMessage("Reservation submitted successfully! (ID: " + reservationService.generateNextId() + ")", false);
    }

    @FXML private void handleRentAction(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a reservation to rent.", true); return; }
        if (selected.getStatus() != ReservationStatus.APPROVED) { showMessage("Only APPROVED reservations can be rented.", true); return; }
        
        rentalService.startRental("RNT-" + System.currentTimeMillis(), selected);
        refreshAll();
        showMessage("Rental started successfully! Car collected.", false);
    }

    @FXML private void handleReturnRental(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a RENTED reservation to return the car.", true); return; }
        if (selected.getStatus() != ReservationStatus.RENTED) { showMessage("Only RENTED reservations can be returned.", true); return; }
        
        Rental activeRental = rentalService.findActiveRentalByReservationId(selected.getReservationId());
        if (activeRental == null) { showMessage("Error: No active rental found for this reservation.", true); return; }
        
        rentalService.completeRental(activeRental.getRentalId());
        refreshAll();
        showMessage("Car returned successfully. Final price calculated.", false);
    }

    @FXML private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a reservation to cancel.", true); return; }
        if (selected.getStatus() == ReservationStatus.PENDING || selected.getStatus() == ReservationStatus.APPROVED) {
            reservationService.cancelReservation(selected.getReservationId());
            refreshAll();
            showMessage("Reservation " + selected.getReservationId() + " cancelled.", false);
        } else {
            showMessage("Only PENDING or APPROVED reservations can be cancelled.", true);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Vehicle Rental System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error logging out.", true);
        }
    }

    private void clearDatePickerFields() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    private void refreshAll() {
        loadAvailableCars();
        loadReservations();
        loadRentals();
    }
}