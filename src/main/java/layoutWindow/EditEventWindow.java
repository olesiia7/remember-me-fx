package layoutWindow;

import controllers.EditEventController;
import entities.EventInfo;
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

import static java.util.Objects.requireNonNull;

public class EditEventWindow {
    private PersonUpdatedListener listener;

    public EditEventWindow(KeepDataHelper dataHelper, Stage ownerStage, EventInfo eventInfo, Image logo) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        EditEventController controller = new EditEventController(dataHelper, eventInfo, stage, logo);
        loader.setController(controller);
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/EditEvent.fxml").toURI().toURL()).openStream());
        controller.addListener(() -> {
            if (listener != null) listener.personUpdated();
        });

        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(content.getPrefWidth());
        stage.setMinHeight(content.getPrefHeight());
        stage.setTitle("Редактирование мероприятия");
        stage.getIcons().add(logo);
        stage.show();
    }

    public void addListener(PersonUpdatedListener listener) {
        this.listener = listener;
    }
}
