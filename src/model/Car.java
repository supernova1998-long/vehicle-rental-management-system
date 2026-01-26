package model;

public class Car extends Vehicle {
    private String type;
    private String fuel;
    private int seats;
    private double pricePerDay;

    // Constructor
    public Car(String id, String model, boolean available,
               String type, String fuel, int seats, double pricePerDay) {
        super(id, model, available);
        this.type = type;
        this.fuel = fuel;
        this.seats = seats;
        this.pricePerDay = pricePerDay;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    // Implement abstract method from Vehicle
    @Override
    public double calculatePrice(int days) {
        return days * pricePerDay;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + getId() + '\'' +
                ", model='" + getModel() + '\'' +
                ", type='" + type + '\'' +
                ", fuel='" + fuel + '\'' +
                ", seats=" + seats +
                ", pricePerDay=" + pricePerDay +
                ", available=" + isAvailable() +
                '}';
    }
}