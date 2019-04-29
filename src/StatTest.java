import java.util.HashMap;
import java.util.Random;

public class StatTest {



        public Flt[][] randFault(){
        Random rand = new Random();
        int gen = rand.nextInt() % 2000;
        int failure = rand.nextInt(4) + 8;
        int denial = rand.nextInt(2) + 3;
        int busy = rand.nextInt(4) + 8;

        Flt[][] res = new Flt[20][18];
        for (int i = 0; i < 20; i++){
            for(int j = 0; j <18; j++)
            res[i][j] = new Flt();
        }

        int[][] flag = new int[18][2];
        for (int i = 0; i < denial; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (flag[ou-1][0]!= 1) {
                    next = true;
                    res[th][ou - 1].state = 1;
                    res[th][ou - 1].fault = "denial";
                    flag[ou - 1][0] = 1;
                    flag[ou - 1][1] = th;
                }
            }while (next == false);
        }
        for (int i = 0; i< gen; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (flag[ou-1][0]!= 1) {
                    next = true;
                    res[th][ou - 1].state = 1;
                    res[th][ou - 1].fault = "generator";
                    flag[ou - 1][0] = 1;
                    flag[ou - 1][1] = th;
                }
            }while (next == false);
        }

        int[][] flag1 = new int[18][2];

        for (int i = 0; i< failure; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (((flag[ou-1][0]!= 1) || (flag[ou-1][1] > th)) && ((flag1[ou-1][0] != 1) ||(flag1[ou-1][1] != th))) {
                    next = true;
                    res[th][ou - 1].state = 1;
                    res[th][ou - 1].fault = "failure";
                    flag1[ou - 1][0] = 1;
                    flag1[ou - 1][1] = th;
                }
            }while (next == false);
        }

        for (int i = 0; i< busy; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (((flag[ou-1][0]!= 1) || (flag[ou-1][1] > th)) && ((flag1[ou-1][0] != 1) ||(flag1[ou-1][1] != th))) {
                    next = true;
                    res[th][ou - 1].state = 1;
                    res[th][ou - 1].fault = "busy";
                    flag1[ou - 1][0] = 1;
                    flag1[ou - 1][1] = th;
                }
            }while (next == false);
        }
        return res;
    }

    public int random(int cnt, int total){
            Random rand = new Random();
            int res = rand.nextInt();
    }

    public void Test(){
        LVS lvs = new LVS();
        Flt[][] Faults = randFault();
        lvs.working_20000(Faults);
    }

}
