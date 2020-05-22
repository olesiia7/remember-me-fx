package entities;

public class EventInfoWithSelected extends EventInfo {
    private boolean isSelected;
    private final EventInfo eventInfo;

    public EventInfoWithSelected(boolean isSelected, EventInfo eventInfo) {
        super(eventInfo.getId(), eventInfo.getName(), eventInfo.getParticipantsCount());
        this.eventInfo = eventInfo;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }
}