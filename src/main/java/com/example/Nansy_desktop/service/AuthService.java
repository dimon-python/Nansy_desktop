package com.example.Nansy_desktop.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.example.Nansy_desktop.manager.ConfigManager;
import com.example.Nansy_desktop.util.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class AuthService {
	private static String httpServerUrl;
    private static String jwtToken;
	private static HttpClient httpClient;
	private final static String LOGIN_ENDPOINT = "/login";
	private final static String REGISTER_ENDPOINT = "/register";
	private final static String CHECK_ENDPOINT = "/check";
	private final static String VERIFY_ENDPOINT = "/verify";

	static {
		httpServerUrl = ConfigManager.getSystemProperty("auth.server.url"); //забираем url ws сервера
		httpClient = HttpClient.newHttpClient(); // создаем клиент
	}

	public static boolean checkConnection() {
		try{
			HttpRequest checkRequest = HttpRequest.newBuilder()
				.uri(URI.create(httpServerUrl + CHECK_ENDPOINT))
				.GET()
				.build();
			
			HttpResponse<String> response = httpClient.send(
				checkRequest,
				HttpResponse.BodyHandlers.ofString()	
			);

			return response.statusCode() == 200;
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean login(String username, String password) {
		try {
			Map<String, String> jsonLoginData = new HashMap<>(); // создаем библиотеку с парами для json
			jsonLoginData.put("username", username);
			jsonLoginData.put("password", password);
			String jsonLogin = new Gson().toJson(jsonLoginData); // превращаем библиотеку в json
			
			HttpRequest loginRequest = HttpRequest.newBuilder() // создаем запрос
				.uri(URI.create(httpServerUrl + LOGIN_ENDPOINT))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonLogin))
				.build();

			HttpResponse<String> response = httpClient.send( // отправляем запрос и ждем ответ
				loginRequest,
				HttpResponse.BodyHandlers.ofString()
			);

			if (response.statusCode() == 401) {
				System.out.println("ошибка логина");
				return false;
			} else {
				Gson gson = new Gson();
				JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
				jwtToken = jsonResponse.get("token").getAsString();
				JwtUtil.setJwtToken(jwtToken);
				System.out.println("ты молодец!)");
				return true;
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Не удалось подключиться");
			return false;
		}  catch (IOException e) {
    		System.err.println("Ошибка ввода-вывода: " + e.getMessage());
    		e.printStackTrace();
			return false;
		}
	}

	public static boolean registry(String username, String password) {
		try{
			Map<String, String> jsonRegistryData = new HashMap<>();
			jsonRegistryData.put("username", username);
			jsonRegistryData.put("password", password);
			String jsonRegistry = new Gson().toJson(jsonRegistryData);

			HttpRequest registryRequest = HttpRequest.newBuilder() // создаем запрос
				.uri(URI.create(httpServerUrl + REGISTER_ENDPOINT))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonRegistry))
				.build();

			HttpResponse<String> response = httpClient.send( // отправляем запрос и ждем ответ
				registryRequest,
				HttpResponse.BodyHandlers.ofString()
			);

			return (response.statusCode() == 200);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Не удалось подключиться");
			return false;
		}  catch (IOException e) {
    		System.err.println("Ошибка ввода-вывода: " + e.getMessage());
    		e.printStackTrace();
			return false;
		}
	}

	public static boolean verify() {
		jwtToken = JwtUtil.getJwtToken();
		try {
			Map<String, String> jsonTokenData = new HashMap<>();
			jsonTokenData.put("token", jwtToken);
			String jsonVerify = new Gson().toJson(jsonTokenData);

			HttpRequest verifyRequest = HttpRequest.newBuilder() // создаем запрос
				.uri(URI.create(httpServerUrl + VERIFY_ENDPOINT))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonVerify))
				.build();

			HttpResponse<String> response = httpClient.send( // отправляем запрос и ждем ответ
				verifyRequest,
				HttpResponse.BodyHandlers.ofString()
			);

			return response.statusCode() == 200;
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Не удалось подключиться");
			return false;
		}  catch (IOException e) {
    		System.err.println("Ошибка ввода-вывода: " + e.getMessage());
    		e.printStackTrace();
			return false;
		}
	}
    
    public static String getJwtToken() { return jwtToken; }
}
