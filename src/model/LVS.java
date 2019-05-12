package model;

import java.util.HashMap;

public class LVS {

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
    private Integer clientsAmount;
    private HashMap<Integer, TerminalDevice> clients = new HashMap<>();
    private Line line = new Line();
    private LineController lineController = new LineController(clients, line);
    public LineController getLineCtrl() {
        return lineController;
    }

    public LVS(int clientsAmount)
    {
        this.clientsAmount = clientsAmount;
        for (int i = 0; i < this.clientsAmount; i++)
            clients.put(i, new TerminalDevice());
    }

    public int[][] simulateX(int multiplier, int sessions){

        int[][] output = new int[20][6];

        for (int i = 0; i < multiplier; i++){

            output[i][4] = lineController.getTime();

            for (int j = 0; j < sessions; j++)
                basework(output[i]);

            output[i][5] = lineController.getTime();

        }
        return output;
    }

    private void basework(int[] flt){

        //================= Симуляция работы =====================
        for(int i = 0; i < clientsAmount; i++)
            clients.get(i).process();

        //================= Подсчёт ошибок =====================
        for(int i = 0; i < clientsAmount; i++) {

            switch (clients.get(i).state){
                case BUSY:
                    flt[3]++;
                    break;
                case FAILURE:
                    flt[2]++;
                    break;
                case DENIAL:
                    flt[1]++;
                    break;
                case GENERATOR:
                    line.setState(LineState.A_GENERATION);
                    flt[0]++;
                    break;
                default:
                    break;
            }
        }
        //===== Действия при генерации ======
        if (line.getState() == LineState.A_GENERATION)
            lineController.findGenerator();

        //====== Запуск действия контроллера =======
        for (int i = 0; i < clientsAmount; i++){
            lineController.reactOn(clients.get(i));
        }

    }
}

