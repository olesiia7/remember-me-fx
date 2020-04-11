package listeners;

/**
 * Слушатель, если изменились мероприятия
 */
public interface EventsUpdatedListener extends DefaultListener {
    void eventUpdated();
}
