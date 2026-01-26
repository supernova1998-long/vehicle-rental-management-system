package model;

public class Admin extends User {

    // Constructor
    public Admin(String id, String name, String email, String phone, String password) {
        super(id, name, email, phone, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    // Admin-specific actions
    public void approveReservation(String reservationId) {
        System.out.println("Reservation " + reservationId + " approved by Admin " + getName());
        // #toconnect: call ReservationService.approveReservation(reservationId)
    }

    public void convertToRental(String reservationId) {
        System.out.println("Reservation " + reservationId + " converted to Rental by Admin " + getName());
        // #toconnect: call RentalService.startRental(reservationId)
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}