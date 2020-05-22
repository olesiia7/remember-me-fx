package utils;

import entities.Person;
import entities.PersonDif;
import entities.SettingsProfile;
import lombok.NonNull;
import tables.Events;
import tables.EventsAndPeople;
import tables.People;
import tables.Pictures;
import tables.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static tables.People.getPeopleFromResultSet;
import static tables.Settings.SettingsTime.ANSWER_TIME;
import static tables.Settings.SettingsTime.WATCH_TIME;
import static utils.FileUtils.deleteFiles;

@SuppressWarnings("JavaDoc")
public class KeepDataHelper {
    private final Connection conn;
    private final People peopleTable;
    private final Pictures picturesTable;
    private final Events eventsTable;
    private final EventsAndPeople eventsAndPeopleTable;
    private final Settings settingsTable;

    public KeepDataHelper(String sqlPath) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + sqlPath + "/RememberMe.db");
        peopleTable = new People(conn);
        picturesTable = new Pictures(conn, peopleTable);
        eventsTable = new Events(conn);
        eventsAndPeopleTable = new EventsAndPeople(conn, peopleTable, eventsTable);
        settingsTable = new Settings(conn);
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
            person.setPictures(picturesTable.getPersonPictures(person.getId(), settingsTable.getDataPath()));
            person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
        }
        return allPeople;
    }

    public Set<Integer> getPeopleWithEvents(Set<Integer> eventsIds) {
        return eventsAndPeopleTable.getPeopleWithEvents(eventsIds);
    }

    public boolean isPersonWithNameExist(String name) {
        return peopleTable.isPersonWithNameExist(name);
    }

    /**
     * Возвращает человека с таким же ФИО
     *
     * @param personName имя искомого человека
     * @return Person, если уже существует, null - если нет
     */
    public Person getPersonWithName(String personName) {
        List<Person> people = peopleTable.getPersonWithName(personName);
        if (people.size() == 0) {
            return null;
        }
        Person person = people.get(0);
        person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
        person.setPictures(picturesTable.getPersonPictures(person.getId(), getDataPath()));
        return person;
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
        settingsTable.createTableIfNotExist();
    }

    /**
     * Помечает человека запомненным/нет
     *
     * @param personId     id человека
     * @param isRemembered запомнен или нет
     */
    public void setPersonRemembered(int personId, boolean isRemembered) {
        peopleTable.setPersonRemembered(personId, isRemembered);
    }

    /**
     * Помечает всеъ людей незапомненными
     */
    public void setAllPeopleNotRemembered() {
        peopleTable.setAllPeopleNotRemembered();
    }

    public SettingsProfile getSettings() {
        return settingsTable.getAllSettings();
    }

    public String getDataPath() {
        return settingsTable.getDataPath();
    }

    /**
     * @return настройки показа полей в режиме самопроверких
     * (строка с настройками через запятую_
     */
    public String getDataShowInControl() {
        return settingsTable.getDataShowInControl();
    }

    /**
     * Добавляет в таблицу настройки показа полей в режиме самопроверки
     */
    public void setDataShowInControl(String fields) {
        settingsTable.setDataShowInControl(fields);
    }

    /**
     * @return время в мс для показа правильного ответа в режиме самопроверки
     */
    public int getAnswerTimeMs() {
        return settingsTable.getSettingsTimeMs(ANSWER_TIME);
    }

    /**
     * @return время в мс для показа человека в режиме быстрого просмотра
     */
    public int getWatchTimeMs() {
        return settingsTable.getSettingsTimeMs(WATCH_TIME);
    }

    /**
     * @param absolutePath абсолютный путь к папке, в которой нужно хранить ресурсы
     */
    public void setDataPathSetting(String absolutePath) {
        settingsTable.setDataPath(absolutePath);
    }

    /**
     * @param watchTimeMs время в мс показа человека в режиме просмотра
     */
    public void setWatchTimeMsSetting(int watchTimeMs) {
        settingsTable.setWatchTimeMs(watchTimeMs);
    }

    /**
     * @param answerTimeMs время в мс показа правильного ответа в режиме самопроверки
     */
    public void setAnswerTimeMsSetting(int answerTimeMs) {
        settingsTable.setAnswerTimeMs(answerTimeMs);
    }

    public void setSettings(SettingsProfile settings) {
        settingsTable.setSettings(settings);
    }

    /**
     * @return список названий всех мероприятий
     */
    public List<String> getAllEventNames() {
        return eventsTable.getAllEventNames();
    }

    /**
     * @return id и названия всех мероприятий
     */
    public Map<Integer, String> getAllEvents() {
        return eventsTable.getAllEvents();
    }

    /**
     * @return количество участников мероприятия
     */
    public int getCountEventsByEventId(int eventId) {
        return eventsAndPeopleTable.getCountEventsByEventId(eventId);
    }

    /**
     * @param eventId id события, которое нужно удалить
     */
    public void deleteEvent(int eventId) {
        eventsTable.deleteEvent(eventId);
    }

    /**
     * @param eventIds id мероприйтий, которые нужно удалить
     */
    public void deleteEvents(Set<Integer> eventIds) {
        eventsTable.deleteEvents(eventIds);
    }

    /**
     * Переименовывает мероприятие
     *
     * @param eventId      id события
     * @param newEventName новое имя мероприятия
     */
    public void setEventName(int eventId, String newEventName) {
        eventsTable.setNewEventName(eventId, newEventName);
    }

    /**
     * Удаляет событие из выбранных пользователей
     *
     * @param eventId      id события, которое нужно удалить
     * @param participants id людей, у которых надо удалить событие
     */
    public void deleteEventsFromPeople(int eventId, List<Integer> participants) {
        eventsAndPeopleTable.deleteEventsFromPeople(eventId, participants);
    }

    /**
     * Удаляет участников из мероприятия
     *
     * @param eventIds id события, которое нужно удалить
     */
    public void deleteEventsParticipants(Set<Integer> eventIds) {
        eventsAndPeopleTable.deleteEventsParticipants(eventIds);
    }

    /**
     * @param id id чедлвека
     * @return список мероприятий пользователя
     */
    public Set<String> getPersonEvents(int id) {
        return eventsAndPeopleTable.getPersonEvents(id);
    }

    /**
     * @return список всех мест работ
     */
    @NonNull
    public List<String> getAllCompanies() {
        return peopleTable.getAllCompanies();
    }

    /**
     * @param events           список мероприятий, должно быть совпадение хотя бы по одному
     * @param companies        список мест работ, должно быть совпадение хотя бы по одному
     * @param unRememberedOnly выбрать только не запомненных
     * @return список людей, кто имеет совпадения и по мероприятиям, и по компаниям
     */
    public List<Person> getPeopleByCriteria(@NonNull List<String> events, @NonNull List<String> companies,
                                            boolean unRememberedOnly)
    {
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
                whereExist = true;
            }
            sqlBuilder.append(People.company).append(" in(");
            sqlBuilder.append(companies.stream().map(company -> "'" + company + "'").collect(Collectors.joining(","))).append(")");
        }
        if (unRememberedOnly) {
            if (whereExist) {
                sqlBuilder.append(" and ");
            } else {
                sqlBuilder.append(" where ");
            }
            sqlBuilder.append(People.remembered).append("=false");
        }
        sqlBuilder.append(";");
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
            people.addAll(getPeopleFromResultSet(resultSet));
            for (Person person : people) {
                person.setPictures(picturesTable.getPersonPictures(person.getId(), settingsTable.getDataPath()));
                person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(sqlBuilder.toString());
            e.printStackTrace();
        }
        return people;
    }

    public List<Person> getPeopleByCriteria(@NonNull List<String> events, @NonNull List<String> companies) {
        return getPeopleByCriteria(events, companies, false);
    }

    /**
     * @param eventId event id, которого не должно быть у людей
     * @return список людей, которые не состоят в данном мероприятии
     */
    public List<Person> getPeopleNotInEvent(int eventId) {
        StringBuilder sqlBuilder = new StringBuilder();
        eventsAndPeopleTable.getPeopleNotHaveEventSQL(sqlBuilder, eventId);
        List<Person> people = peopleTable.getPeopleById(sqlBuilder.toString());
        for (Person person : people) {
            // получаем изображения по каждому человеку
            person.setPictures(picturesTable.getPersonPictures(person.getId(), settingsTable.getDataPath()));
            person.setEvents(eventsAndPeopleTable.getPersonEvents(person.getId()));
        }
        return people;
    }

    /**
     * Удаляет все возможные таблицы
     */
    private void dropAllTables() {
        eventsAndPeopleTable.dropTable(conn);
        eventsTable.dropTable(conn);
        picturesTable.dropTable(conn);
        peopleTable.dropTable(conn);
        settingsTable.dropTable(conn);
    }

    /**
     * Массово добавляет людей в БД и возвращает их id
     *
     * @param people список новых людей, которые необходимо вставить в БД
     * @return список id только что созданных людей
     */
    @NonNull
    public void savePeople(@NonNull Person... people) {
        for (Person person : people) {
            savePerson(person);
        }
    }

    /**
     * Массово добавляет людей в БД и возвращает их id
     *
     * @param people список новых людей, которые необходимо вставить в БД
     * @return список id только что созданных людей
     */
    @NonNull
    public void savePeople(@NonNull List<Person> people) {
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
    public void savePerson(@NonNull Person person) {
        // сохранение в таблицу человека
        peopleTable.savePersonAndGetId(person);
        // сохранение в таблицу картинок человека
        picturesTable.setPersonPictures(person.getId(), person.getPictures());
        Set<Integer> eventIds = eventsTable.addPersonEventsAndGetIds(person.getEvents());
        eventsAndPeopleTable.addPersonEvents(person.getId(), eventIds);
    }


    /**
     * Ищет человека в БД по всем полям, кроме id и картинок
     *
     * @param person данные человека (id и картинки не учитываются)
     * @return true, если человек существует, false в противном случае,
     * null - в случае ошибки
     */
    public Boolean isPersonExist(@NonNull Person person) {
        return peopleTable.isPersonExist(person);
    }

    /**
     * Возвращает id существуюшего мероприятия или создает новое
     *
     * @param eventName название мероприятия
     * @return id мероприятия
     */
    public int createEventAndGetId(String eventName) {
        return eventsTable.addEventAndGetId(eventName);
    }

    /**
     * Добавляет человека в указанные мероприятия
     *
     * @param personId id человека, которого нужно добавить в мероприятия
     * @param eventIds список id мероприятий
     */
    public void addEventsToPerson(int personId, Set<Integer> eventIds) {
        eventsAndPeopleTable.addPersonEvents(personId, eventIds);
    }

    /**
     * Добавляет в мероприятие указанных людей
     *
     * @param eventId   id мероприятия
     * @param peopleIds список id людей, которых надо добавить в мероприятие
     */
    public void addPeopleToEvent(int eventId, Set<Integer> peopleIds) {
        eventsAndPeopleTable.addPeopleToEvent(eventId, peopleIds);
    }

    /**
     * Добавляет человека в указанные мероприятия
     *
     * @param personId id человека, которого нужно добавить в мероприятия
     * @param eventId  id мероприятия
     */
    public void addEventsToPerson(int personId, Integer eventId) {
        HashSet<Integer> eventsIds = new HashSet<>();
        eventsIds.add(eventId);
        eventsAndPeopleTable.addPersonEvents(personId, eventsIds);
    }

    /**
     * Перезаписывает данные пользователя
     *
     * @param personDif изменения человека
     */
    public void updatePerson(@NonNull PersonDif personDif) {
        // обновление данных человека
        if (personDif.isNameChanged()
                || personDif.isCompanyChanged()
                || personDif.isRoleChanged()
                || personDif.isDescriptionChanged())
        {
            peopleTable.updatePerson(personDif);
        }
        // обновление картинок
        if (personDif.isPicturesChanged()) {
            int personId = personDif.getId();
            deletePeoplePictures(singletonList(personId));
            picturesTable.updatePersonPictures(personId, personDif.getPictures());
        }
        // обновление мероприятий
        if (personDif.isEventsChanged()) {
            int personId = personDif.getId();
            Set<String> existPersonEvents = eventsAndPeopleTable.getPersonEvents(personId);
            Set<String> actualEvents = personDif.getEvents();
            // удаление ненужных мероприятий
            Set<String> redundantEvents = existPersonEvents.stream()
                    .filter(event -> !actualEvents.contains(event))
                    .collect(Collectors.toSet());
            eventsAndPeopleTable.deletePersonEvents(redundantEvents, personId);
            // добавление новых мероприятий
            Set<String> newEvents = actualEvents.stream()
                    .filter(event -> !existPersonEvents.contains(event))
                    .collect(Collectors.toSet());
            Set<Integer> eventIds = eventsTable.addPersonEventsAndGetIds(newEvents);
            eventsAndPeopleTable.addPersonEvents(personId, eventIds);
        }
    }

    public List<String> getAllPictures() {
        return picturesTable.getAllPictures(settingsTable.getDataPath());
    }

    /**
     * Удаляет человека из таблиц, удаляет изображения
     *
     * @return
     */
    public void deletePeople(@NonNull List<Integer> ids) {
        deletePeopleButLeaveEvent(ids);
        eventsAndPeopleTable.deleteUnusedEvents();
    }

    /**
     * Удаляет людей, но оставляет мероприятие, даже если в нем нет участников
     */
    public void deletePeopleButLeaveEvent(@NonNull List<Integer> peopleIds) {
        deletePeoplePictures(peopleIds);
        peopleTable.deletePerson(peopleIds);
    }

    /**
     * @param ids id людей, картинки которых надо удалить с компьютера
     */
    public void deletePeoplePictures(@NonNull List<Integer> ids) {
        List<String> uninstallImages = new ArrayList<>();
        for (int id : ids) {
            List<String> picturesName = picturesTable.getPersonPictures(id, getDataPath());
            uninstallImages.addAll(picturesName);
        }
        deleteFiles(uninstallImages);
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
