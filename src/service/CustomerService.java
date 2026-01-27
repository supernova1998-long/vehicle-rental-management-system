package service;

import io.CustomerFileManager;
import io.ReservationFileManager;
import model.Customer;
import model.Reservation;
import model.ReservationStatus;

import java.util.List;

public class CustomerService {

    /**
     * Retrieves all customer records from the JSON store.
     */
    public List<Customer> getAllCustomers() {
        return CustomerFileManager.loadCustomers();
    }

    public void addCustomer(Customer customer) {
        CustomerFileManager.addCustomer(customer);
        System.out.println("CustomerService: Customer added -> " + customer.getName());
    }

    public void updateCustomer(Customer updatedCustomer) {
        List<Customer> customers = CustomerFileManager.loadCustomers();
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equals(updatedCustomer.getId())) {
                customers.set(i, updatedCustomer);
                break;
            }
        }
        CustomerFileManager.saveCustomers(customers);
        System.out.println("CustomerService: Customer updated -> " + updatedCustomer.getId());
    }

    public String generateNextId() {
        List<Customer> customers = CustomerFileManager.loadCustomers();
        if (customers.isEmpty()) {
            return "CUST001";
        }
        String lastId = customers.get(customers.size() - 1).getId();
        try {
            // Assuming ID format is "CUSTxxx"
            int idNum = Integer.parseInt(lastId.replace("CUST", ""));
            return String.format("CUST%03d", idNum + 1);
        } catch (NumberFormatException e) {
            return "CUST" + (customers.size() + 1);
        }
    }

    /**
     * Deletes a customer only if they have no active or pending reservations/rentals.
     * This prevents orphan records in the reservation system.
     */
    public boolean removeCustomer(String customerId) {
        if (canRemoveCustomer(customerId)) {
            CustomerFileManager.removeCustomer(customerId);
            System.out.println("CustomerService: Customer removed -> " + customerId);
            return true;
        } else {
            System.err.println("CustomerService: Cannot remove customer " + customerId + " - Active/Pending bookings detected.");
            return false;
        }
    }

    /**
     * Business Rule: A customer is "locked" if they have PENDING, APPROVED, or RENTED status.
     */
    public boolean canRemoveCustomer(String customerId) {
        List<Reservation> reservations = ReservationFileManager.loadReservations();
        return reservations.stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .noneMatch(r -> r.getStatus() == ReservationStatus.PENDING ||
                        r.getStatus() == ReservationStatus.APPROVED ||
                        r.getStatus() == ReservationStatus.RENTED);
    }

    /**
     * Finds a specific customer by their ID.
     */
    public Customer findCustomerById(String customerId) {
        return CustomerFileManager.findCustomerById(customerId);
    }
}