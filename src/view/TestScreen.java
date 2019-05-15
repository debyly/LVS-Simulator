package view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Tester;

import java.io.File;
import java.util.Observable;

public class TestScreen {

    @FXML
    private Button startButton;
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
    @FXML
    private Text progressText;

    private Stage initStage;
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

        progressBar.setVisible(false);
        progressText.setVisible(false);
    }

    private void disableAll(boolean disable){

        startButton.setMouseTransparent(disable);
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
        } else {
            startButton.setText("Запуск теста");
        }
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
            alert.setContentText("Что-то у вас явно не то\n\n" + "Причина:\n" + e.getCause());
            alert.initOwner(initStage);
            alert.showAndWait();
            return;
        }

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0){

                    progressBar.setVisible(false);
                    progressText.setVisible(false);
                    disableAll(false);
                  }
            });

        int[] args = new int[]{
                tds, msg, groups, gen, den, fail, busy
        };

        progressBar.setProgress(.0);
        progressBar.setVisible(true);
        progressText.setVisible(true);
        disableAll(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчёт");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "XLSX File",
                        "*.xlsx"));
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Desktop"));
        File file = fileChooser.showSaveDialog(initStage);

        if (file != null) {

            progressBar.setProgress(0.01);


            Tester tester = new Tester();
            tester.test(args, tbls,
                    progressText.textProperty(),
                    progressBar.progressProperty(),
                    file);
        } else {
            progressBar.setProgress(1.0);
        }
    }

    @FXML
    private void profileHandle() { manager.mainWindow(); }


}
