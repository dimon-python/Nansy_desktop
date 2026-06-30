package com.example.Nansy_desktop.util;

import com.example.Nansy_desktop.manager.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JwtUtil {
    private static String jwtToken;
    
    public static String parseJwtToken(String responseBody) {
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

    public static Boolean jwtIsExists() {
        jwtToken = ConfigManager.getUserProperty("auth.jwt.token");
        return jwtToken != null && !jwtToken.isEmpty();
    }

    public static String getJwtToken() {
        if (jwtIsExists() == true) {
            return jwtToken;
        } else {
            return null;
        }
    }

    public static void setJwtToken(String token) {
        if (token == null || token.isEmpty()) {
            System.err.println("Token is null");
            return;
        } else {
            ConfigManager.setUserProperty("auth.jwt.token", token);
        }
    }
}
