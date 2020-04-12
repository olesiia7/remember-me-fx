package controllers;

import com.google.common.collect.ImmutableList;
import entities.EventDeleteMode;
import entities.EventInfo;
import entities.Person;
import entities.PersonWithSelected;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import layoutWindow.AddEventParticipantsWindow;
import listeners.EventsUpdatedListener;
import org.controlsfx.control.CheckComboBox;
import utils.KeepDataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static entities.DefaultPeopleTable.fillCheckBoxColumn;
import static entities.DefaultPeopleTable.fillDefaultFields;
import static entities.EventDeleteMode.ALL;
import static entities.EventDeleteMode.ONLY_CHOSEN_PARTICIPANTS;
import static entities.EventDeleteMode.ONLY_EVENT_FOR_CHOSEN_PARTICIPANTS;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static utils.AlertUtils.showErrorAlert;

public class EditEventController {
    @FXML
    private TextField eventName;
    @FXML
    private TextField partCount;
    @FXML
    private TableView<PersonWithSelected> tablePeople;
    @FXML
    private TableColumn<PersonWithSelected, CheckBox> selectedColumn;
    @FXML
    private TableColumn<Person, ImageView> picColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> eventsColumn;
    @FXML
    private TableColumn<Person, String> companyColumn;
    @FXML
    private TableColumn<Person, String> roleColumn;
    @FXML
    private TableColumn<Person, String> descriptionColumn;

    private final EventInfo eventInfo;
    private final KeepDataHelper dataHelper;
    private final Stage ownStage;
    private EventsUpdatedListener listener;
    private final ObservableList<PersonWithSelected> peopleData = FXCollections.observableArrayList();

    public EditEventController(KeepDataHelper dataHelper, EventInfo eventInfo, Stage ownStage) {
        this.dataHelper = dataHelper;
        this.eventInfo = eventInfo;
        this.ownStage = ownStage;
    }

    @FXML
    protected void initialize() {
        // заполняем данными человека
        eventName.setText(eventInfo.getName());
        // если меняется название - перекрашиваю цвет рамки
        eventName.setOnKeyTyped(action -> {
            String newName = eventName.getText();
            boolean same = newName.equals(eventInfo.getName());
            Paint color;
            if (same) {
                color = Paint.valueOf(Integer.toHexString(Color.GREY.hashCode()));
            } else {
                color = Paint.valueOf(Integer.toHexString(Color.GREEN.hashCode()));
            }
            eventName.setStyle("-fx-border-color: #" + color.toString().substring(2));
        });
        partCount.setText(eventInfo.getParticipantsCount());

        // устанавливаем тип и значение которое должно хранится в колонке
        fillDefaultFields(picColumn, nameColumn, eventsColumn, companyColumn, roleColumn, descriptionColumn);
        fillCheckBoxColumn(selectedColumn, peopleData);
        refreshData(true);
    }


    /**
     * Обновляет данные в таблице
     *
     * @param requestPeople нужно ли запрашивать заново людей из БД
     */
    private void refreshData(boolean requestPeople) {
        List<PersonWithSelected> peopleWithSelected;
        if (requestPeople) {
            List<Person> people = dataHelper.getPeopleByCriteria(singletonList(eventInfo.getName()), EMPTY_LIST);
            peopleWithSelected = new ArrayList<>();
            for (Person person : people) {
                // если не новый пользователь, то ставим прошлую отметку
                Optional<PersonWithSelected> optional = peopleData.stream()
                        .filter(pers -> pers.getId() == person.getId())
                        .findFirst();
                boolean selected = false;
                if (optional.isPresent()) {
                    selected = optional.get().isSelected();
                }
                peopleWithSelected.add(new PersonWithSelected(selected, person));
            }
            // обновляем кол-во участников мероприятия
            partCount.setText("" + peopleWithSelected.size());
            notifyListeners();
        } else {
            peopleWithSelected = ImmutableList.copyOf(peopleData);
        }
        setDataToTable(peopleWithSelected);
        tablePeople.requestLayout();
    }

    private void notifyListeners() {
        if (listener != null) {
            listener.eventUpdated();
        }
    }

    /**
     * Обновление данных таблицы
     *
     * @param people список людей
     */
    public void setDataToTable(List<PersonWithSelected> people) {
        peopleData.clear();
        peopleData.addAll(people);
        tablePeople.setItems(peopleData);
//        setAutoResize();
    }

