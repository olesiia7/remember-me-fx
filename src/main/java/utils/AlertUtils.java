package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.ButtonBar.ButtonData.OTHER;

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
        return createCustomAlert(text, "Да", "Нет", "Отменить");
    }

    /**
     * Создает кастомный аллерт с множественным выбором
     *
     * @param text        текст вопроса
     * @param buttonTexts тексты кнопок
     * @return алерт
     */
    public static Alert createCustomAlert(String text, String... buttonTexts) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.getButtonTypes().clear();
        for (String buttonText : buttonTexts) {
            alert.getButtonTypes().add(new ButtonType(buttonText, OTHER));
        }
        return alert;
    }
}
