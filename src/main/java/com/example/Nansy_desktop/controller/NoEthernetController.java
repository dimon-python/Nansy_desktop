package com.example.Nansy_desktop.controller;

import com.example.Nansy_desktop.NansyDesktopApplication;
import com.example.Nansy_desktop.service.AuthService;

import javafx.fxml.FXML;

public class NoEthernetController {
    @FXML
    private void checkEthernetConnection() {
        if (AuthService.checkConnection()) {
            NansyDesktopApplication.startCheck();
        }
    }
}
