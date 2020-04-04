package controllers;

import entities.SettingsProfile;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.io.File;
import java.util.regex.Pattern;

public class SettingsController {
    public TextField dirPath;
    private Stage stage;
    public KeepDataHelper dataHelper;
    private DirectoryChooser directoryChooser;

    @FXML
    private TextField answerTimeMs;
    @FXML
    private TextField watchTimeMs;

    @FXML
    private void initialize() {
        // ограничение ввода не цифр и не точки
        Pattern p = Pattern.compile("(\\d+)?");
        answerTimeMs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) answerTimeMs.setText(oldValue);
        });
        watchTimeMs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) watchTimeMs.setText(oldValue);
        });

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для хранения информации");
    }

    @FXML
    // ToDo: написать перенос данных при смене пути
    public void changeDir() {
        directoryChooser.setInitialDirectory(new File(dirPath.getText()));
        File dir = directoryChooser.showDialog(getStage());
        if (dir != null) {
            dirPath.setText(dir.getAbsolutePath());
            cancel();
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
        refresh(settings);
    }

    Stage getStage() {
        return (Stage) answerTimeMs.getScene().getWindow();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Добавляет класс и выстанавливает значения
     *
     * @param dataHelper dataHelper
     */
    public void setDataHelper(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
        SettingsProfile settings = dataHelper.getSettings();
        refresh(settings);
    }

    private void refresh(SettingsProfile settings) {
        dirPath.setText(settings.getDataPath());
        answerTimeMs.setText("" + settings.getAnswerTimeMs());
        watchTimeMs.setText("" + settings.getWatchTimeMs());
    }
}


