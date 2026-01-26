package model;

public enum ReservationStatus {
    PENDING,     // Reservation created but not yet approved (#toconnect: ReservationService will handle approval)
    APPROVED,    // Reservation approved by Admin (#toconnect: ReservationService updates status)
    CANCELLED,   // Reservation cancelled by Admin or Customer (#toconnect: ReservationService cancels)
    RENTED,      // Reservation converted into an active rental (#toconnect: RentalService starts rental)
    COMPLETED    // Rental finished and vehicle returned (#toconnect: RentalService completes rental)
}