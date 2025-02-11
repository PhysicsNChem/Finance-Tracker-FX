package com.app.demo;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.input.*;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

public class TransactionController {
    @FXML
    public Button home;
    private Category categoryValue;
    private String selectedAssetLiabilityName;
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
    public double amount;
    public String memo;
    public String incomeExpense;
    public String payer;
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

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactionList = new FilteredList<>(transactionList);
    private DoubleProperty totalBalance = new SimpleDoubleProperty(0);
    private ObservableList<Category> categories = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        initializeTreeView();
        updateButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));
        removeButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));
        addTransactionButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        "Main view".equals(assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue()),
                assetsLiabilitiesTreeView.getSelectionModel().selectedItemProperty()));
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
        updateButton.disableProperty().bind(Bindings.isNull(transactionsTable.getSelectionModel().selectedItemProperty()));

        filteredTransactionList = new FilteredList<>(transactionList, p -> true);

        transactionsTable.setItems(filteredTransactionList);
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        List<Transaction> transactions = TransactionDAO.getTransactions();
        transactionList.setAll(transactions);
        transactionsTable.setItems(transactionList);
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        Category selectedCategory = categoryFilterComboBox.getValue();

        filteredTransactionList.setPredicate(transaction -> {
            boolean matchesSearchText = searchText.isEmpty() ||
                    transaction.getDate().toLowerCase().contains(searchText) ||
                    transaction.getDescription().toLowerCase().contains(searchText) ||
                    transaction.getCategory().getName().toLowerCase().contains(searchText) ||
                    transaction.getIncomeExpense().toLowerCase().contains(searchText) ||
                    transaction.getPayer().toLowerCase().contains(searchText) ||
                    String.valueOf(transaction.getAmount()).contains(searchText);

            boolean matchesCategory = selectedCategory == null || transaction.getCategory().equals(selectedCategory);

            boolean matchesAssetLiability = selectedAssetLiabilityName == null || transaction.getSource().equals(selectedAssetLiabilityName);

            return matchesSearchText && matchesCategory && matchesAssetLiability;
        });
        transactionsTable.refresh();
    }

    @SuppressWarnings("unchecked")
    private void initializeTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Transactions");
        TreeItem<String> mainPageItem = new TreeItem<>("Main view");
        TreeItem<String> assetsItem = new TreeItem<>("Assets");
        TreeItem<String> liabilitiesItem = new TreeItem<>("Liabilities");

        List<Account> assetLiabilityTypes = TransactionDAO.getAssetLiabilityTypes();
        for (Account assetType: assetLiabilityTypes) {
            if(assetType.getType().equals("Asset")) {
                assetsItem.getChildren().add(new TreeItem<>(assetType.getName()));
            }
        }

        List<Account> liabilityTypes = TransactionDAO.getAssetLiabilityTypes();
        for (Account liabilityType : liabilityTypes) {
            if(liabilityType.getType().equals("Liability")) {
                liabilitiesItem.getChildren().add(new TreeItem<>(liabilityType.getName()));
            }
        }

        rootItem.getChildren().addAll(mainPageItem, assetsItem, liabilitiesItem);
        assetsItem.setExpanded(true);
        liabilitiesItem.setExpanded(true);
        assetsLiabilitiesTreeView.setRoot(rootItem);
        assetsLiabilitiesTreeView.setShowRoot(false);
        assetsLiabilitiesTreeView.getSelectionModel().select(mainPageItem);

        assetsLiabilitiesTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleTreeViewSelection(newValue);
                viewLabel.setText(getFullPath(newValue));
            }
        });
    }

    private void handleTreeViewSelection(TreeItem<String> selectedItem) {
        String selectedItemValue = selectedItem.getValue();
        if ("Main view".equals(selectedItemValue)) {
            selectedAssetLiabilityName = null;
            loadMainTransactionPage();
        } else {
            selectedAssetLiabilityName = selectedItemValue;
        }
        updateFilter();
    }

    @FXML
    private void loadMainTransactionPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/main-view.fxml"));
            AnchorPane mainPage = loader.load();
            mainContainer.getChildren().setAll(mainPage);
        } catch (IOException e) {
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
        categories.addAll(
                new Category("All categories"),
                new Category("Food"),
                new Category("Transport"),
                new Category("Entertainment"),
                new Category("Salary"),
                new Category("Dividend"),
                new Category("Other")
        );
    }

    private ObservableList<Category> filterCategories(String type) {
        return categories.filtered(category -> {
            if (type.equals("Income")) {
                return category.getName().equals("Salary") || category.getName().equals("Dividend");
            } else if (type.equals("Expense")) {
                return !category.getName().equals("Salary") && !category.getName().equals("Dividend");
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
        incomeExpense.setValue("Expense");
        ComboBox<Category> categoryComboBox = new ComboBox<>(filterCategories(incomeExpense.getValue()));
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());
        TextField descriptionField = new TextField();

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

        if (result.isPresent() && result.get() == addButtonType && !assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue().equals("Main view")) {
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    System.out.println("Invalid amount entered");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Enter a non-zero value within the 64-bit flimit, please.");
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
            Transaction transaction = new Transaction(assetsLiabilitiesTreeView.getSelectionModel().getSelectedItem().getValue(), date, memo, amount, categoryValue, incomeExpenseValue, payerValue);
            transactionList.add(transaction);
            updateTotalBalance(incomeExpenseValue, amount);
            TransactionDAO.insertTransaction(transaction);
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
    private void onViewTransaction(ActionEvent event) {
        // Handle View transaction button click
    }

    @FXML
    private void onSaveTransaction(ActionEvent event) {
        // Handle Save transactions button click
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
        assetTypeComboBox.getItems().addAll("Chequing Account", "Savings Account", "Brokerage", "Properties", "Others");

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
            if (assetName != null && !assetName.trim().isEmpty()) {
                TreeItem<String> assetsNode = assetsLiabilitiesTreeView.getRoot().getChildren().stream()
                        .filter(item -> "Assets".equals(item.getValue()))
                        .findFirst()
                        .orElse(null);
                if (assetsNode != null) {
                    assetsNode.getChildren().add(new TreeItem<>(assetName));
                    assetsNode.setExpanded(true);
                    Account account = new Account(assetName, "Asset", assetTypeComboBox.getValue());
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
        liabilityTypeComboBox.getItems().addAll("Credit Card", "Mortgage", "Loans", "Others");
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
            if (liabilityName != null && !liabilityName.trim().isEmpty()) {
                TreeItem<String> liabilitiesNode = assetsLiabilitiesTreeView.getRoot().getChildren().stream()
                        .filter(item -> "Liabilities".equals(item.getValue()))
                        .findFirst()
                        .orElse(null);
                if (liabilitiesNode != null) {
                    liabilitiesNode.getChildren().add(new TreeItem<>(liabilityName));
                    liabilitiesNode.setExpanded(true);
                    Account account = new Account(liabilityName, "Liability", liabilityTypeComboBox.getValue());
                    TransactionDAO.insertAssetLiabilityType(account);
                }
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
            updateTotalBalance(transaction.getIncomeExpense(), -transaction.getAmount());
            transaction.setAmount(amount);
            transaction.setCategory(categoryValue);
            transaction.setIncomeExpense(incomeExpenseValue);
            transaction.setPayer(payerValue);
            transaction.setDate(date);
            transaction.setDescription(memo);
            updateTotalBalance(incomeExpenseValue, amount);
            TransactionDAO.updateTransaction(transaction);
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
        if (result.isPresent() && result.get() == removeButtonType) {
            transactionList.remove(transaction);
            updateTotalBalance(transaction.getIncomeExpense(), -transaction.getAmount());
            TransactionDAO.deleteTransaction(transaction);
        }
        System.out.println("Remove Transaction button clicked");
    }
    private void onRemoveAccount(ActionEvent event) {
        // Handle Remove Account button click
    }

    private void updateTotalBalance(String incomeExpense, double amount) {
        totalBalance.set(totalBalance.get() + amount);
    }

    private String getFullPath(TreeItem<String> item) {
        StringBuffer fullPath = new StringBuffer(item.getValue());
        TreeItem<String> parent = item.getParent();
        while (parent != null && parent.getValue() != null) {
            fullPath.insert(0, parent.getValue() + " → ");
            parent = parent.getParent();
        }
        return fullPath.toString();
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
        if(buttonLabel.equals("home")) {
            HelloApplication.loadHomePage();
        }
    }


}