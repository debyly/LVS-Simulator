package model;

import javafx.util.Pair;

public class Tester {

    public static Pair<int[][], Integer> simulateX(
            int clientsAmount, int gen, int den, int fail, int busy, int multiplier, int sessions){

        LVS lvs = new LVS(clientsAmount, gen, den, fail, busy);
        int[][] statistics = new int[20][8];


        for (int i = 0; i < multiplier; i++){

            statistics[i][5] = lvs.getLineCtrl().getTime();
            for (int j = 0; j < sessions; j++)
                lvs.start(statistics[i]);

            statistics[i][6] = lvs.getLineCtrl().getTime();
            statistics[i][7] = (statistics[i][6] - statistics[i][5]) / (sessions * lvs.getClientsAmount());
        }
        Integer totalTime = lvs.getLineCtrl().getTime();

        return new Pair<>(statistics, totalTime);
    }
}
