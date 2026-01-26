package model;

public abstract class User {
  private String id;
  private String name;
  private String email;
  private String phone;
  private String password;

  // Constructor
  public User(String id, String name, String email, String phone, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.password = password;
  }

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  // Abstract method(s) for role-specific behavior
  public abstract String getRole();

  @Override
  public String toString() {
    return "User{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", role='" + getRole() + '\'' +
            '}';
  }
}