import java.util.HashMap;
import java.util.Random;

public class StatTest {



    public HashMap<Integer,String>[] randFault(){
        Random rand = new Random();
        int gen = rand.nextInt(2);
        int failure = rand.nextInt(4) + 8;
        int denial = rand.nextInt(2) + 3;
        int busy = rand.nextInt(4) + 8;
        HashMap<Integer, String>[] res = new HashMap[20];
        for (int i = 0; i < 20; i++){
            res[i] = new HashMap<>();
        }
        int[][] flag = new int[18][2];
        for (int i = 0; i < denial; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (flag[ou-1][0]!= 1) {
                    next = true;
                    res[th].put(ou, "denial");
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
                    res[th].put(ou, "generator");
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
                if ((flag[ou-1][0]!= 1) || (flag[ou-1][1] > th)) {
                    next = true;
                    res[th].put(ou, "failure");
                    flag1[ou - 1][0] = 1;
                    flag1[ou - 1][1] = th;
                }
            }while (next == false);
        }

        for (int i = 0; i< failure; i++){
            boolean next = false;
            do {
                int th = rand.nextInt(20);
                int ou = rand.nextInt(18) + 1;
                if (((flag[ou-1][0]!= 1) || (flag[ou-1][1] > th)) && ((flag1[ou-1][0] != 1) ||(flag1[ou-1][1] != th))) {
                    next = true;
                    res[th].put(ou, "buzy");
                }
            }while (next == false);
        }
        return res;
    }

    public void Test(){
        LVS lvs = new LVS();
        HashMap<Integer, String>[] Faults = randFault();
        lvs.working_20000(Faults);
    }

}
