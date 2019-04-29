import java.util.HashMap;

public class Controller {
    TimeCounter timer;

    Controller(){
        timer = new TimeCounter();
    }

    public int getTime(){
        return timer.getTime();
    }

    public void Failure(){
        timer.addTime("command");
        timer.addTime("word");
        timer.addTime("pause_before_answer");
    }

    public void Denial(LineStat ls){
        for (int i = 0; i < 2; i++){
            timer.addTime("command");
            timer.addTime("word");
            timer.addTime("pause_before_answer");
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
    }

    public void NormalWork(LineStat ls){
        timer.addTime("command");
        timer.addTime("word");
        timer.addTime("pause_before_answer");
        timer.addTime("answer");
        ls.line = "A";
        ls.status = "working";
    }


    public void findGenerator(HashMap<Integer, OU> clients, LineStat ls){
        for(int i=1;i<=18;i++){
            timer.addTime("command");
            timer.addTime("pause_before_answer");
        }
        ls.line = "B";
        ls.status = "working";
            for(int i=1;i<=18;i++){
                timer.addTime("block");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("blocked");
            }
            int i = 0;
            do{
                ls.line = "B";
                ls.status = "working";
                i++;
                timer.addTime("unblock");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("working");

                ls.line = "A";
                ls.status = "working";

                timer.addTime("command");
                timer.addTime("word");
                timer.addTime("pause_before_answer");
                if(!(clients.get(i).state.equals("generator"))) timer.addTime("answer");
                else {
                    //Опрос предыдущего ОУ
                    timer.addTime("command");
                    timer.addTime("word");
                    timer.addTime("pause_before_answer");
                    //
                    //Блокируем генерящий элемент
                    ls.line = "B";
                    ls.status = "working";

                    timer.addTime("block");
                    timer.addTime("pause_before_answer");
                    timer.addTime("answer");

                    clients.get(i).chState("blocked");
                    //
                    break;
                }
            }while(true);
            i++;
            for(; i< 18; i++ ){
                timer.addTime("unblock");
                timer.addTime("pause_before_answer");
                timer.addTime("answer");
                clients.get(i).chState("working");
            }
        ls.line = "A";
        ls.status = "working";
    }
}
