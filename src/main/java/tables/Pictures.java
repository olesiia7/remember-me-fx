package tables;

import entities.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static tables.Table.appendWithDelimiter;

public class Pictures implements Table {
    private final Connection conn;
    private final People peopleTable;

    public static final String id = "id";
    public static final String path = "path";
    public static final String person_id = "person_id";

    public Pictures(Connection conn, People peopleTable) {
        this.conn = conn;
        this.peopleTable = peopleTable;
    }

    @Override
    public String getTableName() {
        return "pictures";
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        String SQL = "create table if not exists " + getTableName() + " (" +
                Pictures.id + " integer constraint picture_pk primary key autoincrement, " +
                Pictures.path + " text not null, " +
                Pictures.person_id + " integer not null, " +
                "foreign key (" + Pictures.person_id + ")" +
                " references " + peopleTable.getTableName() + "(" + People.id + ")" +
                " ON DELETE CASCADE ON UPDATE CASCADE);";
        statement.execute(SQL);
        statement.close();
    }

    /**
     * Добавляет в таблицу картинки человека
     *
     * @param person человек с выставленным id
     * @throws SQLException
     */
    // ToDo: написать массовую вставку изображений
    public void setPersonPictures(Person person) throws SQLException {
        List<String> pictures = person.getPictures();
        if (pictures == null || pictures.isEmpty()) {
            return;
        }
        String SQL = "INSERT INTO " + getTableName() + " (" + getFieldNamesWithoutId() + ") " +
                "VALUES (?, ?);";
        for (String picture : pictures) {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, picture);
            ps.setInt(2, person.getId());
            ps.execute();
            ps.close();
        }
    }

    /**
     * Удаляет старые и добавляет новые записи о изображениях пользователя
     *
     * @param person человек с выставленным id
     * @throws SQLException
     */
    public void updatePersonPictures(Person person) throws SQLException {
        deletePersonPictures(person.getId());
        setPersonPictures(person);
    }

    /**
     * Удаляет картики пользователя (действует каскадное удаление при удалении пользователя,
     * эта функция только для update людей)
     */
    private void deletePersonPictures(int personId) {
        String SQL = "delete from " + getTableName() + " where " + person_id + " = " + personId + ";";
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

    public List<String> getPersonPictures(int personId) {
        List<String> pictures = new ArrayList<>();
        String SQL = "select " + path + " from " + getTableName() + " where " + person_id + "=?";
        try {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setInt(1, personId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                pictures.add(resultSet.getString(path));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    /**
     * @return список всех картинок пользователей
     */
    public List<String> getAllPictures() {
        List<String> pictures = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + getTableName() + ";");
            while (resultSet.next()) {
                pictures.add(resultSet.getString(path));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    public static String getFieldNamesWithoutId() {
        return path +
                appendWithDelimiter(person_id);
    }
}
