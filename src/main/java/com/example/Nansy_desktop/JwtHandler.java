package com.example.Nansy_desktop;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JwtHandler {
    private static String jwtToken;
    
    public String parseJwtToken(String responseBody) {
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

    public Boolean jwtIsExists() {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream("nansy.properties")) {
			properties.load(input);
            jwtToken = properties.getProperty("auth.jwt.token");
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }

    public String getJwtToken() {
        if (jwtIsExists() == true) {
            return jwtToken;
        } else {
            return null;
        }
    }

    public void setJwtToken(String token) {
        if (token == null || token.isEmpty()) {
            System.err.println("Token is null");
            return;
        } else {
            Properties properties = new Properties();
            
            try (InputStream input = new FileInputStream("nansy.properties")) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Error loading config and JWT: " + e.getMessage());
            }

            properties.setProperty("auth.jwt.token", token);
            try (FileOutputStream output = new FileOutputStream("nansy.properties")) {
                properties.store(output, null);
            } catch (Exception e) {
                System.err.println("Error saving properties: " + e.getMessage());
            }
        }
    }
}
