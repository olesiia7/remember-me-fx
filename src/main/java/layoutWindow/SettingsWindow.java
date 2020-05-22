package layoutWindow;

import controllers.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class SettingsWindow {
    public SettingsWindow(KeepDataHelper dataHelper, Stage ownerStage, Image logo) throws HeadlessException, IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        SettingsController controller = new SettingsController(dataHelper, stage, logo);
        loader.setController(controller);
        VBox content = loader.load(requireNonNull(new File("src/main/layouts/Settings.fxml").toURI().toURL().openStream()));

        stage.initOwner(ownerStage);
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Настройки");
        stage.getIcons().add(logo);
        stage.show();
    }
}
