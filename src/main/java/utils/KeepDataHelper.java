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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.FileHelper.getTruePath;

@SuppressWarnings("JavaDoc")
public class KeepDataHelper {
    private final Connection conn;
    private final People peopleTable;
    private final Pictures picturesTable;
    private final Events eventsTable;
    private final EventsAndPeople eventsAndPeopleTable;

    public KeepDataHelper() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + getTruePath("src/main/java/resources/RememberMe.db"));
        peopleTable = new People(conn);
        picturesTable = new Pictures(conn, peopleTable);
        eventsTable = new Events(conn);
        eventsAndPeopleTable = new EventsAndPeople(conn, peopleTable, eventsTable);
    }

    /**
     * Берет данные из таблиц
     *
     * @return список людей, записанных в БД
     * @throws SQLException
     */
    @NonNull
    public List<Person> getSavedPeople() {
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

    /**
     * Создает таблицы, если они не существуют
     *
     * @throws SQLException
     */
    public void createTablesIfNotExists(boolean dropAll) throws SQLException {
        if (dropAll) {
            dropAllTables();
        }
        peopleTable.createTableIfNotExist();
        picturesTable.createTableIfNotExist();
        eventsTable.createTableIfNotExist();
        eventsAndPeopleTable.createTableIfNotExist();
    }

    /**
     * @return список всех мероприятий
     */
    public List<String> getAllEvents() {
        return eventsTable.getAllEvents();
    }

    /**
     * @return список всех мест работ
     */
    @NonNull
    public List<String> getAllCompanies() {
        return peopleTable.getAllCompanies();
    }

    /**
     * @param events    список мероприятий, должно быть совпадение хотя бы по одному
     * @param companies список мест работ, должно быть совпадение хотя бы по одному
     * @return список людей, кто имеет совпадения и по мероприятиям, и по компаниям
     */
    public List<Person> getPeopleByCriteria(@NonNull List<String> events, @NonNull List<String> companies) {
        Set<Integer> peopleId = new HashSet<>(eventsAndPeopleTable.getPeopleByEvents(events));
        Set<Integer> peopleByCompanies = peopleTable.getPeopleIdByCompanies(companies);
        peopleId.retainAll(peopleByCompanies);
        List<Person> selectedPeople = peopleTable.getPeopleById(peopleId);
        for (Person person : selectedPeople) {
            person.setPictures(picturesTable.getPersonPictures(person.getId()));
            person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
        }
        return selectedPeople;
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
