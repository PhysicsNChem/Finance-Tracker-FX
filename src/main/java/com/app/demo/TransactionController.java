package com.app.demo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class TransactionController {
    private SceneSwitcher switcher;
    private Category categoryValue;
    @FXML
    private Label emptyLabel;
    @FXML
    private TextField searchField;
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

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }

    @FXML
    private TableView<Transaction> transactionsTable;
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
    private TableColumn<Transaction, Void> actionColumn;
    @FXML
    private Label totalBalanceLabel;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactionList = new FilteredList<>(transactionList);
    private DoubleProperty totalBalance = new SimpleDoubleProperty(0.0);
    private ObservableList<Category> categories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        emptyLabel = new Label("No transactions available. Click or tap 'Add Transaction' to get started!");
        incomeExpenseColumn.setCellValueFactory(new PropertyValueFactory<>("incomeExpense"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        payerColumn.setCellValueFactory(new PropertyValueFactory<>("payer"));

        loadTransactionsFromDatabase();

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

        filteredTransactionList = new FilteredList<>(transactionList, p -> true);

        transactionsTable.setItems(transactionList);
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

        initializeTreeView();
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
            boolean matchesSearchText = searchText == null || searchText.isEmpty() ||
                    transaction.getDate().toLowerCase().contains(searchText) ||
                    transaction.getDescription().toLowerCase().contains(searchText) ||
                    transaction.getCategory().getName().toLowerCase().contains(searchText) ||
                    transaction.getIncomeExpense().toLowerCase().contains(searchText) ||
                    transaction.getPayer().toLowerCase().contains(searchText) ||
                    String.valueOf(transaction.getAmount()).contains(searchText);

            boolean matchesCategory = selectedCategory == null || transaction.getCategory().equals(selectedCategory);

            return matchesSearchText && matchesCategory;
        });
    }

    private void initializeTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Transactions");
        TreeItem<String> mainPageItem = new TreeItem<>("Main view");
        TreeItem<String> assetsItem = new TreeItem<>("Assets");
        TreeItem<String> liabilitiesItem = new TreeItem<>("Liabilities");

        rootItem.getChildren().addAll(mainPageItem, assetsItem, liabilitiesItem);
        assetsLiabilitiesTreeView.setRoot(rootItem);
        assetsLiabilitiesTreeView.setShowRoot(false);

        assetsLiabilitiesTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleTreeViewSelection(newValue);
            }
        });
    }

    private void handleTreeViewSelection(TreeItem<String> selectedItem) {
        String selectedItemValue = selectedItem.getValue();
        if ("Main view".equals(selectedItemValue)) {
            // Load the main transaction page
            loadMainTransactionPage();
        }
        // Handle other selections if needed
    }
    @FXML
    private void loadMainTransactionPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/main-view.fxml"));
            AnchorPane mainPage = loader.load();
            mainContainer.getChildren().setAll(mainPage);
        }  catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load main view");
            alert.setContentText("An error occurred while loading the main view. Please try again.");
            alert.showAndWait();
       }  catch (IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Illegal State");
            alert.setContentText("An illegal state occurred. Please check the FXML file and initialization.");
            alert.showAndWait();
        }
    }

    private void initializeCategories() {
        categories.addAll(
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
        grid.add(new Label("Memo (optional):"), 0, 4);
        grid.add(descriptionField, 1, 4);

        incomeExpense.valueProperty().addListener((observable, oldValue, newValue) -> {
            categoryComboBox.setItems(filterCategories(newValue));
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> amountField.requestFocus());

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
            String date = java.time.LocalDate.now().toString();
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
            Transaction transaction = new Transaction(date, memo, amount, categoryValue, incomeExpenseValue, payerValue);
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

    private void updateTotalBalance(String incomeExpense, double amount) {
        totalBalance.set(totalBalance.get() + amount);
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
        assetTypeComboBox.getItems().addAll( "Chequing Account", "Savings Account", "Brokerage", "Properties", "Others");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Asset Name:"), 0, 0);
        grid.add(assetNameField, 1, 0);
        grid.add(new Label("Asset Type:"), 0, 1);
        grid.add(assetTypeComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> assetNameField.requestFocus());

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

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Liability Name:"), 0, 0);
        grid.add(liabilityNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> liabilityNameField.requestFocus());

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
        GridPane grid = new GridPane();
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        System.out.println("Update Transaction button clicked");
    }

    @FXML
    private void onRemoveTransaction(ActionEvent event) {
        // Handle Remove Transaction button click
        System.out.println("Remove Transaction button clicked");
    }
}