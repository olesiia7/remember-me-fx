package controllers;

import entities.Person;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import javax.imageio.ImageIO;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static utils.FileUtils.getPictureFromClipboard;

public class CreateNewPersonController {
    private KeepDataHelper dataHelper;
    private List<String> allEvents;

    public ScrollPane scrollPane;
    public TextField name;
    public TextField events;
    public ContextMenu eventsSuggester;
    public TextField company;
    public TextField role;
    public TextArea description;

    public Button setImage;
    public Button setTestData;

    public Button cancel;
    public HBox hBox;

    private NewUserListener listener;
    public void setDataHelper(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    public void setEventsList(List<String> allEvents) {
        this.allEvents = allEvents;
    }

    /**
     * Закрывает окно
     */
    public void cancel() {
        getStage().close();
    }

    /**
     * При сохранении записывает в БД нового человека
     */
    public void save() {
        List<String> imgPaths = saveImagesToComputer();
        Set<String> eventsSet = new HashSet<>(Arrays.asList((events.getText().trim().split(","))));
        Person person = new Person(name.getText().trim(), eventsSet,
                company.getText().trim(), role.getText().trim(), description.getText().trim(), imgPaths);
        try {
            dataHelper.savePerson(person);
            // уведомить, что создан новый человек
            if (listener != null) {
                listener.newUserHasBeenCreated();
            }
        } catch (SQLException e) {
            System.out.println("Ошибки при записи в файл");
            e.printStackTrace();
        }
        getStage().close();
    }

    /**
     * Заполняет тестовую информацию
     */
    public void setTestData() {
        name.setText("ФИО");
        events.setText("Мероприятие1,Мероприятие2");
        company.setText("Компания");
        role.setText("Должность");
        description.setText("Описание долгое :;\"\", описание ...");
        setSampleImages();
    }

    /**
     * Добавляет тестовые изображения
     */
    private void setSampleImages() {
        for (int i = 1; i < 3; i++) {
            try {
                Image image = new Image(new FileInputStream("src/main/resources/sample" + i + ".jpg"));
                VBox imageLayout = getImageLayout(image);
                hBox.getChildren().addAll(imageLayout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Добавляет изображение из буфера обмена
     */
    public void addImage() {
        try {
            Image image = SwingFXUtils.toFXImage(getPictureFromClipboard(), null);
            VBox imageLayout = getImageLayout(image);
            hBox.getChildren().addAll(imageLayout);
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println("вы выбрали не картинку!");
        }
    }

    /**
     * @param image изображение, которое следует поместить в layout
     * @return возвращает готовый layout с картинкой (картинка + кнопка "Удалить")
     */
    private VBox getImageLayout(Image image) {
        VBox imageLayout = new VBox();
        imageLayout.setSpacing(5);
        imageLayout.setPadding(new Insets(10, 5, 10, 5));
        ImageView imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.setImage(image);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        // открыть изображение при нажатии на него
        imageView.addEventHandler(MOUSE_CLICKED, event -> {
            event.consume();
            ImageView imageView1 = (ImageView) event.getSource();
            Image image1 = imageView1.getImage();
            ImageView fullScreenImageView = new ImageView(image1);
            StackPane pane = new StackPane();
            pane.getChildren().add(fullScreenImageView);
            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });

        Button deleteImageButton = new Button("Удалить");
        deleteImageButton.setOnAction(actionEvent -> {
            Button source = (Button) actionEvent.getSource();
            VBox parent = (VBox) source.getParent();
            hBox.getChildren().remove(parent);
            System.out.println("Удалить " + parent);
        });
        double realWidth = imageView.boundsInLocalProperty().getValue().getWidth();
        deleteImageButton.setMaxWidth(realWidth);

        imageLayout.getChildren().addAll(imageView, deleteImageButton);
        return imageLayout;
    }

    /**
     * @return сцену (окно)
     */
    private Stage getStage() {
        return (Stage) cancel.getScene().getWindow();
    }

    /**
     * Сохраняет выбранные изображения на компьютер
     *
     * @return список <b><i>относительных</i></b> путей созхранения изображений
     */
    // ToDo: Брать основной путь файла из настроек (чтобы при переносе всё сработало)
    public List<String> saveImagesToComputer() {
        List<String> paths = new ArrayList<>();
        String uuid = UUID.randomUUID().toString();
        File filePath = new File("src/main/java/resources");
        //noinspection ResultOfMethodCallIgnored
        filePath.mkdir();
        int i = 0;
        List<Image> chosenImages = hBox.getChildren().stream()
                .map(object -> {
                    VBox vBox = (VBox) object;
                    ImageView imageView = (ImageView) vBox.getChildren().get(0);
                    return imageView.getImage();
                })
                .collect(Collectors.toList());
        for (Image image : chosenImages) {
            File file = new File(filePath + "/" + uuid + "_" + i++ + ".png");
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                ImageIO.write(fromFXImage(image, null), "png", file);
                paths.add(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * Добавляет автозаполнение к Мероприятиям
     */
    public void getSuggestions() {
        eventsSuggester.hide();
        eventsSuggester.getItems().clear();
        String text = events.getText();
        if (text == null || text.trim().isEmpty() || text.endsWith(",")) {
            return;
        }
        List<String> existFields = new ArrayList<>();
        String textToSearch;
        // если елемент не единственный - взять последний
        if (text.contains(",")) {
            textToSearch = text.substring(text.lastIndexOf(",") + 1);
            existFields.addAll(Arrays.asList(text.split(",")));
            existFields.remove(textToSearch);
        } else {
            textToSearch = text;
        }
        List<String> possibleEvents = allEvents.stream()
                .filter(event -> event.toUpperCase().contains(textToSearch.toUpperCase()))
                .filter(event -> !existFields.contains(event))
                .filter(event -> !event.equals(textToSearch))
                .collect(Collectors.toList());
        if (!possibleEvents.isEmpty()) {
            for (String possibleEvent : possibleEvents) {
                Label entryLabel = new Label();
                entryLabel.setGraphic(buildTextFlow(possibleEvent, textToSearch));
                entryLabel.setPrefHeight(10);  //don't sure why it's changed with "graphic"
                CustomMenuItem item = new CustomMenuItem(entryLabel, true);
                item.setOnAction(action -> {
                    existFields.add(possibleEvent);
                    events.setText(String.join(",", existFields));
                    eventsSuggester.hide();
                    events.positionCaret(events.getText().length());
                });
                eventsSuggester.getItems().add(item);
                eventsSuggester.show(events, Side.BOTTOM, 0, 0);
            }
        }
    }

    /**
     * Раскрашивает в тексте места, где text совпадает с filter
     *
     * @param text   текст, в котором надо раскрасить
     * @param filter символны, которые надо раскрасить
     * @return раскрашенный текст
     */
    private static TextFlow buildTextFlow(String text, String filter) {
        int filterIndex = text.toLowerCase().indexOf(filter.toLowerCase());
        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + filter.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + filter.length())); //instead of "filter" to keep all "case sensitive"
        textFilter.setFill(Color.ORANGE);
        textFilter.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
        return new TextFlow(textBefore, textFilter, textAfter);
    }

    public void addNewPersonListener(NewUserListener evtListener) {
        this.listener = evtListener;
    }
}