    @FXML
    void deleteParticipants() {
        List<Person> selectedPeople = getSelectedPeople();

        Stage stage = new Stage();
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));
        Label chooseDeleteModeLabel = new Label("Выберите режим удаления:");
        EventDeleteMode[] deleteModeValues = EventDeleteMode.values();
        ObservableList<Object> deleteModes = FXCollections.observableArrayList();
        for (EventDeleteMode value : deleteModeValues) {
            deleteModes.add(value.getName());
        }
        ChoiceBox modeChoiceBox = new ChoiceBox(deleteModes);
        TextArea modeInfo = new TextArea();
        modeInfo.setEditable(false);
        modeInfo.setPrefRowCount(3);
        modeChoiceBox.setValue(ALL.getName());
        modeInfo.setText(ALL.getExplanation());

        modeChoiceBox.setOnAction(action -> {
            String selectedMode = modeChoiceBox.getSelectionModel().getSelectedItem().toString();
            modeInfo.setText(EventDeleteMode.getModeByName(selectedMode).getExplanation());
        });

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 0, 0, 0));
        hbox.setAlignment(Pos.CENTER_RIGHT);
        Button okButton = new Button("Готово");
        okButton.setOnAction(action -> {
            String selectedMode = modeChoiceBox.getSelectionModel().getSelectedItem().toString();
            EventDeleteMode mode = EventDeleteMode.getModeByName(selectedMode);
            if ((mode == ONLY_CHOSEN_PARTICIPANTS || mode == ONLY_EVENT_FOR_CHOSEN_PARTICIPANTS)
                    && selectedPeople.isEmpty()) {
                showErrorAlert("Невозможно произвести операцию: Вы не выбрали ни одного человека");
                return;
            }
            mode.provideActions(dataHelper, eventInfo.getId(),
                    getSelectedPeople().stream().map(Person::getId).collect(Collectors.toList()),
                    peopleData.stream().map(Person::getId).collect(Collectors.toList()));
            refreshData(true);
            notifyListeners();
            stage.close();
            // при удалении мероприятия закрыть окно редактирования этого мероприятия
            if (mode == ALL) {
                ownStage.close();
            }
        });
        Button cancelButton = new Button("Отменить");
        cancelButton.setOnAction(action -> {
            stage.close();
        });
        hbox.getChildren().addAll(okButton, cancelButton);
        vBox.getChildren().addAll(chooseDeleteModeLabel, modeChoiceBox, modeInfo, hbox);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Выберите режим удаления");
        stage.show();
    }

    @FXML
    void replaceParticipants() {
        List<Person> selectedPeople = getSelectedPeople();
        if (selectedPeople.isEmpty()) {
            showErrorAlert("Вы не выбрали ни одного человека");
            return;
        }
        // получаем список мероприятий, кроме текущего
        Map<Integer, String> allEvents = dataHelper.getAllEvents();
        allEvents.remove(eventInfo.getId());
        if (allEvents.isEmpty()) {
            showErrorAlert("Других групп для переноса не существует");
            return;
        }
        final ObservableList<String> possibleEvents = FXCollections.observableArrayList();
        possibleEvents.addAll(allEvents.values());

        Stage stage = new Stage();
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));
        Label chooseEventsToMoveLabel = new Label("Выберите группы для переноса:");

        CheckComboBox<String> eventsCheckBox = new CheckComboBox<>();
        eventsCheckBox.setPrefWidth(240);
        eventsCheckBox.getItems().addAll(possibleEvents);

        CheckBox leaveInCurrentGroupCheckBox = new CheckBox("Оставить в этом мероприятии");
        leaveInCurrentGroupCheckBox.setTooltip(new Tooltip("Если Вы уберете отсюда галочку, " +
                "данное мероприятие будет удалено из списка мероприятий выбранных участников"));
        leaveInCurrentGroupCheckBox.setSelected(true);

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 0, 0, 0));
        hbox.setAlignment(Pos.CENTER_RIGHT);
        Button okButton = new Button("Готово");
        okButton.setOnAction(action -> {
            ObservableList<String> eventsToMove = eventsCheckBox.getCheckModel().getCheckedItems();
            if (eventsToMove.isEmpty()) {
                showErrorAlert("Невозможно произвести операцию: Вы не выбрали ни одного мероприятия для переноса");
                return;
            }
            Set<Integer> eventsIds = new HashSet<>();
            eventsToMove.forEach(event -> eventsIds.add(getEventsIdByName(event, allEvents)));
            for (Person selectedPerson : selectedPeople) {
                dataHelper.addEventsToPerson(selectedPerson.getId(), eventsIds);
            }
            if (!leaveInCurrentGroupCheckBox.isSelected()) {
                List<Integer> selectedPeopleIds = selectedPeople.stream()
                        .map(Person::getId)
                        .collect(Collectors.toList());
                dataHelper.deleteEventsFromPeople(eventInfo.getId(), selectedPeopleIds);
            }
            refreshData(true);
            notifyListeners();
            stage.close();
        });
        Button cancelButton = new Button("Отменить");
        cancelButton.setOnAction(action -> {
            stage.close();
        });
        hbox.getChildren().addAll(okButton, cancelButton);
        vBox.getChildren().addAll(chooseEventsToMoveLabel, eventsCheckBox, leaveInCurrentGroupCheckBox, hbox);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Выберите мероприятия для переноса");
        stage.show();
    }

    private Integer getEventsIdByName(String eventName, Map<Integer, String> events) {
        for (Map.Entry<Integer, String> entry : events.entrySet()) {
            if (eventName.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @FXML
    void selectAll() {
        peopleData.forEach(person -> person.setSelected(true));
        refreshData(false);
    }

    @FXML
    void unselectAll() {
        peopleData.forEach(person -> person.setSelected(false));
        refreshData(false);
    }

    /**
     * Сохраняем новое имя и удаляем, если нет участников
     */
    @FXML
    void saveEvent() {
        String newName = eventName.getText();
        if (!newName.equals(eventInfo.getName())) {
            if (dataHelper.getAllEventNames().contains(newName)) {
                showErrorAlert("Мероприятие с таким именем уже существует");
                return;
            }
            dataHelper.setEventName(eventInfo.getId(), newName);
            tablePeople.refresh();
            listener.eventUpdated();
        }
        if (peopleData.isEmpty()) {
            dataHelper.deleteEvent(eventInfo.getId());
            tablePeople.refresh();
            listener.eventUpdated();
        }
        ownStage.close();
    }

    private List<Person> getSelectedPeople() {
        return peopleData.stream()
                .filter(PersonWithSelected::isSelected)
                .map(person -> (Person) person)
                .collect(Collectors.toList());
    }

    public void addListener(EventsUpdatedListener evtListener) {
        this.listener = evtListener;
    }

    @FXML
    private void addParticipants() {
        try {
            AddEventParticipantsWindow addEventParticipantsWindow = new AddEventParticipantsWindow(dataHelper, ownStage, eventInfo);
            addEventParticipantsWindow.addListener(() -> this.refreshData(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

