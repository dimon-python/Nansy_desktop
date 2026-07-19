package com.example.Nansy_desktop.controller;

import java.util.UUID;

import com.example.Nansy_desktop.handler.StompWebSocketHandler;
import com.example.Nansy_desktop.manager.UIManager;

import javafx.fxml.FXML;

public class MainController {

    private static String pcId;
    private static final StompWebSocketHandler ws = new StompWebSocketHandler();

    @FXML
    public void initialize() {
        System.out.println("Инициализация MainController");
        try{
            pcId = UUID.randomUUID().toString();

            ws.connect();
            String topic = "/topic/id/" + pcId;
            String yesTopic = "/topic/yes/" + pcId;

            ws.subscribe(yesTopic, v -> {
                System.out.println("dimond " + v);
            });

            ws.subscribe(topic, v -> {
                System.out.println("YES: " + v);
            });

            ws.send("/app/announce/pc", pcId);

            try {
                Thread.sleep(100); // Задержка в миллисекундах (1000 = 1 секунда)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            ws.send("/app/yes", "yeeeees");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // private void openQRWindow() {

    // }

    @FXML
    private void openCommandEditorWindow() {
        UIManager.openWindow("/fxml/command_editor.fxml", "Редактор комманд");
    }
} 