package model;

import java.util.HashMap;

public class TimeController {
    TimeCounter timer;
    int msgcnt;

    TimeController(){
        timer = new TimeCounter();
        msgcnt = 0;
    }

    public int getTime(){
        return timer.getTime();
    }
    public int getMsgcnt(){ return msgcnt; }

    public void Failure(){
        timer.addTime("command");
        timer.addTime("word");
        timer.addTime("pause_before_answer");
        msgcnt += 13;
    }

    public void Denial(LineStat ls){
        for (int i = 0; i < 2; i++){
            timer.addTime("command");
            timer.addTime("word");
            timer.addTime("pause_before_answer");
            msgcnt += 13;
        }
        ls.line = "B";
        ls.status = "working";
    }

    public void Busy(){
        timer.addTime("command");
        timer.addTime("word");
        timer.addTime("pause_before_answer");
        timer.addTime("answer");
        timer.addTime("pause_if_busy");
        msgcnt += 14;
    }

    public void NormalWork(LineStat ls){
        timer.addTime("command");
        timer.addTime("word");
        timer.addTime("pause_before_answer");
        timer.addTime("answer");
        ls.line = "A";
        ls.status = "working";
        msgcnt +=14;
    }


    public void findGenerator(HashMap<Integer, OU> clients, LineStat ls){
        // ================ Тест МКО ====================
        for(int i = 0; i < 18; i++){
            timer.addTime("command");
            timer.addTime("pause_before_answer");
            msgcnt += 1;
        }
        //================================================

        //=============== Блокировка всех ОУ =============
        ls.line = "B";
        ls.status = "working";
            for(int i = 0; i < 18; i++) {
                timer.addTime("block");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("blocked");
                msgcnt += 2;
            }
        //==================================================
            int i = 0;
            do{
                ls.line = "B";
                ls.status = "working";
                //======= Разблокировка одного ОУ ==========
                i++;
                timer.addTime("unblock");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("working");
                msgcnt += 2;
                //===========================================
                ls.line = "A";
                ls.status = "working";
                //========================= Опрос текущего ОУ ===============================
                timer.addTime("command");
                timer.addTime("pause_before_answer");
                msgcnt += 1;
                if(!(clients.get(i).state.equals("generator"))) {timer.addTime("answer"); msgcnt += 1; }
                //===========================================================================
                else {
                    //======== Опрос предыдущего ОУ ==========
                    timer.addTime("command");
                    timer.addTime("pause_before_answer");
                    msgcnt += 1;
                    //========================================
                    ls.line = "B";
                    ls.status = "working";
                    //====== Блокируем генерящий элемент ======
                    timer.addTime("block");
                    timer.addTime("pause_before_answer");
                    timer.addTime("answer");
                    clients.get(i).chState("blocked");
                    msgcnt += 2;
                    //=========================================
                    break;
                }
            }while(true);
            i++;
            //===== Разблокировка ОУ после генерящего =====
            for(; i< 18; i++ ){
                timer.addTime("unblock");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("working");
                msgcnt += 2;
            //==============================================
            }
        ls.line = "A";
        ls.status = "working";
    }
}
