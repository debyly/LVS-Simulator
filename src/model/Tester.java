package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import util.Reporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    private List<List<List<Double>>> tables;

    public void test(int[] args, int sleepAmount, int tablesAmount, StringProperty progressDetails, DoubleProperty progressBarProperty, File file){

        Runnable testTask = () -> {

            Double proDouble = .0;

            tables = new ArrayList<>(tablesAmount);

            progressDetails.setValue("Выполнено 0% : Инициализация теста...");

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < tablesAmount; i++) {

                tables.add(Tester.simulateX(args, sleepAmount));

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

            Reporter r = new Reporter(progressBarProperty, progressDetails);
            r.makeReport(tables);
            r.saveReport(file);
        };

        (new Thread(testTask)).start();
    }

    private static List<List<Double>> simulateX(int[] args, int sleepAmount)
    {
        LVS lvs = new LVS(false, sleepAmount,
                args[0], args[3], args[4], args[5], args[6]);

        List<List<Double>> statistics = new ArrayList<>(args[2]);

        int restMessages = args[1];

        for (int i = 0; i < args[2]; i++){

            statistics.add(new ArrayList<>(8));

            for (int i1 = 0; i1 < 8; i1++)
                statistics.get(i).add(.0);


            int step = args[1] / (args[2] * args[0]) + 1;
            int sessions = restMessages < step * args[0] ? restMessages / args[0] : step;

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
                    M += data.get(4);
                    intervals[j] = data.get(4);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            for (int j = 0; j < sessions; j++)
                SKO += Math.pow(intervals[j]-(M/(sessions)), 2);

            SKO = Math.sqrt(SKO / sessions);

            statistics.get(i).set(0, (double) sessions * args[0]);
            statistics.get(i).set(1,data.get(0));
            statistics.get(i).set(2,data.get(1));
            statistics.get(i).set(3,data.get(2));
            statistics.get(i).set(4,data.get(3));
            statistics.get(i).set(5, M);
            M /= sessions;
            statistics.get(i).set(6, M);
            statistics.get(i).set(7, SKO);
        }
        return statistics;
    }
}
