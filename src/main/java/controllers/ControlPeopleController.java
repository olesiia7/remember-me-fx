package controllers;

import entities.Person;
import entities.ShowModeEnum;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.KeepDataHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static utils.AlertUtils.showInformationAlert;

public class ControlPeopleController extends DefaultPersonController {
    private final List<Person> people;
    private final int answerTimeInMs;
    private final Stage stage;
    private static int indexPerson;
    private Timer timer = new Timer();
    private Button markUnrememberedButton;
    private Button rememberedButton;
    private Button mistakeButton;
    private Scene scene;

    public ControlPeopleController(KeepDataHelper dataHelper, List<Person> people, int answerTimeInMs, Stage stage) {
        super(dataHelper);
        this.people = people;
        this.answerTimeInMs = answerTimeInMs;
        this.stage = stage;
        indexPerson = 0;
    }

    @FXML
    protected void initialize() {
        super.initialize();
        // все поля нередактируемые
        setAllFieldsDisabled();
        gridPane.setPrefHeight(gridPane.getPrefHeight() + 50);
        BorderPane borderPane = new BorderPane();
        rememberedButton = new Button(">");
        rememberedButton.setMaxHeight(Double.MAX_VALUE);
        rememberedButton.setPrefWidth(60);
        rememberedButton.setOnAction(action -> {
            showTrueAnswer(true);
        });
        BorderPane.setAlignment(rememberedButton, Pos.CENTER);
        BorderPane.setMargin(rememberedButton, new Insets(5));
        borderPane.setRight(rememberedButton);
        borderPane.setCenter(gridPane);
        markUnrememberedButton = new Button("<");
        markUnrememberedButton.setMaxHeight(Double.MAX_VALUE);
        markUnrememberedButton.setPrefWidth(60);
        borderPane.setLeft(markUnrememberedButton);
        BorderPane.setAlignment(markUnrememberedButton, Pos.CENTER);
        BorderPane.setMargin(markUnrememberedButton, new Insets(5));
        markUnrememberedButton.setOnAction(action -> {
            showTrueAnswer(false);
        });
        mistakeButton = new Button("Отменить предыдущий выбор");
        mistakeButton.setDisable(true);
        mistakeButton.setOnAction(action -> {
            Person person = people.get(indexPerson - 2);
            boolean lastChoice = person.getRemembered();
            dataHelper.setPersonRemembered(person.getId(), !lastChoice);
            person.setRemembered(!lastChoice);
            String status;
            if (lastChoice) {
                status = " не запомненным";
            } else {
                status = " запомненным";
            }
            showInformationAlert("Готово!", person.getName() + " отменен " + status);
            mistakeButton.setDisable(true);
        });
        borderPane.setBottom(mistakeButton);
        BorderPane.setAlignment(mistakeButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(mistakeButton, new Insets(5));
        setNextPerson();
        scene = new Scene(borderPane);
        scene.setOnKeyPressed(ke -> {
            switch (ke.getCode().getName()) {
                case "Right":
                case "D":
                case "В":
                    showTrueAnswer(true);
                    break;
                case "Left":
                case "A":
                case "Ф":
                    showTrueAnswer(false);
                    break;
                default:
                    System.out.println(ke.getCode().getName());
                    break;
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    private void showTrueAnswer(boolean remembered) {
        setButtonDisabled(true);
        Person person = people.get(indexPerson - 1);
        if (!ShowModeEnum.NAME.isEnabled()) {
            name.setText(person.getName());
        }
        if (!ShowModeEnum.EVENTS.isEnabled()) {
            events.setText(String.join(",", person.getEvents()));
        }
        if (!ShowModeEnum.COMPANY.isEnabled()) {
            company.setText(person.getCompany());
        }
        if (!ShowModeEnum.ROLE.isEnabled()) {
            role.setText(person.getRole());
        }
        if (!ShowModeEnum.DESCRIPTION.isEnabled()) {
            description.setText(person.getDescription());
        }
        if (!ShowModeEnum.PICTURES.isEnabled()) {
            fillPersonImages(person, false);
        }
        dataHelper.setPersonRemembered(person.getId(), remembered);
        person.setRemembered(remembered);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    setButtonDisabled(false);
                    timer.cancel();
                    setNextPerson();
                });
            }
        }, answerTimeInMs, 1000);
    }

    private void setNextPerson() {
        if (indexPerson == people.size()) {
            indexPerson++;
            rememberedButton.setDisable(true);
            markUnrememberedButton.setDisable(true);
            mistakeButton.setDisable(false);
            scene.setOnKeyPressed(action -> {
            });
            return;
        }
        clearAllFields();

        Person person = people.get(indexPerson++);
        stage.setTitle("Режим проверки: " + indexPerson + "/" + people.size());
        if (ShowModeEnum.NAME.isEnabled()) {
            name.setText(person.getName());
        }
        if (ShowModeEnum.EVENTS.isEnabled()) {
            events.setText(String.join(",", person.getEvents()));
        }
        if (ShowModeEnum.COMPANY.isEnabled()) {
            company.setText(person.getCompany());
        }
        if (ShowModeEnum.ROLE.isEnabled()) {
            role.setText(person.getRole());
        }
        if (ShowModeEnum.DESCRIPTION.isEnabled()) {
            description.setText(person.getDescription());
        }
        if (ShowModeEnum.PICTURES.isEnabled()) {
            fillPersonImages(person, false);
        }
    }

    private void clearAllFields() {
        name.clear();
        events.clear();
        company.clear();
        role.clear();
        description.clear();
        imageHBox.getChildren().clear();
    }

    private void setButtonDisabled(boolean disabled) {
        markUnrememberedButton.setDisable(disabled);
        rememberedButton.setDisable(disabled);
        mistakeButton.setDisable(disabled);
    }

    @FXML
    private void getSuggestions() {}
}
