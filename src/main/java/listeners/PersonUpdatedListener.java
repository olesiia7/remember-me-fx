package listeners;

/**
 * Слушатель изменений данных человека (обновить информацию, если данные пользователя изменились)
 */
public interface PersonUpdatedListener extends DefaultListener {
    void personUpdated();
}
