package com.app.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import java.time.*;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class ReportController {
    @FXML
    public Button home;
    @FXML
    public Button reports;
    @FXML
    public Button transactions;
    @FXML
    public ComboBox<Object> dateComboBox;
    private SceneSwitcher switcher;
    private static final String CUSTOM_DATE = "Select Custom Date";

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }
    @FXML
    public void initialize(){
        //Add predefined time lengths plus the date picker
        dateComboBox.getItems().addAll(
                "today",
                "yesterday",
                "this week",
                "the past week",
                "this month",
                "the past month",
                "this year to date",
                "one year ago",
                "three years ago",
                CUSTOM_DATE
        );
        dateComboBox.setConverter(new StringConverter<Object>() {
            //Sets a StringConverter to handle dates appropriately
            @Override
            public String toString(Object object) {
                if(object == null){
                    return "";
                }
                else if(object instanceof LocalDate){
                    return ((LocalDate) object).toString();
                } else{
                    return object.toString();
                }
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });
    }
    @FXML
    private void handleToolBarAction(ActionEvent event) {
        Button clickedButton = (Button) event.getTarget();
        String buttonLabel = clickedButton.getId();
        switch (buttonLabel) {
            case "home" -> HelloApplication.loadHomePage();
            case "transactions" -> {
                setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                switcher.preloadScene("transactions", "/com/app/demo/transactions-page.fxml");
                switcher.switchToPreloaded("transactions");
            }
        }
    }
}
