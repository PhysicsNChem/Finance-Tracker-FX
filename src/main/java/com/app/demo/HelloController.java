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
        try{
            switcher.switchToPreloaded("transactions");
        } catch (Exception e) {
            SceneSwitcher s = new SceneSwitcher(HelloApplication.primaryStage);
            s.preloadScene("transactions", "/com/app/demo/transactions-page.fxml");
            s.switchToPreloaded("transactions");
        }
    }
    public void onReportButtonClick(ActionEvent actionEvent) {
        try{
            switcher.switchToPreloaded("reports");
        } catch (Exception e) {
            SceneSwitcher s = new SceneSwitcher(HelloApplication.primaryStage);
            s.preloadScene("reports", "/com/app/demo/reports-page.fxml");
            s.switchToPreloaded("reports");
        }
    }
}