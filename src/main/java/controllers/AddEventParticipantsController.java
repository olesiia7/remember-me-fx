package controllers;

import com.google.common.collect.ImmutableList;
import entities.EventInfo;
import entities.Person;
import entities.PersonWithSelected;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import listeners.EventsUpdatedListener;
import utils.KeepDataHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static entities.DefaultPeopleTable.fillCheckBoxColumn;
import static entities.DefaultPeopleTable.fillDefaultFields;
import static utils.AlertUtils.showInformationAlert;

public class AddEventParticipantsController {

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

    public AddEventParticipantsController(KeepDataHelper dataHelper, EventInfo eventInfo, Stage ownStage) {
        this.dataHelper = dataHelper;
        this.eventInfo = eventInfo;
        this.ownStage = ownStage;
    }

    @FXML
    protected void initialize() {
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
            List<Person> people = dataHelper.getPeopleNotInEvent(eventInfo.getId());
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
        } else {
            peopleWithSelected = ImmutableList.copyOf(peopleData);
        }
        setDataToTable(peopleWithSelected);
        tablePeople.requestLayout();
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
    void cancel() {
        ownStage.close();
    }

    @FXML
    void saveNewParticipants() {
        List<Person> selectedPeople = getSelectedPeople();
        if (selectedPeople.isEmpty()) {
            showInformationAlert("Предупреждение", "Вы не выбрали ни одного человека для добавления.");
            ownStage.close();
        }
        HashSet<Integer> eventId = new HashSet<>();
        eventId.add(eventInfo.getId());
        for (Person selectedPerson : selectedPeople) {
            dataHelper.addEventsToPerson(selectedPerson.getId(), eventId);
        }
        // добавить людей
        listener.eventUpdated();
        ownStage.close();
    }
}

