import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.TerminalDevice;
import view.MainScreen;

import java.util.HashMap;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String mainfxml = "view/MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = loader.load();

        primaryStage.setTitle("Симулятор ЛВС 2000");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        MainScreen MS = loader.getController();
        MS.setInitStage(primaryStage);
        MS.createLVS(18,20000,5000,2000,2000);
        MS.drawLVS();
        primaryStage.show();
    }
}
