package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import listeners.NewPersonListener;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static utils.AlertUtils.createCustomAlert;
import static utils.AlertUtils.showErrorAlert;

public class CreateNewPersonController extends DefaultNewOrEditPersonController {
    public NewPersonListener listener;

    public CreateNewPersonController(KeepDataHelper dataHelper, Image logo) {
        super(dataHelper, logo);
    }

    @Override
    @FXML
    void initialize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        gridPane.setMaxSize(screenSize.getWidth(), screenSize.getHeight());
        events.setTooltip(new Tooltip("Добавье мероприятия через запятую"));
    }

    /**
     * При сохранении записывает в БД нового человека
     */
    @Override
    public void save() {
        String personName = name.getText();
        if (personName.isEmpty()) {
            showErrorAlert("ФИО не может быть пустым");
            return;
        }
        boolean personNameExist = dataHelper.isPersonWithNameExist(personName);
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
                        showDuplicatePerson(dataHelper.getPersonWithName(personName));
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
        Person person = createPersonFromFields();
        // если человек с идентичными данными существует - отменить вставку
        if (dataHelper.isPersonExist(person)) {
            showErrorAlert("Полностью идентичный человек уже создан. Сохранение отменено");
            getStage().close();
            return;
        }
        dataHelper.savePerson(person);
        // уведомить, что создан новый человек
        if (listener != null) {
            listener.newPersonCreated();
        }
        getStage().close();
    }

    /**
     * Заполняет тестовую информацию
     */
    public void setTestData() {
        name.setText("ФИО");
        events.setText("Мероприятие1,Мероприятие2");
        company.setText("Компания");
        role.setText("Должность");
        description.setText("Описание долгое :;\"\", описание ...");
        setSampleImages();
    }

    /**
     * Добавляет тестовые изображения
     */
    private void setSampleImages() {
        for (int i = 1; i < 3; i++) {
            try {
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/sample" + i + ".jpg");
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                VBox imageLayout = getImageLayout(image);
                imageHBox.getChildren().addAll(imageLayout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addListener(NewPersonListener evtListener) {
        this.listener = evtListener;
    }
}
