import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.KeepDataHelper;

public class Menu extends Application {
    private final KeepDataHelper dataHelper;

    public Menu(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 400, 400);
        stage.setTitle("Remember me");
        stage.setScene(scene);
        stage.show();
    }
}
