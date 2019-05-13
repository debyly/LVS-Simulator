import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        mainWindow(primaryStage);
    }

    public void mainWindow(Stage primaryStage) throws IOException {

        if (primaryStage.isShowing()) primaryStage.close();

        String mainfxml = "view/MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = loader.load();
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

    public void testWindow(Stage primaryStage) throws IOException {

        if (primaryStage.isShowing()) primaryStage.close();

        String mainfxml = "view/TestScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = loader.load();
        primaryStage.setTitle("Симулятор ЛВС 2000");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        TestScreen TS = loader.getController();
        TS.setInitStage(primaryStage);
        TS.setMain(this);
        primaryStage.show();
    }
}
