package model;

import java.time.LocalDate;

public class Reservation {
    private String reservationId;
    private String customerId;
    private String carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;

    // Constructor
    public Reservation(String reservationId, String customerId, String carId,
                       LocalDate startDate, LocalDate endDate, ReservationStatus status) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    // Reservation actions
    public void approve() {
        this.status = ReservationStatus.APPROVED;
        System.out.println("Reservation " + reservationId + " approved.");
        // #toconnect: ReservationService will persist status change
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        System.out.println("Reservation " + reservationId + " cancelled.");
        // #toconnect: ReservationService will persist status change
    }

    public void convertToRental() {
        this.status = ReservationStatus.RENTED;
        System.out.println("Reservation " + reservationId + " converted to rental.");
        // #toconnect: RentalService will create Rental record
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", carId='" + carId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}