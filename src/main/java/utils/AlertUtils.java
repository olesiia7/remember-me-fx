package utils;

import javafx.scene.control.Alert;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class AlertUtils {
    public static void showErrorAlert(String text) {
        Alert alert = new Alert(ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void showInformationAlert(String title, String text) {
        Alert alert = new Alert(INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
