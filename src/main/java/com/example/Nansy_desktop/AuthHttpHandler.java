package com.example.Nansy_desktop;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.io.IOException;

public class AuthHttpHandler {

    private static StompWebSocketHandler stompHandler;
	private static Properties properties;
    private static String serverUrl;
	private static String authUrl;
	private static JwtHandler jwtHandler;
    private static String jwtToken;

    public static void authenticateAndConnect(String pcUsername) {
		properties = new Properties();
		jwtHandler = new JwtHandler();
		stompHandler = new StompWebSocketHandler();

		try (InputStream input = new FileInputStream("nansy.properties")) {
			properties.load(input);
            authUrl = properties.getProperty("auth.server.url");
			URI uri = URI.create(authUrl);
				
			try{
				String json = String.format(
					"{\"username\":\"%s\"}",
					pcUsername
				);
					
				HttpClient httpClient = HttpClient.newHttpClient();
				
				HttpRequest request = HttpRequest.newBuilder()
					.uri(uri)
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();
				
				HttpResponse<String> response = httpClient.send(
					request,
					HttpResponse.BodyHandlers.ofString()
				);

				System.out.println("Response: " + response);
				System.out.println(response.body());
				jwtToken = jwtHandler.parseJwtToken(response.body());
					
				jwtHandler.setJwtToken(jwtToken);
				System.out.println("token saved");

				System.out.println("jwtToken: " + jwtToken);

			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Не удалось подключиться");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (InputStream input = new FileInputStream("nansy.properties")) {
			properties.load(input);
			serverUrl = properties.getProperty("websocket.server.url");
			jwtToken = jwtHandler.getJwtToken();

			stompHandler.connect(serverUrl, pcUsername, jwtToken);
			stompHandler.subscribe("/topic/echo", message -> {
				System.out.println(message);
			});

			stompHandler.send("/app/echo", "Hello, World!");

		} catch (IOException e) {
			System.err.println(e);
		}
	}
    
    public static String getJwtToken() { return jwtToken; }
}
