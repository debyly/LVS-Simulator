package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static Pair<List<List<Double>>, Integer> simulateX(
            int clientsAmount, int gen, int den, int fail,
            int busy, int multiplier, int sessions) throws InterruptedException {

        LVS lvs = new LVS(false, clientsAmount, gen, den, fail, busy);
        List<List<Double>> statistics = new ArrayList<>(multiplier);//Integer[multiplier][8];



        for (int i = 0; i < multiplier; i++){

            statistics.add(new ArrayList<>(8));

            for (int i1 = 0; i1 < 8; i1++){
                statistics.get(i).add(.0);
            }

            List<Double> temp = new ArrayList<Double>(){{

                for (int i = 0; i < 5; i++)
                    add(.0);

            }};


            Double initTime = (double) lvs.getLineCtrl().getTime();

            for (int j = 0; j < sessions; j++) {
                lvs.start(temp);

            }

            statistics.get(i).set(4,temp.get(0));
            statistics.get(i).set(2,temp.get(1));
            statistics.get(i).set(1,temp.get(2));
            statistics.get(i).set(3,temp.get(3));
            statistics.get(i).set(0,temp.get(4));

            statistics.get(i).set(5, lvs.getLineCtrl().getTime() - initTime);

            Double M = (lvs.getLineCtrl().getTime() - initTime) / (sessions * clientsAmount);

            Double D = .0;//(lvs.getLineCtrl().getTime() - initTime) / (sessions * clientsAmount)

            statistics.get(i).set(6, M);
            statistics.get(i).set(7, D);
        }
        Integer totalTime = lvs.getLineCtrl().getTime();

        return new Pair<>(statistics, totalTime);
    }
}
