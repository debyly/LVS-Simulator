package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.LVS;

import java.io.IOException;
import java.util.ArrayList;


public class MainScreen {

    @FXML
    TextArea console;
    @FXML
    ToggleButton turnButton;
    @FXML
    Button execButton;
    @FXML
    Button changeLineButton;
    @FXML
    Text statePrompt;
    @FXML
    Pane lvsPane;
    @FXML
    Line lineA;
    @FXML
    Line lineB;

    private ArrayList<VisualDevice> visualDevices;
    private LVS lvs;
    private Stage initStage;

    public void setInitStage(Stage initStage) {
        this.initStage = initStage;
    }

    @FXML
    void initialize(){

        visualDevices = new ArrayList<>();

        changeLineButton.setDisable(true);
        execButton.setDisable(true);
        console.setMouseTransparent(true);
        turnButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            changeLineButton.setDisable(!newValue);
            execButton.setDisable(!newValue);
                lineA.setStroke(Paint.valueOf( newValue ? "#5bd983" : "#b1b1b1"));
                lineB.setStroke(Paint.valueOf("#b1b1b1"));
        });
    }

    public void drawLVS(){

        try {
            for (int i = 0; i < lvs.getClientsAmount(); i++){
                FXMLLoader loader = new FXMLLoader(getClass().getResource(i % 2 == 0 ? "DeviceUpper.fxml" : "DeviceLower.fxml"));
                Node elm = loader.load();
                visualDevices.add(loader.getController());
                visualDevices.get(i).setTerminalDevice(i, lvs.getClients().get(i));
                visualDevices.get(i).setOff();
                lvsPane.getChildren().add(elm);
                elm.setLayoutX(i%2 == 0 ? i*35+4 : i*35 + 10);
                elm.setLayoutY(i%2 == 0 ? 8 : 115);
            }

        } catch (IOException e){

            lvs = null;
            lvsPane.getChildren().clear();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Ошибка инициализации ЛВС");
            alert.setTitle("Error: внутренняя ошибка");
            alert.setContentText("Сообщение ошибки:\n" + e.getMessage());
            alert.initOwner(initStage);
            alert.showAndWait();
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

        for (VisualDevice visualDevice : visualDevices){
            if (turnButton.isSelected())
                visualDevice.setOn();
            else
                visualDevice.setOff();

        }
    }
    @FXML
    void execHandle(){



    }
    @FXML
    void changeLineHandle(){

        if (lvs.getLineCtrl().getLineState() == LVS.LineState.A_WORKING){


            lineB.setStroke(Paint.valueOf("#5bd983"));
            lineA.setStroke(Paint.valueOf("#b1b1b1"));

        }
        else
            if (lvs.getLineCtrl().getLineState() == LVS.LineState.B_WORKING){


                lineB.setStroke(Paint.valueOf("#b1b1b1"));
                lineA.setStroke(Paint.valueOf("#5bd983"));
            }
        }

    @FXML
    void testHandle(){

    }

    public void createLVS(int clientsAmount, int gen, int den, int fail, int busy){

        lvs = new LVS(true, clientsAmount, gen, den, fail, busy);
    }
}
