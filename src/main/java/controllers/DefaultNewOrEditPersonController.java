package controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.imageio.ImageIO;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static utils.FileUtils.getPictureFromClipboard;

public abstract class DefaultNewOrEditPersonController extends DefaultPersonController {
    private List<String> allEvents;
    public ContextMenu eventsSuggester;
    public Button setImageButton;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Добавляет кнопки "Добавить изображение", "Сохранить", "Отменить"
     */
    @FXML
    @Override
    void initialize() {
        super.initialize();
        // кнопка добавления изображения
        setImageButton = new Button("Добавить ихображение");
        setImageButton.setOnAction(action -> addImage());
        gridPane.add(setImageButton, 0, 3, 2, 1);
        GridPane.setHalignment(setImageButton, HPos.CENTER);
        GridPane.setValignment(setImageButton, VPos.CENTER);
        GridPane.setMargin(setImageButton, new Insets(10));

        // кнопки сохранить, отмена
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setSpacing(5);
        saveButton = new Button("Сохранить");
        saveButton.setOnAction(action -> save());
        cancelButton = new Button("Отменить");
        cancelButton.setOnAction(action -> cancel());
        GridPane.setMargin(hBox, new Insets(10));
        hBox.getChildren().addAll(saveButton, cancelButton);
        gridPane.add(hBox, 1, 4);
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
     * Необходимо переопределить метод
     */
    public void save() {
        throw new IllegalArgumentException("Необходимо переопределить метод");
    }

    /**
     * Добавляет изображение из буфера обмена
     */
    public void addImage() {
        try {
            Image image = SwingFXUtils.toFXImage(getPictureFromClipboard(), null);
            VBox imageLayout = getImageLayout(image);
            imageHBox.getChildren().addAll(imageLayout);
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println("вы выбрали не картинку!");
        }
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
        List<Image> chosenImages = imageHBox.getChildren().stream()
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
}
