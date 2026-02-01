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
import model.Customer;
import model.Reservation;
import model.ReservationStatus;
import model.Rental;
import service.CarService;
import service.CustomerService;
import service.ReservationService;
import service.RentalService;
import service.ValidationService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {

    // --- Common ---
    @FXML private TabPane tabPane;
    @FXML private Label messageLabel;

    // --- Cars Tab ---
    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> carIdColumn;
    @FXML private TableColumn<Car, String> carModelColumn;
    @FXML private TableColumn<Car, String> carTypeColumn;
    @FXML private TableColumn<Car, String> carFuelColumn;
    @FXML private TableColumn<Car, Integer> carSeatsColumn;
    @FXML private TableColumn<Car, Double> carPriceColumn;
    @FXML private TableColumn<Car, Boolean> carAvailabilityColumn;

    @FXML private TextField newCarModelField;
    @FXML private TextField newCarTypeField;
    @FXML private TextField newCarFuelField;
    @FXML private TextField newCarSeatsField;
    @FXML private TextField newCarPriceField;

    // --- Customers Tab ---
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> customerNameColumn;
    @FXML private TableColumn<Customer, String> customerEmailColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;

    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    @FXML private TextField customerPhoneField;
    @FXML private PasswordField customerPasswordField;

    // --- Reservations Tab ---
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> reservationCustomerColumn;
    @FXML private TableColumn<Reservation, String> reservationCarColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

    // --- Rentals / Prices Tab ---
    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, String> rentalIdColumn;
    @FXML private TableColumn<Rental, String> rentalReservationColumn;
    @FXML private TableColumn<Rental, LocalDate> rentalStartDateColumn;
    @FXML private TableColumn<Rental, LocalDate> rentalEndDateColumn;
    @FXML private TableColumn<Rental, Double> rentalPriceColumn;
    @FXML private TableColumn<Rental, Boolean> rentalPaidColumn;

    private CarService carService = new CarService();
    private CustomerService customerService = new CustomerService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    @FXML
    private void initialize() {
        // --- Initialize Tables ---
        setupCarTable();
        setupCustomerTable();
        setupReservationTable();
        setupRentalTable();

        // --- Initial Data Load ---
        refreshAll();

        // --- Listeners ---
        carTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) populateCarFields(newSelection);
        });

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) populateCustomerFields(newSelection);
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                refreshAll();
                clearMessage();
            }
        });
    }

    private void setupCarTable() {
        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        carFuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        carSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
        carPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        carAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        carAvailabilityColumn.setCellFactory(column -> createStatusCell(available -> available ? "Available" : "Booked/Rented", available -> available ? Color.GREEN : Color.RED));
    }

    private void setupCustomerTable() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void setupReservationTable() {
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupRentalTable() {
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualStartDate"));
        rentalEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualEndDate"));
        rentalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalCharge"));
        rentalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
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

    private void loadCars() { carTable.getItems().setAll(carService.getAllCars()); }
    private void loadCustomers() { customerTable.getItems().setAll(customerService.getAllCustomers()); }
    private void loadReservations() { reservationTable.getItems().setAll(reservationService.getAllReservations()); }
    private void loadRentals() {
        List<Rental> returnedRentals = rentalService.getAllRentals().stream().filter(Rental::isReturned).collect(Collectors.toList());
        rentalTable.getItems().setAll(returnedRentals);
    }

    private void populateCarFields(Car car) {
        newCarModelField.setText(car.getModel());
        newCarTypeField.setText(car.getType());
        newCarFuelField.setText(car.getFuel());
        newCarSeatsField.setText(String.valueOf(car.getSeats()));
        newCarPriceField.setText(String.valueOf(car.getPricePerDay()));
    }

    private void populateCustomerFields(Customer customer) {
        customerNameField.setText(customer.getName());
        customerEmailField.setText(customer.getEmail());
        customerPhoneField.setText(customer.getPhone());
        customerPasswordField.setText(customer.getPassword());
    }

    // --- Car Actions ---

    @FXML private void handleAddCar(ActionEvent event) {
        String validationError = ValidationService.validateCarInput(newCarModelField.getText(), newCarTypeField.getText(), newCarFuelField.getText(), newCarSeatsField.getText(), newCarPriceField.getText());
        if (validationError != null) {
            showMessage(validationError, true);
            return;
        }
        carService.addCar(new Car(carService.generateNextId(), newCarModelField.getText(), true, newCarTypeField.getText(), newCarFuelField.getText(), Integer.parseInt(newCarSeatsField.getText()), Double.parseDouble(newCarPriceField.getText())));
        refreshAll();
        clearCarInputFields();
        showMessage("Car added successfully.", false);
    }

    @FXML private void handleEditCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a car to edit.", true); return; }
        if (!selected.isAvailable()) { showMessage("Cannot edit car: It is currently booked or rented.", true); return; }

        String validationError = ValidationService.validateCarInput(newCarModelField.getText(), newCarTypeField.getText(), newCarFuelField.getText(), newCarSeatsField.getText(), newCarPriceField.getText());
        if (validationError != null) {
            showMessage(validationError, true);
            return;
        }
        selected.setModel(newCarModelField.getText());
        selected.setType(newCarTypeField.getText());
        selected.setFuel(newCarFuelField.getText());
        selected.setSeats(Integer.parseInt(newCarSeatsField.getText()));
        selected.setPricePerDay(Double.parseDouble(newCarPriceField.getText()));
        carService.updateCar(selected);
        refreshAll();
        clearCarInputFields();
        showMessage("Car updated successfully.", false);
    }

    @FXML private void handleRemoveCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a car to remove.", true); return; }
        if (!carService.canRemoveOrEdit(selected.getId())) { showMessage("Cannot remove car: It has active reservations.", true); return; }
        carService.removeCar(selected.getId());
        refreshAll();
        clearCarInputFields();
        showMessage("Car removed successfully.", false);
    }

    @FXML private void handleAddCustomer(ActionEvent event) {
        String validationError = ValidationService.validateCustomerInput(customerNameField.getText(), customerEmailField.getText(), customerPhoneField.getText(), customerPasswordField.getText());
        if (validationError != null) {
            showMessage(validationError, true);
            return;
        }
        customerService.addCustomer(new Customer(customerService.generateNextId(), customerNameField.getText(), customerEmailField.getText(), customerPhoneField.getText(), customerPasswordField.getText()));
        refreshAll();
        clearCustomerInputFields();
        showMessage("Customer added successfully.", false);
    }

    @FXML private void handleEditCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a customer to edit.", true); return; }
        String validationError = ValidationService.validateCustomerInput(customerNameField.getText(), customerEmailField.getText(), customerPhoneField.getText(), customerPasswordField.getText());
        if (validationError != null) {
            showMessage(validationError, true);
            return;
        }
        selected.setName(customerNameField.getText());
        selected.setEmail(customerEmailField.getText());
        selected.setPhone(customerPhoneField.getText());
        selected.setPassword(customerPasswordField.getText());
        customerService.updateCustomer(selected);
        refreshAll();
        clearCustomerInputFields();
        showMessage("Customer updated successfully.", false);
    }

    @FXML private void handleRemoveCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a customer to remove.", true); return; }
        if (!customerService.removeCustomer(selected.getId())) { showMessage("Cannot remove customer: They have active or pending reservations.", true); return; }
        refreshAll();
        clearCustomerInputFields();
        showMessage("Customer removed successfully.", false);
    }

    @FXML private void handleApproveReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a reservation to approve.", true); return; }
        if (selected.getStatus() != ReservationStatus.PENDING) { showMessage("Only PENDING reservations can be approved.", true); return; }
        reservationService.approveReservation(selected.getReservationId());
        refreshAll();
        showMessage("Reservation " + selected.getReservationId() + " approved.", false);
    }

    @FXML private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a reservation to cancel or revert.", true); return; }
        if (selected.getStatus() == ReservationStatus.PENDING) {
            reservationService.cancelReservation(selected.getReservationId());
            refreshAll();
            showMessage("Reservation " + selected.getReservationId() + " has been cancelled.", false);
        } else if (selected.getStatus() == ReservationStatus.APPROVED) {
            reservationService.revertApproval(selected.getReservationId());
            refreshAll();
            showMessage("Reservation " + selected.getReservationId() + " has been reverted to PENDING.", false);
        } else {
            showMessage("Only PENDING or APPROVED reservations can be managed here.", true);
        }
    }

    @FXML private void handleMarkPaid(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a rental to mark as paid.", true); return; }
        rentalService.updateRentalPaidStatus(selected.getRentalId(), true);
        refreshAll();
        showMessage("Rental " + selected.getRentalId() + " marked as PAID.", false);
    }

    @FXML private void handleMarkUnpaid(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("Please select a rental to mark as unpaid.", true); return; }
        rentalService.updateRentalPaidStatus(selected.getRentalId(), false);
        refreshAll();
        showMessage("Rental " + selected.getRentalId() + " marked as UNPAID.", false);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Car Rental System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error logging out.", true);
        }
    }

    private void clearCarInputFields() {
        newCarModelField.clear();
        newCarTypeField.clear();
        newCarFuelField.clear();
        newCarSeatsField.clear();
        newCarPriceField.clear();
    }

    private void clearCustomerInputFields() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerPasswordField.clear();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    private void refreshAll() {
        loadCars();
        loadCustomers();
        loadReservations();
        loadRentals();
    }
}
