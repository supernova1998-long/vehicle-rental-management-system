package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import model.User;
import service.AuthService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    private AuthService authService = new AuthService();

    // Handle login button click
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        boolean success = authService.login(email, password);

        if (success) {
            User user = authService.getLoggedInUser();
            messageLabel.setText("Login successful! Welcome " + user.getName());

            if ("Admin".equals(user.getRole())) {
                // #toconnect: Load Admin Dashboard
                loadDashboard("/resources/admin_dashboard.fxml", "Admin Dashboard");
            } else if ("Customer".equals(user.getRole())) {
                // #toconnect: Load Customer Dashboard
                loadDashboard("/resources/customer_dashboard.fxml", "Customer Dashboard");
            }
        } else {
            messageLabel.setText("Invalid email or password. Please try again.");
        }
    }

    // Load dashboard scene
    private void loadDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
        }
    }
}