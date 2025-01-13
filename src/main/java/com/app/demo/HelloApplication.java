package com.app.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/hello-view.fxml"));
        Scene scene = new Scene(loader.load());

        DatabaseManager.createTables();

        HelloController controller = loader.getController();
        SceneSwitcher switcher = new SceneSwitcher(primaryStage);
        controller.setSceneSwitcher(switcher);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1067);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(1067);
        primaryStage.setHeight(600);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}