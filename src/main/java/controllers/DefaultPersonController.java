package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * Базовый класс для окон создания/редактирования/просмотра/проверки людей
 */
public abstract class DefaultPersonController {
    public GridPane gridPane;
    KeepDataHelper dataHelper;

    public TextField name;
    public TextField events;
    public TextField company;
    public TextField role;
    public TextArea description;
    public HBox imageHBox;

    public DefaultPersonController(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    @FXML
    void initialize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        gridPane.setMaxSize(screenSize.getWidth(), screenSize.getHeight());
    }

    public void setDataHelper(KeepDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    /**
     * @param image изображение, которое следует поместить в layout
     * @return возвращает готовый layout с картинкой (картинка + кнопка "Удалить")
     */
    VBox getImageLayout(Image image) {
        VBox imageLayout = new VBox();
        imageLayout.setSpacing(5);
        imageLayout.setPadding(new Insets(10, 5, 10, 5));
        ImageView imageView = createImageView(image);
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
            imageHBox.getChildren().remove(parent);
            System.out.println("Удалить " + parent);
        });
        double realWidth = imageView.boundsInLocalProperty().getValue().getWidth();
        deleteImageButton.setMaxWidth(realWidth);

        imageLayout.getChildren().addAll(imageView, deleteImageButton);
        return imageLayout;
    }

    VBox getImageLayoutWithOutDelete(Image image) {
        VBox imageLayout = new VBox();
        imageLayout.setSpacing(5);
        imageLayout.setPadding(new Insets(10, 5, 10, 5));
        ImageView imageView = createImageView(image);
        imageLayout.getChildren().addAll(imageView);
        return imageLayout;
    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.setImage(image);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * @return сцену (окно)
     */
    Stage getStage() {
        return (Stage) gridPane.getScene().getWindow();
    }

    public void setAllFieldsDisabled() {
        name.setEditable(false);
        events.setEditable(false);
        company.setEditable(false);
        role.setEditable(false);
        description.setEditable(false);
    }

    public void fillPersonImages(Person person, boolean withDeleteButton) {
        for (String picture : person.getPictures()) {
            try {
                Image image = new Image(new FileInputStream(picture));
                VBox imageLayout;
                if (withDeleteButton) {
                    imageLayout = getImageLayout(image);
                } else {
                    imageLayout = getImageLayoutWithOutDelete(image);
                }
                imageHBox.setAlignment(Pos.CENTER);
                imageLayout.setAlignment(Pos.CENTER);
                imageHBox.getChildren().addAll(imageLayout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
