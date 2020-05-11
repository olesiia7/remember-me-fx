package graber;

import entities.Person;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jsoup.Jsoup.connect;
import static utils.AlertUtils.showErrorAlert;
import static utils.AlertUtils.showInformationAlert;
import static utils.FileUtils.createLogFile;

/**
 * Общий класс для грабберов
 */
public abstract class Grabber {
    protected final String baseURL;
    protected static final List<Person> people = new ArrayList<>();
    protected static final List<String> errorLog = new ArrayList<>();

    public Grabber(String baseURL, String logPath) {
        this.baseURL = baseURL;
        showInformationAlert("Предупреждение",
                "Сейчас начнется загрузка участников, пожалуйста, подождите несколько минут");
        people.clear();
        startParsing();
        if (!errorLog.isEmpty()) {
            showErrorAlert("В ходе выполнения было получено несколько ошибок, " +
                    "вы можете ознакомиться с ними в файле " + logPath + "\\grabberLogs.txt");
            try {
                createLogFile(errorLog, logPath + "/grabberLogs.txt");
            } catch (IOException e) {
                showErrorAlert("Ошибка при сохранении файла с логами: " + e.getMessage());
            }
        }
    }

    /**
     * Начало парсинга информации
     */
    protected abstract void startParsing();

    /**
     * Подключается к сайту
     *
     * @param URL url сайта, откуда надо брать информацию
     * @return содержание страницы или null
     */
    protected Document connectToSite(String URL) {
        try {
            return connect(URL).get();
        } catch (IOException e) {
            showErrorAlert("Ошибка перехода по ссылке " + URL);
            return null;
        }
    }

    /**
     * Вытаскивает из веб-элемента информацию о человеке
     *
     * @param item элемент, содержащий информацию о человеке
     */
    protected abstract void getItemInfo(Element item);

    /**
     * Выцепляет картинку с сайта и сохраняет ее
     *
     * @param photoElm   елемент, содержащий ссылку на фото
     * @param personName ФИО
     * @return относительный путь сохраненной картинки (или null)
     */
    protected abstract String getImage(Element photoElm, String personName);

    /**
     * @param text строку, которую надо проверить
     * @return true, если в строка == null или в ней ничего нет
     */
    protected boolean isStringNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Сохраняет человека в список (не в БД)
     *
     * @param name  имя человека
     * @param photo путь к фото человека
     */
    protected void addPerson(String name, String photo) {
        // если нет имени, не сохраняем
        if (isStringNullOrEmpty(name)) {
            return;
        }
        Set<String> events = new HashSet<>();
        people.add(new Person(name, events, "", "", "",
                Collections.singletonList(photo)));
    }

    /**
     * Загружает изображение по src и сохранят на компьютер по указанному пути
     *
     * @param src        source картинки
     * @param personName ФИО человека
     * @param path       относительный путь сохранения файла
     * @return относительный путь сохранения файла
     */
    protected boolean getAndSaveImage(String src, String personName, String path) {
        String imageURL = baseURL + src;
        try {
            Connection.Response resultImageResponse = connect(imageURL).ignoreContentType(true).execute();
            File file = new File(path);
            FileOutputStream out = (new FileOutputStream(file));
            out.write(resultImageResponse.bodyAsBytes());
            out.close();
            return true;
        } catch (IOException e) {
            errorLog.add("Ошибка при сохранении изображения для " + personName +
                    " (" + imageURL + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * @return список людей, которых удалось загрузить
     */
    public List<Person> getPeople() {
        return people;
    }
}
