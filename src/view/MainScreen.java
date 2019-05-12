package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.LVS;

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

        LVS lvs = new LVS(18, chances);
        int[][] output = lvs.simulateX(20,55);

        consoletext.setText("");
        textflow.getChildren().remove(0);
        for (int i = 0; i < 20; i++) {

            String str = "\n\n--Количество ошибок--\n"
                    + "Генерация: " + output[i][0]
                    + "\r\nОтказ: " + output[i][1]
                    + "\r\nСбой: " + output[i][2]
                    + "\r\nАбонент занят: " + output[i][3]
                    + "\nВремени потрачено: " + (output[i][6] - output[i][5])
                    + " сек\nОжидалось потратить: " + ((output[i][6] - output[i][5]) / 990)
                    + " сек\nПередано сообщений: " + output[i][4] + " штук\r\n";
            consoletext.setText(consoletext.getText() + str);
        }

        consoletext.setText("Общее время работы программы: \n" + lvs.getLineCtrl().getTime() + "секунд" + consoletext.getText());

        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleClear(){

        textflow.getChildren().remove(0);
        textflow.getChildren().add(new Text("Console Cleared"));
    }

}
