package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import model.Car;
import model.Customer;
import model.Reservation;
import model.ReservationStatus;
import model.Rental;
import service.CarService;
import service.CustomerService;
import service.ReservationService;
import service.RentalService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {

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

    @FXML private Label messageLabel;

    private CarService carService = new CarService();
    private CustomerService customerService = new CustomerService();
    private ReservationService reservationService = new ReservationService();
    private RentalService rentalService = new RentalService();

    @FXML
    private void initialize() {
        // Initialize Car Table
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
                    setText(available ? "Available" : "Booked/Rented");
                    setTextFill(available ? Color.GREEN : Color.RED);
                }
            }
        });

        // Initialize Customer Table
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Initialize Reservation Table
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        reservationCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        reservationCarColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize Rental Table
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalReservationColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        rentalStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualStartDate"));
        rentalEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualEndDate"));
        rentalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalCharge"));
        rentalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));

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

        // Load Data
        loadCars();
        loadCustomers();
        loadReservations();
        loadRentals();
        
        // Add listener to car table selection to populate edit fields
        carTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCarFields(newSelection);
            }
        });

        // Add listener to customer table selection to populate edit fields
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCustomerFields(newSelection);
            }
        });
    }

    private void loadCars() {
        carTable.getItems().setAll(carService.getAllCars());
    }

    private void loadCustomers() {
        customerTable.getItems().setAll(customerService.getAllCustomers());
    }

    private void loadReservations() {
        reservationTable.getItems().setAll(reservationService.getAllReservations());
    }

    private void loadRentals() {
        // Only show returned rentals
        List<Rental> returnedRentals = rentalService.getAllRentals().stream()
                .filter(Rental::isReturned)
                .collect(Collectors.toList());
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

    @FXML
    private void handleAddCar(ActionEvent event) {
        try {
            String model = newCarModelField.getText();
            String type = newCarTypeField.getText();
            String fuel = newCarFuelField.getText();
            String seatsStr = newCarSeatsField.getText();
            String priceStr = newCarPriceField.getText();

            if (model.isEmpty() || type.isEmpty() || fuel.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
                messageLabel.setText("Please fill all car fields.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            
            int seats = Integer.parseInt(seatsStr);
            double price = Double.parseDouble(priceStr);

            String id = carService.generateNextId();
            Car newCar = new Car(id, model, true, type, fuel, seats, price);
            carService.addCar(newCar);

            loadCars();
            clearCarInputFields();
            messageLabel.setText("Car added successfully!");
            messageLabel.setTextFill(Color.GREEN);

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number for seats or price.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleEditCar(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select a car to edit.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!selected.isAvailable()) {
            messageLabel.setText("Cannot edit car: Car is currently booked or rented.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            String model = newCarModelField.getText();
            String type = newCarTypeField.getText();
            String fuel = newCarFuelField.getText();
            String seatsStr = newCarSeatsField.getText();
            String priceStr = newCarPriceField.getText();

            if (model.isEmpty() || type.isEmpty() || fuel.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
                messageLabel.setText("Please fill all car fields to update.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            
            int seats = Integer.parseInt(seatsStr);
            double price = Double.parseDouble(priceStr);

            selected.setModel(model);
            selected.setType(type);
            selected.setFuel(fuel);
            selected.setSeats(seats);
            selected.setPricePerDay(price);

            carService.updateCar(selected);
            loadCars();
            clearCarInputFields();
            messageLabel.setText("Car updated successfully!");
            messageLabel.setTextFill(Color.GREEN);

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number for seats or price.");
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
                clearCarInputFields();
                messageLabel.setText("Car removed.");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("Cannot remove: Car has active reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    // --- Customer Actions ---

    @FXML
    private void handleAddCustomer(ActionEvent event) {
        String name = customerNameField.getText();
        String email = customerEmailField.getText();
        String phone = customerPhoneField.getText();
        String password = customerPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all customer fields.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        String id = customerService.generateNextId();
        Customer newCustomer = new Customer(id, name, email, phone, password);
        customerService.addCustomer(newCustomer);

        loadCustomers();
        clearCustomerInputFields();
        messageLabel.setText("Customer added successfully!");
        messageLabel.setTextFill(Color.GREEN);
    }

    @FXML
    private void handleEditCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select a customer to edit.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        String name = customerNameField.getText();
        String email = customerEmailField.getText();
        String phone = customerPhoneField.getText();
        String password = customerPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all customer fields to update.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        selected.setName(name);
        selected.setEmail(email);
        selected.setPhone(phone);
        selected.setPassword(password);

        customerService.updateCustomer(selected);
        loadCustomers();
        clearCustomerInputFields();
        messageLabel.setText("Customer updated successfully!");
        messageLabel.setTextFill(Color.GREEN);
    }

    @FXML
    private void handleRemoveCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (customerService.removeCustomer(selected.getId())) {
                loadCustomers();
                clearCustomerInputFields();
                messageLabel.setText("Customer removed.");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("Cannot remove: Customer has active/pending reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    // --- Reservation Actions ---

    @FXML
    private void handleApproveReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() == ReservationStatus.PENDING) {
            reservationService.approveReservation(selected.getReservationId());
            refreshAll();
            messageLabel.setText("Reservation " + selected.getReservationId() + " Approved");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Selection must be PENDING to approve.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus() == ReservationStatus.PENDING) {
                reservationService.cancelReservation(selected.getReservationId());
                refreshAll();
                messageLabel.setText("Reservation Cancelled.");
                messageLabel.setTextFill(Color.ORANGE);
            } else if (selected.getStatus() == ReservationStatus.APPROVED) {
                reservationService.revertApproval(selected.getReservationId());
                refreshAll();
                messageLabel.setText("Reservation Reverted to PENDING.");
                messageLabel.setTextFill(Color.BLUE);
            } else {
                messageLabel.setText("Can only cancel PENDING or revert APPROVED reservations.");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    // --- Rental Actions ---

    @FXML
    private void handleMarkPaid(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.updateRentalPaidStatus(selected.getRentalId(), true);
            refreshAll();
            messageLabel.setText("Rental " + selected.getRentalId() + " marked as PAID.");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Please select a rental.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleMarkUnpaid(ActionEvent event) {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentalService.updateRentalPaidStatus(selected.getRentalId(), false);
            refreshAll();
            messageLabel.setText("Rental " + selected.getRentalId() + " marked as UNPAID.");
            messageLabel.setTextFill(Color.ORANGE);
        } else {
            messageLabel.setText("Please select a rental.");
            messageLabel.setTextFill(Color.RED);
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

    private void refreshAll() {
        loadCars();
        loadCustomers();
        loadReservations();
        loadRentals();
    }
}