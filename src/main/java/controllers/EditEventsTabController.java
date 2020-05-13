package controllers;

import entities.EventInfo;
import entities.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Callback;
import layoutWindow.EditEventWindow;
import utils.KeepDataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static utils.AlertUtils.createConfirmationAlert;
import static utils.AlertUtils.showErrorAlert;

public class EditEventsTabController {
    private final Stage stage;
    private final KeepDataHelper dataHelper;

    public TabPane tabPanel;
    private TableView<EventInfo> tableEvents;

    private final ObservableList<EventInfo> eventsData = FXCollections.observableArrayList();

    public EditEventsTabController(Stage stage, KeepDataHelper dataHelper)
    {
        this.stage = stage;
        this.dataHelper = dataHelper;
    }

    protected void initialize(TabPane tabPanel,
                              Tab editEventsTab,
                              TableView<EventInfo> tableEvents,
                              TableColumn<EventInfo, String> eventNameColumn,
                              TableColumn<EventInfo, String> eventsCountColumn,
                              TableColumn<EventInfo, String> eventEditColumn,
                              TableColumn<EventInfo, String> eventDeleteColumn)
    {
        this.tabPanel = tabPanel;
        this.tableEvents = tableEvents;

        // устанавливаем тип и значение которое должно хранится в колонке
        eventNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        eventsCountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getParticipantsCount()));

        Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>> cellFactory
                = new Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>>() {
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

        Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>> deleteCellFactory
                = new Callback<TableColumn<EventInfo, String>, TableCell<EventInfo, String>>() {
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
    public void setDataToTable(List<EventInfo> events) {
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
        List<EventInfo> eventInfos = new ArrayList<>();
        allEvents.keySet().forEach(eventId -> {
            int participantCount = dataHelper.getCountEventsByEventId(eventId);
            eventInfos.add(new EventInfo(eventId, allEvents.get(eventId), participantCount));
        });
        setDataToTable(eventInfos);
        tableEvents.getItems().sort(Comparator.comparing(EventInfo::getName));
        tabPanel.requestLayout();
    }
}
