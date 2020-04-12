package controllers;

import entities.Person;
import javafx.fxml.FXML;
import listeners.PersonUpdatedListener;
import utils.KeepDataHelper;

import java.sql.SQLException;

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
    }

    /**
     * При сохранении записывает в БД нового человека
     */
    @Override
    public void save() {
        Person updatedPerson = createPersonFromFields();
        updatedPerson.setId(person.getId());
        try {
            dataHelper.updatePerson(updatedPerson);
            // уведомить, что данные пользователя обновились
            if (listener != null) {
                listener.personUpdated();
            }
        } catch (SQLException e) {
            System.out.println("Ошибки при записи в файл");
            e.printStackTrace();
        }
        getStage().close();
    }

    /**
     * @param listener добавляет слушатель, если изменены данные пользователя
     */
    public void addListener(PersonUpdatedListener listener) {
        this.listener = listener;
    }
}
