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
        int[] Flt = new int[4];
        for (int i = 0; i < 55; i++){
            working_18(Flt);
        }
        System.out.println("Количество ошибок:");
        System.out.println("Генерация: " + Flt[0] + "\r\nОтказ: " +
                Flt[1] + "\r\nСбой: " + Flt[2] + "\r\nАбонент занят: " + Flt[3]);
        time = ctrl.getTime() - time;
        System.out.println("Time for 1000: " + time);
        System.out.println("Expected time: " + (time / 990) + "\r\n");
    }

    public void working_18(int[] Flt){
        //================= Случайное возникновение неполадок =====================
        for(int i = 0; i < 18; i++) {
            if (clients.get(i).state.equals("denial")) Flt[1]--;
            clients.get(i).Fault();
            if (clients.get(i).state.equals("generator")) {
                ls.status = "generation";
                Flt[0]++;
            }
            if (clients.get(i).state.equals("denial")) {
                Flt[1]++;
            }
            if (clients.get(i).state.equals("failure")) {
                Flt[2]++;
            }
            if (clients.get(i).state.equals("busy")) {
                Flt[3]++;
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

