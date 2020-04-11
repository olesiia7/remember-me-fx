package entities;

/**
 * Обертка для мероприятий. Содержит id мероприяти, название и кол-во участников
 */
public class EventInfo {
    private final int id;
    private final String name;
    private final int participantsCount;

    public EventInfo(int id, String name, int participantsCount) {
        this.id = id;
        this.name = name;
        this.participantsCount = participantsCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParticipantsCount() {
        return "" + participantsCount;
    }
}
