import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import view.TestScreen;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String mainfxml = "view/TestScreen.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(mainfxml));
        Parent root = loader.load();
        //TestScreen TSController = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
