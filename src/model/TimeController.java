package model;

import java.util.HashMap;

import static model.LVS.LineState.*;
import static model.OU.State.*;
import static model.TimeCounter.TimeType.*;

public class TimeController {
    private TimeCounter timer = new TimeCounter();
    private int messageCount = 0;

    public int getTime(){
        return timer.getTime();
    }

    public int getMessageCount() {
        return messageCount;
    }

    void Failure(){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        messageCount += 13;
    }

    void NormalWork(LVS.Line line){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        messageCount += 14;
        line.setState(A_WORKING);
    }

    void Denial (LVS.Line line){
        for (int i = 0; i < 2; i++){
            timer.addTime(COMMAND);
            timer.addTime(WORD);
            timer.addTime(PAUSE_BEFORE_ANSWER);
            messageCount += 13;
        }
        line.setState(B_WORKING);
    }

    void Busy(){
        timer.addTime(COMMAND);
        timer.addTime(WORD);
        timer.addTime(PAUSE_BEFORE_ANSWER);
        timer.addTime(ANSWER);
        timer.addTime(PAUSE_IF_BUSY);
        messageCount += 14;
    }

    void findGenerator(HashMap<Integer, OU> clients, LVS.Line line){

        // ================ Тест МКО ====================
        for(int i = 0; i < 18; i++){
            timer.addTime(COMMAND);
            timer.addTime(PAUSE_BEFORE_ANSWER);

            messageCount += 1;
        }
        //================================================

        //=============== Блокировка всех ОУ =============
        line.setState(B_WORKING);
            for(int i = 0; i < 18; i++) {

                timer.addTime(BLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                clients.get(i).chState(BLOCKED);

                messageCount += 2;
            }
        //==================================================

            for (int i = 0; i < 18; i++){

                line.setState(B_WORKING);

                //======= Разблокировка одного ОУ ==========
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);

                clients.get(i).chState(WORKING);

                messageCount += 2;
                //===========================================

                line.setState(A_WORKING);

                //========================= Опрос текущего ОУ ===============================
                timer.addTime(COMMAND);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                messageCount += 1;

                if(!(clients.get(i).state == GENERATOR)) {
                    timer.addTime(ANSWER);
                    messageCount += 1;
                }
                //===========================================================================

                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime(COMMAND);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    messageCount += 1;
                    //========================================
                    line.setState(B_WORKING);
                    //====== Блокируем генерящий элемент ======
                    timer.addTime(BLOCK);
                    timer.addTime(PAUSE_BEFORE_ANSWER);
                    timer.addTime(ANSWER);
                    clients.get(i).chState(BLOCKED);
                    messageCount += 2;
                    //===================================================================
                    //======= Остановка после обнаружения генерящего элемента ===========
                    break;
                }
            }
            //===== Разблокировка ОУ после генерящего =====
            for(int i = 0; i< 18; i++ ){
                timer.addTime(UNBLOCK);
                timer.addTime(PAUSE_BEFORE_ANSWER);
                timer.addTime(ANSWER);
                clients.get(i).chState(WORKING);
                messageCount += 2;
            //==============================================
            }
        line.setState(A_WORKING);
    }
}
