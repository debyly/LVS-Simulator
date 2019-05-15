package model;

import javafx.beans.property.SimpleObjectProperty;
import model.TerminalDevice.DeviceState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.TerminalDevice.DeviceState.WORKING;

public class LVS {


    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}

    public static class LineStateProperty extends SimpleObjectProperty<LVS.LineState> {
        public LineStateProperty(LineState state){ super(state); }
    }

    private boolean real;
    private LineController lineController;
    private ArrayList<TerminalDevice> clients = new ArrayList<>();
    private LineStateProperty state = new LineStateProperty(LineState.A_WORKING);

    void setLineState(LVS.LineState state) {
        this.state.set(state);
    }

    public LineStateProperty getLineStateProperty(){
        return state;
    }

    LineController getLineCtrl() {
        return lineController;
    }

    int sleepAmount = 0;
    
    public LVS(boolean real, int sleepAmount, int clientsAmount, int gen, int den, int fail, int busy)
    {
        
        this.sleepAmount = sleepAmount;
        
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
            clients.add(new TerminalDevice(chances, this));
    }

    public int getClientsAmount(){ return clients.size(); }

    public ArrayList<TerminalDevice> getClients() {
        return clients;
    }

    public void start(List<Double> statistics) throws InterruptedException {


        //================= Симуляция работы ===================
        //================= Подсчёт ошибок =====================
        if (!real)
            for (TerminalDevice client : clients) {
                client.backup();
                DeviceState finalState = client.process();
                switch (finalState) {
                    case FAILURE:
                            statistics.set(0, statistics.get(0) + 1);
                        break;
                    case DENIAL:
                            statistics.set(1, statistics.get(1) + 1);
                        break;
                    case BUSY:
                            statistics.set(2, statistics.get(2) + 1);
                        break;
                    case GENERATOR:
                            statistics.set(3, statistics.get(3) + 1);
                        break;
                    default:
                        break;
                }
            }

        if (real) Thread.sleep(sleepAmount);

        //======== Действия при генерации ==========
        while (state.get() == LineState.A_GENERATION)
            lineController.findGenerator();

        //====== Запуск действия контроллера =======
        for (TerminalDevice client : clients)
            lineController.reactOn(client);
    }
}

