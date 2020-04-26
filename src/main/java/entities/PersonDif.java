package entities;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Класс-контейнер, содержащий изменившуюся информацию о человеке
 *
 * @author olesiia7
 * @since 13.05.2020
 */
public class PersonDif {
    private int id;
    private String name = null;
    private Set<String> events = null;
    private String company = null;
    private String role = null;
    private String description = null;
    private List<String> pictures = null;

    /**
     * @param person        исходный человек (важно, чтобы был id)
     * @param updatedPerson измененный человек
     * @return изменения человека + id
     */
    public static PersonDif createPersonDif(Person person, Person updatedPerson) {
        PersonDif personDif = new PersonDif();
        personDif.id = person.getId();
        if (!person.getName().equals(updatedPerson.getName())) {
            personDif.name = updatedPerson.getName();
        }
        if (!isCollectionSameWithoutOrder(person.getEvents(), updatedPerson.getEvents())) {
            personDif.events = updatedPerson.getEvents();
        }
        if (!person.getCompany().equals(updatedPerson.getCompany())) {
            personDif.company = updatedPerson.getCompany();
        }
        if (!person.getRole().equals(updatedPerson.getRole())) {
            personDif.role = updatedPerson.getRole();
        }
        if (!person.getDescription().equals(updatedPerson.getDescription())) {
            personDif.description = updatedPerson.getDescription();
        }
        if (!isCollectionSameWithoutOrder(person.getPictures(), updatedPerson.getPictures())) {
            personDif.pictures = updatedPerson.getPictures();
        }
        return personDif;
    }

    /**
     * @param personDif изменения человека
     * @return true, если изменения не пустые и false в противном случае
     */
    public static boolean isPersonChanged(PersonDif personDif) {
        return personDif.isNameChanged()
                || personDif.isEventsChanged()
                || personDif.isCompanyChanged()
                || personDif.isRoleChanged()
                || personDif.isDescriptionChanged()
                || personDif.isPicturesChanged();
    }

    /**
     * @param collection1 первая коллекция
     * @param collection2 вторая коллекция
     * @return true, если коллекции одинаковые, независимо от порядка элементов
     */
    public static boolean isCollectionSameWithoutOrder(Collection<String> collection1, Collection<String> collection2) {
        if (!collection1.containsAll(collection2)) {
            return false;
        }
        return collection2.containsAll(collection1);
    }

    public boolean isNameChanged() {
        return name != null;
    }

    public boolean isEventsChanged() {
        return events != null;
    }

    public boolean isCompanyChanged() {
        return company != null;
    }

    public boolean isRoleChanged() {
        return role != null;
    }

    public boolean isDescriptionChanged() {
        return description != null;
    }

    public boolean isPicturesChanged() {
        return pictures != null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getEvents() {
        return events;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPictures() {
        return pictures;
    }
}
