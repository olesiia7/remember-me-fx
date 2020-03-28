package utils;

import entities.Person;
import lombok.NonNull;
import tables.Events;
import tables.EventsAndPeople;
import tables.People;
import tables.Pictures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("JavaDoc")
public class KeepDataHelper {
    private final Connection conn;
    private final People peopleTable;
    private final Pictures picturesTable;
    private final Events eventsTable;
    private final EventsAndPeople eventsAndPeopleTable;

    public KeepDataHelper() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:src/main/java/resources/RememberMe.db");
        peopleTable = new People(conn);
        picturesTable = new Pictures(conn, peopleTable);
        eventsTable = new Events(conn);
        eventsAndPeopleTable = new EventsAndPeople(conn, peopleTable, eventsTable);
    }

    /**
     * Создает таблицы, если они не существуют, и берет из них данные
     *
     * @return список людей, записанных в БД
     * @throws SQLException
     */
    @NonNull
    public List<Person> getSavedPeople() throws SQLException {
        // Тестовое
//         dropAllTables();
        peopleTable.createTableIfNotExist();
        picturesTable.createTableIfNotExist();
        eventsTable.createTableIfNotExist();
        eventsAndPeopleTable.createTableIfNotExist();

        List<Person> allPeople = peopleTable.getAllPeople();
        for (Person person : allPeople) {
            // получаем изображения по каждому человеку
            person.setPictures(picturesTable.getPersonPictures(person.getId()));
            person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
            System.out.println(person);
            System.out.println(" ----- ");
        }
        return allPeople;
    }

    public List<String> getAllEvents() throws SQLException {
        return eventsTable.getAllEvents();
    }

    /**
     * Удаляет все возможные таблицы
     *
     * @throws SQLException
     */
    private void dropAllTables() throws SQLException {
        peopleTable.dropTable(conn);
        picturesTable.dropTable(conn);
        eventsTable.dropTable(conn);
        eventsAndPeopleTable.dropTable(conn);
    }

    /**
     * Массово добавляет людей в БД и возвращает их id
     *
     * @param people список новых людей, которые необходимо вставить в БД
     * @return список id только что созданных людей
     * @throws SQLException
     */
    //ToDo: сделать массовую вставку (сейчас вставка по одному)
    @NonNull
    private List<Integer> insertNewPerson(@NonNull List<Person> people) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        for (Person person : people) {
            savePersonAndGetId(person);
        }
        return ids;
    }

    /**
     * Сохраняет новые данные в БД
     *
     * @param person данные, которые необходимо сохранить
     * @throws SQLException
     */
    public void savePersonAndGetId(@NonNull Person person) throws SQLException {
        // сохранение в таблицу человека
        peopleTable.savePersonAndGetId(person);
        // сохранение в таблицу картинок человека
        picturesTable.setPersonPictures(person);
        Set<Integer> eventIds = eventsTable.addPersonEventsAndGetIds(person);
        eventsAndPeopleTable.addPersonEvents(person.getId(), eventIds);
    }

    /**
     * Закрывает соединение
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        if (!conn.isClosed()) {
            conn.close();
        }
    }
}
