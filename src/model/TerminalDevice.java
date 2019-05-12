package model;

import java.util.Random;

import static model.TerminalDevice.State.*;

class TerminalDevice {

    public enum State{WORKING, BLOCKED,
        BUSY, FAILURE, DENIAL, GENERATOR}

    State state = WORKING;

    void chState(State st){
        if (st != WORKING){
            state = st;
        }
    }

    void restore(){
        state = WORKING;
    }

    void process(){
        Random rand = new Random();
        
        if ((state != BLOCKED) && ( state != DENIAL)){
            
            int gen = rand.nextInt() % 20000;

            if (gen == 0) {
                state = GENERATOR;
                return;
            }
            int den = rand.nextInt() % 5000;
            if (den == 0) {
                state = DENIAL;
                return;
            }
            int fail = rand.nextInt() % 2000;
            if (fail == 0) {
                state = FAILURE;
                return;
            }
            int busy = rand.nextInt() % 2000;
            if (busy == 0) {
                state = BUSY;
            }
        }
    }
}
