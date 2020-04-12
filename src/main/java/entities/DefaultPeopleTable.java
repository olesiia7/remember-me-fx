package entities;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

public abstract class DefaultPeopleTable {

    /**
     * Заполняет основные поля таблицы
     *
     * @param picColumn         изображение
     * @param nameColumn        имя
     * @param eventsColumn      мероприятия
     * @param companyColumn     компания
     * @param roleColumn        должность
     * @param descriptionColumn описание
     */
    public static void fillDefaultFields(TableColumn<Person, ImageView> picColumn,
                                         TableColumn<Person, String> nameColumn,
                                         TableColumn<Person, String> eventsColumn,
                                         TableColumn<Person, String> companyColumn,
                                         TableColumn<Person, String> roleColumn,
                                         TableColumn<Person, String> descriptionColumn)
    {
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
        eventsColumn.setCellValueFactory(cellData -> {
            Set<String> personEvents = cellData.getValue().getEvents();
            String events = personEvents == null ? "" : String.join(",", personEvents);
            return new SimpleStringProperty(events);
        });
        companyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompany()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    /**
     * Заполняет колонку CheckBox, при нажатии отмечает в PersonWithSelected соответствующее значение
     *
     * @param selectedColumn столбец с CheckBox
     * @param peopleData     сохраненные данные
     */
    public static void fillCheckBoxColumn(TableColumn<PersonWithSelected, CheckBox> selectedColumn,
                                          ObservableList<PersonWithSelected> peopleData)
    {
        selectedColumn.setCellValueFactory(data -> {
            PersonWithSelected curPerson = data.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(curPerson.isSelected());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> peopleData.stream()
                    .filter(person -> person.getId() == curPerson.getId())
                    .forEach(person -> person.setSelected(new_val)));
            return new SimpleObjectProperty<>(checkBox);
        });
    }
}
