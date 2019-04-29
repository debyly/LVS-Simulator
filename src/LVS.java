import java.util.HashMap;

public class LVS {
    public HashMap<Integer,OU> clients;
    Controller ctrl;
    LineStat ls;


    LVS()
    {
        ctrl = new Controller();
        clients = new HashMap<>();

        for (int i = 0; i < 18; i++) clients.put(i, new OU());

        ls = new LineStat();
    }



    public void working_20000(){
        for (int i = 0; i < 20; i++){
            working_1000();
        }
        System.out.print("total time: ");
        System.out.println(ctrl.getTime());
    }

    public  void working_1000(){
        int time = ctrl.getTime();
        for (int i = 0; i < 55; i++){
            working_18();
        }
        time = ctrl.getTime() - time;
        System.out.print("Time for 1000: ");
        System.out.println(time);
    }

    public void working_18(){
        for(int i = 0; i < 18; i++) {
            clients.get(i).Fault();
            if (clients.get(i).state.equals("generator")) ls.status = "generation";
        }
        if (ls.status.equals("generation")){
            ctrl.findGenerator(clients, ls);
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
                ctrl.Denial(ls);
            }
            ctrl.NormalWork(ls);
        }
    }
}

