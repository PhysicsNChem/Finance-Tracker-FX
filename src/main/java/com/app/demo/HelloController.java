package com.app.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class HelloController {

    private SceneSwitcher switcher;

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }


    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void onTransactionButtonClick(ActionEvent actionEvent) {
        if (switcher != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/transactions-page.fxml"));
            switcher.switchTo("/com/app/demo/transactions-page.fxml");
        } else {
            System.out.println("Switcher is not initialized!");
        }
    }
    public void onReportButtonClick(ActionEvent actionEvent) {
        welcomeText.setText("Reports");
    }
}