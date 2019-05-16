package model;

import static model.LVS.LineState.*;
import static model.TerminalDevice.DeviceState.*;
import static model.TimeCounter.TimeType.*;

class LineController {
    private TimeCounter timer = new TimeCounter();
    private boolean real;
    private LVS lvs;

    LineController(boolean real, LVS lvs){

        this.real = real;
        this.lvs = lvs;
    }

    int getTime(){
        return timer.getTime();
    }

    //======= Действия при определенных неполадках ============
    void reactOn(TerminalDevice td) throws InterruptedException {

        td.startMessaging();
        if (real) Thread.sleep(lvs.sleepAmount);

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

        td.endMessaging();
        if (real) Thread.sleep(lvs.sleepAmount);
    }

    private void failure() {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
    }

    private void denial() {
        for (int i = 0; i < 2; i++){
            timer.addTime(COMMAND);
            timer.addTime(WORD);
            timer.addTime(PAUSE_BEFORE_ANSWER);
           }
        lvs.setLineState(B_WORKING);
    }

    private void busy() {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        timer.addTime(PAUSE_IF_BUSY);
    }
    private void normalWork() {
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        lvs.setLineState(A_WORKING);
    }

    void findGenerator() throws InterruptedException {

        int lastDevice = 0;

        // ================ Тест МКО ====================
        for(int i = 0; i < lvs.getClients().size(); i++){
            timer.addTime(COMMAND);
            timer.addTime(PAUSE_BEFORE_ANSWER);
        }
        //================================================

        //=============== Блокировка всех ОУ =============
        lvs.setLineState(B_WORKING);
        for (TerminalDevice client : lvs.getClients()) {

            timer.addTime(BLOCK);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            timer.addTime(ANSWER);

            client.startMessaging();
            if (real) Thread.sleep(lvs.sleepAmount);
            client.changeState(BLOCKED);
            if (real) Thread.sleep(lvs.sleepAmount);
            client.endMessaging();
        }
        //==================================================
            for (int i = 0; i < lvs.getClients().size(); i++){

                lvs.setLineState(B_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getClients().get(i).startMessaging();
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).endMessaging();
                //===========================================

                lvs.setLineState(A_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);

                if(!(lvs.getClients().get(i).getState() == GENERATOR)) {
                    lvs.getClients().get(i).startMessaging();
                    if (real) Thread.sleep(lvs.sleepAmount);
                    timer.addTime(ANSWER);
                    lvs.getClients().get(i).endMessaging();
                }
                    //==================================================

                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime(COMMAND);
                    timer.addTime(PAUSE_BEFORE_ANSWER);

                    //========================================
                    lvs.setLineState(B_WORKING);

                    //====== Блокируем генерящий элемент ======
                    timer.addTime(BLOCK);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    timer.addTime(ANSWER);
                    lvs.getClients().get(i).startMessaging();
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getClients().get(i).changeState(DENIAL);
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getClients().get(i).endMessaging();
                    //=========================================

                    //======= Остановка после обнаружения генерящего элемента ===========
                    lastDevice = i;
                    break;
                }
            }
            //===== Разблокировка ОУ после генерящего =====
            for (int i = lastDevice + 1; i < lvs.getClients().size(); i++ ){

                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getClients().get(i).startMessaging();
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).endMessaging();
                //==============================================
            }
        if (real) Thread.sleep(lvs.sleepAmount);
    }
}
