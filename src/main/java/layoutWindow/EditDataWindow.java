package layoutWindow;

import controllers.EditDataController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static utils.FileHelper.getTruePathURL;

public class EditDataWindow {
    public EditDataWindow(KeepDataHelper dataHelper, Stage ownerStage) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        TabPane content = loader.load(requireNonNull(getTruePathURL("src/main/layouts/EditData.fxml")).openStream());
        EditDataController editDataController = loader.getController();
        editDataController.setDataHelper(dataHelper);
        editDataController.setDataFirstTime(dataHelper.getSavedPeople());

        BorderPane root = new BorderPane();
        root.setCenter(content);
        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Редактирование записей");
        stage.show();
    }
}
