package graber;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class GrabberEduforum extends Grabber {

    public GrabberEduforum(String logPath, String eventName) {
        super("http://eduforum.spb.ru/", logPath, eventName);
    }

    @Override
    protected void startParsing() {
        String URL = "http://eduforum.spb.ru/forum-v-licah/";
        Document doc = connectToSite(URL);
        if (doc == null) {
            return;
        }
        List<Element> elements = doc.getElementsByClass("forum-faces").get(0).children();
        for (Element element : elements) {
            if (element.className().contains("faces-item")) {
                getItemInfo(element);
            }
        }
        elements = doc.getElementsByClass("faces-no-quotes").get(0).children();
        for (Element element : elements) {
            if (element.className().contains("item wow fadeInDownBig")) {
                String name = element.children().get(1).text();
                // если имени нет, то сохранять не нужно
                if (isStringNullOrEmpty(name)) {
                    errorLog.add("Не удалось получить имя человека, class 'item wow fadeInDownBig'");
                    continue;
                }
                String photo = getAndSaveImage(element.children().get(0), name);
                addPerson(name, photo);
            }
        }
    }

    @Override
    protected void getItemInfo(Element item) {
        String name = item.getElementsByClass("quote").get(0)
                .getElementsByClass("quote-inner").get(0)
                .children().get(1).text();
        if (isStringNullOrEmpty(name)) {
            errorLog.add("Не удалось получить имя человека, class 'faces-item'");
            return;
        }
        String photo = getAndSaveImage(item.getElementsByClass("photo").get(0)
                .getElementsByClass("visible-xs").get(0), name);
        addPerson(name, photo);
    }
}
