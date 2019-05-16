package model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.LVS.LineState;
import java.util.Map;

import static model.TerminalDevice.DeviceState.*;

public class TerminalDevice {

    public enum DeviceState {INITIAL, WORKING, BLOCKED, UNBLOCKING,
        BUSY, FAILURE, DENIAL, GENERATOR}

    private DeviceStateProperty state = new DeviceStateProperty(INITIAL);
    private ActiveProperty active = new ActiveProperty(false);

    private DeviceState previousState = WORKING;

    public static class ActiveProperty extends SimpleObjectProperty<Boolean>{
        private StringProperty lastMessage = new SimpleStringProperty();
        public ActiveProperty(boolean active){ super(active);}

        void setLastMessage(String message){
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
    DeviceState getState() { return state.get(); }

    public ActiveProperty activeProperty() { return active; }

    public void startMessaging(String message){
        active.set(true);
        active.setLastMessage(message);
    }

    public void endMessaging(String message){
        active.set(false);
        active.setLastMessage(message);
    }

    private Map<DeviceState, Integer> chances;

    TerminalDevice (Map<DeviceState, Integer> chances, LVS lvs){

        this.chances = chances;
        state.addListener((observable, oldValue, newValue) -> {

            if (newValue == GENERATOR)
                lvs.setLineState(LineState.A_GENERATION);
            if (oldValue == GENERATOR) {
                for (TerminalDevice device : lvs.getClients()) {
                    if (device.getState() == GENERATOR)
                        return;
                }
                if (lvs.getLineStateProperty().get() == LineState.A_GENERATION)
                    lvs.setLineState(LineState.A_WORKING);
            }
        });
    }

    void changeState(DeviceState st){
        if (st == UNBLOCKING && state.get() == BLOCKED){
            state.set(previousState);
        }

        else if (state.get() != BLOCKED && state.get() != DENIAL){
            previousState = state.get();
            state.set(st);
        }
    }

    public void restore(){
        state.set(WORKING);
        previousState = WORKING;
    }

    public void systemSetState(DeviceState st){
        state.set(st);
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
