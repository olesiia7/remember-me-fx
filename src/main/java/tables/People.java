package tables;

import entities.Person;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static tables.Table.appendWithDelimiter;

public class People implements Table {
    private final Connection conn;
    public static final String id = "id";
    public static final String name = "name";
    public static final String events = "events";
    public static final String company = "company";
    public static final String role = "role";
    public static final String description = "description";
    public static final String remembered = "remembered";

    public People(Connection conn) {
        this.conn = conn;
    }

    @Override
    public String getTableName() {
        return "people";
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("create table if not exists " + getTableName() + "(" +
                People.id + " integer constraint people_pk primary key autoincrement, " +
                People.name + " text, " +
                People.events + " text, " +
                People.company + " text, " +
                People.role + " text, " +
                People.description + " text, " +
                People.remembered + " boolean default false);");
        statement.close();
    }

    /**
     * Сохраняет человека в таблицу и возвращает его id
     *
     * @param person человек, которого нужно сохранить
     * @throws SQLException
     */
    public void savePersonAndGetId(Person person) throws SQLException {
        String SQL = "INSERT INTO " + getTableName() + " (" + getFieldNamesWithoutIdAndRemembered() + ") " +
                "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(SQL);
        setPersonInfo(person, ps);
        ps.execute();
        ps.close();
        setPersonId(person);
    }

    /**
     * @return всех людей из таблицы
     * @throws SQLException
     */
    public List<Person> getAllPeople() throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet peopleResultSet = statement.executeQuery("Select * from " + getTableName() + ";");
        List<Person> people = new ArrayList<>();
        while (peopleResultSet.next()) {
            Person person = getPersonFromResultSet(peopleResultSet);
            people.add(person);
        }
        statement.close();
        peopleResultSet.close();
        return people;
    }

    /**
     * Возвращает Person из ResultSet
     *
     * @param rs ResultSet, из которого надо вытянуть данные
     * @return данные о человеке из таблицы
     * @throws SQLException
     */
    @NonNull
    private Person getPersonFromResultSet(@NonNull ResultSet rs) throws SQLException {
        int id = rs.getInt(People.id);
        String name = rs.getString(People.name);
        String company = rs.getString(People.company);
        String role = rs.getString(People.role);
        String description = rs.getString(People.description);
        boolean remembered = rs.getBoolean(People.remembered);
        return new Person(id, name, null, company, role, description, null, remembered);
    }

    /**
     * Вставляет данные в готовый PreparedStatement
     *
     * @param person person, который нужно вставить
     * @param ps     PreparedStatement, куда нужно вставить данные
     * @throws SQLException
     */
    private static void setPersonInfo(@NonNull Person person, @NonNull PreparedStatement ps) throws SQLException {
        ps.setString(1, person.getName());
        String events = String.join(",", person.getEvents());
        ps.setString(2, events);
        ps.setString(3, person.getCompany());
        ps.setString(4, person.getRole());
        ps.setString(5, person.getDescription());
    }

    /**
     * Достает id только что созданного человека и сохраняет его
     *
     * @param person данные, по которым нужно найти id
     * @throws SQLException
     */
    private void setPersonId(@NonNull Person person) throws SQLException {
        String SQL = "SELECT " + id + " FROM " + getTableName() +
                " WHERE " + name + "=? AND " + events + "=? AND " + company + "=? " +
                "AND " + role + "=? AND " + description + "=?;";
        PreparedStatement ps = conn.prepareStatement(SQL);
        setPersonInfo(person, ps);
        ResultSet resultSet = ps.executeQuery();
        int id = resultSet.getInt(People.id);
        person.setId(id);
        ps.close();
        resultSet.close();
    }

    public String getFieldNamesWithoutIdAndRemembered() {
        return name +
                appendWithDelimiter(events) +
                appendWithDelimiter(company) +
                appendWithDelimiter(role) +
                appendWithDelimiter(description);
    }
}
