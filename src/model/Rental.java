package model;

import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private String reservationId;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private double totalCharge;
    private boolean returned;
    private boolean paid;

    public Rental(String rentalId, String reservationId,
                  LocalDate actualStartDate, LocalDate actualEndDate,
                  double totalCharge, boolean returned, boolean paid) {
        this.rentalId = rentalId;
        this.reservationId = reservationId;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.totalCharge = totalCharge;
        this.returned = returned;
        this.paid = paid;
    }

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

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void completeRental(LocalDate endDate, double finalCharge) {
        this.actualEndDate = endDate;
        this.totalCharge = finalCharge;
        this.returned = true;
        // Paid status is not set here, it's set by Admin
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
                ", paid=" + paid +
                '}';
    }
}