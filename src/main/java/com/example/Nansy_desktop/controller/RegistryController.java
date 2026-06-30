package com.example.Nansy_desktop.controller;

import com.example.Nansy_desktop.manager.UIManager;
import com.example.Nansy_desktop.service.AuthService;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistryController {
    @FXML
    private TextField registryUsernameField;
    @FXML
    private PasswordField registryPasswordField;

    @FXML
    private void registryButton() {
        if (AuthService.registry(registryUsernameField.getText(), registryPasswordField.getText())) {
            UIManager.openWindow("/fxml/login.fxml", "Вход");
        }
    }

    @FXML
    private void openLoginWindow() {
        UIManager.openWindow("/fxml/login.fxml", "Вход");
    }
}
