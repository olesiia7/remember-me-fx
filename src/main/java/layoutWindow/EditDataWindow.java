package layoutWindow;

import controllers.EditDataController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class EditDataWindow {
    public EditDataWindow(KeepDataHelper dataHelper, Stage ownerStage, Image logo) throws HeadlessException, IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        EditDataController controller = new EditDataController(dataHelper, stage, logo);
        loader.setController(controller);
        TabPane content = loader.load(requireNonNull(new File("src/main/layouts/EditData.fxml").toURI().toURL().openStream()));

        stage.initOwner(ownerStage);
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Редактирование записей");
        stage.getIcons().add(logo);
        stage.show();
    }
}
