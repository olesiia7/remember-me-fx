package controllers;

import entities.Person;
import entities.PersonDif;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;
import listeners.PersonUpdatedListener;
import utils.KeepDataHelper;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static entities.PersonDif.createPersonDif;
import static entities.PersonDif.isCollectionSameWithoutOrder;
import static entities.PersonDif.isPersonChanged;
import static utils.AlertUtils.showInformationAlert;

public class EditPersonController extends DefaultNewOrEditPersonController {
    private final Person person;
    private PersonUpdatedListener listener;

    public EditPersonController(KeepDataHelper dataHelper, Person person) {
        super(dataHelper);
        this.person = person;
    }

    @FXML
    protected void initialize() {
        super.initialize();
        // заполняем данными человека
        name.setText(person.getName());
        events.setText(String.join(",", person.getEvents()));
        company.setText(person.getCompany());
        role.setText(person.getRole());
        description.setText(person.getDescription());
        gridPane.setPrefHeight(gridPane.getPrefHeight() + 50);
        fillPersonImages(person, true);

        // установить изменение цвета рамки
        setOnKeyAction(name, person.getName());
        setOnKeyAction(company, person.getCompany());
        setOnKeyAction(role, person.getRole());
        setOnKeyAction(description, person.getDescription());
        events.setOnKeyTyped(action -> {
            Set<String> newEvents = new HashSet<>(Arrays.asList((events.getText().trim().split(","))));
            boolean isChanged = !isCollectionSameWithoutOrder(newEvents, person.getEvents());
            recolorFieldBorder(events, isChanged);
        });
    }

    /**
     * Перекрашивает рамку при изменении для простых полей,
     * где нет списков (все, кроме Мероприятий и картинок)
     *
     * @param field    поле ввода (TextField или TextArea)
     * @param oldValue изначальное значение поля
     */
    private void setOnKeyAction(TextInputControl field, String oldValue) {
        field.setOnKeyTyped(action -> {
            String newValue = field.getText();
            boolean isChanged = !newValue.equals(oldValue);
            recolorFieldBorder(field, isChanged);
        });
    }

    /**
     * При сохранении записывает в БД нового человека
     */
    @Override
    public void save() {
        Person updatedPerson = createPersonFromFields(person);
        PersonDif personDif = createPersonDif(person, updatedPerson);
        if (isPersonChanged(personDif)) {
            try {
                dataHelper.updatePerson(personDif);
                // уведомить, что данные пользователя обновились
                if (listener != null) {
                    listener.personUpdated();
                }
            } catch (SQLException e) {
                System.out.println("Ошибки при записи в файл");
                e.printStackTrace();
            }
            getStage().close();
        } else {
            showInformationAlert("Изменения не применены", "Вы не изменили ни одно поле");
        }
    }

    /**
     * @param listener добавляет слушатель, если изменены данные пользователя
     */
    public void addListener(PersonUpdatedListener listener) {
        this.listener = listener;
    }
}
