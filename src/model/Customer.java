package model;

public class Customer extends User {

    // Constructor
    public Customer(String id, String name, String email, String phone, String password) {
        super(id, name, email, phone, password);
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    // Customer-specific actions
    public void makeReservation(String reservationId) {
        System.out.println("Customer " + getName() + " made reservation " + reservationId);
        // #toconnect: call ReservationService.createReservation(reservationId, this)
    }

    public void viewReservations() {
        System.out.println("Customer " + getName() + " is viewing their reservations");
        // #toconnect: call ReservationService.getReservationsByCustomer(getId())
    }

    public void returnRental(String rentalId) {
        System.out.println("Customer " + getName() + " returned rental " + rentalId);
        // #toconnect: call RentalService.completeRental(rentalId)
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}