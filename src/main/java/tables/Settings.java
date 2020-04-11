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

    public Settings(Connection conn) {
        this.conn = conn;
    }

    public String getDataPathField() {
        return getTableName() + "." + dataPath;
    }

    public String getAnswerTimeMsField() {
        return getTableName() + "." + answerTimeMs;
    }

    public String getWatchTimeMsField() {
        return getTableName() + "." + watchTimeMs;
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
                watchTimeMs + " int default 1000);");
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
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
    }

    /**
     * Добавляет в таблицу время в мс показа человека в режиме просмотра
     */
    public void setWatchTimeMs(int watchTimeMsValue) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                watchTimeMs + "='" + watchTimeMsValue + "' " + " WHERE " + id + "=0;";
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

    /**
     * Добавляет в таблицу время в мс показа правильного ответа в режиме самопроверки
     */
    public void setAnswerTimeMs(int answerTimeMsValue) {
        String SQL = "UPDATE " + getTableName() + " SET " +
                answerTimeMsValue + "='" + answerTimeMsValue + "' " + " WHERE " + id + "=0;";
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
     * @return абсолютный путь хранения данных
     */
    public String getDataPath() {
        String SQL = "SELECT " + dataPath + " FROM " + getTableName() +
                " WHERE " + id + "=0;";
        String actualDataPath = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            actualDataPath = resultSet.getString(dataPath);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return actualDataPath;
    }

    /**
     * @return время в мс для показа правильного ответа в режиме самопроверки
     */
    public int getAnswerTimeMs() {
        String SQL = "SELECT " + answerTimeMs + " FROM " + getTableName() +
                " WHERE " + id + "=0;";
        int actualAnswerTimeMs = 1000;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            actualAnswerTimeMs = resultSet.getInt(answerTimeMs);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return actualAnswerTimeMs;
    }

    /**
     * @return время в мс для показа человека в режиме быстрого просмотра
     */
    public int getWatchTimeMs() {
        String SQL = "SELECT " + watchTimeMs + " FROM " + getTableName() +
                " WHERE " + id + "=0;";
        int actualWatchTimeMs = 1000;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL);
            actualWatchTimeMs = resultSet.getInt(watchTimeMs);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return actualWatchTimeMs;
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
            System.out.println("Ошибка при исполнении SQL:");
            System.out.println(SQL);
            e.printStackTrace();
        }
        return settings;
    }
}
