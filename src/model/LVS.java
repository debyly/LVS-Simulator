package model;

import java.util.HashMap;

public class LVS {
    private HashMap<Integer,OU> clients;
    private TimeController ctrl;
    private LineStat ls;


    public TimeController getTimeCtrl() {
        return ctrl;
    }

    public LVS()
    {
        ctrl = new TimeController();
        clients = new HashMap<>();
        ls = new LineStat();

        for (int i = 0; i < 18; i++) clients.put(i, new OU());
    }



    public int[][] working_20000(){

        int[][] output = new int[20][6];

        for (int i = 0; i < 20; i++){
            output[i] = working_1000();
        }

        return output;

    }

    public  int[] working_1000(){
        int[] flt = new int[6];
        flt[4] = ctrl.getTime();
        for (int i = 0; i < 55; i++){
            working_18(flt);
        }
        flt[5] = ctrl.getTime();
        return flt;

    }

    public void working_18(int[] flt){
        //================= Случайное возникновение неполадок =====================
        for(int i = 0; i < 18; i++) {
            if (clients.get(i).state.equals("denial")) flt[1]--;
            clients.get(i).Fault();
            if (clients.get(i).state.equals("generator")) {
                ls.status = "generation";
                flt[0]++;
            }
            if (clients.get(i).state.equals("denial")) {
                flt[1]++;
            }
            if (clients.get(i).state.equals("failure")) {
                flt[2]++;
            }
            if (clients.get(i).state.equals("busy")) {
                flt[3]++;
            }

        }
        //=========================================================================

        //===== Действия при генерации ======
        if (ls.status.equals("generation")){
            ctrl.findGenerator(clients, ls);
        }
        //===================================


        for (int i = 0; i < 18; i++){
            //====================== Действия при определенных неполадках ============================
            // Сбой
            if (clients.get(i).state.equals("failure")){
                ctrl.Failure();
                clients.get(i).chState("working");
            }
            // Абонент занят
            if (clients.get(i).state.equals("busy")){
                ctrl.Busy();
                clients.get(i).chState("working");
            }
            // Отказ или блокировка ОУ
            if ((clients.get(i).state.equals("denial")) || (clients.get(i).state.equals("blocked"))){
                ctrl.Denial(ls);
            }
            //========================================================================================

            //Нормальная работа
            ctrl.NormalWork(ls);
        }
    }
}

