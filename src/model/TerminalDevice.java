package model;

import java.util.Map;
import java.util.Random;

import static model.TerminalDevice.DeviceState.*;

public class TerminalDevice {

    public enum DeviceState {WORKING, BLOCKED,
        BUSY, FAILURE, DENIAL, GENERATOR}

    private DeviceState state = WORKING;
    private DeviceState my_buf;

    private Map<DeviceState, Integer> chances;

    DeviceState getState() {
        return state;
    }

    TerminalDevice(Map<DeviceState, Integer> probMap){

        chances = probMap;
    }

    void changeState(DeviceState st){
        if (st == WORKING){
            state = my_buf;
        }
        else{
            my_buf = state;
            state = st;
        }
    }

    void process(){
        Random rand = new Random();
        
        if ((state != BLOCKED) && ( state != DENIAL)){

            if (rand.nextInt(chances.get(GENERATOR)) == 0) {
                state = GENERATOR;
                return;
            }
            if (rand.nextInt(chances.get(DENIAL)) == 0) {
                state = DENIAL;
                return;
            }
            if (rand.nextInt(chances.get(FAILURE)) == 0) {
                state = FAILURE;
                return;
            }
            if (rand.nextInt(chances.get(BUSY)) == 0)
                state = BUSY;
        }
    }
}
