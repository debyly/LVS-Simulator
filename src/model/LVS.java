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
    private final boolean real;
    private final LineController lineController;
    private final ArrayList<TerminalDevice> devices = new ArrayList<>();
    private final LineStateProperty state = new LineStateProperty(LineState.A_WORKING);

    void setLineState(LVS.LineState state) {

        if (state == LineState.A_WORKING)
            for (TerminalDevice device : devices)
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

    final int sleepAmount;
    
    private LVS(boolean real, int sleepAmount, int devicesAmount, double gen, double den, double fail, double busy)
    {
        
        this.sleepAmount = sleepAmount;
        
        Map<DeviceState, Double> chances
                = new HashMap<DeviceState, Double>(){{

            put(DeviceState.GENERATOR, gen);
            put(DeviceState.DENIAL, den);
            put(DeviceState.FAILURE, fail);
            put(DeviceState.BUSY, busy);
        }};

        this.real = real;

        lineController = new LineController(real, this);

        for (int i = 0; i < devicesAmount; i++)
            devices.add(new TerminalDevice(chances, this));
    }

    public static LVS realLVS(int sleepAmount, int devicesAmount){

        return new LVS(true, sleepAmount, devicesAmount,0,0,0,0);
    }

    static LVS testLVS(int devicesAmount, double gen, double den, double fail, double busy){

        return new LVS(false, 0, devicesAmount,gen,den,fail,busy);
    }

    public ArrayList<TerminalDevice> getDevices() {
        return devices;
    }

    public void start() throws Exception{

        if (real) start(new ArrayList<>());
        else throw new IllegalAccessException("Вызов неподходящего метода работы ЛВС!");
    }

    void start(List<Double> data) throws InterruptedException {

        double initTime = lineController.getTime();

        if (!real)
            for (TerminalDevice client : devices) {

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
        for (TerminalDevice client : devices)
            lineController.reactOn(client);

        //====== Сохранение времени работы ========
        if (!real) data.set(4, lineController.getTime()-initTime);
    }
}

