import java.util.HashMap;

public class LVS {
    public HashMap<Integer,OU> clients;
    Controller ctrl;
    String line;
    String status;


    LVS()
    {
        ctrl = new Controller();
        clients = new HashMap<>();

        for (int i = 0; i < 18; i++) clients.put(i, new OU());

        line = "A";
        status = "working";
    }



    public void working_20000(Flt[][] Faults){
        for (int i = 0; i< 20; i++){
            working_1000(Faults[i]);
            System.out.println("done");
        }
        System.out.print("total time: ");
        System.out.println(ctrl.getTime());
    }

    public  void working_1000(Flt[] Fault){
        int time = ctrl.getTime();
        for (int i = 0; i< 18; i++){
            Flt f = Fault[i];
            if (f.state == 1){
                clients.get(i).chState(f.fault);
                if (f.fault.equals("generator")) line = "generation";
            }
        }
        for (int i = 0; i < 55; i++){
            working_18();
        }
        time = ctrl.getTime() - time;
        System.out.print("Time for 1000: ");
        System.out.println(time);
    }

    public void working_18(){
        if (status.equals("generation")){
            ctrl.findGenerator(clients);
        }
        for (int i = 0; i < 18; i++){
            if (clients.get(i).state.equals("failure")){
                ctrl.Failure();
                clients.get(i).chState("working");
            }
            if (clients.get(i).state.equals("busy")){
                ctrl.Busy();
                clients.get(i).chState("working");
            }
            if ((clients.get(i).state.equals("denial")) || (clients.get(i).state.equals("blocked"))){
                ctrl.Denial();
            }
            ctrl.NormalWork();
        }
    }
}
