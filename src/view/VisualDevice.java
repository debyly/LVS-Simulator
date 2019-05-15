package view;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.TerminalDevice;

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
    Line tail;

    private Paint basePaint = Paint.valueOf("#b1b1b1");

    private TerminalDevice terminalDevice;

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
            = new TerminalDevice.DeviceStateProperty(TerminalDevice.DeviceState.INITIAL);

    TerminalDevice.DeviceStateProperty getVirtualDeviceState(){
        return virtualDeviceState;
    }

    private static Map<VisualState, Paint> stateColor =
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
    }

    void setTerminalDevice(int number, TerminalDevice td){

        tdLabel.setText("ОУ №" + (number+1));
        terminalDevice = td;
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

    @FXML
    void changeStateHandle(){

        state.set(state.next());

        switch (state.get()){

            case ONLINE:
                terminalDevice.systemSetState(TerminalDevice.DeviceState.WORKING);
                break;
            case BLOCKED:
                terminalDevice.systemSetState(TerminalDevice.DeviceState.BLOCKED);
                break;
            case GENERATOR:
                terminalDevice.systemSetState(TerminalDevice.DeviceState.GENERATOR);
                break;
            case FAILURE:
                terminalDevice.systemSetState(TerminalDevice.DeviceState.FAILURE);
                break;
            case DENIAL:
                terminalDevice.systemSetState(TerminalDevice.DeviceState.DENIAL);
                break;
        }
    }

    void setOff(){
        terminalDevice.getDeviceStateProperty().set(TerminalDevice.DeviceState.INITIAL);
        stateIndicator.setFill(basePaint);
        tail.setStroke(basePaint);
        tdStateButton.setDisable(true);
        tdStateButton.setText("откл");
    }

    void setOn(){
        terminalDevice.restore();
        stateIndicator.setFill(stateColor.get(ONLINE));
        tdStateButton.setText(stateAbbr.get(ONLINE));
        tail.setStroke(stateColor.get(ONLINE));
        tdStateButton.setDisable(false);
    }

    void disableButton(boolean disable){

        tdStateButton.setDisable(disable);
    }
}
