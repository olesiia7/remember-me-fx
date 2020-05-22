package controllers;

import entities.Person;
import javafx.fxml.FXML;
import utils.KeepDataHelper;

public class WatchDuplicatePersonController extends DefaultPersonController {
    private final Person person;


    public WatchDuplicatePersonController(Person person, KeepDataHelper dataHelper) {
        super(dataHelper);
        this.person = person;
    }

    @Override
    @FXML
    void initialize() {
        setAllFieldsDisabled();
        name.setText(person.getName());
        events.setText(String.join(",", person.getEvents()));
        company.setText(person.getCompany());
        role.setText(person.getRole());
        description.setText(person.getDescription());
        gridPane.setPrefHeight(gridPane.getPrefHeight() + 50);
        imageHBox.getChildren().clear();
        fillPersonImages(person, false);
        gridPane.requestLayout();
    }

    @FXML
    public void getSuggestions() {}
}
