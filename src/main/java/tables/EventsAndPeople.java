package tables;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tables.Table.appendWithDelimiter;

public class EventsAndPeople implements Table {
    private final Connection conn;
    private final People peopleTable;
    private final Events eventsTable;

    public static final String personIdField = "person_id";
    public static final String eventIdField = "event_id";

    public EventsAndPeople(Connection conn, People peopleTable, Events eventsTable) {
        this.conn = conn;
        this.peopleTable = peopleTable;
        this.eventsTable = eventsTable;
    }

    @Override
    public String getTableName() {
        return "events_and_people";
    }

    public String getEventIdField() {
        return getTableName() + "." + eventIdField;
    }

    public String getPersonIdField() {
        return getTableName() + "." + personIdField;
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("create table if not exists " + getTableName() + " (" +
                personIdField + " integer not null, " +
                eventIdField + " integer not null, " +
                "foreign key (" + personIdField + ") references " + peopleTable.getTableName() + "(" + People.id + ") ON DELETE CASCADE " +
                "foreign key (" + eventIdField + ") references " + eventsTable.getTableName() + "(" + Events.id + ") " +
                "ON DELETE CASCADE);");
        statement.close();
    }

    /**
     * Добавляет в таблицу мероприятия человека
     *
     * @param personId id человека
     * @param eventIds список id ероприятий
     * @throws SQLException
     */
    // ToDo: написать массовую вставку мероприятий
    public void addPersonEvents(int personId, Set<Integer> eventIds) throws SQLException {
        if (eventIds.isEmpty()) {
            return;
        }
        String SQL = "INSERT INTO " + getTableName() + " (" + getFieldNamesWithoutId() + ") " +
                "VALUES (?, ?);";
        for (int eventId : eventIds) {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setInt(1, personId);
            ps.setInt(2, eventId);
            ps.execute();
            ps.close();
        }
    }

    /**
     * @param personId id человека
     * @return список мероприятий человека
     */
    public Set<String> getPeopleEvents(List<Integer> personId) {
        Set<String> events = new HashSet<>();
        StringBuilder SQL = new StringBuilder();
        SQL.append("select distinct ").append(eventsTable.getName())
                .append(" from ").append(getTableName())
                .append(" INNER JOIN ").append(eventsTable.getTableName()).append(" ON ")
                .append(eventsTable.getId())
                .append("=").append(getEventIdField())
                .append(" where ")
                .append(personIdField).append(" in (");
        try {
            SQL.append(personId.stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
            PreparedStatement ps = conn.prepareStatement(SQL.toString());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                events.add(resultSet.getString(Events.name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Set<String> getPersonEvents(int personId) {
        return getPeopleEvents(Collections.singletonList(personId));
    }

    /**
     * Добавляет в sqlBuilder запрос, возвращающий список id людей, у которых
     * есть хоть одно мероприятие
     *
     * @param sqlBuilder sqlBuilder, в который дополнится запрос
     * @param events     список мероприятий, в которых должен быть человек
     */
    public void getPeopleByEventsSQL(StringBuilder sqlBuilder, @NonNull List<String> events) {
        sqlBuilder.append("select distinct ").append(getPersonIdField()).append(" from ").append(eventsTable.getTableName())
                .append(" INNER JOIN ").append(getTableName()).append(" ON ")
                .append(eventsTable.getId()).append("=").append(getEventIdField());
        if (!events.isEmpty()) {
            sqlBuilder.append(" where " + Events.name + " in (");
            sqlBuilder.append(events.stream().map(event -> "'" + event + "'").collect(Collectors.joining(","))).append(")");
        }
    }

    public static String getFieldNamesWithoutId() {
        return personIdField + appendWithDelimiter(eventIdField);
    }

    /**
     * Удаляет мероприятия, которых нет в таблице EventsAndPeople
     */
    public void deleteUnusedEvents() {
        String SQL = "delete from " + eventsTable.getTableName() + " where " + Events.id + " in (SELECT " + Events.id +
                " FROM " + eventsTable.getTableName() + " LEFT JOIN " + getTableName() +
                " ON " + eventsTable.getId() + "=" + getEventIdField() + " WHERE " + getEventIdField() + " is null);";
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQL);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
    }

    /**
     * @param eventsToDelete мероприятия, которые надо удалить из данного пользователя
     */
    public void deletePersonEvents(@NonNull Set<String> eventsToDelete, int personId) {
        if (eventsToDelete.isEmpty()) {
            return;
        }
        String SQL = "delete from " + getTableName() +
                " where " + eventIdField +
                " in (SELECT " + eventsTable.getId() + " FROM " + eventsTable.getTableName() +
                " WHERE " + Events.name +
                " in('" + String.join("','", eventsToDelete) + "')) " +
                "AND " + personIdField + "=" + personId + ";";
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQL);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        deleteUnusedEvents();
    }
}
