package model;

import model.TerminalDevice.DeviceState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static model.TerminalDevice.DeviceState.WORKING;

public class LVS {

    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}
    public class NetLine {

        private LineState state = LineState.A_WORKING;
        public LineState getState() {
            return state;
        }
        void setState(LineState state) {
            this.state = state;
        }
    }

    private boolean real;

    private ArrayList<TerminalDevice> clients = new ArrayList<>();
    private NetLine netLine = new NetLine();
    private LineController lineController;

    public LineController getLineCtrl() {
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

        lineController = new LineController(real, clients, netLine);

        for (int i = 0; i < clientsAmount; i++)
            clients.add(new TerminalDevice(real, chances));
    }

    public int getClientsAmount(){

        return clients.size();
    }

    public ArrayList<TerminalDevice> getClients() {
        return clients;
    }

    public void start(int[] statistics) throws InterruptedException {

        //================= Симуляция работы =====================
        for (TerminalDevice client : clients) client.process();

        //================= Подсчёт ошибок =====================
        for (TerminalDevice client : clients) {
            switch (client.getState()) {
                case BUSY:
                    statistics[3]++;
                    break;
                case FAILURE:
                    statistics[2]++;
                    break;
                case DENIAL:
                    if (client.getPreviousState() == WORKING)
                        statistics[1]++;
                    break;
                case GENERATOR:
                    netLine.setState(LineState.A_GENERATION);
                    statistics[0]++;
                    break;
                default:
                    break;
            }
        }
        //===== Действия при генерации ======
        if (netLine.getState() == LineState.A_GENERATION)
            lineController.findGenerator();
        //====== Запуск действия контроллера =======
        for (TerminalDevice client : clients)
            lineController.reactOn(client);
        //====== Запись общего кол-ва сообщений =====
        statistics[4] = lineController.getMessageCount();
    }
}

