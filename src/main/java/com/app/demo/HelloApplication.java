package com.app.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;


        DatabaseManager.createTables();


        SceneSwitcher switcher = new SceneSwitcher(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/hello-view.fxml"));
        Scene scene = new Scene(loader.load());
        HelloController controller = loader.getController();
        controller.setSceneSwitcher(switcher);

        FXMLLoader transactionsLoader = new FXMLLoader(getClass().getResource("/com/app/demo/transactions-page.fxml"));
        Parent transactionsRoot = transactionsLoader.load();
        TransactionController transactionsController = transactionsLoader.getController();
        transactionsController.setSceneSwitcher(switcher);

        FXMLLoader reportsLoader = new FXMLLoader(getClass().getResource("/com/app/demo/reports-page.fxml"));
        reportsLoader.load();

        primaryStage.setTitle("FBLA Project");



        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1067);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}