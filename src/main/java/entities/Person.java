package entities;

import java.util.List;
import java.util.Set;

/**
 * Класс-контейнер, содержащий информацию о человеке
 *
 * @author olesiia7
 * @since 13.05.2020
 */
public class Person {
    int id;
    String name;
    Set<String> events;
    String company;
    String role;
    String description;
    List<String> pictures;
    boolean remembered;

    /**
     * @param id          уникальный id
     * @param name        ФИО
     * @param events      список мероприятий
     * @param company     место работы
     * @param role        должность
     * @param description описание
     * @param pictures    список путей к изображениям
     * @param remembered  запомнен или нет
     */
    public Person(int id, String name, Set<String> events, String company,
                  String role, String description, List<String> pictures, boolean remembered)
    {
        this.id = id;
        this.name = name;
        this.events = events;
        this.company = company;
        this.role = role;
        this.description = description;
        this.pictures = pictures;
        this.remembered = remembered;
    }

    /**
     * @param name        ФИО
     * @param events      список мероприятий
     * @param company     место работы
     * @param role        должность
     * @param description описание
     * @param pictures    список путей к изображениям
     */
    public Person(String name, Set<String> events, String company,
                  String role, String description, List<String> pictures)
    {
        this.name = name;
        this.events = events;
        this.company = company;
        this.role = role;
        this.description = description;
        this.pictures = pictures;
        this.remembered = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getEvents() {
        return events;
    }

    public void setEvents(Set<String> events) {
        this.events = events;
    }

    public String getCompany() {
        return company;
    }

    public Person setCompany(String company) {
        this.company = company;
        return this;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public boolean getRemembered() {
        return remembered;
    }

    public void setRemembered(boolean remembered) {
        this.remembered = remembered;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id = " + id + ",\n" +
                "ФИО: " + name + ",\n" +
                "Мероприятия: " + events + ",\n" +
                "Место работы: " + company + ",\n" +
                "Должность: " + role + ",\n" +
                "Описание: " + description + ",\n" +
                "Изображения: " + pictures + ",\n" +
                "Запомнен: " + remembered + '}';
    }
}
