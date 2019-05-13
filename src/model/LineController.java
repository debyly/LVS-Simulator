package model;
import model.TerminalDevice.DeviceState;
import java.util.ArrayList;

import static model.LVS.LineState.*;
import static model.TerminalDevice.DeviceState.*;
import static model.TimeCounter.TimeType.*;

public class LineController {
    private TimeCounter timer = new TimeCounter();
    private ArrayList<TerminalDevice> clients;
    private LVS.NetLine netLine;
    private int messageCount = 0;

    LineController(ArrayList<TerminalDevice> clients, LVS.NetLine netLine){

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
    void reactOn(TerminalDevice td){
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

    private void failure(){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        messageCount += 13;
    }

    private void denial(){
        for (int i = 0; i < 2; i++){
            timer.addTime(COMMAND);
            timer.addTime(WORD);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            messageCount += 13;
        }
        netLine.setState(B_WORKING);
    }

    private void busy(){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        timer.addTime(PAUSE_IF_BUSY);
        messageCount += 14;
    }

    private void normalWork(){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        messageCount += 14;
        netLine.setState(A_WORKING);
    }

    void findGenerator(){

        // ================ Тест МКО ====================
        for(int i = 0; i < clients.size(); i++){
            timer.addTime(COMMAND);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            messageCount += 1;
        }
        //================================================

        //=============== Блокировка всех ОУ =============
        netLine.setState(B_WORKING);
            for(int i = 0; i < clients.size(); i++) {

                timer.addTime(BLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                clients.get(i).changeState(DeviceState.BLOCKED);

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

                clients.get(i).changeState(UNBLOCKING);

                messageCount += 2;
                //===========================================

                netLine.setState(A_WORKING);

                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                messageCount += 1;

                if(!(clients.get(i).getState() == GENERATOR)) {
                    timer.addTime(ANSWER);
                    messageCount += 1;
                }
                //==================================================

                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime(COMMAND);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    messageCount += 1;
                    //========================================
                    netLine.setState(B_WORKING);
                    //====== Блокируем генерящий элемент ======
                    timer.addTime(BLOCK);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    timer.addTime(ANSWER);
                    clients.get(i).changeState(BLOCKED);
                    messageCount += 2;
                    //===================================================================
                    //======= Остановка после обнаружения генерящего элемента ===========
                    lastDevice = i;
                    break;
                }
            }

            //===== Разблокировка ОУ после генерящего =====
            for (int i = lastDevice + 1; i< clients.size(); i++ ){
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                clients.get(i).changeState(WORKING);
                messageCount += 2;
            //==============================================
            }
        netLine.setState(A_WORKING);
    }
}
