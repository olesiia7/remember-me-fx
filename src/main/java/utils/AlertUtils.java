package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.NO;
import static javafx.scene.control.ButtonBar.ButtonData.YES;

/**
 * Класс с инструментами для создания аллеров
 */
public class AlertUtils {
    /**
     * Создание сообщения об ошибке
     *
     * @param text текст ошибки
     */
    public static void showErrorAlert(String text) {
        Alert alert = new Alert(ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /**
     * Создание информационного аллерта
     *
     * @param title заголовок
     * @param text  текст сообщения
     */
    public static void showInformationAlert(String title, String text) {
        Alert alert = new Alert(INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /**
     * Создает да - нет - отмена аллерт
     *
     * @param text текст вопроса
     * @return алерт
     */
    public static Alert createConfirmationAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);
        ButtonType okButton = new ButtonType("Да", YES);
        ButtonType noButton = new ButtonType("Нет", NO);
        ButtonType cancelButton = new ButtonType("Отменить", CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        return alert;
    }
}
