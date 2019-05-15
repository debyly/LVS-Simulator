package model;

import javafx.beans.property.SimpleObjectProperty;
import model.LVS.LineState;
import view.VisualDevice;

import java.util.Map;
import java.util.Random;

import static model.TerminalDevice.DeviceState.*;

public class TerminalDevice {

    public enum DeviceState {INITIAL, WORKING, BLOCKED, UNBLOCKING,
        BUSY, FAILURE, DENIAL, GENERATOR}

    private DeviceStateProperty state = new DeviceStateProperty(INITIAL);
    private DeviceState previousState = WORKING;

    public static class DeviceStateProperty extends SimpleObjectProperty<DeviceState>{
        public DeviceStateProperty(DeviceState state){
            super(state);
        }
    }
    public DeviceStateProperty getDeviceStateProperty(){
        return state;
    }
    DeviceState getState() { return state.get(); }
    DeviceState getPreviousState() { return previousState; }

    private LVS lvs;
    private Map<DeviceState, Integer> chances;

    TerminalDevice (Map<DeviceState, Integer> chances, LVS lvs){

        this.lvs = lvs;
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

    public void backup(){
        previousState = state.get();
    }

    public void systemSetState(DeviceState st){

        state.set(st);
    }

    void process() {

        if (state.get() == INITIAL)
            changeState(WORKING);

        Random rand = new Random();
        double randDouble = rand.nextDouble() % 1.0;

        if (state.get() != DENIAL && state.get() != BLOCKED)

            if (randDouble < 1.0 / chances.get(GENERATOR))
                changeState(GENERATOR);
            else if (randDouble < 1.0 / chances.get(DENIAL))
                changeState(DENIAL);
            else if (randDouble < 1.0 / chances.get(FAILURE))
                changeState(FAILURE);
            else if (randDouble < 1.0 / chances.get(BUSY))
                changeState(BUSY);
    }
}
