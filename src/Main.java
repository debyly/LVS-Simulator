import javafx.application.Application;
import javafx.stage.Stage;
import view.WindowManager;

public class Main extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        WindowManager manager = new WindowManager(primaryStage);
        manager.start();
    }

}
