package com.app.demo;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;

import java.text.*;
import java.text.NumberFormat;
import java.time.*;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.util.StringConverter;

public class ReportController extends ComboBox<Object> {
    @FXML
    public Button home;
    @FXML
    public Button reports;
    @FXML
    public Button transactions;
    @FXML
    public ComboBox<Object> dateComboBox;
    @FXML
    public Label incomeLabel;
    @FXML
    public Label expenseLabel;
    @FXML
    public Button help;
    private SceneSwitcher switcher;
    //Sentinel value for custom date
    private static final String CUSTOM_DATE = "Custom date...";
    private DoubleProperty relIncome = new SimpleDoubleProperty(0);
    private DoubleProperty relExpenses = new SimpleDoubleProperty(0);

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }

    @FXML
    public void initialize() {
        //Add predefined time lengths plus the date picker
        dateComboBox.getItems().addAll(
                "today",
                "yesterday",
                "the start of the week",
                "the past seven days",
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
                if (object == null) {
                    return "";
                } else if (object instanceof LocalDate) {
                    return ((LocalDate) object).toString();
                } else {
                    return object.toString();
                }
            }

            //unused but required for method
            @Override
            public Object fromString(String string) {
                return string;
            }
        });
        dateComboBox.setCellFactory(listView -> new ListCell<>() {
            //Sets the cell factory to handle the custom date option
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        //Set a custom button cell to handle the custom date option
        dateComboBox.setButtonCell(new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else if(item instanceof LocalDate) {
                    setText(((LocalDate) item).toString());
                } else{
                    setText(item.toString());
                }
            }
        });
        //When an item is selected, check if it's the sentinel value and if so, open a date picker
        dateComboBox.setOnAction(event -> {
           Object selected = dateComboBox.getSelectionModel().getSelectedItem();
           //using == for same object reference
              if (selected == CUSTOM_DATE) {
                DatePicker datePicker = new DatePicker();
                datePicker.setOnAction(dateEvent -> {
                     dateComboBox.getItems().add(datePicker.getValue());
                     dateComboBox.getSelectionModel().select(datePicker.getValue());
                });
                Popup popup = new Popup();
                popup.getContent().add(datePicker);
                popup.setAutoHide(true);

                Bounds comboBounds = dateComboBox.localToScreen(dateComboBox.getBoundsInLocal());
                Platform.runLater(() -> {
                    popup.show(dateComboBox, comboBounds.getMinX(), comboBounds.getMinY());
                });


           }
        });
        //binds the income and expense labels to the respective properties
        relIncome.set(compileIncome());
        incomeLabel.textProperty().bind(Bindings.createStringBinding(() -> NumberFormat.getCurrencyInstance().format(relIncome.get()), relIncome));
        //As the expenses are negative, the absolute value is used
        relExpenses.set(Math.abs(compileExpenses()));
        expenseLabel.textProperty().bind(Bindings.createStringBinding(() -> NumberFormat.getCurrencyInstance().format(relExpenses.get()), relExpenses));
    }
    public double compileIncome(){
        double happy = 0; //happy is the total income, it makes a greedy person happy
        for (Transaction transaction : TransactionDAO.getTransactions()) {
            if (transaction.getIncomeExpense().equals("Income")) {
                happy += transaction.getAmount();
            }
        }
        return happy;
    }
    public double compileExpenses(){
        double sad = 0; //sad is the total expenses, it makes a sad person sad
        for (Transaction transaction : TransactionDAO.getTransactions()) {
            if (transaction.getIncomeExpense().equals("Expense")) {
                sad += transaction.getAmount();
            }
        }
        return sad;
    }

        @FXML
        private void handleToolBarAction (ActionEvent event){
            Button clickedButton = (Button) event.getTarget();
            String buttonLabel = clickedButton.getId();
            switch (buttonLabel) {
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
                case "help" -> {
                    try{
                        switcher.switchToPreloaded("help");
                    } catch (Exception e){
                        setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                        switcher.preloadScene("help", "/com/app/demo/help-view.fxml");
                        switcher.switchToPreloaded("help");
                    }
                }
            }
        }
    }
