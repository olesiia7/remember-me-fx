package controllers;

import entities.Person;
import entities.PersonDif;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import listeners.PersonUpdatedListener;
import utils.KeepDataHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static entities.PersonDif.createPersonDif;
import static entities.PersonDif.isCollectionSameWithoutOrder;
import static entities.PersonDif.isPersonChanged;
import static utils.AlertUtils.createCustomAlert;
import static utils.AlertUtils.showErrorAlert;
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
        if (updatedPerson.getName().isEmpty()) {
            showErrorAlert("ФИО не может быть пустым");
            return;
        }
        PersonDif personDif = createPersonDif(person, updatedPerson);
        // проверка - чтобы не было переименования на существующего пользователя с таким же ФИО
        if (personDif.isNameChanged()) {
            boolean personNameExist = dataHelper.isPersonWithNameExist(personDif.getName());
            AtomicBoolean close = new AtomicBoolean(false);
            if (personNameExist) {
                Alert customAlert = createCustomAlert("Пользователь с таким ФИО уже существует, Вы уверены, что хотите сохранить?",
                        "Посмотреть дубликат",
                        "Да",
                        "Нет");
                customAlert.showAndWait().ifPresent(type -> {
                    String choice = type.getText();
                    switch (choice) {
                        case "Посмотреть дубликат":
                            // открыть в отдельном окне страничку дубликата
                            showDuplicatePerson(dataHelper.getPersonWithName(personDif.getName()));
                            close.set(true);
                            break;
                        case "Нет":
                            getStage().close();
                            close.set(true);
                            return;
                        case "Да":
                        default:
                            break;
                    }
                });
            }
            // ничего не делаем, если отменили сохранение
            if (close.get()) {
                return;
            }
        }
        if (isPersonChanged(personDif))
        {
            dataHelper.updatePerson(personDif);
            // уведомить, что данные пользователя обновились
            if (listener != null) {
                listener.personUpdated();
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
