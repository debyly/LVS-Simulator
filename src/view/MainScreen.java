package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import model.Tester;

import java.util.HashMap;
import java.util.Map;

import static model.TerminalDevice.*;

public class MainScreen {

    @FXML
    Button btnstart;

    @FXML
    Button btnclear;

    @FXML
    TextFlow textflow;

    private Text consoletext;

    public MainScreen(){


    }

    @FXML
    private void initialize(){

        consoletext = new Text("Welcome");
        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleStart(){

        Map<DeviceState, Integer> chances = new HashMap<DeviceState, Integer>(){{

            put(DeviceState.GENERATOR, 20000);
            put(DeviceState.DENIAL, 5000);
            put(DeviceState.FAILURE, 2000);
            put(DeviceState.BUSY, 2000);
        }};

        int clientsAmount = 18;
        int multiplier = 20;
        int sessions = 55;

        Pair<int[][],Integer> output
                = Tester.simulateX(clientsAmount, chances, multiplier, sessions);

        consoletext.setText("");
        textflow.getChildren().remove(0);
        for (int i = 0; i < 20; i++) {

            String str = "\n\n--Количество ошибок--\n"
                    + "Генерация: " + output.getKey()[i][0]
                    + "\r\nОтказ: " + output.getKey()[i][1]
                    + "\r\nСбой: " + output.getKey()[i][2]
                    + "\r\nАбонент занят: " + output.getKey()[i][3]
                    + "\nВремени потрачено: " + (output.getKey()[i][6] - output.getKey()[i][5])
                    + " сек\nОжидалось потратить: " + (output.getKey()[i][7])
                    + " сек\n\nПередано сообщений: " + output.getKey()[i][4] + " штук\r\n";
            consoletext.setText(consoletext.getText() + str);
        }

        consoletext.setText("Общее время работы программы: \n" + output.getValue() + "секунд" + consoletext.getText());

        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleClear(){

        textflow.getChildren().remove(0);
        textflow.getChildren().add(new Text("Console Cleared"));
    }

}
