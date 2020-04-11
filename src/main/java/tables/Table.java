package tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface Table {

    /**
     * @return имя таблицы
     */
    String getTableName();

    /**
     * Создает таблицу, если ее нет
     *
     * @throws SQLException sql exception
     */
    void createTableIfNotExist() throws SQLException;

    /**
     * Удаляет таблицу, если существует
     *
     * @param conn connection
     */
    default void dropTable(Connection conn) {
        executeSQL(conn, "drop table if exists " + getTableName() + ";");
    }

    static String appendWithDelimiter(String s) {
        return "," + s;
    }

    default void executeSQL(Connection conn, String SQL) {
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
}
