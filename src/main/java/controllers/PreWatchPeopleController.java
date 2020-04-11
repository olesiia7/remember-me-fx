package controllers;

import entities.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import layoutWindow.WatchDataWindow;
import org.controlsfx.control.CheckComboBox;
import utils.KeepDataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static utils.AlertUtils.showErrorAlert;
import static utils.AlertUtils.showInformationAlert;

public class PreWatchPeopleController {
    private final KeepDataHelper dataHelper;
    @FXML
    private TextField watchTimeMs;
    @FXML
    private HBox companyHBox;
    @FXML
    private HBox eventsHBox;

    private CheckComboBox<String> eventsFilter;
    private CheckComboBox<String> companiesFilter;

    public PreWatchPeopleController(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    @FXML
    private void initialize() {
        int watchTimeMsValue = dataHelper.getWatchTimeMs();
        watchTimeMs.setText("" + watchTimeMsValue);
        Pattern p = Pattern.compile("(\\d+)?");
        watchTimeMs.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) watchTimeMs.setText(oldValue);
        });

        final ObservableList<String> allEvents = FXCollections.observableArrayList();
        allEvents.addAll(dataHelper.getAllEventNames());

        final ObservableList<String> allCompanies = FXCollections.observableArrayList();
        allCompanies.addAll(dataHelper.getAllCompanies());

        eventsFilter = new CheckComboBox<>();
        eventsFilter.setPrefWidth(240);
        eventsFilter.getItems().addAll(allEvents);
        eventsHBox.getChildren().add(eventsFilter);

        companiesFilter = new CheckComboBox<>();
        companiesFilter.setPrefWidth(240);
        companiesFilter.getItems().addAll(allCompanies);
        companyHBox.getChildren().add(companiesFilter);
    }

    @FXML
    // ToDo: дописать "будет показано ... человек" и в зависимости от этого блокировать кнопку или нет
    public void startWatch() throws IOException {
        int watchTimeMsValue = Integer.parseInt(watchTimeMs.getText());
        dataHelper.setWatchTimeMsSetting(watchTimeMsValue);
        // собрать фильтры и по ним инфу
        ObservableList<String> companiesItems = companiesFilter.getCheckModel().getCheckedItems();
        ObservableList<String> eventsItems = eventsFilter.getCheckModel().getCheckedItems();
        List<Person> people = new ArrayList<>(dataHelper.getPeopleByCriteria(eventsItems, companiesItems));
        if (people.isEmpty()) {
            showErrorAlert("Под выбранный фильтр не подходит ни один человек");
        } else {
            showInformationAlert("Переход к просмотру",
                    "Приготовьтесь к просмотру! Вам будут показаны " + people.size() + " человек(а)");
            getStage().close();
            new WatchDataWindow(dataHelper, getStage(), watchTimeMsValue, people);
        }
    }

    @FXML
    public void cancel() {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) watchTimeMs.getScene().getWindow();
    }
}
