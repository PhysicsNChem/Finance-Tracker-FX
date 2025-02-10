package com.app.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {

    private static Stage primaryStage;
    private static FXMLLoader loader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        HelloApplication.primaryStage = primaryStage;

        DatabaseManager.createTables();


        SceneSwitcher switcher = new SceneSwitcher(primaryStage);
        HelloApplication.loader = new FXMLLoader(getClass().getResource("/com/app/demo/hello-view.fxml"));
        Scene scene = new Scene(loader.load());
        HelloController controller = loader.getController();
        controller.setSceneSwitcher(switcher);

        System.out.println("SceneSwitcher set in HelloController");

        switcher.preloadScene("transactions", "/com/app/demo/transactions-page.fxml");
        switcher.preloadScene("reports", "/com/app/demo/reports-page.fxml");




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
    public static void loadHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/app/demo/hello-view.fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}