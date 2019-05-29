package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import model.LVS;
import model.LineState;

import java.io.IOException;
import java.util.ArrayList;

import static view.VisualDevice.*;
import static view.VisualState.*;

public class MainScreen {

    @FXML
    TextArea console;
    @FXML
    TextField amountField;
    @FXML
    ToggleButton turnButton;
    @FXML
    Button execButton;
    @FXML
    Button stopButton;
    @FXML
    Button cleanButton;
    @FXML
    Button profileButton;
    @FXML
    Pane lvsPane;
    @FXML
    Line lineA;
    @FXML
    Line lineB;

    private int devicesAmount = 32;
    private int sleepAmount = 200;

    private Thread modelThread = null;
    private ArrayList<VisualDevice> visualDevices;
    private ArrayList<Node> elements;
    private LVS lvs;
    private WindowManager manager;
    private final LVS.LineStateProperty lineStateProperty
            = new LVS.LineStateProperty();

    void setManager(WindowManager manager) {
        this.manager = manager;
    }

    private void addToConsole(String string){
        Platform.runLater(() -> {

            console.setText(console.getText() + string + "\n" );
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
        elements = new ArrayList<>();

        execButton.setDisable(true);
        stopButton.setDisable(true);
        cleanButton.setDisable(true);
        console.setEditable(false);

        lineStateProperty.addListener((o, old, value) -> {

            if (value == LineState.A_WORKING) {

                addToConsole("ЛВС: * Линия А - активна *");

                lineA.setStroke(stateColor.get(ONLINE));
                lineB.setStroke(baseColor);
            }
            if (value == LineState.B_WORKING) {

                addToConsole("ЛВС: * Линия B - активна *");

                lineA.setStroke(baseColor);
                lineB.setStroke(stateColor.get(ONLINE));
            }
            if (value == LineState.A_GENERATION) {

                addToConsole("ЛВС: * Линия А - обнаружена генерация *");

                lineA.setStroke(stateColor.get(GENERATOR));
                lineB.setStroke(baseColor);
            }
        });


    }

    @FXML
    void turnHandle(){

        try {


            if (turnButton.isSelected()) {

                if (amountField.getText().isEmpty())
                    throw new IOException("Введите значение количества ОУ!");

                devicesAmount = Integer.parseInt(amountField.getText());

                if (devicesAmount < 1 || devicesAmount > 32)
                    throw new NumberFormatException("Неверное количество ОУ! Должно быть число в пределах от 1 до 32!");

                for (int i = 0; i < devicesAmount; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            i % 2 == 0 ? "DeviceUpper.fxml" : "DeviceLower.fxml"));

                    Node elm = loader.load();
                    elements.add(elm);
                    visualDevices.add(loader.getController());
                    lvsPane.getChildren().add(elm);
                    elm.setLayoutX(5 + 680 * i / ((devicesAmount > 1 ? devicesAmount : 2) - 1));
                    elm.setLayoutY(i % 2 == 0 ? 9 : 105);
                }

                for (VisualDevice visualDevice : visualDevices) {
                    visualDevice.transparentButton(!turnButton.isSelected());
                    visualDevice.disableButton(!turnButton.isSelected());
                    visualDevice.setConsole(console);
                }

                lvs = LVS.realLVS(sleepAmount, devicesAmount);

                lineStateProperty.bindBidirectional(lvs.lineStateProperty());

                for (int vdi = 0; vdi < devicesAmount; vdi++) {

                    visualDevices.get(vdi).setOn(vdi, lvs.getDevices().get(vdi));
                }

            } else {

                lineStateProperty.unbindBidirectional(lvs.lineStateProperty());

                for (int vdi = 0; vdi < devicesAmount; vdi++) {
                    visualDevices.get(vdi).setOff(lvs.getDevices().get(vdi));
                }

                visualDevices.clear();

                lvsPane.getChildren().removeAll(elements);

                lvs = null;
            }

            execButton.setDisable(!turnButton.isSelected());
            cleanButton.setDisable(!turnButton.isSelected());
            turnButton.setText(turnButton.isSelected() ? "ВЫКЛЮЧИТЬ СЕТЬ" : "ВКЛЮЧИТЬ СЕТЬ");

            lineA.setStroke(turnButton.isSelected() ? stateColor.get(ONLINE) : baseColor);
            lineB.setStroke(baseColor);
            amountField.setDisable(turnButton.isSelected());
            cleanConsole(turnButton.isSelected() ? "*СИСТЕМА ЛВС ВКЛЮЧЕНА*" : "*ОТКЛЮЧЕНО*");

        } catch(IOException | NumberFormatException e) {

            lvsPane.getChildren().removeAll(elements);
            visualDevices.clear();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Ошибка инициализации ЛВС");
            alert.setTitle("Error: внутренняя ошибка");
            alert.setContentText("Сообщение ошибки:\n" + e.getMessage());
            alert.showAndWait();
            turnButton.setSelected(false);
        }
    }

    @FXML
    void execHandle(){

        addToConsole("Контроллер сети: * Запуск *");

        turnUI();

        Runnable r = () -> {
            try {

                lvs.start();
                Platform.runLater(this::turnUI);
                Platform.runLater(()-> addToConsole("Контроллер сети: * Завершение работы *"));

            } catch (Exception e) {

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

        modelThread = new Thread(r);
        modelThread.start();
    }

    @FXML
    void stopHandle(){

        if (modelThread == null) return;

        if (modelThread.isAlive()) {
            modelThread.interrupt();
            addToConsole("Контроллер сети: * Работа прервана! *");
        }

        modelThread = null;
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
    void cleanHandle(){
        cleanConsole("*СИСТЕМА ЛВС ВКЛЮЧЕНА*");
    }

    @FXML
    void profileHandle() {
        try {
            manager.testWindow();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Внутренняя ошибка");
            alert.setContentText("Ошибка загрузки\n"
                    + e.getCause() + "\n" + e.getMessage());
            alert.show();
        }
    }
}