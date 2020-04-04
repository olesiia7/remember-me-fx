package listeners;

/**
 * Слушатель, если появился новый пользователь
 */
public interface NewPersonListener extends DefaultListener {
    void newPersonCreated();
}
