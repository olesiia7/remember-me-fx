package controllers;

import entities.Person;
import javafx.scene.control.ContextMenu;
import listeners.PersonUpdatedListener;

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
//        List<String> imgPaths = saveImagesToComputer();
//        Set<String> eventsSet = new HashSet<>(Arrays.asList((events.getText().trim().split(","))));
//        Person person = new Person(name.getText().trim(), eventsSet,
//                company.getText().trim(), role.getText().trim(), description.getText().trim(), imgPaths);
//        try {
//            dataHelper.savePerson(person);
//            // уведомить, что данные пользователя обновились
//            if (listener != null) {
//                listener.personUpdated();
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибки при записи в файл");
//            e.printStackTrace();
//        }
        getStage().close();
    }

    /**
     * @param listener добавляет слушатель, если изменены данные пользователя
     */
    public void addListener(PersonUpdatedListener listener) {
        this.listener = listener;
    }
}
