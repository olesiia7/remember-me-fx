package controllers;

import entities.Person;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import utils.KeepDataHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WatchPeopleController extends DefaultPersonController {
    private final int watchTimeMs;
    private final List<Person> people;
    private int i = 0;
    private Timer timer;


    public WatchPeopleController(KeepDataHelper dataHelper, int watchTimeMs, List<Person> people, Image logo) {
        super(dataHelper, logo);
        this.watchTimeMs = watchTimeMs;
        this.people = people;
    }

    @Override
    @FXML
    void initialize() {
        setAllFieldsDisabled();

        HBox hBox = new HBox();
        Button retryButton = new Button("Начать сначала");
        retryButton.setOnAction(action -> start());

        Button cancelButton = new Button("Назад");
        cancelButton.setOnAction(action -> {
            timer.cancel();
            getStage().close();
        });
        hBox.getChildren().addAll(retryButton, cancelButton);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10));
        gridPane.add(hBox, 0, 3, 2, 1);
        start();
    }

    private void start() {
        i = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setNewPerson();
                    }
                });
            }
        }, 0, watchTimeMs);
    }

    public void setNewPerson() {
        if (i >= people.size()) {
            timer.cancel();
            System.out.println("опять");
            return;
        }
        Person person = people.get(i);
        // заполняем данными человека
        name.setText(person.getName());
        events.setText(String.join(",", person.getEvents()));
        company.setText(person.getCompany());
        role.setText(person.getRole());
        description.setText(person.getDescription());
        gridPane.setPrefHeight(gridPane.getPrefHeight() + 50);
        imageHBox.getChildren().clear();
        fillPersonImages(person, false);
        gridPane.requestLayout();
        i++;
    }

    @FXML
    public void getSuggestions() {}
}
