package model;

import view.VisualDevice;

import java.util.Map;
import java.util.Random;

import static model.TerminalDevice.DeviceState.*;

public class TerminalDevice {

    public enum DeviceState {WORKING, BLOCKED, UNBLOCKING,
        BUSY, FAILURE, DENIAL, GENERATOR;
        private static DeviceState[] vals = values();
        public DeviceState next() {
            return vals[(this.ordinal()+1) % vals.length];
        }
        public DeviceState previous(){
            return vals[(this.ordinal() + vals.length -1) % vals.length];
        }}

    private DeviceState state = WORKING;
    private DeviceState previousState = WORKING;

    private Map<DeviceState, Integer> chances;

    public DeviceState getState() {
        return state;
    }

    public DeviceState getPreviousState() {
        return previousState;
    }

    TerminalDevice (Map<DeviceState, Integer> chances){

        this.chances = chances;
    }

    public void changeState(DeviceState st){
        if (st == UNBLOCKING && state == BLOCKED){
            state = previousState;
        }
        else{
            previousState = state;
            state = st;
        }
    }

    public void restore(){

        state = WORKING;
        previousState = WORKING;
    }

    void setRandomState() {
        Random rand = new Random();

        previousState = state;

        if ((state != BLOCKED) && ( state != DENIAL)){

            if (rand.nextInt(chances.get(GENERATOR)) == 1) {
                changeState(GENERATOR);
                return;
            }
            if (rand.nextInt(chances.get(DENIAL)) == 1) {
                changeState(DENIAL);
                return;
            }
            if (rand.nextInt(chances.get(FAILURE)) == 1) {
                changeState(FAILURE);
                return;
            }
            if (rand.nextInt(chances.get(BUSY)) == 1)
                changeState(BUSY);
        }
    }
}
