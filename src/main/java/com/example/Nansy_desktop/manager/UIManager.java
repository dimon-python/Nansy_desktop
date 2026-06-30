package com.example.Nansy_desktop.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UIManager {
    private static Stage primaryStage;
    
    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setHeight(1);
        primaryStage.setWidth(1);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.setMaxWidth(800);
        primaryStage.setMaxHeight(600);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    public static void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(UIManager.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
        
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setWidth(primaryStage.getWidth());
            primaryStage.setHeight(primaryStage.getHeight());
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("UI Error: " + e.getMessage());
        }
    }
}
