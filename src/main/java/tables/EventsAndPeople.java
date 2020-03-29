package tables;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("create table if not exists " + getTableName() + " (" +
                personIdField + " integer not null, " +
                eventIdField + " integer not null, " +
                "foreign key (" + personIdField + ") references " + peopleTable.getTableName() + "(" + People.id + ") " +
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
    public Set<String> getPersonEvents(int personId) {
        Set<String> events = new HashSet<>();
        String SQL = "select " + eventsTable.getTableName() + "." + Events.name + " from " + getTableName()
                + " INNER JOIN " + eventsTable.getTableName() + " ON "
                + eventsTable.getTableName() + "." + Events.id + "=" + getTableName() + "." + eventIdField +
                " where " + personIdField + "=?";
        try {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setInt(1, personId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                events.add(resultSet.getString(Events.name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    /**
     * Добавляет в sqlBuilder запрос, возвращающий список id людей, у которых
     * есть хоть одно мероприятие
     *
     * @param sqlBuilder sqlBuilder, в который дополнится запрос
     * @param events     список мероприятий, в которых должен быть человек
     */
    public void getPeopleByEventsSQL(StringBuilder sqlBuilder, @NonNull List<String> events) {
        sqlBuilder.append("select distinct ").append(getTableName()).append(".").append(personIdField).append(" from ").append(eventsTable.getTableName()).append(" INNER JOIN ").append(getTableName()).append(" ON ").append(eventsTable.getTableName()).append(".").append(Events.id).append("=").append(getTableName()).append(".").append(eventIdField);
        if (!events.isEmpty()) {
            sqlBuilder.append(" where " + Events.name + " in (");
            for (String event : events) {
                sqlBuilder.append("'").append(event).append("',");
            }
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1).append(")");
        }
    }

    public static String getFieldNamesWithoutId() {
        return personIdField + appendWithDelimiter(eventIdField);
    }
}
