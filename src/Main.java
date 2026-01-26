import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Vehicle Rental System - Login");
            primaryStage.setScene(new Scene(root, 400, 250));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading login.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}