package model;

import javafx.util.Pair;

import java.util.Map;

public class Tester {

    public static Pair<int[][], Integer> simulateX(int clientsAmount, Map<TerminalDevice.DeviceState, Integer> chances, int multiplier, int sessions){

        LVS lvs = new LVS(clientsAmount, chances);
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
