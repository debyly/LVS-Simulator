import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.LVS;

import java.io.IOException;
import java.util.List;

public class TestScreen {

    @FXML
    Button btnstart;
    @FXML
    Button btnclear;
    @FXML
    TextFlow textflow;

    private Stage initStage;
    private Text consoletext;
    private Main main;

    public TestScreen(){


    }

    public void setInitStage(Stage initStage){

        this.initStage = initStage;
    }

    @FXML
    private void initialize(){

        consoletext = new Text("Welcome");
        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void toConsole(Pair<List<List<Double>>,Integer> output){
        consoletext.setText("");
        textflow.getChildren().clear();
        for (int i = 0; i < 20; i++) {

            String str = "\n\n--Количество ошибок--\n"
                    + "Генерация: " + output.getKey().get(i).get(4)
                    + "\r\nОтказ: " + output.getKey().get(i).get(2)
                    + "\r\nСбой: " + output.getKey().get(i).get(1)
                    + "\r\nАбонент занят: " + output.getKey().get(i).get(3)
                    + "\nВремени потрачено: " + (output.getKey().get(i).get(5))
                    + " сек\nОжидалось потратить: " + (output.getKey().get(i).get(6))
                    + " сек\n\nПередано сообщений: " + output.getKey().get(i).get(0) + " штук\r\n";
            consoletext.setText(consoletext.getText() + str);
        }
        consoletext.setText("Общее время работы программы: \n" + output.getValue() + " секунд" + consoletext.getText());
        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleStart(){

        // arguments:
        // clientsAmount, messages, groups, genProb, denProb, failProb, busyProb
        int[] args = new int[]{18,20000,20,20000,5000,2000,2000};

        try {

            Reporter reporter = new Reporter();
            reporter.report(args, initStage);
            toConsole(reporter.getOutput());

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear(){

        textflow.getChildren().clear();
        textflow.getChildren().add(new Text("Console Cleared"));
    }

    @FXML
    private void profileHandle() throws IOException {

        main.mainWindow(initStage);
    }

    public void setMain(Main main){
        this.main = main;
    }

}
