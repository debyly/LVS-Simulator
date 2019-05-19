
import javafx.application.Application;
import javafx.stage.Stage;
import view.WindowManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        WindowManager manager = new WindowManager(primaryStage);
        manager.start();
    }

    public static void main(String[] args) { launch(args); }
}
