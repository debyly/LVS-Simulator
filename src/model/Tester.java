package model;

import javafx.concurrent.Task;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static Pair<List<List<Double>>, Integer> simulateX(int[] args)
    {

        LVS lvs = new LVS(false, args[0],args[3], args[4], args[5], args[6]);
        List<List<Double>> statistics = new ArrayList<>(args[2]);

        int restMessages = args[1];

        for (int i = 0; i < args[2]; i++){

            statistics.add(new ArrayList<>(8));

            for (int i1 = 0; i1 < 8; i1++)
                statistics.get(i).add(.0);

            List<Double> temp = new ArrayList<Double>(){{
                for (int i = 0; i < 5; i++)
                    add(.0);

            }};

            double initTime = (double) lvs.getLineCtrl().getTime();

            int step = args[1] / (args[2] * args[0]) + 1;
            int sessions = restMessages < step * args[0] ? restMessages / args[0] : step;

            restMessages -= sessions * args[0];

            for (int j = 0; j < sessions; j++) {
                try {
                    lvs.start(temp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            statistics.get(i).set(0, (double) sessions * args[0]);
            statistics.get(i).set(1,temp.get(0));
            statistics.get(i).set(2,temp.get(1));
            statistics.get(i).set(3,temp.get(2));
            statistics.get(i).set(4,temp.get(3));

            statistics.get(i).set(5, lvs.getLineCtrl().getTime() - initTime);

            Double M = (lvs.getLineCtrl().getTime() - initTime) / (sessions * args[0]);

            Double D = .0;

            statistics.get(i).set(6, M);
            statistics.get(i).set(7, D);
        }
        Integer totalTime = lvs.getLineCtrl().getTime();

        return new Pair<>(statistics, totalTime);
    }
}
