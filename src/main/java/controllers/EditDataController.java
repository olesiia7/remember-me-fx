package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.KeepDataHelper;

public class EditDataController {
    private KeepDataHelper dataHelper;
    private Stage stage;

    @FXML
    public TabPane tabPanel;

    // редактирование людей
    private EditPeopleTabController peopleTab;
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

    public EditDataController(KeepDataHelper dataHelper, Stage stage) {
        this.dataHelper = dataHelper;
        this.stage = stage;
        peopleTab = new EditPeopleTabController(stage, dataHelper);
    }

    // инициализируем форму данными
    @FXML
    private void initialize() {
        peopleTab.initialize(tabPanel, filterPanel, tablePeople,
                picColumn, nameColumn, eventsColumn, companyColumn, roleColumn, descriptionColumn, editColumn, deleteColumn);
    }

    @FXML
    public void addNewPerson() {
        peopleTab.addNewPerson();
    }
}
