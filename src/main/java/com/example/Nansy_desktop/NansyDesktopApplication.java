package com.example.Nansy_desktop;

import java.io.IOException;

import com.example.Nansy_desktop.handler.AuthHttpHandler;
import com.example.Nansy_desktop.handler.JwtHandler;
import com.example.Nansy_desktop.handler.StompWebSocketHandler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NansyDesktopApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (!AuthHttpHandler.checkConnection()) {
			openNoEthernetWindow(primaryStage);
		} else if (!JwtHandler.jwtIsExists()) {
			openLoginWindow(primaryStage);
		}

		openMainWindow(primaryStage);

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

	private void openMainWindow(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
			Scene scene = new Scene(loader.load());

			primaryStage.setTitle("Nansy");
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);
            primaryStage.setMaxWidth(800);
            primaryStage.setMaxHeight(600);
            primaryStage.setResizable(true);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void openLoginWindow(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
			Scene scene = new Scene(loader.load());

			primaryStage.setTitle("Nansy: вход");
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);
            primaryStage.setMaxWidth(800);
            primaryStage.setMaxHeight(600);
            primaryStage.setResizable(true);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void openNoEthernetWindow(Stage primaryStage) {
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/no_ethernet.fxml"));
			Scene scene = new Scene(loader.load());

			primaryStage.setTitle("Nansy: нет подключения к интернету");
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);
            primaryStage.setMaxWidth(800);
            primaryStage.setMaxHeight(600);
            primaryStage.setResizable(true);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}