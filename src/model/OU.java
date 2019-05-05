package model;

import java.util.HashMap;
import java.util.Random;

public class OU {

    HashMap<String,String> states;
    public String state;
    String my_buf;

    OU()
    {
        states = new HashMap<>();
        states.put("working","working");
        states.put("not_working","not_working");
        states.put("blocked", "blocked");
        states.put("busy", "busy");
        states.put("failure", "failure");
        states.put("denial", "denial");
        states.put("generator", "generator");

        state = states.get("working");
        my_buf = "";
    }

    public String lookStatus(){
        return  state;
    }

    public void chState(String st){
        if (st.equals("working")){
            state = my_buf;
        }
        else{
            my_buf = state;
            state = states.get(st);
        }
    }

    public void Fault(){
        Random rand = new Random();
        if (!((state.equals("blocked")) || ( state.equals("denial")))){
            int gen = rand.nextInt() % 20000;
            if (gen == 0) {
                state = "generator";
                return;
            }
            int den = rand.nextInt() % 5000;
            if (den == 0) {
                state = "denial";
                return;
            }
            int fail = rand.nextInt() % 2000;
            if (fail == 0) {
                state = "failure";
                return;
            }
            int busy = rand.nextInt() % 2000;
            if (busy == 0) {
                state = "busy";
                return;
            }
        }
    }

}
