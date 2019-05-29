package model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Map;

import static model.DeviceState.*;

public class TerminalDevice {


    private final DeviceStateProperty state = new DeviceStateProperty(INITIAL);
    private final ActiveProperty active = new ActiveProperty(false);

    private DeviceState previousState = WORKING;

    public static class ActiveProperty extends SimpleObjectProperty<Boolean>{
        private final StringProperty lastMessage = new SimpleStringProperty();
        public ActiveProperty(boolean active){ super(active);}

        public void setLastMessage(String message){
            lastMessage.setValue(message);
        }
        public StringProperty getLastMessage(){
            return lastMessage;
        }
    }

    public static class DeviceStateProperty extends SimpleObjectProperty<DeviceState>{
        public DeviceStateProperty(DeviceState state){
            super(state);
        }
    }
    public DeviceStateProperty deviceStateProperty(){
        return state;
    }

    public DeviceState getState() { return state.get(); }

    public ActiveProperty activeProperty() { return active; }

    void startMessaging(String message){
        active.setLastMessage(message);
        active.set(true);
    }

    void endMessaging(String message){

        active.setLastMessage(message);
        active.set(false);
    }

    private final Map <DeviceState, Double> chances;

    TerminalDevice (Map<DeviceState, Double> chances, LVS lvs){

        this.chances = chances;
        state.addListener((observable, oldValue, newValue) -> {

            if (newValue == GENERATOR && lvs.getLineState() == LineState.A_WORKING) {
                lvs.setLineState(LineState.A_GENERATION);
            }
            if (oldValue == GENERATOR) {
                for (TerminalDevice device : lvs.getDevices()) {
                    if (device.getState() == GENERATOR)
                        return;
                }
                if (lvs.lineStateProperty().get() == LineState.A_GENERATION)
                    lvs.setLineState(LineState.A_WORKING);
            }
        });
    }

    public void changeState(DeviceState st){
        if (st == UNBLOCKING && state.get() == BLOCKED){
            state.set(previousState);
        }

        else if (state.get() != BLOCKED && state.get() != DENIAL){
            previousState = state.get();
            state.set(st);
        }
    }

    DeviceState process() {

        if (state.get() == INITIAL) changeState(WORKING);
        previousState = state.get();

        DeviceState randomState = MyRandom.getRandomState(
                chances.get(GENERATOR),
                chances.get(DENIAL),
                chances.get(FAILURE),
                chances.get(BUSY)
        );

        changeState(randomState);
        return randomState;
    }
}