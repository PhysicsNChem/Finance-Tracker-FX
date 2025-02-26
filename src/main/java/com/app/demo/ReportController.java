package com.app.demo;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;

import java.text.NumberFormat;
import java.time.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
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
    public Button help;
    @FXML
    public Text netLabel;
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

        dateComboBox.setValue(dateComboBox.getItems().getFirst());
        updateNetLabel(dateComboBox.getValue().toString());
        dateComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    //When the value changes, update the net label
                    updateNetLabel(newValue.toString());
                }
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
                } else if (item instanceof LocalDate) {
                    setText(((LocalDate) item).toString());
                } else {
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
        //concatenate the income and expenses to the netLabel
    }
        public void updateNetLabel(String date) {
            relIncome.set(compileIncome(date));
            System.out.println(date);
            //As the expenses are negative, the absolute value is used
            relExpenses.set(Math.abs(compileExpenses(date)));
            if (relIncome.get() < relExpenses.get()) {
                netLabel.setText(",  you've made " + NumberFormat.getCurrencyInstance().format(relIncome.get()) + " and spent " + NumberFormat.getCurrencyInstance().format(relExpenses.get()) + ", resulting in a net loss of " + NumberFormat.getCurrencyInstance().format(relExpenses.get() - relIncome.get()));
            } else {
                netLabel.setText(",  you've made " + NumberFormat.getCurrencyInstance().format(relIncome.get()) + " and spent " + NumberFormat.getCurrencyInstance().format(relExpenses.get()) + ", resulting in a net gain of " + NumberFormat.getCurrencyInstance().format(relIncome.get() - relExpenses.get()));
            }
        }

    public double compileIncome(String date){
        double happy = 0; //happy is the total income, it makes a greedy person happy
        //Iterate through the transactions and add up the income
        switch(date){
            case "today" -> {
                date = LocalDate.now().toString();
            }
            case "yesterday" -> {
                date = LocalDate.now().minusDays(1).toString();
            }
            case "the start of the week" -> {
                date = LocalDate.now().with(DayOfWeek.MONDAY).toString();
            }
            case "the past seven days" -> {
                date = LocalDate.now().minusDays(7).toString();
            }
            case "this month" -> {
                date = LocalDate.now().withDayOfMonth(1).toString();
            }
            case "the past month" -> {
                date = LocalDate.now().minusMonths(1).withDayOfMonth(1).toString();
            }
            case "this year to date" -> {
                date = LocalDate.now().withDayOfYear(1).toString();
            }
            case "one year ago" -> {
                date = LocalDate.now().minusYears(1).withDayOfYear(1).toString();
            }
            case "three years ago" -> {
                date = LocalDate.now().minusYears(3).withDayOfYear(1).toString();
            }
            case CUSTOM_DATE -> {
                date = dateComboBox.getValue().toString();
            }
        }
        for (Transaction transaction : TransactionDAO.getTransactions()) {
            long transactionDate = LocalDate.parse(transaction.getDate()).toEpochDay();
            long selectedDate = LocalDate.parse(date).toEpochDay();
            System.out.println(transactionDate);
            if (transaction.getIncomeExpense().equals("Income") && transactionDate >= selectedDate) {
                //use epoch day to compare dates
                happy += transaction.getAmount();

            }
        }
        return happy;
    }
    public double compileExpenses(String date){
        double sad = 0;//sad is the total expenses, it makes a sad person sad
        switch(date){
            case("today") -> {
                date = LocalDate.now().toString();
            }
            case("yesterday") -> {
                date = LocalDate.now().minusDays(1).toString();
            }
            case("the start of the week") -> {
                date = LocalDate.now().with(DayOfWeek.MONDAY).toString();
            }
            case("the past seven days") -> {
                date = LocalDate.now().minusDays(7).toString();
            }
            case("this month") -> {
                date = LocalDate.now().withDayOfMonth(1).toString();
            }
            case("the past month") -> {
                date = LocalDate.now().minusMonths(1).withDayOfMonth(1).toString();
            }
            case("this year to date") -> {
                date = LocalDate.now().withDayOfYear(1).toString();
            }
            case("one year ago") -> {
                date = LocalDate.now().minusYears(1).withDayOfYear(1).toString();
            }
            case("three years ago") -> {
                date = LocalDate.now().minusYears(3).withDayOfYear(1).toString();
            }
            case(CUSTOM_DATE) -> {
                date = dateComboBox.getValue().toString();
            }

        }
        for (Transaction transaction : TransactionDAO.getTransactions()) {
            long transactionDate = LocalDate.parse(transaction.getDate()).toEpochDay();
            long selectedDate = LocalDate.parse(date).toEpochDay();
            if (transaction.getIncomeExpense().equals("Expense") && transactionDate >= selectedDate) {
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
