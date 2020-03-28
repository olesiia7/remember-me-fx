import controllers.CreateNewPersonController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static utils.FileHelper.getTruePathURL;

public class CreatePersonWindow {

    public CreatePersonWindow(KeepDataHelper dataHelper, Stage ownerStage) throws HeadlessException, IOException {
        try {
            List<String> allEvents = new ArrayList<>(dataHelper.getAllEvents());
        } catch (SQLException e) {
            System.out.println("Ошибка чтения списка мероприятий: " + e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader();
        Pane content = loader.load(requireNonNull(getTruePathURL("src/main/layouts/NewPerson.fxml")).openStream());
        CreateNewPersonController createNewPersonController = loader.getController();
        createNewPersonController.setDataHelper(dataHelper);
        BorderPane root = new BorderPane();
        root.setCenter(content);
        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Добавление нового человека");
        stage.show();
    }
}
