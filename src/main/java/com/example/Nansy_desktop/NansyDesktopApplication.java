package com.example.Nansy_desktop;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class NansyDesktopApplication {

	private static StompWebSocketHandler stompHandler;
	private static CommandExecutor commandExecutor;
	private static String jwtToken;
	private static String pcUsername;
	private static Properties properties;
    private static String serverUrl;
	private static String authUrl;

	public static void main(String[] args) {
		System.out.println("запуск Nansy");
		commandExecutor = new CommandExecutor();

		pcUsername = System.getProperty("user.name");
		System.out.println("Имя пк: " + pcUsername);

		authenticateAndConnect();

	}

	private static void authenticateAndConnect() {
		properties = new Properties();

		try (InputStream input = NansyDesktopApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
			
			if (input == null) {
				System.out.println("Файл конфиг не найден");
			} else {
				properties.load(input);
                authUrl = properties.getProperty("auth.server.url");
				URI uri = URI.create(authUrl);
				// System.out.println(authUrl);
				
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
					jwtToken = parseJwtToken(response.body());
					System.out.println("jwtToken: " + jwtToken);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Не удалось подключиться");
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String parseJwtToken(String responseBody) {
		try {
			Gson gson = new Gson();
			JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
			String jwtToken = jsonResponse.get("token").getAsString();

			return jwtToken;
		} catch (Exception e) {
			System.err.println("Error jwt token");
			return null;
		}
	}

	
}