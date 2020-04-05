import entities.Person;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;
import utils.KeepDataHelper;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.FileUtils.deleteUnusedFiles;

public class DbTests {
    KeepDataHelper dataHelper;

    @Before
    public void setup() throws SQLException, ClassNotFoundException {
        dataHelper = new KeepDataHelper("src/test/resources");
        dataHelper.createTablesIfNotExists(true);
        // удаление неиспользуемых картинок
        deleteUnusedFiles(EMPTY_LIST, "src/test/resources");
    }

    /**
     * Проверяет, удаляются ли каскадно данные из изображений (и сами файлы изображения) и таблицы people
     */
    @Test
    public void imageCascadeDeleteTest() throws SQLException, IOException {
        Person person = getPersonExample("test1");
        person.setPictures(Arrays.asList("src/test/resources/testImage.png", "src/test/resources/testImage2.png"));
        dataHelper.savePerson(person);
        List<Person> savedPeople = dataHelper.getSavedPeople();
        assertEquals(1, savedPeople.size());
        List<String> picPath = dataHelper.getAllPictures();
        assertEquals(2, picPath.size());
        for (String path : picPath) {
            File file = new File(path);
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            assertTrue(file.exists());
        }

        dataHelper.deletePeople(singletonList(savedPeople.get(0).getId()));
        savedPeople = dataHelper.getSavedPeople();
        assertEquals(0, savedPeople.size());
        assertEquals(0, dataHelper.getAllPictures().size());
        for (String path : picPath) {
            File file = new File(path);
            assertFalse(file.exists());
        }
    }

    /**
     * Проверяет, удаляются ли каскадно данные из мероприятий и таблицы people
     */
    @Test
    public void eventsCascadeDeleteTest() throws SQLException {
        Person deletedPerson = getPersonExample("test1");
        deletedPerson.setEvents(new HashSet<>(Arrays.asList("Мероприятие1", "Мероприятие2")));
        deletedPerson.setPictures(Arrays.asList("src/test/resources/testImage.png", "src/test/resources/testImage2.png"));
        Person existPerson = getPersonExample("test2");
        existPerson.setEvents(new HashSet<>(singletonList("Мероприятие2")));
        existPerson.setPictures(Arrays.asList("src/test/resources/testImage.png", "src/test/resources/testImage2.png"));
        dataHelper.savePeople(deletedPerson, existPerson);

        // проверяем, что пользователи и мероприятия созданы
        List<Person> savedPeople = dataHelper.getSavedPeople();
        assertEquals(2, savedPeople.size());
        List<String> events = dataHelper.getAllEvents();
        assertEquals(2, events.size());

        // проверяем, что остался только 1 пользователь
        int deletedPersonId = deletedPerson.getId();
        int existPersonId = existPerson.getId();
        dataHelper.deletePeople(singletonList(deletedPersonId));
        savedPeople = dataHelper.getSavedPeople();
        assertEquals(1, savedPeople.size());
        assertEquals(existPersonId, savedPeople.get(0).getId());

        Set<String> deletedPersonEvents = dataHelper.getPersonEvents(deletedPersonId);
        assertEquals(0, deletedPersonEvents.size());

        Set<String> existPersonEvents = dataHelper.getPersonEvents(existPersonId);
        assertEquals(1, existPersonEvents.size());
        assertEquals(existPersonEvents, new HashSet<>(singletonList("Мероприятие2")));
        assertEquals(1, dataHelper.getAllEvents().size());
    }

    /**
     * Проверяет, удаляются ли каскадно данные из изображений (и сами файлы изображения) и таблицы people
     */
    @Test
    public void updatePersonTest() throws SQLException {
        Person person = getPersonExample("test1");
        Person person2 = getPersonExample("test2");
        person.setPictures(Arrays.asList("src/test/resources/testImage.png", "src/test/resources/testImage2.png"));
        person.setEvents(new HashSet<>(Arrays.asList("Мероприятие1", "Мероприятие2")));
        person2.setEvents(new HashSet<>(singletonList("Мероприятие1")));
        dataHelper.savePerson(person);
        dataHelper.savePerson(person2);
        List<Person> savedPeople = dataHelper.getSavedPeople();
        assertEquals(2, savedPeople.size());
        person = savedPeople.get(0);
        assertEquals(person.getName(), "test1");

        Person expectedUpdatedPerson = person;
        expectedUpdatedPerson.setName("test3");
        person.setEvents(new HashSet<>(Arrays.asList("Мероприятие2", "Мероприятие3")));
        person.setPictures(Arrays.asList("src/test/resources/testImage3.png", "src/test/resources/testImage4.png"));
        dataHelper.updatePerson(expectedUpdatedPerson);

        savedPeople = dataHelper.getSavedPeople();
        assertEquals(2, savedPeople.size());
        Person updatedPerson = savedPeople.get(0);
        assertEquals(expectedUpdatedPerson.getName(), updatedPerson.getName());
        assertEquals(expectedUpdatedPerson.getPictures(), updatedPerson.getPictures());
        assertEquals(expectedUpdatedPerson.getEvents(), updatedPerson.getEvents());
        List<String> allEvents = dataHelper.getAllEvents();
        assertEquals(3, allEvents.size());
    }

    /**
     * @param name имя человека
     * @return {@link Person}, в котором нет id, events, pictures, isRemembered
     */
    private Person getPersonExample(@NonNull String name) {
        return new Person(name, new HashSet<>(), "Company", "Role", "description\"sdf", null);
    }
}