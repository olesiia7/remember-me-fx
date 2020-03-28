import entities.Person;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import layoutWindow.CreatePersonWindow;
import layoutWindow.EditDataWindow;
import utils.KeepDataHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class InitialFX extends Application {
    private static final List<Person> savedPeople = new ArrayList<>();
    public static KeepDataHelper dataHelper;

    public static void main(String[] args) throws Exception {
        dataHelper = new KeepDataHelper();
        dataHelper.createTablesIfNotExists(false);
        savedPeople.addAll(dataHelper.getSavedPeople());
        // удаление неиспользуемых картинок
        deleteUnusedFiles();
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


    /**
     * Удаляет все файлы, кроме БД и тех, которые есть в БД
     */
    private static void deleteUnusedFiles() {
        List<String> existPic = new ArrayList<>();
        savedPeople.stream()
                .map(Person::getPictures)
                .forEach(existPic::addAll);
        File dir = new File("src/main/java/resources");
        File[] arrFiles = dir.listFiles();
        if (arrFiles != null) {
            List<String> redundantPic = Arrays.stream(arrFiles)
                    .map(File::getPath)
                    .filter(pic -> !existPic.contains(pic) && !pic.matches("[\\w\\W]*.db"))
                    .collect(Collectors.toList());
            for (String path : redundantPic) {
                File file = new File(path);
                file.deleteOnExit();
            }
        }
    }
}
