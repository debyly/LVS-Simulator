package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class WindowManager {

    private final Stage window;

    public WindowManager(Stage window){ this.window = window;}

    public void start() throws IOException { mainWindow(); }

    void mainWindow() throws IOException {

        if (window.isShowing()) window.close();

        String fxml = "MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root;

        root = loader.load();

        window.setTitle("Симулятор ЛВС 2000");
        window.setScene(new Scene(root));
        window.setResizable(false);
        MainScreen MS = loader.getController();
        MS.setManager(this);
        window.show();
    }

    void testWindow() throws IOException {

        if (window.isShowing()) window.close();

        String fxml = "TestScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root;
        root = loader.load();

        window.setTitle("Симулятор ЛВС 2000");
        window.setScene(new Scene(root));
        window.setResizable(false);
        TestScreen TS = loader.getController();
        TS.setInitStage(window);
        TS.setManager(this);
        window.show();
    }
}