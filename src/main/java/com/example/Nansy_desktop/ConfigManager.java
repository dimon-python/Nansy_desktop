package com.example.Nansy_desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private Properties defaultProps = new Properties();
    private Properties userProps = new Properties();
    private File userConfigFile;

    public ConfigManager() {
        try (InputStream input = getClass().getResourceAsStream("nansy.properties")) {
            defaultProps.load(input);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки системной конфигурации");
        }

        userConfigFile = new File(System.getProperty("user.dir"), "nansy.user.properties");
        if (userConfigFile.exists()) {
            try (FileInputStream input = new FileInputStream(userConfigFile)) {
                userProps.load(input);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки пользовательской конфигурации");
            }
        }
    }

    public String getUserProperty(String key) {
        return userProps.getProperty(key);
    }

    public String getSystemProperty(String key) {
        return defaultProps.getProperty(key);
    }

    public void setUserProperty(String key, String value) {
        userProps.setProperty(key, value);
        try (FileOutputStream output = new FileOutputStream(userConfigFile)) {
            userProps.store(output, null);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользовательской конфигурации");
        }
    }
}
