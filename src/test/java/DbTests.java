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

import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.FileUtils.deleteUnusedFiles;

public class DbTests {
    KeepDataHelper dataHelper;

    @Before
    public void setup() throws SQLException, ClassNotFoundException {
        dataHelper = new KeepDataHelper("src/test/resources/RememberMe.db");
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

        dataHelper.deletePeople(savedPeople.get(0).getId());
        savedPeople = dataHelper.getSavedPeople();
        assertEquals(0, savedPeople.size());
        assertEquals(0, dataHelper.getAllPictures().size());
        for (String path : picPath) {
            File file = new File(path);
            assertFalse(file.exists());
        }
    }

    /**
     * @param name имя человека
     * @return {@link Person}, в котором нет id, events, pictures, isRemembered
     */
    private Person getPersonExample(@NonNull String name) {
        return new Person(name, new HashSet<>(), "Company", "Role", "description", null);
    }
}