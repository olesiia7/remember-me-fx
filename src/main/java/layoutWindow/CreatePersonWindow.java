package layoutWindow;

import controllers.CreateNewPersonController;
import listeners.NewPersonListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CreatePersonWindow {
    private NewPersonListener listener;

    public CreatePersonWindow(KeepDataHelper dataHelper, Stage ownerStage) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/NewPerson.fxml").toURI().toURL()).openStream());
        CreateNewPersonController controller = loader.getController();
        controller.setDataHelper(dataHelper);
        // передаем событие - создан новый пользователь
        controller.addListener(() -> {
            if (listener != null) listener.newPersonCreated();
        });
        List<String> allEvents = new ArrayList<>(dataHelper.getAllEvents());
        controller.setEventsList(allEvents);

        Stage stage = new Stage();
        stage.initOwner(ownerStage);

        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(content.getPrefWidth());
        stage.setMinHeight(content.getPrefHeight());
        stage.setTitle("Добавление нового человека");
        stage.show();
    }

    public void addListener(NewPersonListener evtListener) {
        this.listener = evtListener;
    }
}
