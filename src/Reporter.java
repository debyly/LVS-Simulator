import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Tester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Reporter {

    private Pair<List<List<Double>>,Integer> output;
    private DoubleProperty property;


    public Reporter(DoubleProperty property){

        this.property = property;
    }

    Pair<List<List<Double>>, Integer> getOutput() {
        return output;
    }

    void report(int[] args, int tables, Stage initStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчёт");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX File", "*.xlsx"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));

        File file = fileChooser.showSaveDialog(initStage);
        int tAmount = tables;

        if (file != null) {

            Task task = new Task() {
                @Override
                protected Void call() throws Exception {

                    updateProgress(.0, 1);

                    List<List<List<Double>>> tables = new ArrayList<>(50);
                    ExcelBook excelBook = new ExcelBook();

                    updateProgress(0.1, 1);

                    for (int tablesIter = 0; tablesIter < tAmount; tablesIter++) {

                        output = Tester.simulateX(args);
                        tables.add(output.getKey());
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

                        ArrayList<String> sumColumnNames = new ArrayList<String>() {{

                            add("Всего сообщений");
                            add("Всего сбоев");
                            add("Всего отказов");
                            add("Всего 'аб. занят'");
                            add("Всего генераций");
                            add("Суммарное время передачи " +  (args[1]) + " сообщений");
                            add("Суммарное матем. ожид. времени передачи сообщений");
                            add("Сумарное СКО времени передачи сообщений");
                        }};

                        List<List<Double>> sumList = new ArrayList<List<Double>>() {{

                            add(new ArrayList<>());

                            for (int i = 0; i < output.getKey().get(0).size(); i ++) {
                                get(0).add(.0);
                                for (int j = 0; j < output.getKey().size(); j++) {

                                    get(0).set(i, get(0).get(i) + output.getKey().get(j).get(i));
                                }
                            }
                        }};

                        excelBook.addSheet(
                                "Тест №"
                                        + (tablesIter+1),tables.get(tablesIter),columnNames);

                        excelBook.addToSheet(
                                tables.get(tablesIter).size() + 1,
                                "Тест №"
                                        + (tablesIter+1), sumList, sumColumnNames);

                        updateProgress( 0.1 + 0.8 * tablesIter / (tAmount-1), 1.0);
                    }
                    updateProgress(0.95, 1.0);
                    excelBook.SaveXLS(file);
                    updateProgress(1.0,1.0);
                    return null;
                }
            };

            property.bind(task.progressProperty());

            task.setOnSucceeded(t -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Запись тестов завершена!");
                alert.setHeaderText("Успешно!");
                alert.setContentText("Было проведено " + args[2] * tAmount
                        + "тестов. Файл " + file.getName() + "успешно сохранён!");
                alert.initOwner(initStage);
                alert.showAndWait();
            });

            Thread th = new Thread(task);
            th.start();
        }
    }
}
