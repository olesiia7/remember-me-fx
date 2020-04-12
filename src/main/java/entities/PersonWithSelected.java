package entities;

public class PersonWithSelected extends Person {
    private boolean isSelected;

    public PersonWithSelected(boolean isSelected, Person person) {
        super(person.getId(), person.getName(), person.getEvents(), person.getCompany(), person.getRole(), person.getDescription(),
                person.getPictures(), person.getRemembered());
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}