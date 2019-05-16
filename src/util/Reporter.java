package util;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reporter {

    private StringProperty progressDetails;
    private DoubleProperty progress;
    private ExcelBook excelBook = new ExcelBook();

    public Reporter(DoubleProperty progress, StringProperty progressDetails) {
        this.progress = progress;
        this.progressDetails = progressDetails;
    }

    public void makeReport(List<List<List<Double>>> outputTables) {

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

            int iter = i;
            List<List<Double>> sumList = new ArrayList<List<Double>>() {{

                add(new ArrayList<>());

                for (int i = 0; i < outputTables.get(iter).get(0).size(); i++) {
                    get(0).add(.0);
                    for (int j = 0; j < outputTables.get(iter).size(); j++)
                        get(0).set(i, get(0).get(i) + outputTables.get(iter).get(j).get(i));

                }
            }};

            ArrayList<String> sumColumnNames = new ArrayList<String>() {{

                add("Всего сообщений");
                add("Всего сбоев");
                add("Всего отказов");
                add("Всего 'аб. занят'");
                add("Всего генераций");
                add("Суммарное время передачи " + sumList.get(0).get(0).intValue() + " сообщений");
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

            double proDouble = 0.75 + 0.25 * ((double) i / (outputTables.size()));
            progress.setValue(proDouble);

            progressDetails.setValue("Выполнено "
                            + ((double) ((int) (proDouble * 1000)) / 10)
                            + "% : Подготовка таблицы " + (i + 1) + " ...");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        progress.setValue(0.99);
        progressDetails.setValue("Почти готово...");
    }

    public void saveReport(File file) {

        try {
            excelBook.SaveXLS(file);

            Platform.runLater(()-> {
                progress.setValue(1.0);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Запись тестов завершена!");
                alert.setHeaderText("Успешно!");
                alert.setContentText("Excel-книга \"" + file.getName() + "\" успешно сохранёна!");
                alert.show();
            });
        }
        catch (IOException e) {
            Platform.runLater(()-> {
                progress.setValue(1.0);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error: внутренняя ошибка");
                alert.setHeaderText("Ошибка сохранения отчёта!");
                alert.setContentText("Сообщение ошибки:\n" + e.getCause());
                alert.show();
            });
        }
    }
}
