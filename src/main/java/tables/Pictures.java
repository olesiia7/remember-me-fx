package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void createTableIfNotExist() {
        String SQL = "create table if not exists " + getTableName() + " (" +
                Pictures.id + " integer constraint picture_pk primary key autoincrement, " +
                Pictures.path + " text not null, " +
                Pictures.person_id + " integer not null, " +
                "foreign key (" + Pictures.person_id + ")" +
                " references " + peopleTable.getTableName() + "(" + People.id + ")" +
                " ON DELETE CASCADE ON UPDATE CASCADE);";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу картинки человека
     *
     * @param personId id человека
     * @param pictures список картинок
     */
    public void setPersonPictures(int personId, List<String> pictures) {
        if (pictures == null || pictures.isEmpty()) {
            return;
        }
        StringBuilder SQL = new StringBuilder()
                .append("INSERT INTO ").append(getTableName())
                .append(" (").append(getFieldNamesWithoutId()).append(") ")
                .append("VALUES ");
        boolean haveElements = false;
        for (String picture : pictures) {
            if (haveElements) SQL.append(", ");
            SQL.append("(").append(getFieldWithQuote(picture)).append(",")
                    .append(personId).append(")");
            haveElements = true;
        }
        executeSQL(conn, SQL.toString());
    }

    /**
     * Удаляет старые и добавляет новые записи о изображениях пользователя
     *
     * @param personId id человека
     * @param pictures список картинок
     */
    public void updatePersonPictures(int personId, List<String> pictures) {
        deletePersonPictures(personId);
        setPersonPictures(personId, pictures);
    }

    /**
     * Удаляет картики пользователя (действует каскадное удаление при удалении пользователя,
     * эта функция только для update людей)
     */
    private void deletePersonPictures(int personId) {
        String SQL = "delete from " + getTableName() + " where " + person_id + " = " + personId + ";";
        executeSQL(conn, SQL);
    }

    public List<String> getPersonPictures(int personId, String dataPath) {
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
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return pictures.stream()
                .map(pictureName -> dataPath + "\\" + pictureName)
                .collect(Collectors.toList());
    }

    /**
     * @return список всех картинок пользователей
     */
    public List<String> getAllPictures(String dataPath) {
        List<String> pictures = new ArrayList<>();
        String SQL = "select * from " + getTableName() + ";";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                pictures.add(resultSet.getString(path));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return pictures.stream()
                .map(pictureName -> dataPath + "\\" + pictureName)
                .collect(Collectors.toList());
    }

    public static String getFieldNamesWithoutId() {
        return path +
                appendWithDelimiter(person_id);
    }
}
