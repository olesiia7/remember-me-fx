package tables;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Events implements Table {
    private final Connection conn;

    public static final String id = "id";
    public static final String name = "name";

    public Events(Connection conn) {
        this.conn = conn;
    }

    public String getId() {
        return getTableName() + "." + id;
    }

    public String getName() {
        return getTableName() + "." + name;
    }

    @Override
    public String getTableName() {
        return "events";
    }

    @Override
    public void createTableIfNotExist() {
        String SQL = "create table if not exists " + getTableName() + " (" +
                Events.id + " integer constraint event_pk primary key autoincrement, " +
                Events.name + " text not null);";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу мероприятия человека
     *
     * @param newEvents названия мероприятий, которые нужно добавить
     */
    // ToDo: написать массовую вставку мероприятий
    public Set<Integer> addPersonEventsAndGetIds(Set<String> newEvents) {
        String SQL;
        Set<Integer> eventsIds = new HashSet<>();
        for (String event : newEvents) {
            SQL = "INSERT INTO " + getTableName() + " (" + name + ") " +
                    "VALUES (" + getFieldWithQuote(event) + ");";
            // проверка, есть ли уже существующий event с таким именем
            Integer eventId = checkEventExistAndGetId(event);
            if (eventId != null) {
                eventsIds.add(eventId);
                continue;
            }
            try (Statement ps = conn.createStatement()) {
                ps.execute(SQL);
                ps.close();
                eventsIds.add(getEventId(event));
            } catch (SQLException e) {
                printSQLError(SQL, e);
            }
        }
        return eventsIds;
    }

    /**
     * Возвращает id существуюшего мероприятия или создает новое
     *
     * @param eventName имя мероприятия
     * @return id мероприятия
     */
    public Integer addEventAndGetId(String eventName) {
        // проверка, есть ли уже существующий event с таким именем
        Integer eventId = checkEventExistAndGetId(eventName);
        if (eventId != null) {
            return eventId;
        }
        String SQL = "INSERT INTO " + getTableName() + " (" + name + ") " +
                "VALUES ('" + eventName + "');";
        executeSQL(conn, SQL);
        return getEventId(eventName);
    }

    /**
     * @return весь список существующих мероприятий
     */
    public List<String> getAllEventNames() {
        List<String> allEvents = new ArrayList<>();
        String SQL = "Select " + name + " from " + getTableName() + " order by " + name + ";";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                allEvents.add(resultSet.getString(name));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return allEvents;
    }

    /**
     * @return весь список существующих мероприятий
     */
    public Map<Integer, String> getAllEvents() {
        Map<Integer, String> allEvents = new HashMap<>();
        String SQL = "Select * from " + getTableName() + " order by " + name + ";";
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                allEvents.put(resultSet.getInt(id), resultSet.getString(name));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return allEvents;
    }

    private Integer checkEventExistAndGetId(String eventName) {
        String SQL = "Select " + id + " from " + getTableName() + " where " + name + "='" + eventName + "';";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            Integer eventId = null;
            if (resultSet.next()) {
                eventId = resultSet.getInt(id);
            }
            statement.close();
            resultSet.close();
            return eventId;
        } catch (SQLException e) {
            printSQLError(SQL, e);
            return null;
        }
    }


    /**
     * @param eventName название мероприятия
     * @return id только что созданного мероприятия
     */
    private Integer getEventId(@NonNull String eventName) {
        String SQL = "SELECT " + id + " FROM " + getTableName() +
                " WHERE " + name + "='" + eventName + "';";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            int eventId = resultSet.getInt(id);
            resultSet.close();
            statement.close();
            return eventId;
        } catch (SQLException e) {
            printSQLError(SQL, e);
            return null;
        }
    }

    /**
     * @param eventId id события, которое нужно удалить
     */
    public void deleteEvent(int eventId) {
        String SQL = "DELETE FROM " + getTableName() +
                " WHERE " + id + "=" + eventId + ";";
        executeSQL(conn, SQL);
    }

    /**
     * Переименовывает мероприятие
     *
     * @param eventId      id события
     * @param newEventName новое имя мероприятия
     */
    public void setNewEventName(int eventId, String newEventName) {
        String SQL = "UPDATE " + getTableName() +
                " SET " + name + "='" + newEventName + "'" +
                " WHERE " + id + "=" + eventId + ";";
        executeSQL(conn, SQL);
    }
}
