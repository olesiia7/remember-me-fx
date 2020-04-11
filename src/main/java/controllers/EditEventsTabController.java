package controllers;

import entities.EventInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import layoutWindow.EditEventWindow;
import utils.KeepDataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
                                dataHelper.deleteEvent(eventInfo.getId());
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
     * Создает окно нового мероприятия. Если мероприятие сохранено, текущее окно обновляется (заполняется таблица)
     */
    public void addNewEvent() {
        System.out.println("Создать новое мероприятие");
//        try {
//            CreateEventInfoWindow createEventInfoWindow = new CreateEventInfoWindow(dataHelper, stage);
//            // обновляем таблицу при новом пользователе
//            createEventInfoWindow.addListener(this::refreshFilters);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
