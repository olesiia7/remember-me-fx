package tables;

import entities.Person;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Events implements Table {
    private final Connection conn;

    public static final String id = "id";
    public static final String name = "name";

    public Events(Connection conn) {
        this.conn = conn;
    }

    @Override
    public String getTableName() {
        return "events";
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("create table if not exists " + getTableName() + " (" +
                Events.id + " integer constraint event_pk primary key autoincrement, " +
                Events.name + " text not null);");
        statement.close();
    }

    /**
     * Добавляет в таблицу мероприятия человека
     *
     * @param person человек с выставленным id
     * @throws SQLException
     */
    // ToDo: написать массовую вставку мероприятий
    public Set<Integer> addPersonEventsAndGetIds(Person person) throws SQLException {
        String SQL = "INSERT INTO " + getTableName() + " (" + name + ") " +
                "VALUES (?);";
        Set<Integer> eventsIds = new HashSet<>();
        for (String event : person.getEvents()) {
            // проверка, есть ли уже существующий event с таким именем
            Integer eventId = checkEventExistAndGetId(event);
            if (eventId != null) {
                eventsIds.add(eventId);
                continue;
            }
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, event);
            ps.execute();
            ps.close();
            eventsIds.add(getEventId(event));
        }
        return eventsIds;
    }

    /**
     * @return весь список существующих мероприятий
     */
    public List<String> getAllEvents() {
        List<String> allEvents = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            String SQL = "Select " + name + " from " + getTableName() + " order by " + name + ";";
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                allEvents.add(resultSet.getString(name));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allEvents;
    }

    private Integer checkEventExistAndGetId(String eventName) throws SQLException {
        String SQL = "Select " + id + " from " + getTableName() + " where " + name + "=?;";
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setString(1, eventName);
        ResultSet resultSet = ps.executeQuery();
        Integer eventId = null;
        if (resultSet.next()) {
            eventId = resultSet.getInt(id);
        }
        ps.close();
        resultSet.close();
        return eventId;
    }


    /**
     * @param eventName название мероприятия
     * @return id только что созданного мероприятия
     * @throws SQLException
     */
    private int getEventId(@NonNull String eventName) throws SQLException {
        String SQL = "SELECT " + id + " FROM " + getTableName() +
                " WHERE " + name + "=?;";
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setString(1, eventName);
        ResultSet resultSet = ps.executeQuery();
        int eventId = resultSet.getInt(id);
        resultSet.close();
        ps.close();
        return eventId;
    }
}
