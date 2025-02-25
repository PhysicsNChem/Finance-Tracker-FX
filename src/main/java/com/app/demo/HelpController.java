package com.app.demo;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class HelpController {
    public Button home;
    public TextArea questionArea;
    public TextArea answerArea;
    public Button transactions;
    private SceneSwitcher switcher;

    @FXML
    public void initialize(){
        questionArea.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleAskAction();
                event.consume(); //prevent new line from being added
            }
        });
    }
    @FXML
    private void handleAskAction(){
        String question = questionArea.getText().trim().toLowerCase();
        answerArea.clear();
        //simple rules-based response system
        if (question.contains("how") && question.contains("use") && question.contains("fbla") || question.contains("how") && question.contains("use") && question.contains("project")) {
            answerArea.setText("To use FBLA Project, you can click on the Transactions button in the toolbar, then click either 'Add Asset' or 'Add Liability'. After that, you can click on the 'Add Transaction' button to add a transaction to the account.");
        } else if (question.contains("how") && question.contains("add") && question.contains("transaction")) {
            answerArea.setText("To add a transaction, click on the Transactions button, then click on the Add Transaction button.");
        } else if (question.contains("how") && question.contains("add") && (question.contains("asset") || question.contains("account"))) {
            answerArea.setText("To add an asset, click on the Transactions button, then click on the Add Asset button.");
        } else if (question.contains("how") && question.contains("add") && question.contains("liability")) {
            answerArea.setText("To add a liability, click on the Transactions button, then click on the Add Liability button.");
        } else if (question.contains("how") && question.contains("generate") && question.contains("report")) {
            answerArea.setText("To generate a report, click on the Reports button, then select the date range you want to view.");
        } else if (question.contains("how") && question.contains("navigate") && question.contains("app")) {
            answerArea.setText("To navigate the app, click on the buttons at the top of the screen.");
        }
        else if(question.contains("how") && question.contains("update")){
            answerArea.setText("To update a transaction, click on the Transactions button in the toolbar, then click on the transaction in the table you want to update. Then, click on the 'Update Transaction' button.");
        }
        else if(question.contains("how") && (question.contains("delete") || question.contains("remove")) && question.contains("transaction")){
            answerArea.setText("To delete a transaction, click on the Transactions button in the toolbar, then click on the transaction in the table you want to delete. Then, click on the 'Delete Transaction' button.");
        }
        else if(question.contains("how") && (question.contains("delete") || question.contains("remove")) && (question.contains("asset") || question.contains("liability") || question.contains("account"))){
            answerArea.setText("To delete an account, click on the Transactions button in the toolbar, then right click on the account in the table you want to delete. Then, click on the 'Delete Account' button.");
        }

        else if (question.contains("hello") || question.contains("hi") || question.contains("hey") || question.contains("yo") || question.contains("henlo")) {
            answerArea.setText("Hello! How can I help you?");
        } else if(question.contains("how")){
            answerArea.setText("Is there something specific you would like to know how to do?");
        } else if(question.contains("help")) {
            answerArea.setText("What can I help you with?");
        } else if(question.contains("what") && question.contains("date") && question.contains("today")){
            answerArea.setText("Today's date is " + java.time.LocalDate.now());
        }
        else if (question.contains("what")) {
            answerArea.setText("What would you like to know?");
        }
        else{
            answerArea.setText("I'm sorry, I don't understand your question.");
        }
    }
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
