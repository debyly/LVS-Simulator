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

        td.startMessaging("Опрос компьютера");
        if (real) Thread.sleep(lvs.sleepAmount);

        switch (td.getState()){
            // Абонент занят
            case BUSY:
                td.endMessaging("Компьютер занят : ожидание...");
                busy();
                td.changeState(WORKING);
                break;
            // Сбой
            case FAILURE:
                td.endMessaging("Компьютер содержит ошибку : исправление...");
                failure();
                td.changeState(WORKING);
                break;
            // Отказ или блокировка ОУ
            case DENIAL:
                denial();
                td.endMessaging("Компьютер вышел из строя! : невозможно исправить");
                break;
            case BLOCKED:
                denial();
                td.endMessaging("Компьютер заблокирован администратором");
                break;
            default:
                td.endMessaging("Компьютер в порядке");
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

            client.startMessaging("Блокировка компьютера");
            if (real) Thread.sleep(lvs.sleepAmount);
            client.changeState(BLOCKED);
            if (real) Thread.sleep(lvs.sleepAmount);
            client.endMessaging("Заблокировано");
        }
        //==================================================
            for (int i = 0; i < lvs.getClients().size(); i++){

                lvs.setLineState(B_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getClients().get(i).startMessaging("Разблокировка");
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).endMessaging("Успешно");
                //===========================================

                lvs.setLineState(A_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                lvs.getClients().get(i).startMessaging("Опрос текущего ОУ");
                if(!(lvs.getClients().get(i).getState() == GENERATOR)) {

                    if (real) Thread.sleep(lvs.sleepAmount);
                    timer.addTime(ANSWER);
                    lvs.getClients().get(i).endMessaging("ОУ не является генератором");
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
                    lvs.getClients().get(i).startMessaging("Устройство является генератором!");
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getClients().get(i).changeState(DENIAL);
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getClients().get(i).endMessaging("Блокировка генерящего элемента");
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
                lvs.getClients().get(i).startMessaging("Разблокировка компьютера");
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getClients().get(i).endMessaging("Успешно");
                //==============================================
            }
        if (real) Thread.sleep(lvs.sleepAmount);
    }
}
