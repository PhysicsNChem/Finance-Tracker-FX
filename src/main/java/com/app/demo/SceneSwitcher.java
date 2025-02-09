package com.app.demo;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class SceneSwitcher {
    private final Stage stage;
    private final Map<String, Scene> scenes = new HashMap<>();

    public SceneSwitcher(Stage stage) {
        this.stage = stage;
    }
    // Load a new scene from an FXML file
    public void switchTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            stage.setMinWidth(1280);
            stage.setMinHeight(720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void preloadScene(String name, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            scenes.put(name, new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToPreloaded(String name) {
        Scene scene = scenes.get(name);
        if (scene != null) {
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("Scene not preloaded: " + name);
        }
    }
}

