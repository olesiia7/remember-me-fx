package controllers;

import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import utils.KeepDataHelper;

public class WatchDuplicatePersonController extends DefaultPersonController {
    private final Person person;


    public WatchDuplicatePersonController(Person person, KeepDataHelper dataHelper, Image logo) {
        super(dataHelper, logo);
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
