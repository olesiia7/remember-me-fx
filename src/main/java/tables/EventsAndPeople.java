package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
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
                ");");
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

    public Set<String> getPersonEvents(int personId) throws SQLException {
        Set<String> events = new HashSet<>();
        String SQL = "select " + eventsTable.getTableName() + "." + Events.name + " from " + getTableName()
                + " INNER JOIN " + eventsTable.getTableName() + " ON "
                + eventsTable.getTableName() + "." + Events.id + "=" + getTableName() + "." + eventIdField +
                " where " + personIdField + "=?";
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setInt(1, personId);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            events.add(resultSet.getString(Events.name));
        }
        return events;
    }

    public static String getFieldNamesWithoutId() {
        return personIdField + appendWithDelimiter(eventIdField);
    }
}
