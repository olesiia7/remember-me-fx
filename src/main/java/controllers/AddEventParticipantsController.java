package controllers;

import com.google.common.collect.ImmutableList;
import entities.EventInfo;
import entities.Person;
import entities.PersonWithSelected;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import listeners.EventsUpdatedListener;
import utils.KeepDataHelper;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static utils.AlertUtils.showInformationAlert;

public class AddEventParticipantsController extends DefaultEventController {

    public AddEventParticipantsController(KeepDataHelper dataHelper, EventInfo eventInfo, Stage ownStage, Image logo) {
        super(dataHelper, eventInfo, ownStage, logo);
    }

    @Override
    protected void refreshData(boolean requestPeople) {
        List<PersonWithSelected> peopleWithSelected;
        if (requestPeople) {
            List<Person> people = dataHelper.getPeopleNotInEvent(eventInfo.getId());
            peopleWithSelected = setSelectionForPeople(people);
        } else {
            peopleWithSelected = ImmutableList.copyOf(peopleData);
        }
        setDataToTable(peopleWithSelected);
        tablePeople.requestLayout();
    }

    @FXML
    void selectAll() {
        peopleData.forEach(person -> person.setSelected(true));
        refreshData(false);
    }

    @FXML
    void unselectAll() {
        peopleData.forEach(person -> person.setSelected(false));
        refreshData(false);
    }

    private List<Person> getSelectedPeople() {
        return peopleData.stream()
                .filter(PersonWithSelected::isSelected)
                .map(person -> (Person) person)
                .collect(Collectors.toList());
    }

    public void addListener(EventsUpdatedListener evtListener) {
        this.listener = evtListener;
    }

    @FXML
    void cancel() {
        ownStage.close();
    }

    @FXML
    void saveNewParticipants() {
        List<Person> selectedPeople = getSelectedPeople();
        if (selectedPeople.isEmpty()) {
            showInformationAlert("Предупреждение", "Вы не выбрали ни одного человека для добавления.");
            ownStage.close();
        }
        HashSet<Integer> eventId = new HashSet<>();
        eventId.add(eventInfo.getId());
        for (Person selectedPerson : selectedPeople) {
            dataHelper.addEventsToPerson(selectedPerson.getId(), eventId);
        }
        // добавить людей
        listener.eventUpdated();
        ownStage.close();
    }
}

