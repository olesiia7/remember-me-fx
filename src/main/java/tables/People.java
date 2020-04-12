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
import java.util.Set;
import java.util.stream.Collectors;

import static tables.Table.appendWithDelimiter;

public class People implements Table {
    private final Connection conn;
    public static final String id = "id";
    public static final String name = "name";
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
    public void createTableIfNotExist() {
        String SQL = "create table if not exists " + getTableName() + "(" +
                People.id + " integer constraint people_pk primary key autoincrement, " +
                People.name + " text, " +
                People.company + " text, " +
                People.role + " text, " +
                People.description + " text, " +
                People.remembered + " boolean default false);";
        executeSQL(conn, SQL);
    }

    /**
     * Сохраняет человека в таблицу
     *
     * @param person человек, которого нужно сохранить
     * @throws SQLException sql exception
     */
    public void savePersonAndGetId(Person person) throws SQLException {
        String SQL = "INSERT INTO " + getTableName() + " (" + getFieldNamesWithoutIdAndRemembered() + ") " +
                "VALUES (?, ?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(SQL);
        setPersonInfo(person, ps);
        ps.execute();
        ps.close();
        setPersonId(person);
    }

    /**
     * Перезаписывает данные пользователя
     *
     * @param person человек, которого нужно сохранить
     */
    // ToDo: сделать перезапись только измененных данных
    public void updatePerson(Person person) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                name + " = " + getFieldWithQuote(person.getName()) + ", " +
                company + " = " + getFieldWithQuote(person.getCompany()) + ", " +
                role + " = " + getFieldWithQuote(person.getRole()) + ", " +
                description + " = " + getFieldWithQuote(person.getDescription()) +
                " WHERE " + id + " = " + person.getId();
        executeSQL(conn, SQL);
    }

    private String getFieldWithQuote(String s) {
        return "'" + s + "'";
    }

    /**
     * @return всех людей из таблицы
     */
    public List<Person> getAllPeople() {
        List<Person> people = new ArrayList<>();
        String SQL = "Select * from " + getTableName() + ";";
        try {
            Statement statement = conn.createStatement();
            ResultSet peopleResultSet = statement.executeQuery(SQL);
            people.addAll(getPeopleFromResultSet(peopleResultSet));
            statement.close();
            peopleResultSet.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return people;
    }

    public List<Person> getPeopleById(@NonNull Set<Integer> peopleId) {
        List<Person> people = new ArrayList<>();
        if (!peopleId.isEmpty()) {
            StringBuilder SQLBuilder = new StringBuilder("select * from " + getTableName() +
                    " where " + id + " in (");
            SQLBuilder.append(peopleId.stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
            try {
                PreparedStatement statement = conn.prepareStatement(SQLBuilder.toString());
                int i = 1;
                for (Integer id : peopleId) {
                    statement.setInt(i++, id);
                }
                ResultSet peopleResultSet = statement.executeQuery();
                people.addAll(getPeopleFromResultSet(peopleResultSet));
                statement.close();
                peopleResultSet.close();
            } catch (SQLException e) {
                System.out.println("Ошибка при исполнении SQL:");
                System.out.println(SQLBuilder.toString());
                e.printStackTrace();
            }
        }
        return people;
    }

    /**
     * @param idSQL sql, в котором будут получены id
     * @return список людей по выбранным id
     */
    public List<Person> getPeopleById(String idSQL) {
        List<Person> people = new ArrayList<>();
        StringBuilder SQLBuilder = new StringBuilder("select * from " + getTableName() +
                " where " + id + " in (").append(idSQL).append(");");
        try {
            Statement statement = conn.createStatement();
            ResultSet peopleResultSet = statement.executeQuery(SQLBuilder.toString());
            people.addAll(getPeopleFromResultSet(peopleResultSet));
            statement.close();
            peopleResultSet.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQLBuilder.toString());
            e.printStackTrace();
        }
        return people;
    }

    /**
     * Возвращает Person из ResultSet
     *
     * @param rs ResultSet, из которого надо вытянуть данные
     * @return данные о людях из таблицы
     * @throws SQLException sql exception
     */
    @NonNull
    public static List<Person> getPeopleFromResultSet(@NonNull ResultSet rs) throws SQLException {
        List<Person> people = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(People.id);
            String name = rs.getString(People.name);
            String company = rs.getString(People.company);
            String role = rs.getString(People.role);
            String description = rs.getString(People.description);
            boolean remembered = rs.getBoolean(People.remembered);
            Person person = new Person(id, name, null, company, role, description, null, remembered);
            people.add(person);
        }
        return people;
    }

    /**
     * Вставляет данные в готовый PreparedStatement
     *
     * @param person person, который нужно вставить
     * @param ps     PreparedStatement, куда нужно вставить данные
     * @throws SQLException sql exception
     */
    private static void setPersonInfo(@NonNull Person person, @NonNull PreparedStatement ps) throws SQLException {
        ps.setString(1, person.getName());
        ps.setString(2, person.getCompany());
        ps.setString(3, person.getRole());
        ps.setString(4, person.getDescription());
    }

    /**
     * Достает id только что созданного человека и сохраняет его
     *
     * @param person данные, по которым нужно найти id
     * @throws SQLException sql exception
     */
    private void setPersonId(@NonNull Person person) throws SQLException {
        String SQL = "SELECT " + id + " FROM " + getTableName() +
                " WHERE " + name + "=? AND " + company + "=? " +
                "AND " + role + "=? AND " + description + "=?;";
        PreparedStatement ps = conn.prepareStatement(SQL);
        setPersonInfo(person, ps);
        ResultSet resultSet = ps.executeQuery();
        int id = resultSet.getInt(People.id);
        person.setId(id);
        ps.close();
        resultSet.close();
    }

    /**
     * Помечает человека запомненным/нет
     *
     * @param personId     id человека
     * @param isRemembered запомнен или нет
     */
    public void setPersonRemembered(int personId, boolean isRemembered) {
        String SQL = "UPDATE " + getTableName() + " SET " + remembered + "=" + isRemembered +
                " WHERE " + id + "=" + personId + ";";
        executeSQL(conn, SQL);
    }

    /**
     * Помечает всеъ людей незапомненными
     */
    public void setAllPeopleNotRemembered() {
        String SQL = "UPDATE " + getTableName() + " SET " + remembered + "=false;";
        executeSQL(conn, SQL);
    }

    public String getFieldNamesWithoutIdAndRemembered() {
        return name +
                appendWithDelimiter(company) +
                appendWithDelimiter(role) +
                appendWithDelimiter(description);
    }

    @NonNull
    public List<String> getAllCompanies() {
        List<String> companies = new ArrayList<>();
        String SQL = "Select distinct " + company +
                " from " + getTableName() + " order by " + company + ";";
        try {
            Statement statement = conn.createStatement();
            ResultSet companiesResultSet = statement.executeQuery(SQL);
            while (companiesResultSet.next()) {
                companies.add(companiesResultSet.getString(company));
            }
            statement.close();
            companiesResultSet.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return companies;
    }

    public void deletePerson(@NonNull List<Integer> ids) {
        if (ids.isEmpty()) {
            return;
        }
        String sql = "delete from " + getTableName() + " where " + id + " in(" +
                ids.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
        executeSQL(conn, sql);
    }
}
