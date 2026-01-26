package model;

import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private String reservationId;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private double totalCharge;
    private boolean returned;

    // Constructor
    public Rental(String rentalId, String reservationId,
                  LocalDate actualStartDate, LocalDate actualEndDate,
                  double totalCharge, boolean returned) {
        this.rentalId = rentalId;
        this.reservationId = reservationId;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.totalCharge = totalCharge;
        this.returned = returned;
    }

    // Getters and Setters
    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(double totalCharge) {
        this.totalCharge = totalCharge;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    // Rental actions
    public void startRental() {
        this.returned = false;
        System.out.println("Rental " + rentalId + " started.");
        // #toconnect: RentalService will persist rental start and calculate charges
    }

    public void completeRental() {
        this.returned = true;
        System.out.println("Rental " + rentalId + " completed.");
        // #toconnect: RentalService will persist rental completion and finalize charges
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId='" + rentalId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", actualStartDate=" + actualStartDate +
                ", actualEndDate=" + actualEndDate +
                ", totalCharge=" + totalCharge +
                ", returned=" + returned +
                '}';
    }
}