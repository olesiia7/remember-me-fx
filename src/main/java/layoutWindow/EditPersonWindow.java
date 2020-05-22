package layoutWindow;

import controllers.EditPersonController;
import entities.Person;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import listeners.PersonUpdatedListener;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class EditPersonWindow {
    private PersonUpdatedListener listener;

    public EditPersonWindow(KeepDataHelper dataHelper, Stage ownerStage, Person person, Image logo) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        EditPersonController controller = new EditPersonController(dataHelper, person, logo);
        loader.setController(controller);
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/DefaultPersonView.fxml").toURI().toURL()).openStream());
        // передаем событие - изменены данные
        controller.addListener(() -> {
            if (listener != null) listener.personUpdated();
        });
        List<String> allEvents = new ArrayList<>(dataHelper.getAllEventNames());
        controller.setEventsList(allEvents);

        Stage stage = new Stage();
        stage.initOwner(ownerStage);

        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(content.getPrefWidth());
        stage.setMinHeight(content.getPrefHeight());
        stage.setTitle("Редактирование человека");
        stage.getIcons().add(logo);
        stage.show();
    }

    public void addListener(PersonUpdatedListener listener) {
        this.listener = listener;
    }
}
