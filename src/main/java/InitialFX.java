import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import layoutWindow.CreatePersonWindow;
import layoutWindow.EditDataWindow;
import layoutWindow.SettingsWindow;
import utils.KeepDataHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static utils.FileUtils.deleteUnusedFiles;

public class InitialFX extends Application {
    public static KeepDataHelper dataHelper;

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream("src/main/java/resources/config.properties");
        Properties properties = new Properties();
        properties.load(fis);

        File recoursePath = new File(properties.getProperty("data.dir"));
        recoursePath.mkdir();
        String dataAbsolutePath = recoursePath.getAbsolutePath();
        dataHelper = new KeepDataHelper(recoursePath.getPath(), properties);
        boolean recreateTables = Boolean.parseBoolean(properties.getProperty("recreate.tables"));
        dataHelper.createTablesIfNotExists(recreateTables);
        // удаление неиспользуемых картинок
        dataHelper.setDataPathSetting(dataAbsolutePath);
        deleteUnusedFiles(dataHelper.getAllPictures(), recoursePath.getPath());
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
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
        Button settingsButton = new Button("Настройки");
        settingsButton.setOnAction(actionEvent -> {
            try {
                new SettingsWindow(dataHelper, stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        vBox.getChildren().addAll(addNewPersonButton, editDataButton, settingsButton);
        Scene scene = new Scene(vBox);
        stage.setTitle("Remember me");
        stage.setScene(scene);
        stage.show();
    }
}
