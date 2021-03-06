package controllers;

import entities.EventInfo;
import entities.EventInfoWithSelected;
import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.KeepDataHelper;

public class EditDataController {
    @FXML
    public TabPane tabPanel;

    // редактирование людей
    private final EditPeopleTabController peopleTab;
    @FXML
    private Tab editPeopleTab;
    @FXML
    private HBox filterPanel;
    @FXML
    private TableView<Person> tablePeople;
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
    @FXML
    private TableColumn<Person, String> editColumn;
    @FXML
    private TableColumn<Person, String> deleteColumn;

    // редактирование мероприятий
    private final EditEventsTabController eventsTab;
    @FXML
    private Tab editEventsTab;
    @FXML
    private TableView<EventInfoWithSelected> tableEvents;
    @FXML
    protected TableColumn<EventInfoWithSelected, CheckBox> selectedColumn;
    @FXML
    private TableColumn<EventInfo, String> eventNameColumn;
    @FXML
    private TableColumn<EventInfo, String> eventsCountColumn;
    @FXML
    private TableColumn<EventInfo, String> eventEditColumn;
    @FXML
    private TableColumn<EventInfo, String> eventDeleteColumn;

    public EditDataController(KeepDataHelper dataHelper, Stage stage, Image logo) {
        peopleTab = new EditPeopleTabController(stage, dataHelper, logo);
        eventsTab = new EditEventsTabController(stage, dataHelper, logo);
    }

    // инициализируем форму данными
    @FXML
    private void initialize() {
        peopleTab.initialize(tabPanel, editPeopleTab, filterPanel, tablePeople,
                picColumn, nameColumn, eventsColumn, companyColumn, roleColumn, descriptionColumn, editColumn, deleteColumn);
        eventsTab.initialize(tabPanel, editEventsTab, tableEvents, selectedColumn, eventNameColumn, eventsCountColumn, eventEditColumn, eventDeleteColumn);
    }

    @FXML
    public void addNewPerson() {
        peopleTab.addNewPerson();
    }

    @FXML
    public void createNewEvent() {
        eventsTab.createNewEvent();
    }

    @FXML
    public void joinEvents() {
        eventsTab.joinEvents();
    }

    @FXML
    void unselectAll() {
        eventsTab.unselectAll();
    }

    @FXML
    void selectAll() {
        eventsTab.selectAll();
    }
}
