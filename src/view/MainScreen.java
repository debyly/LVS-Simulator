package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.LVS;

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

        consoletext.setText("");
        textflow.getChildren().remove(0);

        LVS lvs = new LVS();
        int[][] output = lvs.working_20000();

        for (int i = 0; i < 20; i++) {

            int time = output[i][5] - output[i][4];
            String str = "\n\nКоличество ошибок:\n"
                    + "Генерация: " + output[i][0]
                    + "\r\nОтказ: " + output[i][1]
                    + "\r\nСбой: " + output[i][2]
                    + "\r\nАбонент занят: " + output[i][3]
                    + "\nTime for 1000: " + time
                    + "\nExpected time: " + (time / 990) + "\r\n";
            consoletext.setText(consoletext.getText() + str);
        }

        consoletext.setText("TOTAL TIME: " + lvs.getTimeCtrl().getTime() + consoletext.getText());

        textflow.getChildren().add(consoletext);
    }

    @FXML
    private void handleClear(){

        textflow.getChildren().remove(0);
        textflow.getChildren().add(new Text("Console Cleared"));
    }

}
