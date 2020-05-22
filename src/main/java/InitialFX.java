import controllers.ControlPeopleController;
import entities.EventInfo;
import entities.Person;
import entities.ShowModeEnum;
import graber.GrabberEdcrunch;
import graber.GrabberEduforum;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import layoutWindow.CreatePersonWindow;
import layoutWindow.EditDataWindow;
import layoutWindow.EditParsedEventWindow;
import layoutWindow.PreWatchDataWindow;
import layoutWindow.SettingsWindow;
import org.controlsfx.control.CheckComboBox;
import utils.KeepDataHelper;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static entities.ShowModeEnum.clearAllShowModeValues;
import static java.util.Objects.requireNonNull;
import static utils.AlertUtils.showErrorAlert;
import static utils.FileUtils.deleteUnusedFiles;

public class InitialFX extends Application {
    public static KeepDataHelper dataHelper;
    public static Image logo;

    public static void main(String[] args) throws Exception {
        File recoursePath = new File("src/main/java/resources");
        recoursePath.mkdir();
        dataHelper = new KeepDataHelper(recoursePath.getPath());
        boolean recreateAllTables = false;
        dataHelper.createTablesIfNotExists(recreateAllTables);
        // удаление неиспользуемых картинок
        if (recreateAllTables) {
            dataHelper.setDataPathSetting(recoursePath.getAbsolutePath());
        } else {
            dataHelper.setDataPathSetting(dataHelper.getDataPath());
        }
        deleteUnusedFiles(dataHelper.getAllPictures(), dataHelper.getDataPath());
        logo = new Image(new FileInputStream("src/main/java/logo.png"));
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        Button startGrabberButton = new Button("Граббер");
        startGrabberButton.setMaxWidth(Double.MAX_VALUE);
        startGrabberButton.setOnAction(actionEvent -> {
            startGrabberWindow();
        });
        Button addNewPersonButton = new Button("Добавить человека");
        addNewPersonButton.setMaxWidth(Double.MAX_VALUE);
        addNewPersonButton.setOnAction(actionEvent -> {
            try {
                new CreatePersonWindow(dataHelper, stage, logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button editDataButton = new Button("Редактировать записи");
        editDataButton.setMaxWidth(Double.MAX_VALUE);
        editDataButton.setOnAction(actionEvent -> {
            try {
                new EditDataWindow(dataHelper, stage, logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button watchDataButton = new Button("Режим просмотра");
        watchDataButton.setMaxWidth(Double.MAX_VALUE);
        watchDataButton.setOnAction(actionEvent -> {
            try {
                new PreWatchDataWindow(dataHelper, stage, logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button controlDataButton = new Button("Режим самопроверки");
        controlDataButton.setMaxWidth(Double.MAX_VALUE);
        controlDataButton.setOnAction(actionEvent -> {
            createPreControlWindow(stage);
        });
        Button settingsButton = new Button("Настройки");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setOnAction(actionEvent -> {
            try {
                new SettingsWindow(dataHelper, stage, logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        vBox.getChildren().addAll(startGrabberButton, addNewPersonButton, editDataButton, watchDataButton, controlDataButton, settingsButton);
        Scene scene = new Scene(vBox);
        stage.setTitle("Remember me");
        stage.setScene(scene);
        stage.setMinWidth(280);
        stage.getIcons().add(logo);
        stage.show();
    }

    private void startGrabberWindow() {
        Stage stage = new Stage();
        stage.getIcons().add(logo);
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));
        ObservableList<Object> sites = FXCollections.observableArrayList();
        sites.addAll("eduforum.spb.ru", "edcrunch.ru");
        ComboBox<String> siteChoiceBox = new ComboBox(sites);
        siteChoiceBox.getSelectionModel().select("edcrunch.ru");
        Button okButton = new Button("Выбрать");
        okButton.setOnAction(action -> {
            String selectedItem = siteChoiceBox.getSelectionModel().getSelectedItem();
            List<Person> people;
            String eventName;
            String dataPath = dataHelper.getDataPath();
            if (selectedItem.equals("edcrunch.ru")) {
                String year = JOptionPane.showInputDialog("Введите год");
                int yearInt = Integer.parseInt(year);
                eventName = "Edcrunch " + year;
                GrabberEdcrunch grabberEdcrunch = new GrabberEdcrunch(yearInt, dataPath, eventName);
                people = grabberEdcrunch.getPeople();

            } else {
                eventName = "Петербургский международный образовательный форум";
                GrabberEduforum grabberEduforum = new GrabberEduforum(dataPath, eventName);
                people = grabberEduforum.getPeople();
            }
            if (people.isEmpty()) {
                return;
            }
            int eventId = dataHelper.createEventAndGetId(eventName);
            EventInfo eventInfo = new EventInfo(eventId, eventName, people.size());
            try {
                stage.close();
                EditParsedEventWindow editParsedEventWindow = new EditParsedEventWindow(dataHelper, stage, eventInfo, people, logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        vBox.getChildren().addAll(siteChoiceBox, okButton);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }

    private void createPreControlWindow(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Настройка режима проверки");
        stage.getIcons().add(logo);
        stage.initOwner(parentStage);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(5);

        HBox hBox1 = new HBox();
        hBox1.setSpacing(5);
        hBox1.setAlignment(Pos.CENTER);
        Label label = new Label("Проверка по полям: ");
        ShowModeEnum[] showModeValues = ShowModeEnum.values();
        ObservableList<Object> showModes = FXCollections.observableArrayList();
        for (ShowModeEnum value : showModeValues) {
            showModes.add(value.getName());
        }
        CheckComboBox modeChoiceBox = new CheckComboBox(showModes);
        String fieldsToShow = dataHelper.getDataShowInControl();
        for (String field : fieldsToShow.split(",")) {
            modeChoiceBox.getCheckModel().check(field);
        }
        clearAllShowModeValues();
        modeChoiceBox.setPrefWidth(250);
        hBox1.getChildren().addAll(label, modeChoiceBox);

        HBox settingBox = new HBox();
        settingBox.setAlignment(Pos.CENTER);
        settingBox.setSpacing(5);
        Label settingLabel = new Label("Время экспозиции правильного ответа");
        TextField timeTextField = new TextField();
        timeTextField.setPrefWidth(77);
        int oldAnswerTimeMs = dataHelper.getAnswerTimeMs();
        timeTextField.setText("" + oldAnswerTimeMs);
        Pattern p = Pattern.compile("(\\d+)?");
        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) timeTextField.setText(oldValue);
        });
        Label msLabel = new Label("мс");
        settingBox.getChildren().addAll(settingLabel, timeTextField, msLabel);

        Label filterLabel = new Label("Фильтры (по умолчанию все):");

        HBox hBox2 = new HBox();
        hBox2.setSpacing(5);
        Label label2 = new Label("Место работы:");
        ObservableList<Object> companiesFilter = FXCollections.observableArrayList();
        companiesFilter.addAll(dataHelper.getAllCompanies());
        CheckComboBox companiesChoiceBox = new CheckComboBox(companiesFilter);
        companiesChoiceBox.setPrefWidth(250);
        hBox2.getChildren().addAll(label2, companiesChoiceBox);
        Label label3 = new Label("Место работы:");
        ObservableList<Object> eventsFilter = FXCollections.observableArrayList();
        eventsFilter.addAll(dataHelper.getAllEventNames());
        CheckComboBox eventsChoiceBox = new CheckComboBox(eventsFilter);
        eventsChoiceBox.setPrefWidth(250);
        hBox2.getChildren().addAll(label3, eventsChoiceBox);

        CheckBox startAgain = new CheckBox("Начать сначала");
        startAgain.setTooltip(new Tooltip("Если галочки нет, запомненные люди не будут показаны"));

        Label hint = new Label("Подсказка: Вы можете импользовать Shift + стрелки, а также 'd', 'в', 'a' и 'ф', чтобы перемещаться");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(5);
        Button saveButton = new Button("Готово!");
        saveButton.setOnAction(action -> {
            List<String> selectedModes = modeChoiceBox.getCheckModel().getCheckedItems();
            if (selectedModes.isEmpty()) {
                showErrorAlert("Вы не выбрали поля для проверки");
                return;
            }
            // выставляем моды
            List<String> modesSetting = new ArrayList<>();
            for (String modeName : selectedModes) {
                ShowModeEnum mode = ShowModeEnum.getModeByName(modeName);
                mode.setEnabled(true);
                modesSetting.add(modeName);
            }
            String settings = String.join(",", modesSetting);
            if (!settings.equals(fieldsToShow)) {
                dataHelper.setDataShowInControl(settings);
            }
            List<String> eventsFilterValue = eventsChoiceBox.getCheckModel().getCheckedItems();
            List<String> companiesFilterValue = companiesChoiceBox.getCheckModel().getCheckedItems();
            List<Person> peopleToShow;
            if (startAgain.isSelected()) {
                // помечаем всех незапомненными
                dataHelper.setAllPeopleNotRemembered();
            }
            peopleToShow = dataHelper.getPeopleByCriteria(eventsFilterValue, companiesFilterValue, true);
            if (peopleToShow.isEmpty()) {
                showErrorAlert("По выбранным критериям нет ни одного человека");
                return;
            }
            int newAnswerTimeMs = Integer.parseInt(timeTextField.getText());
            // если значение изменилось, сохраняем его в настройки
            if (newAnswerTimeMs != oldAnswerTimeMs) {
                dataHelper.setAnswerTimeMsSetting(newAnswerTimeMs);
            }
            stage.close();
            startControl(peopleToShow, newAnswerTimeMs);
        });
        Button cancelButton = new Button("Отменить");
        cancelButton.setOnAction(action -> stage.close());
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        vBox.getChildren().addAll(hBox1, settingBox, filterLabel, hBox2, startAgain, hint, buttonBox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void startControl(List<Person> peopleToShow, int newAnswerTimeMs) {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = new Stage();
        stage.getIcons().add(logo);
        ControlPeopleController controller = new ControlPeopleController(dataHelper, peopleToShow, newAnswerTimeMs, stage, logo);
        loader.setController(controller);
        try {
            loader.load(requireNonNull(new File("src/main/layouts/DefaultPersonView.fxml").toURI().toURL()).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
