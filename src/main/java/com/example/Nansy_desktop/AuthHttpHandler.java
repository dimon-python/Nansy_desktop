package com.example.Nansy_desktop;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class AuthHttpHandler {
	private static String authUrl;
    private static String jwtToken;

    public static void authenticate(String pcUsername) {
        authUrl = ConfigManager.getSystemProperty("auth.server.url");
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
			jwtToken = JwtHandler.parseJwtToken(response.body());
					
			JwtHandler.setJwtToken(jwtToken);
			System.out.println("token saved");

			System.out.println("jwtToken: " + jwtToken);

		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Не удалось подключиться");
		}  catch (IOException e) {
    		System.err.println("Ошибка ввода-вывода: " + e.getMessage());
    		e.printStackTrace();
		}
	}
    
    public static String getJwtToken() { return jwtToken; }
}
