package layoutWindow;

import controllers.PreWatchPeopleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class PreWatchDataWindow {
    public PreWatchDataWindow(KeepDataHelper dataHelper, Stage ownerStage) throws HeadlessException, IOException {
        FXMLLoader loader = new FXMLLoader();
        PreWatchPeopleController controller = new PreWatchPeopleController(dataHelper);
        loader.setController(controller);
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/PreWatchPerson.fxml").toURI().toURL().openStream()));

        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Настройка режима просмотра");
        stage.show();
    }
}
