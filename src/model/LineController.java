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
                td.endMessaging("! Занят : ожидание...");
                busy();
                if (real) Thread.sleep(lvs.sleepAmount);
                td.changeState(WORKING);
                break;
            // Сбой
            case FAILURE:
                td.endMessaging("! Содержит ошибку : исправление...");
                failure();
                if (real) Thread.sleep(lvs.sleepAmount);
                td.changeState(WORKING);
                break;
            // Отказ или блокировка ОУ
            case DENIAL:
                denial();
                td.endMessaging("!!!Вышел из строя");
                break;
            case BLOCKED:
                denial();
                td.endMessaging("!!!Заблокирован администратором");
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
    }

    void findGenerator() throws InterruptedException {

        int lastDevice = 0;

        // ================ Тест МКО ====================
        for (int i = 0; i < lvs.getDevices().size(); i++) {
            timer.addTime(COMMAND);
            timer.addTime(PAUSE_BEFORE_ANSWER);
        }

        //================================================

        //=============== Блокировка всех ОУ =============
        lvs.setLineState(B_WORKING);
        for (TerminalDevice client : lvs.getDevices()) {

            timer.addTime(BLOCK);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            timer.addTime(ANSWER);

            client.startMessaging("Блокировка компьютера");
            if (real) Thread.sleep(lvs.sleepAmount / 2);
            client.changeState(BLOCKED);
            if (real) Thread.sleep(lvs.sleepAmount / 2);
            client.endMessaging("");
        }

        //==================================================
            for (int i = 0; i < lvs.getDevices().size(); i++){

                lvs.setLineState(B_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                lvs.getDevices().get(i).startMessaging("Разблокировка");
                if (real) Thread.sleep(lvs.sleepAmount);
                lvs.getDevices().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount / 2);

                if (lvs.getDevices().get(i).getState() == DENIAL){

                    for (int i1 = 0; i1 < 2; i1++){
                        timer.addTime(COMMAND);
                        timer.addTime(WORD);
                        timer.addTime(PAUSE_BEFORE_ANSWER);
                    }

                    lvs.getDevices().get(i).endMessaging("Устройство не отвечает");
                    if (real) Thread.sleep(lvs.sleepAmount / 2);
                    continue;
                }

                lvs.getDevices().get(i).endMessaging("");
                if (real) Thread.sleep(lvs.sleepAmount / 2);
                //===========================================

                lvs.setLineState(A_WORKING);
                if (real) Thread.sleep(lvs.sleepAmount);
                //============= Опрос текущего ОУ =================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                lvs.getDevices().get(i).startMessaging("Опрос текущего ОУ");

                //=========== Если элемент сети не генерирует сигнала =========
                if(!(lvs.getDevices().get(i).getState() == GENERATOR)) {

                    timer.addTime(ANSWER);

                    if (real) Thread.sleep(lvs.sleepAmount / 2);
                    lvs.getDevices().get(i).endMessaging("ОУ не является генератором");
                    if (real) Thread.sleep(lvs.sleepAmount / 2);
                }


                //================ Если элемент - генератор ===================
                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime(COMMAND);
                    timer.addTime(PAUSE_BEFORE_ANSWER);

                    //========================================
                    lvs.setLineState(B_WORKING);
                    if (real) Thread.sleep(lvs.sleepAmount / 4);

                    //====== Блокируем генерящий элемент ======
                    timer.addTime(BLOCK);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    timer.addTime(ANSWER);
                    lvs.getDevices().get(i).startMessaging("Устройство является генератором!");
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getDevices().get(i).changeState(DENIAL);
                    if (real) Thread.sleep(lvs.sleepAmount);
                    lvs.getDevices().get(i).endMessaging("Блокировка генерящего элемента");
                    //=========================================

                    //======= Остановка после обнаружения генерящего элемента ===========
                    lastDevice = i;
                    break;
                }
            }

            //===== Разблокировка ОУ после генерящего =====
            for (int i = lastDevice + 1; i < lvs.getDevices().size(); i++ ) {
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                lvs.getDevices().get(i).startMessaging("Разблокировка компьютера");
                if (real) Thread.sleep(lvs.sleepAmount / 2);
                lvs.getDevices().get(i).changeState(UNBLOCKING);
                if (real) Thread.sleep(lvs.sleepAmount / 2);
                lvs.getDevices().get(i).endMessaging("");
                //==============================================
            }

            lvs.setLineState(A_WORKING);
            if (real) Thread.sleep(lvs.sleepAmount);
    }
}
