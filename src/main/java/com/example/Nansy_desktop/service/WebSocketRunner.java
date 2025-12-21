package com.example.Nansy_desktop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

@Component
public class WebSocketRunner implements CommandLineRunner{
    @Autowired
    private WebSocketService webSocketClient;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Клиент запущен. Подключение через 2 секунды...");
        Thread.sleep(2000);
        webSocketClient.connect();
    }
}
