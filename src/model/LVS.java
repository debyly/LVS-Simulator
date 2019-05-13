package model;

import java.util.ArrayList;
import java.util.Map;

import static model.TerminalDevice.DeviceState.WORKING;

public class LVS {

    public enum LineState{A_WORKING, A_GENERATION, B_WORKING}
    class NetLine {

        LineState state = LineState.A_WORKING;
        LineState getState() {
            return state;
        }
        void setState(LineState state) {
            this.state = state;
        }
    }
    private ArrayList<TerminalDevice> clients = new ArrayList<>();
    private NetLine netLine = new NetLine();
    private LineController lineController = new LineController(clients, netLine);

    public LineController getLineCtrl() {
        return lineController;
    }

    public LVS(int clientsAmount, Map<TerminalDevice.DeviceState, Integer> chances)
    {
        for (int i = 0; i < clientsAmount; i++)
            clients.add(new TerminalDevice(chances));
    }

    public int getClientsAmount(){

        return clients.size();
    }

    public ArrayList<TerminalDevice> getClients() {
        return clients;
    }

    public void start(int[] statistics){

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

