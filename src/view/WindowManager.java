package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class WindowManager {

    private Stage window;

    public WindowManager(Stage window){ this.window = window;}

    public void start(){ mainWindow(); }

    void mainWindow() {

        if (window.isShowing()) window.close();

        String fxml = "MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Внутренняя ошибка");
            alert.setContentText("Ошибка загрузки\n"
                    + e.getCause() + "\n" + e.getMessage());
            alert.show();
            return;
        }
        window.setTitle("Симулятор ЛВС 2000");
        window.setScene(new Scene(root));
        window.setResizable(false);
        MainScreen MS = loader.getController();
        MS.setManager(this);
        window.show();
    }

    void testWindow() {

        if (window.isShowing()) window.close();

        String fxml = "TestScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Внутренняя ошибка");
            alert.setContentText("Ошибка загрузки\n"
                    + e.getCause() + "\n" + e.getMessage());
            alert.showAndWait();
            mainWindow();
            return;
        }

        window.setTitle("Симулятор ЛВС 2000");
        window.setScene(new Scene(root));
        window.setResizable(false);
        TestScreen TS = loader.getController();
        TS.setInitStage(window);
        TS.setManager(this);
        window.show();
    }
}
