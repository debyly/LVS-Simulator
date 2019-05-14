package util;

import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Reporter {

    private DoubleProperty property;

    public Reporter(DoubleProperty property){
        this.property = property;
    }

    public void save(List<List<List<Double>>> outputTables, Stage initStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчёт");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX File", "*.xlsx"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        File file = fileChooser.showSaveDialog(initStage);

        if (file != null) {

            Task task = new Task() {
                @Override
                protected Object call() throws Exception {

                    property.bind(progressProperty());

                    ExcelBook excelBook = new ExcelBook();

                    for (int i = 0; i < outputTables.size(); i++) {

                        ArrayList<String> columnNames = new ArrayList<String>() {{
                            add("Число сообщений в группе");
                            add("Наличие сбоя в группе");
                            add("Наличие отказа в группе");
                            add("Наличие 'аб. занят' в группе");
                            add("Наличие генерации в сеансе");
                            add("Время передачи группы сообщений");
                            add("Матем. ожид. времени передачи сообщений");
                            add("СКО Времени передачи");
                        }};

                        updateProgress(0.8 + 0.25 * ((double) 2*i / (outputTables.size() - 1)), 1);

                        int finalIner = i;
                        List<List<Double>> sumList = new ArrayList<List<Double>>() {{

                            add(new ArrayList<>());

                            for (int i = 0; i < outputTables.get(finalIner).get(0).size(); i++) {
                                get(0).add(.0);
                                for (int j = 0; j < outputTables.size(); j++) {

                                    get(0).set(i, get(0).get(i) + outputTables.get(finalIner).get(j).get(i));
                                }
                            }
                        }};

                        ArrayList<String> sumColumnNames = new ArrayList<String>() {{

                            add("Всего сообщений");
                            add("Всего сбоев");
                            add("Всего отказов");
                            add("Всего 'аб. занят'");
                            add("Всего генераций");
                            add("Суммарное время передачи " + (sumList.get(0).get(0)) + " сообщений");
                            add("Суммарное матем. ожид. времени передачи сообщений");
                            add("Сумарное СКО времени передачи сообщений");
                        }};

                        excelBook.addSheet(
                                "Тест №"
                                        + (i + 1), outputTables.get(i), columnNames);

                        excelBook.addToSheet(
                                outputTables.get(i).size() + 1,
                                "Тест №"
                                        + (i + 1), sumList, sumColumnNames);

                        updateProgress(0.8 + 0.25 * (((double)2*i + 1) / outputTables.size()), 1);
                    }
                    excelBook.SaveXLS(file);

                    property.setValue(1.0);

                    return null;
                }
            };

            (new Thread(task)).start();
        }
    }
}
