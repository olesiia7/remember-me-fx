package utils;

import entities.Person;
import lombok.NonNull;
import tables.Events;
import tables.EventsAndPeople;
import tables.People;
import tables.Pictures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tables.People.getPeopleFromResultSet;
import static utils.FileUtils.deleteFiles;

@SuppressWarnings("JavaDoc")
public class KeepDataHelper {
    private final Connection conn;
    private final People peopleTable;
    private final Pictures picturesTable;
    private final Events eventsTable;
    private final EventsAndPeople eventsAndPeopleTable;

    public KeepDataHelper() throws ClassNotFoundException, SQLException {
        this("src/main/java/resources/RememberMe.db");
    }

    public KeepDataHelper(String sqlPath) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + sqlPath);
        peopleTable = new People(conn);
        picturesTable = new Pictures(conn, peopleTable);
        eventsTable = new Events(conn);
        eventsAndPeopleTable = new EventsAndPeople(conn, peopleTable, eventsTable);
        // включаем поддержку сторонних ключей
        Statement statement = conn.createStatement();
        statement.execute("PRAGMA foreign_keys=ON");
        statement.close();
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
            person.setEvents(eventsAndPeopleTable.getPeopleEvents(person.getId()));
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
     * @param id id чедлвека
     * @return список мероприятий пользователя
     */
    public Set<String> getPersonEvents(int id) {
        return eventsAndPeopleTable.getPeopleEvents(id);
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
        List<Person> people = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from ").append(peopleTable.getTableName());
        boolean whereExist = false;
        if (!events.isEmpty()) {
            sqlBuilder.append(" where ").append(People.id).append(" in (");
            eventsAndPeopleTable.getPeopleByEventsSQL(sqlBuilder, events);
            sqlBuilder.append(")");
            whereExist = true;
        }
        if (!companies.isEmpty()) {
            if (whereExist) {
                sqlBuilder.append(" and ");
            } else {
                sqlBuilder.append(" where ");
            }
            sqlBuilder.append(People.company).append(" in(");
            sqlBuilder.append(companies.stream().map(company -> "'" + company + "'").collect(Collectors.joining(","))).append(")");
        }
        sqlBuilder.append(";");
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
            people.addAll(getPeopleFromResultSet(resultSet));
            for (Person person : people) {
                person.setPictures(picturesTable.getPersonPictures(person.getId()));
                person.setEvents(eventsAndPeopleTable.getPeopleEvents(person.getId()));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    /**
     * Удаляет все возможные таблицы
     *
     * @throws SQLException
     */
    private void dropAllTables() throws SQLException {
        eventsAndPeopleTable.dropTable(conn);
        eventsTable.dropTable(conn);
        picturesTable.dropTable(conn);
        peopleTable.dropTable(conn);
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
    public void savePeople(@NonNull Person... people) throws SQLException {
        for (Person person : people) {
            savePerson(person);
        }
    }

    /**
     * Сохраняет новые данные в БД
     *
     * @param person данные, которые необходимо сохранить
     * @throws SQLException
     */
    public void savePerson(@NonNull Person person) throws SQLException {
        // сохранение в таблицу человека
        peopleTable.savePersonAndGetId(person);
        // сохранение в таблицу картинок человека
        picturesTable.setPersonPictures(person);
        Set<Integer> eventIds = eventsTable.addPersonEventsAndGetIds(person);
        eventsAndPeopleTable.addPersonEvents(person.getId(), eventIds);
    }

    public List<String> getAllPictures() {
        return picturesTable.getAllPictures();
    }

    /**
     * Удаляет человека из таблиц, удаляет изображения
     *
     * @return
     */
    public void deletePeople(@NonNull List<Integer> ids) {
        List<String> uninstallImages = new ArrayList<>();
        for (int id : ids) {
            uninstallImages.addAll(picturesTable.getPersonPictures(id));
        }
        deleteFiles(uninstallImages);
        peopleTable.deletePerson(ids);
        eventsAndPeopleTable.deleteUnusedEvents();
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
