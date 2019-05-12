package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import java.io.IOException;


public class MainScreen {

    public MainScreen(){

    }

    @FXML
    TextArea console;
    @FXML
    ToggleButton turnButton;
    @FXML
    Text statePrompt;

    @FXML
    void initialize(){

        console.setMouseTransparent(true);

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeviceUpper.fxml"));
            Node elm = loader.load();
        } catch (IOException e){

            e.printStackTrace();
        }
    }

    @FXML
    void turnHandle(){


        turnButton.setText(turnButton.isSelected() ? "ВЫКЛЮЧИТЬ" : "ВКЛЮЧИТЬ");

        if (turnButton.isSelected()){
            statePrompt.setText("Все ОУ работают");
        }
        else {
            statePrompt.setText("ЛВС Отключена");
        }
    }
    @FXML
    void execHandle(){

    }
    @FXML
    void changeLineHandle(){

    }
    @FXML
    void testHandle(){

    }


}
