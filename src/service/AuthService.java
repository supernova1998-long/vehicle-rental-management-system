package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Admin;
import model.Customer;
import model.User;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static final String ADMIN_FILE = "data/admins.json";
    private static final String CUSTOMER_FILE = "data/customers.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private User loggedInUser;

    // Load admins from JSON
    private List<Admin> loadAdmins() {
        try (FileReader reader = new FileReader(ADMIN_FILE)) {
            Type listType = new TypeToken<ArrayList<Admin>>() {}.getType();
            List<Admin> admins = gson.fromJson(reader, listType);
            return admins != null ? admins : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading admins: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Load customers from JSON
    private List<Customer> loadCustomers() {
        try (FileReader reader = new FileReader(CUSTOMER_FILE)) {
            Type listType = new TypeToken<ArrayList<Customer>>() {}.getType();
            List<Customer> customers = gson.fromJson(reader, listType);
            return customers != null ? customers : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Authenticate user (Admin or Customer)
    public boolean login(String email, String password) {
        // Check admins
        for (Admin admin : loadAdmins()) {
            if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
                loggedInUser = admin;
                System.out.println("AuthService: Admin logged in -> " + admin.getName());
                // #toconnect: LoginController will call this for admin login
                return true;
            }
        }

        // Check customers
        for (Customer customer : loadCustomers()) {
            if (customer.getEmail().equals(email) && customer.getPassword().equals(password)) {
                loggedInUser = customer;
                System.out.println("AuthService: Customer logged in -> " + customer.getName());
                // #toconnect: LoginController will call this for customer login
                return true;
            }
        }

        System.out.println("AuthService: Login failed for email -> " + email);
        return false;
    }

    // Logout
    public void logout() {
        if (loggedInUser != null) {
            System.out.println("AuthService: User logged out -> " + loggedInUser.getName());
            loggedInUser = null;
            // #toconnect: Controllers will call this when user logs out
        } else {
            System.out.println("AuthService: No user currently logged in.");
        }
    }

    // Get currently logged-in user
    public User getLoggedInUser() {
        return loggedInUser;
    }
}