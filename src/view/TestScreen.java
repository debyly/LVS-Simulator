package view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Tester;
import util.Reporter;

import java.util.List;

public class TestScreen {

    @FXML
    private Button startButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextFlow console;
    @FXML
    private Button profileButton;
    @FXML
    private TextField tdField;
    @FXML
    private TextField msgField;
    @FXML
    private TextField groupField;
    @FXML
    private TextField tablesField;
    @FXML
    private TextField failField;
    @FXML
    private TextField denyField;
    @FXML
    private TextField busyField;
    @FXML
    private TextField genField;
    @FXML
    private ProgressBar progressBar;

    private Stage initStage;
    private Text consoleText;
    private WindowManager manager;

    void setManager(WindowManager manager) {
        this.manager = manager;
    }

    public TestScreen(){


    }

    void setInitStage(Stage initStage){
        this.initStage = initStage;
    }

    @FXML
    private void initialize(){

        consoleText = new Text("Welcome");
        console.getChildren().add(consoleText);
        progressBar.setVisible(false);
    }

    private void disableAll(boolean disable){

        startButton.setMouseTransparent(disable);//setDisable(disable);
        clearButton.setDisable(disable);
        profileButton.setDisable(disable);
        tdField.setDisable(disable);
        msgField.setDisable(disable);
        groupField.setDisable(disable);
        tablesField.setDisable(disable);
        failField.setDisable(disable);
        denyField.setDisable(disable);
        busyField.setDisable(disable);
        genField.setDisable(disable);

        if (disable){
            startButton.setText("Тест...");
            clearButton.setText("");
        } else {
            startButton.setText("Запуск теста");
            clearButton.setText("Очистить консоль");
        }

    }

    @FXML
    private void toConsole(Pair<List<List<Double>>,Integer> output){
        consoleText.setText("");
        console.getChildren().clear();
        for (int i = 0; i < 20; i++) {

            String str = "\n\n--Количество ошибок--\n"
                    + "Генерация: " + output.getKey().get(i).get(4)
                    + "\r\nОтказ: " + output.getKey().get(i).get(2)
                    + "\r\nСбой: " + output.getKey().get(i).get(1)
                    + "\r\nАбонент занят: " + output.getKey().get(i).get(3)
                    + "\nВремени потрачено: " + (output.getKey().get(i).get(5))
                    + " сек\nОжидалось потратить: " + (output.getKey().get(i).get(6))
                    + " сек\n\nПередано сообщений: " + output.getKey().get(i).get(0) + " штук\r\n";
            consoleText.setText(consoleText.getText() + str);
        }
        consoleText.setText("Общее время работы программы: \n" + output.getValue() + " секунд" + consoleText.getText());
        console.getChildren().add(consoleText);
    }

    @FXML
    private void handleStart(){

        // arguments:
        // clientsAmount, messages, groups, genProb, denProb, failProb, busyProb
        int tds, msg, groups, gen, den, fail, busy, tbls;

        try {

            tds = Integer.parseInt(tdField.getText());
            msg = Integer.parseInt(msgField.getText());
            groups = Integer.parseInt(groupField.getText());
            gen = Integer.parseInt(genField.getText());
            den = Integer.parseInt(denyField.getText());
            fail = Integer.parseInt(failField.getText());
            busy = Integer.parseInt(busyField.getText());
            tbls = Integer.parseInt(tablesField.getText());

            if (tds < 2 || msg < 100 || groups < 2 || tbls < 1 || gen < 100 || den < 50 || fail < 10 || busy < 10)
                throw new NumberFormatException("Выход за пределы");

        } catch (NumberFormatException e){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Внутренняя ошибка");
            alert.setHeaderText("Проверьте значения");
            alert.setContentText("Что-то у вас явно не то");
            alert.initOwner(initStage);
            alert.showAndWait();
            return;
        }

        int[] args = new int[]{
                tds, msg, groups, gen, den, fail, busy
                };

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {

                if (newValue.doubleValue() == 1.0){

                    progressBar.setVisible(false);
                    disableAll(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Запись тестов завершена!");
                    alert.setHeaderText("Успешно!");
                    alert.setContentText("Было проведено " + groups * tbls
                            + " тестов.\nФайл успешно сохранён!");
                    alert.initOwner(initStage);
                    alert.showAndWait();
                }
            });

        progressBar.setProgress(.0);
        progressBar.setVisible(true);
        disableAll(true);
        progressBar.setProgress(0.05);
        Tester.test(args, initStage, progressBar.progressProperty());
    }

    @FXML
    private void clearHandle(){

        console.getChildren().clear();
        console.getChildren().add(new Text("Console Cleared"));
    }

    @FXML
    private void profileHandle() { manager.mainWindow(); }


}
