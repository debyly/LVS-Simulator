package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.LVS;

import java.io.IOException;
import java.util.ArrayList;

import static view.VisualDevice.*;
import static view.VisualDevice.VisualState.*;


public class MainScreen {

    @FXML
    TextArea console;
    @FXML
    ToggleButton turnButton;
    @FXML
    Button execButton;
    @FXML
    Button stopButton;
    @FXML
    Button profileButton;
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
    private WindowManager manager;
    private LVS.LineStateProperty lineStateProperty = new LVS.LineStateProperty(LVS.LineState.A_WORKING);

    private Thread dangerousThread = null;

    void setManager(WindowManager manager) {
        this.manager = manager;
    }

    private void addToConsole(String string){
        Platform.runLater(() -> {

            console.setText(console.getText() + "\n" + string);
            console.selectPositionCaret(console.getLength());
            console.deselect();
        });
    }

    private void cleanConsole(String prompt){

        console.setPromptText(prompt);
        console.clear();
    }

    @FXML
    void initialize(){

        visualDevices = new ArrayList<>();

        execButton.setDisable(true);
        stopButton.setDisable(true);
        console.setEditable(false);

        lineStateProperty.addListener((o, old, value) -> {
            if (value == LVS.LineState.A_WORKING) {

                addToConsole("*Линия А активна*");

                lineA.setStroke(stateColor.get(ONLINE));
                lineB.setStroke(baseColor);
            }
            if (value == LVS.LineState.B_WORKING) {

                addToConsole("*Запущена линия B*");

                lineA.setStroke(baseColor);
                lineB.setStroke(stateColor.get(ONLINE));
            }
            if (value == LVS.LineState.A_GENERATION) {

                addToConsole("*Обнаружена генерация на линии А*");

                lineA.setStroke(stateColor.get(GENERATOR));
                lineB.setStroke(baseColor);
            }
        });

        try {
            for (int i = 0; i < 18; i++){
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        i % 2 == 0 ? "DeviceUpper.fxml" : "DeviceLower.fxml"));

                Node elm = loader.load();

                visualDevices.add(loader.getController());
                lvsPane.getChildren().add(elm);
                elm.setLayoutX(i*34 + 10);
                elm.setLayoutY(i % 2 == 0 ? 10 : 106);
            }

        } catch (IOException e){

            lvsPane.getChildren().clear();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Ошибка инициализации ЛВС");
            alert.setTitle("Error: внутренняя ошибка");
            alert.setContentText("Сообщение ошибки:\n" + e.getCause() + "\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void turnHandle(){

        execButton.setDisable(!turnButton.isSelected());

        turnButton.setText(turnButton.isSelected() ? "ВЫКЛЮЧИТЬ СЕТЬ" : "ВКЛЮЧИТЬ СЕТЬ");

        statePrompt.setText("ЛВС "
                + (turnButton.isSelected() ? "включена" : "отключена"));

        for (VisualDevice visualDevice : visualDevices) {
            visualDevice.transparentButton(!turnButton.isSelected());
            visualDevice.disableButton(!turnButton.isSelected());
        }

        if (turnButton.isSelected()){

            lvs = new LVS(true, 250, 18,20000,5000,2000,2000);
            lineStateProperty.bind(lvs.getLineStateProperty());

            for (int vdi = 0; vdi < visualDevices.size(); vdi++) {

                visualDevices.get(vdi).setTerminalDevice(vdi, lvs.getClients().get(vdi));
                visualDevices.get(vdi).setConsole(console);
                visualDevices.get(vdi).powerSwitch();
            }
        } else {

            lineStateProperty.unbind();
            for (VisualDevice visualDevice : visualDevices) {
                visualDevice.powerSwitch();
                visualDevice.dropTerminalDevice();
            }
            lvs = null;
        }

        lineA.setStroke(turnButton.isSelected() ? stateColor.get(ONLINE) : baseColor);
        lineB.setStroke(baseColor);
        cleanConsole(turnButton.isSelected() ? "*СИСТЕМА ЛВС ВКЛЮЧЕНА*" : "*ОТКЛЮЧЕНО*");
    }

    @FXML
    void execHandle(){

        addToConsole("*Запуск контроллера сети*\n");

        turnUI();

        Runnable r = () -> {
            try {

                lvs.start(new ArrayList<Double>(){{
                    for (int i = 0; i < 5; i++) add(.0);
                }});

                Platform.runLater(this::turnUI);

            } catch (InterruptedException e) {

                Platform.runLater(() -> {
                    turnUI();
                    execButton.setDisable(true);
                    for (VisualDevice v : visualDevices)
                        v.transparentButton(true);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Работа программы прервана");
                    alert.setHeaderText("Остановка!");
                    alert.setContentText("Процесс работы контроллера сети прерван. " +
                            "Продолжение невозможно. " +
                            "\n\nПерезапустите сеть");
                    alert.show();
                });
               }
        };

        dangerousThread = new Thread(r);
        dangerousThread.start();
    }

    @FXML
    void stopHandle(){

        if (dangerousThread == null) return;

        if (dangerousThread.isAlive()) {
            dangerousThread.interrupt();
        }

        dangerousThread = null;
    }

    private void turnUI() {

        turnButton.setDisable(!turnButton.isDisabled());
        execButton.setDisable(!execButton.isDisabled());
        stopButton.setDisable(!stopButton.isDisabled());
        profileButton.setDisable(!profileButton.isDisabled());

        for (VisualDevice v : visualDevices)
            v.transparentButton(execButton.isDisabled());
    }

    @FXML
    void profileHandle() { manager.testWindow(); }

}
