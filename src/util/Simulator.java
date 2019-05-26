package util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import model.LVS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private List<List<List<Double>>> tables;

    public void testX(int[] args, double[] probs, int tablesAmount, StringProperty progressDetails, DoubleProperty progressBarProperty, File file){

        Runnable testTask = () -> {

            double proDouble = .0;

            tables = new ArrayList<>(tablesAmount);

            progressDetails.setValue("Выполнено 0% : Инициализация теста...");

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < tablesAmount; i++) {

                tables.add(Simulator.test(args, probs));

                proDouble = 0.05 + 0.7 * (i / (double) (tablesAmount - 1));
                progressBarProperty.setValue(proDouble);
                progressDetails.setValue(
                        "Выполнено "
                                + ((double)((int)(proDouble * 1000))/10)
                                + "% : Проводится тест №" + (i+1) + " ...");

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            progressDetails.setValue(
                    "Выполнено "
                            + (double)((int)(proDouble * 1000))/10
                            + "% : Завершение тестов...");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                Reporter r = new Reporter(progressBarProperty, progressDetails);
                r.makeReport(tables);
                r.saveReport(file);
        };

        (new Thread(testTask)).start();
    }

    private static List<List<Double>> test(int[] args, double[] probs)
    {
        // arguments (args):
        // 0 clientsAmount, 1 messages, 2 groups,
        // probabilities (probs):
        // 0 genProb, 1 denProb, 2 failProb, 3 busyProb
        LVS lvs = LVS.testLVS(args[0],
                probs[0], probs[1], probs[2], probs[3]);

        List<List<Double>> statistics = new ArrayList<>(args[2]);

        int restMessages = args[1];

        for (int i = 0; i < args[2]; i++){

            statistics.add(new ArrayList<>());

           int sessions = Math.round(restMessages/(args[0]*(args[2]-i)));
            restMessages -= sessions * args[0];

            double M = .0;
            double SKO = .0;

            double[] intervals = new double[sessions];

            List<Double> data = new ArrayList<Double>(){{
                for (int i = 0; i < 5; i++) add(.0);
            }};

            for (int j = 0; j < sessions; j++)
                try {
                    lvs.start(data);
                    M += data.get(4)/(sessions*args[0]);
                    intervals[j] = data.get(4) / args[0];

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            for (int j = 0; j < sessions; j++)
                SKO += Math.pow(intervals[j] - M, 2);

            SKO = Math.sqrt(SKO / (sessions*args[0]));

            statistics.get(i).add((double)sessions * args[0]);
            statistics.get(i).add(data.get(0));
            statistics.get(i).add(data.get(1));
            statistics.get(i).add(data.get(2));
            statistics.get(i).add(data.get(3));
            statistics.get(i).add(M * (sessions*args[0]));
            statistics.get(i).add(M);
            statistics.get(i).add(SKO);
        }
        return statistics;
    }
}