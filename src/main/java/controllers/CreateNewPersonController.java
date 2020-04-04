package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import listeners.NewPersonListener;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class CreateNewPersonController extends DefaultNewOrEditPersonController {
    public NewPersonListener listener;
    public Button setTestData;

    @Override
    @FXML
    void initialize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        gridPane.setMaxSize(screenSize.getWidth(), screenSize.getHeight());
    }

    /**
     * При сохранении записывает в БД нового человека
     */
    @Override
    public void save() {
        Person person = createPersonFromFields();
        try {
            dataHelper.savePerson(person);
            // уведомить, что создан новый человек
            if (listener != null) {
                listener.newPersonCreated();
            }
        } catch (SQLException e) {
            System.out.println("Ошибки при записи в файл");
            e.printStackTrace();
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
                Image image = new Image(new FileInputStream("src/main/resources/sample" + i + ".jpg"));
                VBox imageLayout = getImageLayout(image);
                imageHBox.getChildren().addAll(imageLayout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addListener(NewPersonListener evtListener) {
        this.listener = evtListener;
    }
}
