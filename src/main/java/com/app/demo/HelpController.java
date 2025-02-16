package com.app.demo;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class HelpController {
    public Button home;
    private SceneSwitcher switcher;

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }
    @FXML
    public void handleToolBarAction(ActionEvent event){
        Button clickedButton = (Button) event.getSource();
        String id = clickedButton.getId();
            switch (id) {
                case "home" -> HelloApplication.loadHomePage();
                case "transactions" -> {
                    try {
                        switcher.switchToPreloaded("transactions");
                    } catch (Exception e) {
                        setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                        switcher.preloadScene("transactions", "/com/app/demo/transactions-page.fxml");
                        switcher.switchToPreloaded("transactions");
                    }
                }
                case "reports" -> {
                    try {
                        switcher.switchToPreloaded("reports");
                    } catch (Exception e) {
                        setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                        switcher.preloadScene("reports", "/com/app/demo/reports-page.fxml");
                        switcher.switchToPreloaded("reports");
                    }
                }
            }
        }
    }
