package controllers;

import com.google.common.collect.ImmutableList;
import entities.Person;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import layoutWindow.CreatePersonWindow;
import layoutWindow.EditPersonWindow;
import org.controlsfx.control.CheckComboBox;
import utils.KeepDataHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditDataController {
    public VBox peopleTabPane;
    @FXML
    public TabPane tabPanel;
    private KeepDataHelper dataHelper;

    private final ObservableList<Person> peopleData = FXCollections.observableArrayList();

    @FXML
    private HBox filterPanel;
    @FXML
    public Button addPersonButton;
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

    private CheckComboBox<String> eventsFilter;
    private CheckComboBox<String> companiesFilter;

    private Stage stage;

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
                    ImageView imageView = new ImageView(image);
                    imageView.setPickOnBounds(true);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    return new SimpleObjectProperty<>(imageView);
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
                                try {
                                    new EditPersonWindow(dataHelper, stage, person);
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
                                dataHelper.deletePeople(Collections.singletonList(person.getId()));
                                refreshFilters();
                            });
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        deleteColumn.setCellFactory(deleteCellFactory);

        eventsFilter = new CheckComboBox<>();
        eventsFilter.setPrefWidth(240);
        companiesFilter = new CheckComboBox<>();
        companiesFilter.setPrefWidth(240);
        setCheckComboBoxListeners(eventsFilter, companiesFilter);
        filterPanel.getChildren().addAll(eventsFilter, new Label("по работодателю:"), companiesFilter);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDataHelper(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    /**
     * Заполняет таблицу и фильтрацию, инициализация фильтрации
     */
    public void setDataFirstTime() {
        refreshFilters();
    }

    @SafeVarargs
    private final void setCheckComboBoxListeners(CheckComboBox<String>... checkComboBoxes) {
        for (CheckComboBox<String> checkComboBox : checkComboBoxes) {
            final ObservableList<String> chosenItems = checkComboBox.getCheckModel().getCheckedItems();
            chosenItems.addListener((ListChangeListener<String>) c -> {
                ObservableList<String> eventsItems = eventsFilter.getCheckModel().getCheckedItems();
                ObservableList<String> companiesItems = companiesFilter.getCheckModel().getCheckedItems();
                setDataToTable(dataHelper.getPeopleByCriteria(eventsItems, companiesItems));
            });
        }
    }

    /**
     * Обновление данных таблицы
     *
     * @param people список людей
     */
    public void setDataToTable(List<Person> people) {
        peopleData.clear();
        peopleData.addAll(people);
        tablePeople.setItems(peopleData);
        setAutoResize();
    }

    /**
     * Выставляет размер колонок по максимальному содержимому
     */
    private void setAutoResize() {
        tablePeople.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        List<Double> columnWidth = new ArrayList<>();
        tablePeople.getColumns().forEach((column) ->
        {
            //Minimal width = column header
            Text t = new Text(column.getText());
            double max;
            switch (column.getText()) {
                case "Редактировать":
                    max = "Редактировать".length() * 8;
                    break;
                case "Удалить":
                    max = "Удалить".length() * 8;
                    break;
                default:
                    max = t.getLayoutBounds().getWidth();
                    break;
            }
            for (int i = 0; i < tablePeople.getItems().size(); i++) {
                //cell must not be empty
                Object cellData = column.getCellData(i);
                if (cellData != null) {
                    double calcWidth;
                    if (cellData instanceof ImageView) {
                        ImageView imageView = (ImageView) cellData;
                        calcWidth = imageView.boundsInLocalProperty().getValue().getWidth();
                    } else {
                        t = new Text(cellData.toString());
                        calcWidth = t.getLayoutBounds().getWidth();
                    }
                    if (calcWidth > max) {
                        max = calcWidth;
                    }
                }
            }
            column.setPrefWidth(max + 10.0d);
            columnWidth.add(max + 10.0d);
        });
        tabPanel.setPrefSize(columnWidth.stream().mapToDouble(a -> a).sum() + 16, tabPanel.getPrefHeight());
        tabPanel.requestLayout();
        stage.setMinHeight(tabPanel.getPrefHeight() + 5);
        stage.setMinWidth(tabPanel.getPrefWidth());
    }

    /**
     * Создает окно нового пользователя. Если пользователь сохранен, текущее окно обновляется (заполняется таблица
     * и фильтры)
     */
    public void addNewPerson() {
        try {
            CreatePersonWindow createPersonWindow = new CreatePersonWindow(dataHelper, stage);
            // обновляем таблицу при новом пользователе
            createPersonWindow.addNewPersonListener(this::refreshFilters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshFilters() {
        final ObservableList<String> allEvents = FXCollections.observableArrayList();
        allEvents.addAll(dataHelper.getAllEvents());

        final ObservableList<String> allCompanies = FXCollections.observableArrayList();
        allCompanies.addAll(dataHelper.getAllCompanies());

        List<String> unmodifiedEventsList = ImmutableList.copyOf(eventsFilter.getCheckModel().getCheckedItems());
        eventsFilter.getItems().clear();
        eventsFilter.getItems().addAll(allEvents);
        for (String selectedItem : unmodifiedEventsList) {
            eventsFilter.getCheckModel().check(selectedItem);
        }

        List<String> unmodifiedCompaniesList = ImmutableList.copyOf(companiesFilter.getCheckModel().getCheckedItems());
        companiesFilter.getItems().clear();
        companiesFilter.getItems().addAll(allCompanies);
        for (String selectedItem : unmodifiedCompaniesList) {
            companiesFilter.getCheckModel().check(selectedItem);
        }

        setDataToTable(dataHelper.getPeopleByCriteria(unmodifiedEventsList, unmodifiedCompaniesList));
        filterPanel.requestLayout();
    }
}
