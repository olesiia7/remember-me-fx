package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import listeners.NewPersonListener;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

import static utils.AlertUtils.showErrorAlert;

public class CreateNewPersonController extends DefaultNewOrEditPersonController {
    public NewPersonListener listener;

    public CreateNewPersonController(KeepDataHelper dataHelper) {
        super(dataHelper);
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
        boolean personExist = dataHelper.isPersonWithNameExist(personName);
        if (personExist) {
            showErrorAlert("Пользователь с таким ФИО уже существует");
            return;
        }
        Person person = createPersonFromFields();
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
