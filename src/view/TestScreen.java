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

        int tds, messages, groupAmount, sessions;
        double gen, den, fail, busy;

        try {

            tds = Integer.parseInt(tdField.getText());
            messages = Integer.parseInt(msgField.getText());
            groupAmount = Integer.parseInt(groupField.getText());
            sessions = Integer.parseInt(tablesField.getText());

            gen = Double.parseDouble(genField.getText());
            den = Double.parseDouble(denyField.getText());
            fail = Double.parseDouble(failField.getText());
            busy = Double.parseDouble(busyField.getText());

            if (tds < 2 || messages < 100 || groupAmount < 2 || sessions < 1 ||
                    gen <= .0 || den <= .0 || fail <= .0 || busy <= .0 ||
                    gen >= 1.0 || den >= 1.0 || fail >= 1.0 || busy >= 1.0 )

                throw new NumberFormatException("Выход значений за допустимые пределы");

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
                tds, messages, groupAmount
        };

        double[] probs = new double[]{gen, den, fail, busy};

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
            tester.test(args, probs, sessions, progressText.textProperty(),
                    progressBar.progressProperty(), file);

        } else {
            progressBar.setProgress(1.0);
        }
    }

    @FXML
    private void profileHandle() { manager.mainWindow(); }


}
