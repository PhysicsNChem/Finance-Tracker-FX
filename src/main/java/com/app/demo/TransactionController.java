package com.app.demo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;

import java.io.*;
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

public class TransactionController {
    @FXML
    public Button home;
    @FXML
    public Button TransferMoney;
    private Category categoryValue;
    @FXML
    private Label emptyLabel;
    @FXML
    private TextField searchField;
    @FXML
    private Label viewLabel;
    @FXML
    private ComboBox<Category> categoryFilterComboBox;
    @FXML
    private TreeView<String> assetsLiabilitiesTreeView;
    @FXML
    private AnchorPane mainContainer;
    protected double amount;
    protected String memo;
    private String date;


    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> sourceColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Category> categoryColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> incomeExpenseColumn;
    @FXML
    private TableColumn<Transaction, String> payerColumn;
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button addTransactionButton;

    private SceneSwitcher switcher;

    private TreeItem<String> rootItem;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactionList = new FilteredList<>(transactionList);
    private DoubleProperty totalBalance = new SimpleDoubleProperty(0);
    private ObservableList<Category> categories = FXCollections.observableArrayList();

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }

    @FXML
    public void initialize() {
        initializeTreeView();
        //disable the update and remove buttons if no transaction is selected
        updateButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));
        removeButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));
        //disable the add transaction button if no account is selected
        addTransactionButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        "Main view".equals(assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue()) ||
                                !assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getChildren().isEmpty(),
                assetsLiabilitiesTreeView.getSelectionModel().selectedItemProperty()));
        //disable the transfer money button if fewer than two accounts exist (at least one asset and one liability)



        emptyLabel = new Label("No transactions available. Click or tap 'Help' for more information.");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        incomeExpenseColumn.setCellValueFactory(new PropertyValueFactory<>("incomeExpense"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        payerColumn.setCellValueFactory(new PropertyValueFactory<>("payer"));

        loadTransactionsFromDatabase();
        totalBalance.set(TransactionDAO.getTotalBalance());
        amountColumn.setCellFactory(column -> new TableCell<Transaction, Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        });
        // Disable the update button if no transaction is selected
        updateButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));

        filteredTransactionList = new FilteredList<>(transactionList, p -> true);

        transactionsTable.setItems(filteredTransactionList);
        transactionsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        transactionsTable.setPlaceholder(emptyLabel);

        totalBalanceLabel.textProperty().bind(Bindings.format("Total Balance: %s", Bindings.createStringBinding(() ->
                NumberFormat.getCurrencyInstance().format(totalBalance.get()), totalBalance)));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        categoryFilterComboBox.setItems(categories);
        categoryFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });


        initializeCategories();
    }

    private void loadTransactionsFromDatabase() {
        // Load transactions from the SQLite database
        List<Transaction> transactions = TransactionDAO.getTransactions();
        transactionList.setAll(transactions);
        transactionsTable.setItems(transactionList);
    }

    //Filtering method for search function
    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        Category selectedCategory = categoryFilterComboBox.getValue();
        //Make a predicate 'transaction' of the transaction class to return for matching text. This will always return true for the FilteredList, as expected
        filteredTransactionList.setPredicate(transaction -> {
            boolean matchesSearchText = searchText.isEmpty() ||
                    transaction.getSource().toLowerCase().contains(searchText) ||
                    transaction.getDate().toLowerCase().contains(searchText) ||
                    transaction.getDescription().toLowerCase().contains(searchText) ||
                    transaction.getCategory().getName().toLowerCase().contains(searchText) ||
                    transaction.getIncomeExpense().toLowerCase().contains(searchText) ||
                    transaction.getPayer().toLowerCase().contains(searchText) ||
                    String.valueOf(transaction.getAmount()).contains(searchText);

            boolean matchesCategory = selectedCategory == null ||
                    "All categories".equals(selectedCategory.getName()) ||
                    transaction.getCategory().getName().equals(selectedCategory.getName());


            return matchesSearchText && matchesCategory;
        });
        transactionsTable.refresh();
    }

    @SuppressWarnings("unchecked")
    private void initializeTreeView() {
        rootItem = new TreeItem<>("Transactions");
        TreeItem<String> mainPageItem = new TreeItem<>("Main view");
        TreeItem<String> assetsItem = new TreeItem<>("Assets");
        TreeItem<String> liabilitiesItem = new TreeItem<>("Liabilities");

        // Group assets by subtype
        Map<String, List<Account>> assetsBySubtype = new HashMap<>();
        List<Account> assetLiabilityTypes = TransactionDAO.getAssetLiabilityTypes();
        for (Account assetType : assetLiabilityTypes) {
            if (assetType.getType().equals("Asset")) {
                assetsBySubtype.computeIfAbsent(assetType.getSubType(), k -> new ArrayList<>()).add(assetType);
            }
        }

        // Add subtypes to the tree
        for (Map.Entry<String, List<Account>> entry : assetsBySubtype.entrySet()) {
            TreeItem<String> subtypeItem = new TreeItem<>(entry.getKey());
            subtypeItem.setExpanded(true); // Expand the subtype view by default
            for (Account asset : entry.getValue()) {
                subtypeItem.getChildren().add(new TreeItem<>(asset.getName() + " → " + NumberFormat.getCurrencyInstance().format(asset.getAccBalance())));
            }
            assetsItem.getChildren().add(subtypeItem);
        }
        //do the same for liabilities
        Map<String, List<Account>> liabilitiesBySubtype = new HashMap<>();
        List<Account> liabilityTypes = TransactionDAO.getAssetLiabilityTypes();
        for (Account liabilityType : liabilityTypes) {
            if (liabilityType.getType().equals("Liability")) {
                liabilitiesBySubtype.computeIfAbsent(liabilityType.getSubType(), k -> new ArrayList<>()).add(liabilityType);
            }
        }
        for (Map.Entry<String, List<Account>> entry : liabilitiesBySubtype.entrySet()) {
            TreeItem<String> subtypeItem = new TreeItem<>(entry.getKey());
            subtypeItem.setExpanded(true); // Expand the subtype view by default
            for (Account liability : entry.getValue()) {
                subtypeItem.getChildren().add(new TreeItem<>(liability.getName() + " → " + NumberFormat.getCurrencyInstance().format(liability.getAccBalance())));
            }
            liabilitiesItem.getChildren().add(subtypeItem);
        }
        // Add the main page and accounts to the root
        rootItem.getChildren().addAll(mainPageItem, assetsItem, liabilitiesItem);
        assetsItem.setExpanded(true);
        liabilitiesItem.setExpanded(true);
        assetsLiabilitiesTreeView.setRoot(rootItem);
        // Root pane is only included for organizational purposes
        assetsLiabilitiesTreeView.setShowRoot(false);
        assetsLiabilitiesTreeView.getSelectionModel().select(mainPageItem);

        assetsLiabilitiesTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleTreeViewSelection(newValue);
                viewLabel.setText(getFullPath(newValue));
            }
        });
    }

    //filtering by account
    private void handleTreeViewSelection(TreeItem<String> selectedItem) {
        String selectedValue = selectedItem.getValue();
        switch (selectedValue) {
            case "Main view" -> transactionsTable.setItems(transactionList);
            case "Assets" -> filterByAccount("Asset", "");
            case "Liabilities" -> filterByAccount("Liability", "");
            case "Chequing accounts", "Savings accounts", "Brokerages", "Properties", "Other assets" ->
                    filterByAccount("Asset", selectedValue);
            case "Credit cards", "Mortgages", "Other loans", "Other liabilities" ->
                    filterByAccount("Liability", selectedValue);
            case null, default -> {
                // Filter transactions based on the selected account
                ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
                for (Transaction transaction : transactionList) {
                    String[] hotel = Objects.requireNonNull(selectedValue).split(" ");
                    if (transaction.getSource().equals(hotel[0])) {
                        filteredTransactions.add(transaction);
                    }
                }
                transactionsTable.setItems(filteredTransactions);
            }
        }
    }

    @FXML
    private void loadMainTransactionPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/main-view.fxml"));
            AnchorPane mainPage = loader.load();
            mainContainer.getChildren().setAll(mainPage);
        } catch (IOException e) {
            //Clicking back onto Main view throws this exception, but is actually a sign of successful loading
            System.out.println("Main view load successful");
        } catch (IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Illegal State");
            alert.setContentText("An illegal state occurred. Please check the FXML file in the source code.");
            alert.showAndWait();
        }
    }

    private void initializeCategories() {
        String[] categoryNames = {"Food", "Transport", "Entertainment", "Salary", "Investments", "Dividend", "Other"};
        for (String name : categoryNames) {
            categories.add(new Category(name));
            TransactionDAO.insertCategory(new Category(name));
        }
    }

    private ObservableList<Category> filterCategories(String type) {
        return categories.filtered(category -> {
            if (type.equals("Income")) {
                return category.getName().equals("Salary") || category.getName().equals("Dividend") || category.getName().equals("Investments");
            } else if (type.equals("Expense")) {
                return !category.getName().equals("Salary") && !category.getName().equals("Dividend") && !category.getName().equals("Investments");
            }
            return false;
        });
    }

    @FXML
    private void onAddTransaction(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Transaction");
        dialog.setHeaderText("Enter transaction details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField amountField = new TextField();
        amountField.setPromptText("Enter an amount here");
        TextField payerField = new TextField();
        payerField.setPromptText("Enter payer/recipient here");
        ComboBox<String> incomeExpense = new ComboBox<>();
        incomeExpense.getItems().addAll("Expense", "Income");
        //default value of expense
        incomeExpense.setValue("Expense");
        ComboBox<Category> categoryComboBox = new ComboBox<>(filterCategories(incomeExpense.getValue()));
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        TextField descriptionField = new TextField();
        //Create formatting for the dialog box and put the fields in a grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Payer/Recipient:"), 0, 1);
        grid.add(payerField, 1, 1);
        grid.add(new Label("Income/Expense:"), 0, 2);
        grid.add(incomeExpense, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryComboBox, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Memo (optional):"), 0, 5);
        grid.add(descriptionField, 1, 5);

        incomeExpense.valueProperty().addListener((observable, oldValue, newValue) -> {
            categoryComboBox.setItems(filterCategories(newValue));
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(amountField::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        //verify that the buttons (which disable themselves if the input is invalid) would not do anything as a result of an invalid input being passed through
        if (result.isPresent() && result.get() == addButtonType && !assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue().equals("Main view")) {
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    System.out.println("Invalid amount entered");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Enter a non-zero value within the 64-bit limit, please.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount entered");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Enter only numbers in the amount field, please.");
                alert.showAndWait();
                return;
            }
            memo = descriptionField.getText();
            String incomeExpenseValue = incomeExpense.getValue();
            String payerValue = payerField.getText();
            date = datePicker.getValue().toString();
            categoryValue = categoryComboBox.getValue();
            if (categoryValue == null) {
                System.out.println("Category not selected");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please select a category.");
                alert.showAndWait();
                return;
            }
            if ("Expense".equals(incomeExpenseValue)) {
                amount = -amount; // Make the amount negative for expenses
            }
            //Source will be tied to the selected account's name, but not other Account variables
            String accGiven = assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue();
            String[] findAccName = accGiven.split(" ");
            Transaction transaction = new Transaction(findAccName[0], date, memo, amount, categoryValue, incomeExpenseValue, payerValue);
            transactionList.add(transaction);
            updateTotalBalance(amount);
            String parent = assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getParent().getValue();
            String grandParent = assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getParent().getParent().getValue();
            System.out.println(parent);
            //update account balance
            switch (grandParent) {
                case "Assets" -> updateAccBalance(findAccName[0], parent, "Asset", amount);
                case "Liabilities" -> updateAccBalance(findAccName[0], parent, "Liability", amount);
            }

            // Add the transaction to the database
            TransactionDAO.insertTransaction(transaction);
            // Update the table view
            handleTreeViewSelection(assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem());
            System.out.println("Transaction added: Amount = " + amount + ", Description = " + memo);
            List<Transaction> transactions = TransactionDAO.getTransactions();
            for (Transaction t : transactions) {
                System.out.println(t.getDescription() + ": " + t.getAmount());
            }
        } else {
            System.out.println("Transaction addition canceled.");
        }
    }


    @FXML
    private void onTransfer(ActionEvent event) {
        // Handle Transfer button click
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Transfer money");
        dialog.setHeaderText("Transfer money from an asset to another asset or a liability account");
        ButtonType transferType = new ButtonType("Complete transfer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(transferType, ButtonType.CANCEL);
        TextField amountField = new TextField();
        amountField.setPromptText("Enter an amount here");
        ComboBox<String> sourceAccountComboBox = new ComboBox<>();
        //populate the source account combo box with the accounts
        List<Account> accounts = TransactionDAO.getAssetLiabilityTypes();
        for(Account acc : accounts){
            if(acc.getType().equals("Asset")) {
                sourceAccountComboBox.getItems().add(acc.getName());
            }
        }

        ComboBox<String> destinationAccountComboBox = new ComboBox<>();
        //populate the destination account combo box with the accounts
        for(Account acc : accounts){
                destinationAccountComboBox.getItems().add(acc.getName());
        }
        //Create formatting for the dialog box and put the fields in a grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(40);
        grid.add(new Label("Select account to transfer from"), 0, 0);
        grid.add(sourceAccountComboBox, 1, 0);
        grid.add(new Label("Enter money to be transferred"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Select account to transfer to"), 0, 2);
        grid.add(destinationAccountComboBox, 1, 2);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == transferType){
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    System.out.println("Invalid amount entered");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Enter a non-zero value within the 64-bit limit, please.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount entered");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Enter only numbers in the amount field, please.");
                alert.showAndWait();

            }
            String sourceAccount = sourceAccountComboBox.getValue();
            String destAccount = destinationAccountComboBox.getValue();
            if (sourceAccount == null || destAccount == null || sourceAccount.equals(destAccount)) {
                System.out.println("Invalid account selection");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please select different accounts to transfer between.");
                alert.showAndWait();
            }
            Transaction first = new Transaction(sourceAccount, LocalDate.now().toString(), "", -amount, new Category("Transfer"), "Expense", destAccount);
            Transaction second = new Transaction(destAccount, LocalDate.now().toString(), "", amount, new Category("Transfer"), "Income", sourceAccount);
            transactionList.add(first);
            transactionList.add(second);
            //insert the transaction into the database
            TransactionDAO.insertTransaction(first);
            TransactionDAO.insertTransaction(second);
            handleTreeViewSelection(assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem());
            //update the account balance
            for(Account acc : accounts){
                if(acc.getName().equals(sourceAccount)){
                    updateAccBalance(acc.getName(), acc.getSubType(), acc.getType(), -amount);
                }
                if(acc.getName().equals(destAccount)){
                    updateAccBalance(acc.getName(), acc.getSubType(), acc.getType(), amount);
                }
            }
        }
    }

    @FXML
    private void onSaveTransaction(ActionEvent event) {
        // Handle Print transactions button click
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        boolean printDialog = printerJob.showPrintDialog(mainContainer.getScene().getWindow());
        if (printDialog) {
            boolean success = printerJob.printPage(mainContainer);
            if (success) {
                printerJob.endJob();
            }
        }
    }

    @FXML
    private void onAddAsset(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Asset");
        dialog.setHeaderText("Enter asset details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField assetNameField = new TextField();
        assetNameField.setPromptText("Enter asset name");
        ComboBox<String> assetTypeComboBox = new ComboBox<>();
        assetTypeComboBox.getItems().addAll("Chequing accounts", "Savings accounts", "Brokerages", "Properties", "Other assets");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Asset Name:"), 0, 0);
        grid.add(assetNameField, 1, 0);
        grid.add(new Label("Asset Type:"), 0, 1);
        grid.add(assetTypeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(assetNameField::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == addButtonType) {
            String assetName = assetNameField.getText();
            String assetSubType = assetTypeComboBox.getValue();


            //Validate user input
            if (assetName == null || assetName.trim().isEmpty() || assetSubType == null) {
                System.out.println("Invalid asset details");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please enter valid asset details.");
                alert.showAndWait();
            }
            //Check if a duplicate account with the same name and subtype already exists
            if (assetName != null && !assetName.trim().isEmpty() && assetSubType != null && !checkDupes(assetName)) {
                TreeItem<String> assetsNode = assetsLiabilitiesTreeView.getRoot().getChildren().stream()
                        .filter(item -> "Assets".equals(item.getValue()))
                        .findFirst()
                        .orElse(null);
                if (assetsNode != null) {
                    TreeItem<String> subtypeNode = assetsNode.getChildren().stream()
                            .filter(item -> assetSubType.equals(item.getValue()))
                            .findFirst()
                            .orElseGet(() -> {
                                TreeItem<String> newSubtypeNode = new TreeItem<>(assetSubType);
                                assetsNode.getChildren().add(newSubtypeNode);
                                return newSubtypeNode;
                            });
                    subtypeNode.getChildren().add(new TreeItem<>(assetName + " → " + NumberFormat.getCurrencyInstance().format(0)));
                    //for better readability
                    subtypeNode.setExpanded(true);
                    Account account = new Account(assetName, "Asset", assetSubType, 0.00);
                    TransactionDAO.insertAssetLiabilityType(account);
                }
            }


        }
    }

    @FXML
    private void onAddLiability(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Liability");
        dialog.setHeaderText("Enter liability details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField liabilityNameField = new TextField();
        liabilityNameField.setPromptText("Enter liability name");

        ComboBox<String> liabilityTypeComboBox = new ComboBox<>();
        liabilityTypeComboBox.getItems().addAll("Credit cards", "Mortgages", "Other loans", "Other liabilities");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Liability Name:"), 0, 0);
        grid.add(liabilityNameField, 1, 0);
        grid.add(new Label("Liability Type:"), 0, 1);
        grid.add(liabilityTypeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(liabilityNameField::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == addButtonType) {
            String liabilityName = liabilityNameField.getText();
            String liabilitySubType = liabilityTypeComboBox.getValue();
            if (liabilityName != null && !liabilityName.trim().isEmpty() && liabilitySubType != null && !checkDupes(liabilityName)) {
                TreeItem<String> liabilitiesNode = assetsLiabilitiesTreeView.getRoot().getChildren().stream()
                        .filter(item -> "Liabilities".equals(item.getValue()))
                        .findFirst()
                        .orElse(null);
                if (liabilitiesNode != null) {
                    TreeItem<String> subtypeNode = liabilitiesNode.getChildren().stream()
                            .filter(item -> liabilitySubType.equals(item.getValue()))
                            .findFirst()
                            .orElseGet(() -> {
                                TreeItem<String> newSubtypeNode = new TreeItem<>(liabilitySubType);
                                liabilitiesNode.getChildren().add(newSubtypeNode);
                                return newSubtypeNode;
                            });
                    subtypeNode.getChildren().add(new TreeItem<>(liabilityName + " → " + NumberFormat.getCurrencyInstance().format(0)));
                    subtypeNode.setExpanded(true);
                    Account account = new Account(liabilityName, "Liability", liabilitySubType, 0.00);
                    TransactionDAO.insertAssetLiabilityType(account);
                }
            }
            if (liabilityName == null || liabilityName.trim().isEmpty() || liabilitySubType == null) {
                System.out.println("Invalid liability details");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please enter valid liability details.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onUpdateTransaction(ActionEvent event) {
        // Handle Update Transaction button click
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Transaction");
        dialog.setHeaderText("Enter updated transaction details");
        ButtonType addButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        TextField amountField = new TextField(String.valueOf(Math.abs(amountColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex()))));
        amountField.setPromptText("Enter an amount here");
        TextField payerField = new TextField(payerColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex()));
        payerField.setPromptText("Enter payer/recipient here");
        ComboBox<String> incomeExpense = new ComboBox<>();
        incomeExpense.getItems().addAll("Expense", "Income");
        incomeExpense.setValue(incomeExpenseColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex()));
        ComboBox<Category> categoryComboBox = new ComboBox<>(filterCategories(incomeExpense.getValue()));
        categoryComboBox.setValue(categoryColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex()));
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.parse(dateColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex())));
        TextField descriptionField = new TextField(descriptionColumn.getCellData(transactionsTable.getSelectionModel().getSelectedIndex()));
        //Set up user input dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Update amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Update payer/recipient:"), 0, 1);
        grid.add(payerField, 1, 1);
        grid.add(new Label("Update income/expense:"), 0, 2);
        grid.add(incomeExpense, 1, 2);
        grid.add(new Label("Update category:"), 0, 3);
        grid.add(categoryComboBox, 1, 3);
        grid.add(new Label("Update date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Update memo (optional):"), 0, 5);
        grid.add(descriptionField, 1, 5);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    System.out.println("Invalid amount entered");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Enter a non-zero value within the 64-bit limit, please.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount entered");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Enter only numbers in the amount field, please.");
                alert.showAndWait();
                return;
            }
            memo = descriptionField.getText();
            String incomeExpenseValue = incomeExpense.getValue();
            String payerValue = payerField.getText();
            date = datePicker.getValue().toString();
            categoryValue = categoryComboBox.getValue();
            if (categoryValue == null) {
                System.out.println("Category not selected");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Please select a category.");
                alert.showAndWait();
                return;
            }
            if ("Expense".equals(incomeExpenseValue)) {
                amount = -amount; // Make the amount negative for expenses
            }
            Transaction transaction = transactionsTable.getSelectionModel().getSelectedItem();
            updateTotalBalance(-transaction.getAmount());
            transaction.setAmount(amount);
            transaction.setCategory(categoryValue);
            transaction.setIncomeExpense(incomeExpenseValue);
            transaction.setPayer(payerValue);
            transaction.setDate(date);
            transaction.setDescription(memo);
            updateTotalBalance(amount);
            TransactionDAO.updateTransaction(transaction);

            List<Account> accounts = TransactionDAO.getAssetLiabilityTypes();
            for (Account acc : accounts) {
                if (transaction.getSource().equals(acc.getName())) {
                    updateAccBalance(acc.getName(), acc.getSubType(), acc.getType(), transaction.getAmount());
                }
            }
            System.out.println("Transaction updated: Amount = " + amount + ", Description = " + memo);
            filteredTransactionList.setPredicate(filteredTransactionList.getPredicate());
            transactionsTable.refresh();
            List<Transaction> transactions = TransactionDAO.getTransactions();
            for (Transaction t : transactions) {
                System.out.println(t.getDescription() + ": " + t.getAmount());
            }
        } else {
            System.out.println("Transaction update canceled.");
        }
        System.out.println("Update Transaction button clicked");
    }

    @FXML
    private void onRemoveTransaction(ActionEvent event) {
        // Handle Remove Transaction button click


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Remove Transaction");
        dialog.setHeaderText("Are you sure you want to remove this transaction?");
        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        Transaction transaction = transactionsTable.getSelectionModel().getSelectedItem();
        //to get account attributes, match the String source with the account's name. The process is under the below conditional flow
        List<Account> accounts = TransactionDAO.getAssetLiabilityTypes();
        if (result.isPresent() && result.get() == removeButtonType) {
            transactionList.remove(transaction);
            for (Account acc : accounts) {
                if (transaction.getSource().equals(acc.getName())) {
                    updateAccBalance(acc.getName(), acc.getSubType(), acc.getType(), -transaction.getAmount());
                    break;
                }
            }
            updateTotalBalance(-transaction.getAmount());
            TransactionDAO.deleteTransaction(transaction);
        }
        System.out.println("Remove Transaction button clicked");
    }

    @FXML
    private void onRemoveAccount(ActionEvent event) {
        // Handle Remove Account button click
        TreeItem<String> selectedItem = assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem();
        //Make sure it's not one of the headers critical to the application structure
        if (selectedItem != null && (!selectedItem.getValue().equals("Main view") && !selectedItem.getValue().equals("Assets") && !selectedItem.getValue().equals("Liabilities"))) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Remove Account");
            dialog.setHeaderText("Are you sure you want to remove this account? \nThis action cannot be undone and will delete all transactions under this account.");
            ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == removeButtonType) {
                List<Account> accounts = TransactionDAO.getAssetLiabilityTypes();
                //logic to remove all transactions under the account and to remove the account from the database
                for (Account account : accounts) {
                    String[] selectedVal = selectedItem.getValue().split(" ");
                    if (account.getName().equals(selectedVal[0]) && account.getSubType().equals(selectedItem.getParent().getValue())) {
                        TransactionDAO.deleteAssetLiabilityType(account);
                    }
                }
                //Make sure to run this under the JavaFX thread
                Platform.runLater(() -> {
                    List<Transaction> toRemove = transactionList.stream()
                            .filter(transaction -> transaction.getSource().equals(selectedItem.getValue().split(" ")[0]))
                            .toList();

                    for (Transaction transaction : toRemove) {
                        updateTotalBalance(-transaction.getAmount());
                        TransactionDAO.deleteTransaction(transaction);
                    }
                    transactionList.removeAll(toRemove);

                    // Rebind the FilteredList to force a refresh.
                    Predicate<Transaction> filterCondition = t -> t.getSource().equals(selectedItem.getValue().split(" ")[0]);
                    FilteredList<Transaction> newFilteredList = new FilteredList<>(transactionList, filterCondition);
                    transactionsTable.setItems(newFilteredList);
                    transactionsTable.refresh();
                });


                //Remove the account from the tree view
                TreeItem<String> parentItem = selectedItem.getParent();
                parentItem.getChildren().remove(selectedItem);

                // Check if the parent has no more children and remove it if necessary
                if (parentItem.getChildren().isEmpty() && parentItem.getParent() != null) {
                    parentItem.getParent().getChildren().remove(parentItem);
                }
            }
        }
    }

    private void updateTotalBalance(double amount) {
        totalBalance.set(totalBalance.get() + amount);
    }

    private void updateAccBalance(String acc, String parent, String grandParent, double amount) {
        List<Account> accounts = TransactionDAO.getAssetLiabilityTypes();
        for (Account account : accounts) {
            if (account.getName().equals(acc) && account.getSubType().equals(parent) && account.getType().equals(grandParent)) {
                double newBalance = account.getAccBalance() + amount;
                account.setAccBalance(newBalance);
                TransactionDAO.updateAccBalance(account);
                System.out.println("Account balance updated: " + account.getAccBalance());
                break;
            }
        }
        updateTreeViewAmounts();
    }

    private void updateTreeViewAmounts() {
        for (TreeItem<String> assetItem : rootItem.getChildren().get(1).getChildren()) {
            for (TreeItem<String> accountItem : assetItem.getChildren()) {
                String subtypeItem = assetItem.getValue();
                for (Account h : TransactionDAO.getAssetLiabilityTypes()) {
                    String accountName = accountItem.getValue().split(" → ")[0];
                    if (accountName.equals(h.getName()) && subtypeItem.equals(h.getSubType()) && "Asset".equals(h.getType())) {
                        System.out.println(h.getAccBalance());
                        accountItem.setValue(accountName + " → " + NumberFormat.getCurrencyInstance().format(h.getAccBalance()));
                    }
                }

            }
        }
        for (TreeItem<String> liabilityItem : rootItem.getChildren().get(2).getChildren()) {
            for (TreeItem<String> accountItem : liabilityItem.getChildren()) {
                for (Account g : TransactionDAO.getAssetLiabilityTypes()) {
                    String accountName = accountItem.getValue().split(" → ")[0];
                    accountItem.setValue(accountName + " → " + NumberFormat.getCurrencyInstance().format(g.getAccBalance()));
                }
            }
        }
    }

    private String getFullPath(TreeItem<String> item) {
        //helper method to build out the path to be displayed on the transactions page
        StringBuffer fullPath = new StringBuffer(item.getValue());
        TreeItem<String> parent = item.getParent();
        while (parent != null && parent.getValue() != null) {
            fullPath.insert(0, parent.getValue() + " → ");
            parent = parent.getParent();
        }
        //If the element selected is a specific account, remove the amount listed
        //parseDouble throws an exception if a double is not found, so surround with a try/catch block
        return fullPath.toString();
    }

    private boolean checkDupes(String assetName) {
        //Check if a duplicate account with the same name and subtype already exists
        List<Account> assetLiabilityTypes = TransactionDAO.getAssetLiabilityTypes();
        for (Account account : assetLiabilityTypes) {
            if (account.getName().equals(assetName)) {
                System.out.println("Duplicate account found");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("An account with the same name already exists.");
                alert.showAndWait();
                return true;
            }
        }
        return false;
    }
    private void filterByAccount(String type, String subtype) {
        if(subtype.isEmpty()) {
            ObservableList<Account> accs = FXCollections.observableArrayList();
            ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
            for (Account account : TransactionDAO.getAssetLiabilityTypes()) {
                if (account.getType().equals(type)) {
                    accs.add(account);
                }
            }
            for (Account asset : accs) {
                for (Transaction transaction : transactionList) {
                    if (transaction.getSource().equals(asset.getName())) {
                        filteredTransactions.add(transaction);
                    }
                }

            }
            transactionsTable.setItems(filteredTransactions);
        }
    }

    @FXML
    private void onCopyTransaction(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent tr = new ClipboardContent();
        tr.putString(transactionsTable.getSelectionModel().getSelectedItem().toString());
        clipboard.setContent(tr);
        System.out.println(transactionsTable.getSelectionModel().getSelectedItem().toString());
    }
    @FXML
    private void handleToolBarAction(ActionEvent event){
        Button clickedButton = (Button) event.getSource();
        String buttonLabel = clickedButton.getId();
        switch (buttonLabel){
            case "home" -> HelloApplication.loadHomePage();
            case "reports" -> {
                try {
                    switcher.switchToPreloaded("reports");
                } catch (Exception e) {
                    setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                    switcher.preloadScene("reports", "/com/app/demo/reports-page.fxml");
                    switcher.switchToPreloaded("reports");
                }
            }
            case "help" -> {
                try {
                    switcher.switchToPreloaded("help");
                } catch (Exception e) {
                    setSceneSwitcher(new SceneSwitcher(HelloApplication.primaryStage));
                    switcher.preloadScene("help", "/com/app/demo/help-view.fxml");
                    switcher.switchToPreloaded("help");
                }
            }
        }
    }


}