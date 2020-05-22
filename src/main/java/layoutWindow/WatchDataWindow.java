package layoutWindow;

import controllers.WatchPeopleController;
import entities.Person;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class WatchDataWindow {
    public WatchDataWindow(KeepDataHelper dataHelper, Stage ownerStage, int watchTimeMs, List<Person> people, Image logo) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        WatchPeopleController controller = new WatchPeopleController(dataHelper, watchTimeMs, people, logo);
        loader.setController(controller);
        Pane content = loader.load(requireNonNull(new File("src/main/layouts/DefaultPersonView.fxml").toURI().toURL().openStream()));

        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Режим просмотра");
        stage.getIcons().add(logo);
        stage.show();
    }
}
