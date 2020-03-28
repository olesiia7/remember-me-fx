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

//        final ObservableList<String> strings = FXCollections.observableArrayList();
//        for (int i = 0; i <= 100; i++) {
//            strings.add("Item " + i);
//        }
//
//        final CheckComboBox<String> checkComboBox = new CheckComboBox<>(strings);
//        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
//            public void onChanged(ListChangeListener.Change<? extends String> c) {
//                System.out.println(checkComboBox.getCheckModel().getCheckedItems());
//            }
//        });
//
//        vBox.getChildren().add(checkComboBox);
//        stage.show();
