import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import layoutWindow.CreatePersonWindow;
import layoutWindow.EditDataWindow;
import utils.KeepDataHelper;

import java.io.IOException;

import static utils.FileUtils.deleteUnusedFiles;

public class InitialFX extends Application {
    public static KeepDataHelper dataHelper;

    public static void main(String[] args) throws Exception {
        dataHelper = new KeepDataHelper();
        dataHelper.createTablesIfNotExists(false);
        // удаление неиспользуемых картинок
        deleteUnusedFiles(dataHelper.getAllPictures(), "src/main/java/resources");
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        VBox vBox = new VBox();
        Button addNewPersonButton = new Button("Добавить человека");
        addNewPersonButton.setOnAction(actionEvent -> {
            try {
                new CreatePersonWindow(dataHelper, stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button editDataButton = new Button("Редактировать записи");
        editDataButton.setOnAction(actionEvent -> {
            try {
                new EditDataWindow(dataHelper, stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        vBox.getChildren().addAll(addNewPersonButton, editDataButton);
        Scene scene = new Scene(vBox);
        stage.setTitle("Remember me");
        stage.setScene(scene);
        stage.show();
    }
}
