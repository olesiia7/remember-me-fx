package controllers;

import entities.SettingsProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.io.File;
import java.util.regex.Pattern;

public class SettingsController {
    private final KeepDataHelper dataHelper;
    @FXML
    public TextField dirPath;
    private DirectoryChooser directoryChooser;

    @FXML
    private TextField answerTimeMs;
    @FXML
    private TextField watchTimeMs;
    @FXML
    private Button saveButton;

    public SettingsController(KeepDataHelper dataHelper, Stage stage) {
        this.dataHelper = dataHelper;
    }

    @FXML
    private void initialize() {
        SettingsProfile settings = dataHelper.getSettings();
        refresh(settings);
        // ограничение ввода не цифр и не точки
        Pattern p = Pattern.compile("(\\d+)?");
        answerTimeMs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) answerTimeMs.setText(oldValue);
        });
        watchTimeMs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) watchTimeMs.setText(oldValue);
        });

        dirPath.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null || newValue.isEmpty());
        });

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для хранения информации");
    }

    @FXML
    // ToDo: написать перенос данных при смене пути
    public void changeDir() {
        String path = dirPath.getText();
        if (path != null && !path.isEmpty()) {
            directoryChooser.setInitialDirectory(new File(path));
        }
        File dir = directoryChooser.showDialog(getStage());
        if (dir != null) {
            dirPath.setText(dir.getAbsolutePath());
        } else {
            dirPath.setText(null);
        }
    }

    @FXML
    void cancel() {
        getStage().close();
    }

    @FXML
    void save() {
        String path = dirPath.getText();
        int answerTime = Integer.parseInt(answerTimeMs.getText());
        int watchTime = Integer.parseInt(watchTimeMs.getText());
        SettingsProfile settings = new SettingsProfile(path, answerTime, watchTime);
        dataHelper.setSettings(settings);
        cancel();
    }

    Stage getStage() {
        return (Stage) answerTimeMs.getScene().getWindow();
    }

    private void refresh(SettingsProfile settings) {
        dirPath.setText(settings.getDataPath());
        answerTimeMs.setText("" + settings.getAnswerTimeMs());
        watchTimeMs.setText("" + settings.getWatchTimeMs());
    }
}


