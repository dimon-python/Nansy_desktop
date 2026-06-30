package com.example.Nansy_desktop.controller;

import com.example.Nansy_desktop.manager.UIManager;
import com.example.Nansy_desktop.service.AuthService;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField loginUsernameField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private void loginButton() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Пароль или имя юзера пустые");
            return;
        }

        if (AuthService.login(username, password)) {
            UIManager.openWindow("/fxml/main.fxml", "Nansy");
        }

        System.out.println("username: " + username);
        System.out.println("password: " + password);
    }

    @FXML
    private void openRegistryWindow() {
        UIManager.openWindow("/fxml/registry.fxml", "Регистрация");
    }
}
