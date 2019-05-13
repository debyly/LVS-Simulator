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

    private LVS lvs;

    private Text consoletext;

    public TestScreen(){


    }

    public void setInitStage(Stage initStage){

        this.initStage = initStage;
    }

    public void createLVS(int clientsAmount, int gen, int den, int fail, int busy){

        lvs = new LVS(true, clientsAmount, gen, den, fail, busy);
    }

    @FXML
    private void initialize(){

        consoletext = new Text("Welcome");
        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleStart(){

        int clientsAmount = 18;
        int genProb = 20000;
        int denProb = 5000;
        int failProb = 2000;
        int busyProb = 2000;
        int multiplier = 20;
        int sessions = 55;


        Pair<List<List<Double>>,Integer> output;
        try {

            Reporter reporter = new Reporter();
            reporter.report(clientsAmount, genProb, denProb, failProb, busyProb, multiplier, sessions, initStage);
            output = reporter.getOutput();

            consoletext.setText("");
            textflow.getChildren().remove(0);
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

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear(){

        textflow.getChildren().remove(0);
        textflow.getChildren().add(new Text("Console Cleared"));
    }

}
