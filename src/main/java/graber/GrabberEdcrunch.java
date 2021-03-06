package graber;

import entities.Person;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrabberEdcrunch extends Grabber {

    public GrabberEdcrunch(int year, String dataPath, String eventName) {
        super("https://" + year + ".edcrunch.ru", dataPath, eventName);
    }

    @Override
    protected void startParsing() {
        String URL = baseURL + "/speakers/?SHOWALL_1=1";
        Document doc = connectToSite(URL);
        if (doc == null) {
            return;
        }
        List<Element> elements = doc.getElementsByClass("speakers-grid__item-list").get(0).children();
        Set<String> notFound = new HashSet<>();
        for (Element element : elements) {
            switch (element.className()) {
                case "speakers-grid__block":
                case "speakers-grid__item":
                case "speakers-grid__item  is-big is-right":
                case "speakers-grid__item  is-big":
                    getItemInfo(element);
                    break;
                case "speakers-grid__block is-four is-right":
                case "speakers-grid__block is-four":
                    for (Element item : element.getElementsByClass("speakers-grid__item ")) {
                        getItemInfo(item);
                    }
                    getItemInfo(element);
                    break;
                case "pagination":
                case "is-hide":
                    break;
                default:
                    notFound.add(element.className());
                    break;
            }
        }
//        if (!notFound.isEmpty()) {
            errorLog.add("Неизвестные теги: " + notFound);
//        }
    }

    @Override
    protected void getItemInfo(Element item) {
        String name = item.getElementsByClass("speakers-grid__item-name").get(0).text();
        if (isStringNullOrEmpty(name)) {
            errorLog.add("Не удалось получить имя человека, class 'speakers-grid__item-name'");
            return;
        }
        Element photoElm = item.getElementsByClass("speakers-grid__item-photo").get(0);
        String image = getAndSaveImage(photoElm, name);

        String description = item.getElementsByClass("speakers-grid__item-position").get(0).text();
        Set<String> events = new HashSet<>();
        people.add(new Person(name, events, "", "", description, Collections.singletonList(image)));
    }
}
