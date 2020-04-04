package controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
            imageHBox.getChildren().remove(parent);
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
    Stage getStage() {
        return (Stage) gridPane.getScene().getWindow();
    }
}