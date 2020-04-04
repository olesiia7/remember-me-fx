package controllers;

import entities.Person;
import javafx.scene.control.ContextMenu;
import listeners.PersonUpdatedListener;

import java.sql.SQLException;

public class EditPersonController extends DefaultNewOrEditPersonController {
    private Person person;
    public ContextMenu eventsSuggester;
    private PersonUpdatedListener listener;

    public void setPerson(Person person) {
        this.person = person;
        // заполняем данными человека
        name.setText(person.getName());
        events.setText(String.join(",", person.getEvents()));
        company.setText(person.getCompany());
        role.setText(person.getRole());
        description.setText(person.getDescription());
        gridPane.setPrefHeight(gridPane.getPrefHeight() + 50);
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
