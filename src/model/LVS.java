package model;

import java.util.HashMap;

import static model.LVS.LineState.A_GENERATION;
import static model.OU.State.*;

public class LVS {
    private HashMap<Integer,OU> clients;
    private TimeController ctrl;

    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}
    class Line {

        LineState state = LineState.A_WORKING;

        LineState getState() {
            return state;
        }

        void setState(LineState state) {
            this.state = state;
        }
    }

    private Line line = new Line();

    public TimeController getTimeCtrl() {
        return ctrl;
    }

    public LVS()
    {
        ctrl = new TimeController();
        clients = new HashMap<>();

        for (int i = 0; i < 18; i++) clients.put(i, new OU());
    }



    public int[][] working_20000(){

        int[][] output = new int[20][6];

        for (int i = 0; i < 20; i++)
            output[i] = working_1000();

        return output;

    }

    private int[] working_1000(){
        int[] flt = new int[6];
        flt[4] = ctrl.getTime();
        for (int i = 0; i < 55; i++){
            working_18(flt);
        }
        flt[5] = ctrl.getTime();
        return flt;

    }

    private void working_18(int[] flt){
        //================= Случайное возникновение неполадок =====================
        for(int i = 0; i < 18; i++) {
            if (clients.get(i).state == DENIAL) flt[1]--;
            clients.get(i).Fault();
            if (clients.get(i).state == GENERATOR) {
                line.setState(A_GENERATION);
                flt[0]++;
            }
            if (clients.get(i).state == DENIAL) {
                flt[1]++;
            }
            if (clients.get(i).state == FAILURE) {
                flt[2]++;
            }
            if (clients.get(i).state == BUSY) {
                flt[3]++;
            }

        }
        //=========================================================================

        //===== Действия при генерации ======
        if (line.getState() == A_GENERATION){
            ctrl.findGenerator(clients, line);
        }
        //===================================


        for (int i = 0; i < 18; i++){
            //====================== Действия при определенных неполадках ============================
            // Сбой
            if (clients.get(i).state == FAILURE){
                ctrl.Failure();
                clients.get(i).chState(WORKING);
            }
            // Абонент занят
            if (clients.get(i).state == BUSY){
                ctrl.Busy();
                clients.get(i).chState(WORKING);
            }
            // Отказ или блокировка ОУ
            if ((clients.get(i).state == DENIAL) || (clients.get(i).state == BLOCKED)){
                ctrl.Denial(line);
            }
            //========================================================================================

            //Нормальная работа
            ctrl.NormalWork(line);
        }
    }
}

