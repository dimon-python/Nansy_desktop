package com.example.Nansy_desktop;

import com.example.Nansy_desktop.handler.StompWebSocketHandler;
import com.example.Nansy_desktop.manager.UIManager;
import com.example.Nansy_desktop.service.AuthService;
import com.example.Nansy_desktop.util.JwtUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class NansyDesktopApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		UIManager.init(primaryStage);

		if (!AuthService.checkConnection()) {
			UIManager.openWindow("/fxml/no_ethernet.fxml", "Отсутствует подключение к интернету");
		} else if (!JwtUtil.jwtIsExists()) {
			UIManager.openWindow("/fxml/login.fxml", "Вход");
		} else {
			if (AuthService.verify()) {
				UIManager.openWindow("/fxml/main.fxml", "Nansy");
			} else {
				UIManager.openWindow("/fxml/login.fxml", "Вход");
			}
		}

		try {
			StompWebSocketHandler ws = new StompWebSocketHandler();
			ws.connect("dimond");
			ws.subscribe("/topic/echo", v -> {
				CommandExecutor.handleCommand(v);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}