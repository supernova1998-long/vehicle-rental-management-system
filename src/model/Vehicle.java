package model;

public abstract class Vehicle {
  private String id;
  private String model;
  private boolean available;

  // Constructor
  public Vehicle(String id, String model, boolean available) {
    this.id = id;
    this.model = model;
    this.available = available;
  }

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean state) {
    this.available = state;
  }

  // Abstract method for subclasses to implement pricing logic
  public abstract double calculatePrice(int days);

  @Override
  public String toString() {
    return "Vehicle{" +
            "id='" + id + '\'' +
            ", model='" + model + '\'' +
            ", available=" + available +
            '}';
  }
}