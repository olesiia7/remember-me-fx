package entities;

/**
 * Обертка для настроек
 */
public class SettingsProfile {
    private final String dataPath;
    private final int answerTimeMs;
    private final int watchTimeMs;

    public SettingsProfile(String dataPath, int answerTimeMs, int watchTimeMs) {
        this.dataPath = dataPath;
        this.answerTimeMs = answerTimeMs;
        this.watchTimeMs = watchTimeMs;
    }

    public String getDataPath() {
        return dataPath;
    }

    public int getAnswerTimeMs() {
        return answerTimeMs;
    }

    public int getWatchTimeMs() {
        return watchTimeMs;
    }
}
