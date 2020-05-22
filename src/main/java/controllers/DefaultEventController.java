package controllers;

import entities.EventInfo;
import entities.Person;
import entities.PersonWithSelected;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import listeners.EventsUpdatedListener;
import utils.KeepDataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static entities.DefaultPeopleTable.fillCheckBoxColumn;
import static entities.DefaultPeopleTable.fillDefaultFields;

/**
 * Общий класс для контроллеров, связанных с редактированием мероприятия (и добавления в него людей)
 */
public abstract class DefaultEventController {

    @FXML
    protected TableView<PersonWithSelected> tablePeople;
    @FXML
    protected TableColumn<PersonWithSelected, CheckBox> selectedColumn;
    @FXML
    protected TableColumn<Person, ImageView> picColumn;
    @FXML
    protected TableColumn<Person, String> nameColumn;
    @FXML
    protected TableColumn<Person, String> eventsColumn;
    @FXML
    protected TableColumn<Person, String> companyColumn;
    @FXML
    protected TableColumn<Person, String> roleColumn;
    @FXML
    protected TableColumn<Person, String> descriptionColumn;

    protected final EventInfo eventInfo;
    protected final KeepDataHelper dataHelper;
    protected final Stage ownStage;
    protected final Image logo;
    protected EventsUpdatedListener listener;
    protected final ObservableList<PersonWithSelected> peopleData = FXCollections.observableArrayList();

    public DefaultEventController(KeepDataHelper dataHelper, EventInfo eventInfo, Stage ownStage, Image logo) {
        this.dataHelper = dataHelper;
        this.eventInfo = eventInfo;
        this.ownStage = ownStage;
        this.logo = logo;
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
    protected abstract void refreshData(boolean requestPeople);

    /**
     * Расставляет отметки выбранности (если новый человек - не выбран, если был, то смотрим, был ли он выбран или нет)
     *
     * @param people список людей, которых надо обработать
     */
    protected List<PersonWithSelected> setSelectionForPeople(List<Person> people) {
        List<PersonWithSelected> peopleWithSelected = new ArrayList<>();
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
        return peopleWithSelected;
    }

    /**
     * Обновление данных таблицы
     *
     * @param people список людей
     */
    protected void setDataToTable(List<PersonWithSelected> people) {
        peopleData.clear();
        peopleData.addAll(people);
        tablePeople.setItems(peopleData);
        //setAutoResize();
    }
}
