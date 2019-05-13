package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    Line tail;

    private Paint basePaint = Paint.valueOf("#b1b1b1");

    private TerminalDevice terminalDevice;

    public enum VisualState{ONLINE, OFFLINE, FAILURE, DENIAL, GENERATOR;
        private static VisualState[] vals = values();
        public VisualState next() {
            return vals[(this.ordinal()+1) % vals.length];
        }
        public VisualState previous(){
            return vals[(this.ordinal() + vals.length -1) % vals.length];
        }}

    private VisualState state = ONLINE;

    private static Map<VisualState, Paint> stateColor =
            new HashMap<VisualState, Paint>(){{

                put(ONLINE, Paint.valueOf("#5bd983"));
                put(OFFLINE, Paint.valueOf("#cc1800"));
                put(FAILURE, Paint.valueOf("#ffdf1f"));
                put(DENIAL, Paint.valueOf("#71725f"));
                put(GENERATOR, Paint.valueOf("#1f9dff"));
            }};

    private static Map<VisualState, String> stateAbbr = new HashMap<VisualState, String>(){{

        put(ONLINE, "вкл");
        put(OFFLINE, "вык");
        put(FAILURE, "сбо");
        put(DENIAL, "отк");
        put(GENERATOR, "ген");
    }};

    @FXML
    void initialize(){
        tdStateButton.setText("откл");
        tail.setStroke(Paint.valueOf("#b1b1b1"));
    }

    void setTerminalDevice(int number, TerminalDevice td){

        tdLabel.setText("ОУ №" + (number+1));
        terminalDevice = td;
    }

    @FXML
    void changeStateHandle(){

        state = state.next();
        stateIndicator.setFill(stateColor.get(state));
        tdStateButton.setText(stateAbbr.get(state));
    }

    void setOff(){
        tail.setStroke(basePaint);
        stateIndicator.setFill(Paint.valueOf("#ffffff"));
        tdStateButton.setText("откл");
        tdStateButton.setDisable(true);
        terminalDevice.changeState(DeviceState.BLOCKED);
    }

    void setOn(){
        tail.setStroke(stateColor.get(ONLINE));
        stateIndicator.setFill(stateColor.get(ONLINE));
        tdStateButton.setText("вкл");
        tdStateButton.setDisable(false);
        terminalDevice.restore();
    }

}
