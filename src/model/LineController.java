package model;
import java.util.ArrayList;

import static model.LVS.LineState.*;
import static model.TerminalDevice.DeviceState.*;
import static model.TimeCounter.TimeType.*;

public class LineController {
    private TimeCounter timer = new TimeCounter();
    private ArrayList<TerminalDevice> clients;
    private LVS.NetLine netLine;
    private boolean real;

    public LVS.LineState getLineState(){

        return netLine.getState();
    }

    private int messageCount = 0;

    LineController(boolean real, ArrayList<TerminalDevice> clients, LVS.NetLine netLine){

        this.real = real;
        this.clients = clients;
        this.netLine = netLine;
    }

    public int getTime(){
        return timer.getTime();
    }

    int getMessageCount() {
        return messageCount;
    }

    //======= Действия при определенных неполадках ============
    void reactOn(TerminalDevice td) throws InterruptedException {
        switch (td.getState()){
            // Абонент занят
            case BUSY:
                busy();
                td.changeState(WORKING);
                break;
            // Сбой
            case FAILURE:
                failure();
                td.changeState(WORKING);
                break;
            // Отказ или блокировка ОУ
            case DENIAL:
            case BLOCKED:
                denial();
                break;
        }
        normalWork();
    }

    private void failure() throws InterruptedException {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        if (real) Thread.sleep(
                timer.getTimeMap().get(COMMAND)
                        + timer.getTimeMap().get(WORD)
                        + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER));
        messageCount += 13;
    }

    private void denial() throws InterruptedException {
        for (int i = 0; i < 2; i++){
            timer.addTime(COMMAND);
            timer.addTime(WORD);
            timer.addTime(PAUSE_BEFORE_ANSWER);

            if (real) Thread.sleep(
                    timer.getTimeMap().get(COMMAND)
                            + timer.getTimeMap().get(WORD)
                            + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER));

            messageCount += 13;
        }
        netLine.setState(B_WORKING);
    }

    private void busy() throws InterruptedException {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        timer.addTime(PAUSE_IF_BUSY);

        if (real) Thread.sleep(
                timer.getTimeMap().get(COMMAND)
                        + timer.getTimeMap().get(WORD)
                        + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                        + timer.getTimeMap().get(ANSWER)
                        + timer.getTimeMap().get(PAUSE_IF_BUSY));

        messageCount += 14;
    }

    private void normalWork() throws InterruptedException {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);

        if (real) Thread.sleep(
                timer.getTimeMap().get(COMMAND)
                        + timer.getTimeMap().get(WORD)
                        + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                        + timer.getTimeMap().get(ANSWER));

        messageCount += 14;
        netLine.setState(A_WORKING);
    }

    void findGenerator() throws InterruptedException {

        // ================ Тест МКО ====================
        for(int i = 0; i < clients.size(); i++){
            timer.addTime(COMMAND);
            timer.addTime(PAUSE_BEFORE_ANSWER);

            if (real) Thread.sleep(
                    timer.getTimeMap().get(COMMAND)
                            + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER));

            messageCount += 1;
        }
        //================================================

        //=============== Блокировка всех ОУ =============
        netLine.setState(B_WORKING);
        for (TerminalDevice client : clients) {

            timer.addTime(BLOCK);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            timer.addTime(ANSWER);

            if (real) Thread.sleep(
                    timer.getTimeMap().get(BLOCK)
                            + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                            + timer.getTimeMap().get(ANSWER));

            client.changeState(BLOCKED);

            messageCount += 2;
        }
        //==================================================
            int lastDevice = 0;
            for (int i = 0; i < clients.size(); i++){

                netLine.setState(B_WORKING);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                if (real) Thread.sleep(
                        timer.getTimeMap().get(UNBLOCK)
                                + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                                + timer.getTimeMap().get(ANSWER));

                clients.get(i).changeState(UNBLOCKING);

                messageCount += 2;
                //===========================================

                netLine.setState(A_WORKING);

                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);

                if (real) Thread.sleep(
                        timer.getTimeMap().get(COMMAND)
                                + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER));

                messageCount += 1;

                if(!(clients.get(i).getState() == GENERATOR)) {

                    timer.addTime(ANSWER);
                    if (real) Thread.sleep(
                            timer.getTimeMap().get(ANSWER));

                    messageCount += 1;
                }
                //==================================================

                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime(COMMAND);
                    timer.addTime(PAUSE_BEFORE_ANSWER);

                    if (real) Thread.sleep(
                            timer.getTimeMap().get(COMMAND)
                                    + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER));

                    messageCount += 1;
                    //========================================
                    netLine.setState(B_WORKING);
                    //====== Блокируем генерящий элемент ======
                    timer.addTime(BLOCK);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    timer.addTime(ANSWER);

                    if (real) Thread.sleep(
                            timer.getTimeMap().get(BLOCK)
                                    + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                                    + timer.getTimeMap().get(ANSWER));

                    clients.get(i).changeState(BLOCKED);
                    messageCount += 2;
                    //===================================================================
                    //======= Остановка после обнаружения генерящего элемента ===========
                    lastDevice = i;
                    break;
                }
            }

            //===== Разблокировка ОУ после генерящего =====
            for (int i = lastDevice + 1; i < clients.size(); i++ ){
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                if (real) Thread.sleep(
                        timer.getTimeMap().get(UNBLOCK)
                                + timer.getTimeMap().get(PAUSE_BEFORE_ANSWER)
                                + timer.getTimeMap().get(ANSWER));

                clients.get(i).changeState(WORKING);
                messageCount += 2;
            //==============================================
            }
        netLine.setState(A_WORKING);
    }
}
