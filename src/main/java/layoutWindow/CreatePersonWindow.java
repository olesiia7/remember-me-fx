package layoutWindow;

import controllers.CreateNewPersonController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import listeners.NewPersonListener;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CreatePersonWindow {
    private NewPersonListener listener;

    public CreatePersonWindow(KeepDataHelper dataHelper, Stage ownerStage, Image logo) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        CreateNewPersonController controller = new CreateNewPersonController(dataHelper, logo);
        loader.setController(controller);
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/NewPerson.fxml").toURI().toURL()).openStream());
        controller.setDataHelper(dataHelper);
        // передаем событие - создан новый пользователь
        controller.addListener(() -> {
            if (listener != null) listener.newPersonCreated();
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
        stage.setTitle("Добавление нового человека");
        stage.getIcons().add(logo);
        stage.show();
    }

    public void addListener(NewPersonListener evtListener) {
        this.listener = evtListener;
    }
}
