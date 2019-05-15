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
            client.changeState(BLOCKED);

            if (real) Thread.sleep(lvs.sleepAmount);
        }
        //==================================================
            for (int i = 0; i < lvs.getClients().size(); i++){

                lvs.setLineState(B_WORKING);

                if (real) Thread.sleep(lvs.sleepAmount);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getClients().get(i).changeState(UNBLOCKING);
                //===========================================

                lvs.setLineState(A_WORKING);

                if (real) Thread.sleep(lvs.sleepAmount);

                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);

                if(!(lvs.getClients().get(i).getState() == GENERATOR))
                    timer.addTime(ANSWER);
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

                    lvs.getClients().get(i).changeState(BLOCKED);

                    if (real) Thread.sleep(lvs.sleepAmount);

                    //===================================================================
                    //======= Остановка после обнаружения генерящего элемента ===========
                    lastDevice = i;
                }
            }
          /*  //===== Разблокировка ОУ после генерящего =====
            for (int i = lastDevice + 1; i < lvs.getClients().size(); i++ ){
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getClients().get(i).changeState(UNBLOCKING);

                if (real) Thread.sleep(lvs.sleepAmount);

                //==============================================
            }*/
        if (real) Thread.sleep(lvs.sleepAmount);
    }
}
