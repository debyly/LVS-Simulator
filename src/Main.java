import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.MainScreen;
import view.TestScreen;
import view.WindowManager;

import java.io.IOException;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        WindowManager manager = new WindowManager(primaryStage);
    }

}
