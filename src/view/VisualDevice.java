package view;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.TerminalDevice;
import model.TerminalDevice.DeviceState;

import java.util.HashMap;
import java.util.Map;

import static view.VisualDevice.VisualState.*;
public class VisualDevice {

    @FXML
    VBox pane;
    @FXML
    Label tdLabel;
    @FXML
    Button tdStateButton;
    @FXML
    Rectangle stateIndicator;
    @FXML
    Rectangle display;
    @FXML
    Line tail;

    private TextArea console = new TextArea();

    static Paint baseColor = Paint.valueOf("#b1b1b1");

    private TerminalDevice terminalDevice;
    private int deviceNumber;

    public enum VisualState{ONLINE, BLOCKED, GENERATOR, FAILURE, DENIAL}

    public class VisualStateProperty extends SimpleObjectProperty<VisualState> {

        private VisualState[] vals = values();

        VisualStateProperty(VisualState state) {
            super(state);
        }
        VisualState next() {
            return vals[(this.get().ordinal()+1) % vals.length];
        }
    }

    private VisualStateProperty state = new VisualStateProperty(ONLINE);

    private TerminalDevice.DeviceStateProperty virtualDeviceState
            = new TerminalDevice.DeviceStateProperty(DeviceState.INITIAL);

    private TerminalDevice.ActiveProperty virtualActive = new TerminalDevice.ActiveProperty(false);

    static Map<VisualState, Paint> stateColor =
            new HashMap<VisualState, Paint>(){{

                put(ONLINE, Paint.valueOf("#5bd983"));
                put(BLOCKED, Paint.valueOf("#cc1800"));
                put(FAILURE, Paint.valueOf("#ffdf1f"));
                put(DENIAL, Paint.valueOf("#71725f"));
                put(GENERATOR, Paint.valueOf("#1f9dff"));
            }};

    private static Map<VisualState, String> stateAbbr = new HashMap<VisualState, String>(){{

        put(ONLINE, "вкл");
        put(BLOCKED, "блк");
        put(FAILURE, "сбо");
        put(DENIAL, "отк");
        put(GENERATOR, "ген");
    }};

    @FXML
    void initialize(){
        tdStateButton.setText("откл");
        tail.setStroke(Paint.valueOf("#b1b1b1"));

        state.addListener((observable, oldValue, newValue) ->
        {
            stateIndicator.setFill(stateColor.get(newValue));
            tdStateButton.setText(stateAbbr.get(newValue));
            tail.setStroke(stateColor.get(newValue));
        });

        virtualActive.addListener((observable, oldValue, newValue) -> setActive(newValue));

        virtualDeviceState.addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> {
                    switch (newValue) {
                        case WORKING:
                            state.set(ONLINE);
                            break;
                        case BLOCKED:
                            state.set(BLOCKED);
                            break;
                        case DENIAL:
                            state.set(DENIAL);
                            break;
                        case BUSY:
                        case FAILURE:
                            state.set(FAILURE);
                            break;
                        case GENERATOR:
                            state.set(GENERATOR);
                            break;
                    }
                }));
    }

    void setConsole(TextArea c){
        console = c;
    }

    void setTerminalDevice(int number, TerminalDevice td){

        deviceNumber = number+1;
        tdLabel.setText("ОУ №" + deviceNumber);
        terminalDevice = td;

        virtualDeviceState.bind(td.deviceStateProperty());
        virtualActive.bind(td.activeProperty());
        virtualActive.getLastMessage().bind(td.activeProperty().getLastMessage());
    }

    void dropTerminalDevice(){

        terminalDevice = null;
        tdLabel.setText("----");

        virtualDeviceState.unbind();
        virtualActive.unbind();
        virtualActive.getLastMessage().unbind();
        virtualDeviceState.set(DeviceState.INITIAL);
        virtualActive.set(false);
        virtualActive.setLastMessage("");
    }

    @FXML
    void changeStateHandle(){

        state.set(state.next());

        switch (state.get()){

            case ONLINE:
                terminalDevice.systemSetState(DeviceState.WORKING);
                break;
            case BLOCKED:
                terminalDevice.systemSetState(DeviceState.BLOCKED);
                break;
            case GENERATOR:
                terminalDevice.systemSetState(DeviceState.GENERATOR);
                break;
            case FAILURE:
                terminalDevice.systemSetState(DeviceState.FAILURE);
                break;
            case DENIAL:
                terminalDevice.systemSetState(DeviceState.DENIAL);
                break;
        }
    }

    private void setActive(boolean active){
                display.setFill(Paint.valueOf(active? "#C4FFAE": "#D4DEFF"));
                if (power && !virtualActive.getLastMessage().getValue().isEmpty())
                    addToConsole("ОУ №" + deviceNumber + ": " + virtualActive.getLastMessage().getValue());
    }

    private boolean power = false;

    void powerSwitch(){
        if (power) setOff(); else setOn();
        power = !power;
    }

    private void setOff(){
        terminalDevice.deviceStateProperty().set(DeviceState.INITIAL);
        stateIndicator.setFill(baseColor);
        tail.setStroke(baseColor);
        display.setFill(Paint.valueOf("#D4DEFF"));
        tdStateButton.setDisable(true);
        tdStateButton.setText("откл");
    }

    private void setOn(){
        terminalDevice.restore();
        stateIndicator.setFill(stateColor.get(ONLINE));
        tdStateButton.setText(stateAbbr.get(ONLINE));
        tail.setStroke(stateColor.get(ONLINE));
        tdStateButton.setDisable(false);
    }

    void disableButton(boolean disable){

        tdStateButton.setDisable(disable);
    }

    void transparentButton(boolean transparent){
        tdStateButton.setMouseTransparent(transparent);
    }

    private void addToConsole(String string){
      Platform.runLater(() ->{

            console.setText(console.getText() + "\n" + string);
            console.selectPositionCaret(console.getLength());
            console.deselect();
        });
    }
}
