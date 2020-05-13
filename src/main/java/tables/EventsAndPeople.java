package tables;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    public void createTableIfNotExist() {
        String SQL = "create table if not exists " + getTableName() + " (" +
                personIdField + " integer not null, " +
                eventIdField + " integer not null, " +
                "foreign key (" + personIdField + ") references " + peopleTable.getTableName() + "(" + People.id + ") ON DELETE CASCADE " +
                "foreign key (" + eventIdField + ") references " + eventsTable.getTableName() + "(" + Events.id + ") " +
                "ON DELETE CASCADE);";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу мероприятия человека
     *
     * @param personId id человека
     * @param eventIds список id ероприятий
     */
    public void addPersonEvents(int personId, Set<Integer> eventIds) {
        if (eventIds.isEmpty()) {
            return;
        }
        // оставляем только те события, которые уже есть
        Set<Integer> notExistEventIds = new HashSet<>();
        for (Integer eventId : eventIds) {
            String SQL = "SELECT * FROM " + getTableName() +
                    " WHERE " + personIdField + "=" + personId + " AND " + eventIdField + "=" + eventId + ";";
            try (Statement statement = conn.createStatement()) {
                ResultSet resultSet = statement.executeQuery(SQL);
                if (!resultSet.next()) {
                    notExistEventIds.add(eventId);
                }
            } catch (SQLException e) {
                printSQLError(SQL, e);
            }
        }
        // если все мероприятия есть, ничего не делать
        if (notExistEventIds.isEmpty()) {
            return;
        }
        StringBuilder SQL = new StringBuilder().append("INSERT INTO ").append(getTableName()).append(" (").append(getFieldNames()).append(") ")
                .append("VALUES ");
        List<String> values = new ArrayList<>();
        for (Integer notExistEventId : notExistEventIds) {
            values.add("(" + personId + "," + notExistEventId + ")");
        }
        SQL.append(String.join(",", values)).append(";");
        executeSQL(conn, SQL.toString());
    }

    /**
     * @param personId id человека
     * @return список мероприятий человека
     */
    public Set<String> getPeopleEvents(List<Integer> personId) {
        Set<String> events = new HashSet<>();
        StringBuilder SQLBuilder = new StringBuilder();
        SQLBuilder.append("select distinct ").append(eventsTable.getName())
                .append(" from ").append(getTableName())
                .append(" INNER JOIN ").append(eventsTable.getTableName()).append(" ON ")
                .append(eventsTable.getId())
                .append("=").append(getEventIdField())
                .append(" where ")
                .append(personIdField).append(" in (");
        SQLBuilder.append(personId.stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        String SQL = SQLBuilder.toString();
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQLBuilder.toString());
            while (resultSet.next()) {
                events.add(resultSet.getString(Events.name));
            }
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return events;
    }

    /**
     * @param eventId id мероприятия
     * @return кол-во участников мероприятия
     */
    public int getCountEventsByEventId(int eventId) {
        int participantsCount = 0;
        StringBuilder SQLBuilder = new StringBuilder();
        SQLBuilder.append("select count(*) from ").append(getTableName())
                .append(" where ")
                .append(eventIdField).append("=").append(eventId).append(";");
        String SQL = SQLBuilder.toString();
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQLBuilder.toString());
            resultSet.next();
            participantsCount = resultSet.getInt("count(*)");
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return participantsCount;
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

    /**
     * Добавляет в sqlBuilder запрос, возвращающий список id людей, которые не состоят в данном мероприятии
     *
     * @param sqlBuilder sqlBuilder, в который дополнится запрос
     * @param eventId    id мероприятия, в котором не должен быть человек
     */
    public void getPeopleNotHaveEventSQL(StringBuilder sqlBuilder, int eventId) {

        sqlBuilder.append("SELECT DISTINCT ").append(getPersonIdField()).append(" from ").append(getTableName())
                .append(" WHERE ").append(personIdField).append(" NOT in(")
                .append("SELECT DISTINCT ").append(getPersonIdField()).append(" from ").append(getTableName())
                .append(" WHERE ").append(eventIdField).append(" IN(").append(eventId).append("))");
    }


    public static String getFieldNames() {
        return personIdField + appendWithDelimiter(eventIdField);
    }

    /**
     * Удаляет мероприятия, которых нет в таблице EventsAndPeople
     */
    public void deleteUnusedEvents() {
        String SQL = "delete from " + eventsTable.getTableName() + " where " + Events.id + " in (SELECT " + Events.id +
                " FROM " + eventsTable.getTableName() + " LEFT JOIN " + getTableName() +
                " ON " + eventsTable.getId() + "=" + getEventIdField() + " WHERE " + getEventIdField() + " is null);";
        executeSQL(conn, SQL);
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
        executeSQL(conn, SQL);
        deleteUnusedEvents();
    }

    /**
     * Удаляет событие из выбранных пользователей
     *
     * @param eventId      id события, которое нужно удалить
     * @param participants id людей, у которых надо удалить событие
     */
    public void deleteEventsFromPeople(int eventId, List<Integer> participants) {
        String partIds = participants.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        String SQL = "DELETE FROM " + getTableName() +
                " WHERE " + eventIdField + "=" + eventId +
                " AND " + personIdField + " in(" + partIds + ");";
        executeSQL(conn, SQL);
    }
}
