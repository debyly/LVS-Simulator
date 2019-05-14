package model;

import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.util.Pair;
import util.Reporter;

import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static void test(int[] args, Stage stage, DoubleProperty progressBarProperty){

        Task testTask = new Task() {

            int groupsAmount = args[2];
            public List<List<List<Double>>> tables;

            @Override
            protected Void call() {

                progressBarProperty.bind(progressProperty());

                tables = new ArrayList<>(groupsAmount);
                for (int i = 0; i < groupsAmount; i++) {

                    updateProgress(0.05 + 0.7 * (i / (double)(groupsAmount-1)), 1);
                    tables.add(Tester.simulateX(args).getKey());
                }

                (new Reporter(progressBarProperty)).save(tables,stage);
                return null;
            }
        };

        (new Thread(testTask)).start();
    }

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
                    lvs.start(temp);
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
