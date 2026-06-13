package com.example.Nansy_desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private Button menuButton;

    @FXML
    public void initialize() {
        System.out.println("Инициализация MainController");
    }

    @FXML
    public void loginButton() {
        openLoginWindow();
    }

    private void openLoginWindow() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            
            Stage thisStage = (Stage) menuButton.getScene().getWindow();
            thisStage.close();

            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setTitle("Вход");
            stage.setScene(scene);
            stage.setMinWidth(600);
            stage.setMinHeight(500);
            stage.setMaxWidth(800);
            stage.setMaxHeight(600);
            stage.setResizable(true);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}