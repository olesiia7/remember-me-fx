package controllers;

import entities.Person;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import layoutWindow.CreatePersonWindow;
import org.controlsfx.control.CheckComboBox;
import utils.KeepDataHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditDataController {
    private KeepDataHelper dataHelper;

    private final ObservableList<Person> peopleData = FXCollections.observableArrayList();

    @FXML
    private HBox filterPanel;
    @FXML
    public Button addPersonButton;
    @FXML
    private TableView<Person> tablePeople;
    @FXML
    private TableColumn<Person, Image> picColumn;
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

    private CheckComboBox<String> eventsFilter;
    private CheckComboBox<String> companiesFilter;

    private List<String> eventsFilterList = new ArrayList<>();
    private List<String> companiesFilterList = new ArrayList<>();


    // инициализируем форму данными
    @FXML
    private void initialize() {
        // устанавливаем тип и значение которое должно хранится в колонке
        picColumn.setCellValueFactory(cellData -> {
            List<String> pictures = cellData.getValue().getPictures();
            if (pictures != null && !pictures.isEmpty()) {
                String path = pictures.get(0);
                try {
                    Image image = new Image(new FileInputStream(path));
                    return new SimpleObjectProperty<>(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        picColumn.setPrefWidth(60);
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        eventsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(",", cellData.getValue().getEvents())));
        companyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompany()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory
                = new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
            @Override
            public TableCell call(final TableColumn<Person, String> param) {
                return new TableCell<Person, String>() {
                    final Button btn = new Button("Редактировать");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                System.out.println(person);
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        editColumn.setCellFactory(cellFactory);

        Callback<TableColumn<Person, String>, TableCell<Person, String>> deleteCellFactory
                = new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
            @Override
            public TableCell call(final TableColumn<Person, String> param) {
                return new TableCell<Person, String>() {
                    final Button btn = new Button("Удалить");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setOnAction(event -> {
                                Person person = getTableView().getItems().get(getIndex());
                                System.out.println(person);
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        deleteColumn.setCellFactory(deleteCellFactory);
    }

    public void setDataHelper(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    /**
     * Заполняет таблицу и фильтрацию, инициализация фильтрации
     *
     * @param people список имеющихся людей
     */
    public void setDataFirstTime(List<Person> people) {
        setData(people);
        filterPanel.getChildren().addAll(eventsFilter, new Label("по работодателю:"), companiesFilter);
    }

    private CheckComboBox<String> getEventsFilter() {
        final ObservableList<String> allEvents = FXCollections.observableArrayList();
        allEvents.addAll(dataHelper.getAllEvents());

        final CheckComboBox<String> checkComboBox = new CheckComboBox<>(allEvents);
        final ObservableList<String> choisenEvents = checkComboBox.getCheckModel().getCheckedItems();
        choisenEvents.addListener((ListChangeListener<String>) c -> {
            eventsFilterList.clear();
            eventsFilterList.addAll(choisenEvents);
            setData(dataHelper.getPeopleByCriteria(eventsFilterList, companiesFilterList));
        });
        checkComboBox.setPrefWidth(240);
        return checkComboBox;
    }

    private CheckComboBox<String> getCompaniesFilter() {
        final ObservableList<String> allCompanies = FXCollections.observableArrayList();
        allCompanies.addAll(dataHelper.getAllCompanies());

        final CheckComboBox<String> checkComboBox = new CheckComboBox<>(allCompanies);
        final ObservableList<String> chosenCompanies = checkComboBox.getCheckModel().getCheckedItems();
        chosenCompanies.addListener((ListChangeListener<String>) c -> {
            companiesFilterList.clear();
            companiesFilterList.addAll(chosenCompanies);
            setData(dataHelper.getPeopleByCriteria(eventsFilterList, companiesFilterList));
        });
        checkComboBox.setPrefWidth(240);
        return checkComboBox;
    }

    /**
     * Обновление данных таблицы
     *
     * @param people список людей
     */
    public void setData(List<Person> people) {
        peopleData.clear();
        peopleData.addAll(people);
        tablePeople.setItems(peopleData);
        setAutoResize();

        eventsFilter = getEventsFilter();
        companiesFilter = getCompaniesFilter();
    }

    /**
     * Выставляет размер колонок по максимальному содержимому
     */
    private void setAutoResize() {
        tablePeople.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tablePeople.getColumns().forEach((column) ->
        {
            //Minimal width = column header
            Text t = new Text(column.getText());
            double max;
            if (column.getText().equals("Редактировать")) {
                max = "Редактировать".length() * 7;
            } else if (column.getText().equals("Удалить")) {
                max = "Удалить".length() * 8;
            } else {
                max = t.getLayoutBounds().getWidth();
                for (int i = 0; i < tablePeople.getItems().size(); i++) {
                    //cell must not be empty
                    Object cellData = column.getCellData(i);
                    if (cellData != null) {
                        double calcWidth;
                        if (cellData instanceof Button) {
                            Button button = (Button) cellData;
                            calcWidth = button.getWidth();
                        } else {
                            t = new Text(cellData.toString());
                            calcWidth = t.getLayoutBounds().getWidth();
                        }
                        if (calcWidth > max) {
                            max = calcWidth;
                        }
                    }
                }
            }
            column.setPrefWidth(max + 10.0d);
        });
    }

    /**
     * Создает окно нового пользователя. Если пользователь сохранен, текущее окно обновляется (заполняется таблица
     * и фильтры)
     */
    public void addNewPerson() {
        try {
            CreatePersonWindow createPersonWindow = new CreatePersonWindow(dataHelper, getStage());
            // обновляем таблицу при новом пользователе
            createPersonWindow.addNewPersonListener(() -> {
                        setData(dataHelper.getSavedPeople());
                        filterPanel.requestLayout();
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return сцену (окно)
     */
    private Stage getStage() {
        return (Stage) addPersonButton.getScene().getWindow();
    }
}
