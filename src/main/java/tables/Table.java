package tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static utils.AlertUtils.showErrorAlert;

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
        try (Statement statement = conn.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
    }

    /**
     * Выводит ошибку и SQL, который ее вызвал
     *
     * @param SQL SQL запрос
     * @param e   возникшая ошибка
     */
    default void printSQLError(String SQL, SQLException e) {
        String alertText = "Ошибка при исполнении SQL:\n" + SQL +
                "\n" + e.getMessage();
        System.out.println(alertText);
        showErrorAlert(alertText);
    }

    default String getFieldWithQuote(String s) {
        return "'" + s + "'";
    }
}
