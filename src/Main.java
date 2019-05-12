import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.MainScreen;
public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String mainfxml = "view/MainScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = loader.load();
        MainScreen MSCtrl = loader.getController();
        primaryStage.setTitle("Симулятор ЛВС 2000");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
