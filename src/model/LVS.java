package model;

import model.TerminalDevice.DeviceState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.TerminalDevice.DeviceState.WORKING;

public class LVS {



    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}

    private boolean real;
    private LineController lineController;
    private ArrayList<TerminalDevice> clients = new ArrayList<>();

    private LVS.LineState state = LVS.LineState.A_WORKING;
    LVS.LineState getLineState() {
        return state;
    }
    void setLineState(LVS.LineState state) {
        this.state = state;
    }


    LineController getLineCtrl() {
        return lineController;
    }

    public LVS(boolean real, int clientsAmount, int gen, int den, int fail, int busy)
    {
        Map<DeviceState, Integer> chances
                = new HashMap<DeviceState, Integer>(){{

            put(DeviceState.GENERATOR, gen);
            put(DeviceState.DENIAL, den);
            put(DeviceState.FAILURE, fail);
            put(DeviceState.BUSY, busy);
        }};

        this.real = real;

        lineController = new LineController(real, this);

        for (int i = 0; i < clientsAmount; i++)
            clients.add(new TerminalDevice(chances));
    }

    public int getClientsAmount(){ return clients.size(); }

    public ArrayList<TerminalDevice> getClients() {
        return clients;
    }

    void start(List<Double> statistics) {

        //================= Симуляция работы =====================
        for (TerminalDevice client : clients) {
            if (!real) client.setRandomState();
        }

        //================= Подсчёт ошибок =====================
        for (TerminalDevice client : clients) {
            switch (client.getState()) {
                case FAILURE:
                    statistics.set(0, statistics.get(0)+1);
                    break;
                case DENIAL:
                    if (client.getPreviousState() == WORKING)
                        statistics.set(1, statistics.get(1)+1);
                    break;
                case BUSY:
                    statistics.set(2, statistics.get(2)+1);
                    break;
                case GENERATOR:
                    statistics.set(3, statistics.get(3)+1);
                    break;
                default:
                    break;
            }
        }

        //===== Действия при генерации ======
        if (getLineState() == LineState.A_GENERATION)
            lineController.findGenerator();

        //====== Запуск действия контроллера =======
        for (TerminalDevice client : clients)
            lineController.reactOn(client);
    }
}

