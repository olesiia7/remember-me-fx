package controllers;

import entities.EventInfo;
import entities.EventInfoWithSelected;
import entities.Person;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import layoutWindow.EditEventWindow;
import utils.KeepDataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static utils.AlertUtils.createConfirmationAlert;
import static utils.AlertUtils.showErrorAlert;

public class EditEventsTabController {
    private final Stage stage;
    private final KeepDataHelper dataHelper;

    public TabPane tabPanel;
    private TableView<EventInfoWithSelected> tableEvents;
    private TableColumn<EventInfoWithSelected, CheckBox> selectedColumn;

    private final ObservableList<EventInfoWithSelected> eventsData = FXCollections.observableArrayList();

    public EditEventsTabController(Stage stage, KeepDataHelper dataHelper)
    {
        this.stage = stage;
        this.dataHelper = dataHelper;
    }

    protected void initialize(TabPane tabPanel,
                              Tab editEventsTab,
                              TableView<EventInfoWithSelected> tableEvents,
                              TableColumn<EventInfoWithSelected, CheckBox> selectedColumn,
                              TableColumn<EventInfo, String> eventNameColumn,
                              TableColumn<EventInfo, String> eventsCountColumn,
                              TableColumn<EventInfo, String> eventEditColumn,
                              TableColumn<EventInfo, String> eventDeleteColumn)
    {
        this.tabPanel = tabPanel;
        this.tableEvents = tableEvents;
        this.selectedColumn = selectedColumn;

        // устанавливаем тип и значение которое должно хранится в колонке
        eventNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        eventsCountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getParticipantsCount()));

        Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>> cellFactory = new Callback<>() {
            @Override
            public TableCell call(final TableColumn<EventInfo, String> param) {
                return new TableCell<EventInfo, String>() {
                    final Button btn = new Button("Редактировать");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction(event -> {
                                EventInfo eventInfo = getTableView().getItems().get(getIndex());
                                System.out.println(eventInfo);
                                try {
                                    EditEventWindow editEventInfoWindow = new EditEventWindow(dataHelper, stage, eventInfo);
                                    editEventInfoWindow.addListener(EditEventsTabController.this::refreshData);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        eventEditColumn.setCellFactory(cellFactory);

        Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>> deleteCellFactory = new Callback<>() {
            @Override
            public TableCell call(final TableColumn<EventInfo, String> param) {
                return new TableCell<EventInfo, String>() {
                    final Button btn = new Button("Удалить");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction(event -> {
                                EventInfo eventInfo = getTableView().getItems().get(getIndex());
                                Alert alert = createConfirmationAlert("Удалить так же участников мероприятия?");
                                alert.showAndWait().ifPresent(type -> {
                                    // удалить мероприятие и участников
                                    if (type.getText().equals("Да")) {
                                        List<Person> eventsParticipants = dataHelper.getPeopleByCriteria(Collections.singletonList(eventInfo.getName()), EMPTY_LIST, false);
                                        List<Integer> participantIds = eventsParticipants.stream()
                                                .map(Person::getId)
                                                .collect(Collectors.toList());
                                        dataHelper.deletePeople(participantIds);
                                        dataHelper.deleteEvent(eventInfo.getId());
                                        // удалить только мероприятие
                                    } else if (type.getText().equals("Нет")) {
                                        dataHelper.deleteEvent(eventInfo.getId());
                                    }
                                    // если cancel - ничего не удалять
                                });
                                refreshData();
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        eventDeleteColumn.setCellFactory(deleteCellFactory);
        fillCheckBoxColumn(selectedColumn, eventsData);
        // обновляем при переключении на вкладку
        editEventsTab.setOnSelectionChanged(event -> {
            if (editEventsTab.isSelected()) {
                refreshData();
            }
        });
    }

    /**
     * Обновление данных таблицы
     *
     * @param events список событий
     */
    public void setDataToTable(List<EventInfoWithSelected> events) {
        eventsData.clear();
        eventsData.addAll(events);
        tableEvents.setItems(eventsData);
    }

    /**
     * Создает окно нового мероприятия.
     * Если мероприятие сохранено, текущее окно обновляется (заполняется таблица)
     */
    public void createNewEvent() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание нового мероприятия");
        dialog.setHeaderText(null);
        dialog.setContentText("Введите название мероприятия:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (dataHelper.getAllEventNames().contains(name)) {
                showErrorAlert("Мероприятие с таким именем уже существует");
                return;
            }
            int newEventId = dataHelper.createEventAndGetId(name);
            EventInfo eventInfo = new EventInfo(newEventId, name, 0);
            try {
                EditEventWindow editEventWindow = new EditEventWindow(dataHelper, stage, eventInfo);
                // обновляем таблицу при новом пользователе
                editEventWindow.addListener(EditEventsTabController.this::refreshData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshData() {
        Map<Integer, String> allEvents = dataHelper.getAllEvents();
        List<EventInfoWithSelected> eventInfos = new ArrayList<>();
        allEvents.keySet().forEach(eventId -> {
            int participantCount = dataHelper.getCountEventsByEventId(eventId);
            Optional<EventInfoWithSelected> optional = eventsData.stream()
                    .filter(event -> event.getId() == eventId)
                    .findFirst();
            boolean selected = false;
            if (optional.isPresent()) {
                selected = optional.get().isSelected();
            }
            eventInfos.add(new EventInfoWithSelected(selected, new EventInfo(eventId, allEvents.get(eventId), participantCount)));
        });
        setDataToTable(eventInfos);
        tableEvents.getItems().sort(Comparator.comparing(EventInfo::getName));
        tabPanel.requestLayout();
    }

    /**
     * Заполняет колонку CheckBox, при нажатии отмечает в EventInfoWithSelected соответствующее значение
     *
     * @param selectedColumn столбец с CheckBox
     * @param peopleData     сохраненные данные
     */
    public static void fillCheckBoxColumn(TableColumn<EventInfoWithSelected, CheckBox> selectedColumn,
                                          ObservableList<EventInfoWithSelected> peopleData)
    {
        selectedColumn.setCellValueFactory(data -> {
            EventInfoWithSelected curEvent = data.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(curEvent.isSelected());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> peopleData.stream()
                    .filter(event -> event.getId() == curEvent.getId())
                    .forEach(event -> event.setSelected(new_val)));
            return new SimpleObjectProperty<>(checkBox);
        });
    }

    public void joinEvents() {
        // разделяем мероприятия на выбранные и нет
        Set<Integer> chosenEvents = new HashSet<>();
        ObservableList<String> otherEvents = FXCollections.observableArrayList();
        eventsData.forEach(eventWithSelection -> {
            if (eventWithSelection.isSelected()) {
                chosenEvents.add(eventWithSelection.getEventInfo().getId());
            } else {
                otherEvents.add(eventWithSelection.getEventInfo().getName());
            }
        });
        if (chosenEvents.size() == 0) {
            showErrorAlert("Вы не выбрали ни одного мероприятия для перемещения");
            return;
        }
        otherEvents.sorted();
        Stage stage = new Stage();
        stage.setTitle("Выберите параметры переноса");
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));
        String information = "Вы выбрали " + chosenEvents.size() + " мероприятий. \n" +
                "Пожалуйста, выберите, в какую групу вы хотите их переместить";
        Label chooseWhereToJoinLabel = new Label(information);
        Label moveToExist = new Label("Переместить в существующее мероприятие: ");

        ListView<String> choseOtherEvents = new ListView(otherEvents);
        choseOtherEvents.setPrefHeight(70);
        CheckBox moveToNewGroup = new CheckBox("Создать новую группу");
        TextField newEventName = new TextField();
        newEventName.setEditable(false);
        moveToNewGroup.setOnAction(event -> {
            if (moveToNewGroup.isSelected()) {
                choseOtherEvents.setDisable(true);
                newEventName.setEditable(true);
            } else {
                choseOtherEvents.setDisable(false);
                newEventName.clear();
                newEventName.setEditable(false);
            }
        });
        CheckBox leaveThisGroup = new CheckBox("Оставить людей в этой группе");
        CheckBox deleteThisGroup = new CheckBox("Удалить выбранную группу после переноса");

        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.setAlignment(Pos.TOP_RIGHT);
        Button moveButton = new Button("Переместить");
        moveButton.setOnAction(action -> {
            String name;
            if (moveToNewGroup.isSelected()) {
                name = newEventName.getText();
                boolean eventNameAlreadyExist = eventsData.stream()
                        .anyMatch(event -> event.getName().equals(name));
                if (eventNameAlreadyExist) {
                    showErrorAlert("Название нового мероприятие должно быть уникальным");
                    return;
                }
            } else {
                name = choseOtherEvents.getSelectionModel().getSelectedItem();
            }
            // получаем список людей из всех мероприятий
            Set<Integer> allPeopleToMove = dataHelper.getPeopleWithEvents(chosenEvents);
            if (allPeopleToMove.isEmpty()) {
                showErrorAlert("В выбранных мероприятиях нет людей");
                return;
            }
            // id мероприятия, куда будет идти перенос
            int eventIdToMoveInto = dataHelper.createEventAndGetId(name);
            // перемещаем людей в мероприятие
            dataHelper.addPeopleToEvent(eventIdToMoveInto, allPeopleToMove);
            // 4 если удалить мероприятие, то удалить
            if (deleteThisGroup.isSelected()) {
                dataHelper.deleteEvents(chosenEvents);
                refreshData();
                return;
            }
            // если не надо оставлять участников в группе, то убрать их
            if (!leaveThisGroup.isSelected()) {
                dataHelper.deleteEventsParticipants(chosenEvents);
                refreshData();
            }
        });
        Button cancelButton = new Button("Отменить");
        cancelButton.setOnAction(action -> {
            stage.close();
        });
        buttons.getChildren().addAll(moveButton, cancelButton);

        vBox.getChildren().addAll(chooseWhereToJoinLabel, moveToExist, choseOtherEvents, moveToNewGroup, newEventName,
                leaveThisGroup, deleteThisGroup, buttons);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }

    void unselectAll() {
        eventsData.forEach(person -> person.setSelected(false));
        refreshData();
    }

    void selectAll() {
        eventsData.forEach(person -> person.setSelected(true));
        refreshData();
    }
}
