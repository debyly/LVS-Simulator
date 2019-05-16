package model;

import javafx.beans.property.SimpleObjectProperty;
import model.TerminalDevice.DeviceState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LVS {

    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}

    public static class LineStateProperty extends SimpleObjectProperty<LVS.LineState> {

      public LineStateProperty(LineState state){
          super(state);
        }
    }
    private boolean real;
    private LineController lineController;
    private ArrayList<TerminalDevice> clients = new ArrayList<>();
    private LineStateProperty state = new LineStateProperty(LineState.A_WORKING);

    void setLineState(LVS.LineState state) {

        if (state == LineState.A_WORKING)
            for (TerminalDevice device : clients)
                if (device.getState() == DeviceState.GENERATOR) {
                    this.state.set(LineState.A_GENERATION);
                    return;
                }

        this.state.set(state);
    }

    LineState getLineState(){
        return state.get();
    }

    public LineStateProperty getLineStateProperty(){
        return state;
    }

    int sleepAmount;
    
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

    public void start(List<Double> data) throws InterruptedException {

        double initTime = lineController.getTime();

        if (!real)
            for (TerminalDevice client : clients) {

                //================= Симуляция работы ===================
                DeviceState finalState = client.process();
                //================= Подсчёт ошибок =====================
                switch (finalState) {
                    case FAILURE:
                            data.set(0, data.get(0) + 1);
                        break;
                    case DENIAL:
                            data.set(1, data.get(1) + 1);
                        break;
                    case BUSY:
                            data.set(2, data.get(2) + 1);
                        break;
                    case GENERATOR:
                            data.set(3, data.get(3) + 1);
                        break;
                    default:
                        break;
                }
            }

        else Thread.sleep(sleepAmount);

        //======== Действия при генерации ==========
        while (state.get() == LineState.A_GENERATION)
            lineController.findGenerator();

        //====== Запуск действия контроллера =======
        for (TerminalDevice client : clients)
            lineController.reactOn(client);

        //====== Сохранение времени работы ========
        data.set(4, lineController.getTime()-initTime);
    }
}

