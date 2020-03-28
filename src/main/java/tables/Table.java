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
     * @throws SQLException
     */
    void createTableIfNotExist() throws SQLException;

    /**
     * Удаляет таблицу, если существует
     *
     * @param conn connection
     * @throws SQLException
     */
    default void dropTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("drop table if exists " + getTableName() + ";");
        statement.close();
    }

    static String appendWithDelimiter(String s) {
        return "," + s;
    }
}
