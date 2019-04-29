import java.util.HashMap;

public class LVS {
    HashMap<Integer,OU> clients;
    Controller ctrl;
    String line;
    String status;


    LVS()
    {
        ctrl = new Controller();
        clients = new HashMap<>();

        for (int i = 1; i <= 18; i++) clients.put(i, new OU());

        line = "A";
        status = "working";
    }



    public void working_20000(HashMap<Integer, String >[] Faults){
        for (int i = 0; i< 20; i++){
            working_1000(Faults[i]);
        }
    }

    public  void working_1000(HashMap<Integer,String > Fault){
        for (int i = 1; i< 19; i++){
            if (Fault.get(i) != null){
                clients.get(i).chState(Fault.get(i));
            }
        }
        for (int i = 0; i < 55; i++){
            working_18();
        }
    }

    public void working_18(){
        if (status.equals("generation")){
            ctrl.findGenerator(clients);
        }
        for (int i = 1; i < 19; i++){
            if (clients.get(i).state.equals("failure")){
                ctrl.Failure();
                clients.get(i).chState("working");
            }
            if (clients.get(i).state.equals("busy")){
                ctrl.Busy();
                clients.get(i).chState("working");
            }
            if (clients.get(i).state.equals("denial")){
                ctrl.Denial();
            }
            ctrl.NormalWork();
        }
    }
}
