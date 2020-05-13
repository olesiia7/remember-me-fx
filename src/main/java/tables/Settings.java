package tables;

import entities.SettingsProfile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Settings implements Table {
    private final Connection conn;

    public static final String id = "id";
    public static final String dataPath = "dataPath";
    public static final String answerTimeMs = "answerTimeMs";
    public static final String watchTimeMs = "watchTimeMs";
    public static final String dataShowInControl = "dataShowInControl";

    /**
     * определяет, какое время из настроек нужно получить
     */
    public enum SettingsTime {
        // время в мс для показа человека в режиме быстрого просмотра
        WATCH_TIME,
        // время в мс для показа правильного ответа в режиме самопроверки
        ANSWER_TIME
    }

    public Settings(Connection conn) {
        this.conn = conn;
    }

    @Override
    public String getTableName() {
        return "settings";
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("create table if not exists " + getTableName() + " (" +
                "id int default 0, " +
                dataPath + " text default null, " +
                answerTimeMs + " int default 1000, " +
                watchTimeMs + " int default 1000, " +
                dataShowInControl + " int default 'ФИО');");
        statement.close();
        statement = conn.createStatement();
        statement.execute("insert into " + getTableName() + " DEFAULT VALUES;");
        statement.close();
    }


    /**
     * Добавляет в таблицу настройки
     */
    public void setSettings(SettingsProfile settings) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                dataPath + "='" + settings.getDataPath() + "'," +
                answerTimeMs + "=" + settings.getAnswerTimeMs() + "," +
                watchTimeMs + "=" + settings.getWatchTimeMs() + " WHERE " + id + "=0;";
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQL);
            statement.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
    }

    /**
     * Добавляет в таблицу время в мс показа человека в режиме просмотра
     */
    public void setWatchTimeMs(int watchTimeMsValue) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                watchTimeMs + "='" + watchTimeMsValue + "' " + " WHERE " + id + "=0;";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу время в мс показа правильного ответа в режиме самопроверки
     */
    public void setAnswerTimeMs(int answerTimeMsValue) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                answerTimeMs + "='" + answerTimeMsValue + "' " + " WHERE " + id + "=0;";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу настройки сохранения
     */
    public void setDataPath(String path) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                dataPath + "='" + path + "' " + " WHERE " + id + "=0;";
        executeSQL(conn, SQL);
    }

    /**
     * Добавляет в таблицу настройки показа полей в режиме самопроверки
     */
    public void setDataShowInControl(String fields) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                dataShowInControl + "='" + fields + "' " + " WHERE " + id + "=0;";
        executeSQL(conn, SQL);
    }

    /**
     * @return абсолютный путь хранения данных
     */
    public String getDataPath() {
        return getStringSetting(dataPath);
    }

    /**
     * @return настройки показа полей в режиме самопроверких
     */
    public String getDataShowInControl() {
        return getStringSetting(dataShowInControl);
    }

    private String getStringSetting(String column) {
        String SQL = "SELECT " + column + " FROM " + getTableName() +
                " WHERE " + id + "=0;";
        String settingValue = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            settingValue = resultSet.getString(column);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return settingValue;
    }

    /**
     * @return время в мс в зависимости от режима
     */
    public int getSettingsTimeMs(SettingsTime settingsTime) {
        String timeColumn = "";
        switch (settingsTime) {
            case WATCH_TIME:
                timeColumn = watchTimeMs;
                break;
            case ANSWER_TIME:
                timeColumn = answerTimeMs;
            default:
                System.out.println("Неверный выбор времени");
        }
        String SQL = "SELECT " + timeColumn + " FROM " + getTableName() +
                " WHERE " + id + "=0;";
        int actualAnswerTimeMs = 1000;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            actualAnswerTimeMs = resultSet.getInt(timeColumn);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return actualAnswerTimeMs;
    }

    /**
     * @return все настройки
     */
    public SettingsProfile getAllSettings() {
        String SQL = "SELECT * FROM " + getTableName() + " WHERE " + id + "=0;";
        SettingsProfile settings = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);

            String path = resultSet.getString(dataPath);
            int answerTime = resultSet.getInt(answerTimeMs);
            int watchTime = resultSet.getInt(watchTimeMs);
            settings = new SettingsProfile(path, answerTime, watchTime);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            printSQLError(SQL, e);
        }
        return settings;
    }
}
