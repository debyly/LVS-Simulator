import java.util.HashMap;

public class OU {

    HashMap<String,String> states;
    String state;
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

}
