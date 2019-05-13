import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        testWindow(primaryStage);
        //mainWindow(primaryStage);
    }

    void mainWindow(Stage primaryStage) {

        if (primaryStage.isShowing()) primaryStage.close();

        String mainfxml = "view/MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Внутренняя ошибка");
            alert.setContentText("Ошибка загрузки\n" + e.getMessage());
            alert.showAndWait();
            return;
        }
        primaryStage.setTitle("Симулятор ЛВС 2000");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        MainScreen MS = loader.getController();
        MS.setInitStage(primaryStage);
        MS.setMain(this);
        MS.createLVS(18,20000,5000,2000,2000);
        MS.drawLVS();
        primaryStage.show();

    }

    void testWindow(Stage primaryStage) {

        if (primaryStage.isShowing()) primaryStage.close();

        String mainfxml = "view/TestScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Внутренняя ошибка");
            alert.setContentText("Ошибка загрузки\n" + e.getMessage());
            alert.showAndWait();
            return;
        }
        primaryStage.setTitle("Симулятор ЛВС 2000");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        TestScreen TS = loader.getController();
        TS.setInitStage(primaryStage);
        TS.setMain(this);
        primaryStage.show();
    }
}
